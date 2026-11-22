package egp.remie.host.file;

import java.io.*;
import java.net.*;
import java.net.http.*;
import egp.remie.host.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.nio.file.*;


public class Hosts {
  
  public static final HttpClient client = HttpClient.newHttpClient();
  
  public static Host THIS_COMPUTER = new Host(){
    private final String _os = System.getProperty("os.name");
    @Override
    public String domain(){
      return "";
    }
    @Override
    public String os(){
      return _os;
    }
  };
  
  public static byte[] serialize(Object obj) throws IOException {    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(obj);
    baos.close();
    return baos.toByteArray();
  }
  
  private static HostShell createLocalShell(HostFile fl) throws IOException {
    Process proc = Server.createShellProcess(fl.getPath());
    return new HostShell(){
      @Override
      public HostFile getWorkingDirectory(){
        return fl;
      }
      @Override
      public long getId(){
        return proc.pid();
      }
      @Override
      public InputStream getInputStream(){
        return proc.getInputStream();
      }
      @Override
      public OutputStream getOutputStream(){
        return proc.getOutputStream();
      }
    };
  }
  
  public static HostShell createShell(HostFile fl) throws IOException, InterruptedException {    
    if(HostFiles.isLocalFile(fl)){
      return createLocalShell(fl);
    }
    try{
      HttpRequest idReq = HttpRequest.newBuilder()
        .uri(URI.create(fl.getHost().domain() + "/shell/create"))
        .header("Content-Type", "text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      long id = (Long)handleResponse(idReq);
      
      HttpRequest isReq = HttpRequest.newBuilder()
        .uri(URI.create(fl.getHost().domain() + "/shell/is?id=" + id))
        .GET()
        .build();
        
      InputStream is = (InputStream)handleResponse(isReq);
      
      PipedOutputStream bais = new PipedOutputStream();      
      PipedInputStream pis = new PipedInputStream(bais); //BufferedReader br = new BufferedReader( new InputStreamReader( new PipedInputStream(bais)));
      
      new Thread(()->{
        try{
          byte[] buffer = new byte[1024];
          int r;
          String command;
          while((r = pis.read(buffer)) > -1){
            command = new String(buffer, 0, r);
            HttpRequest osReq = HttpRequest.newBuilder()
              .uri(URI.create(fl.getHost().domain() + "/shell/os?id=" + id))
              .headers("Content-Type", "text/plain")
              .POST(HttpRequest.BodyPublishers.ofString(command))
              .build();
          
            int statusCode = (Integer)handleResponse(osReq);
          }
        }catch(ClassNotFoundException | InterruptedException | IOException ex){
          ex.printStackTrace();
        }
      }).start();
      
      
      return new HostShell(){
        @Override
        public HostFile getWorkingDirectory(){
          return fl;
        }
        @Override
        public long getId(){
          return id;
        }
        @Override
        public InputStream getInputStream(){
          return is;
        }
        @Override
        public OutputStream getOutputStream(){
          return bais;
        }
      };  
    } catch (Exception ex){
      ex.printStackTrace();
    }
    return null;
  }
  
  public static Host get(String domainName) throws IOException, InterruptedException {   
    HttpRequest req = HttpRequest.newBuilder()
      .uri(URI.create(domainName + "/os"))
      .GET()
      .build();    
      HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
      
    
    return new Host(){
      @Override
      public String domain(){
        return domainName;
      }
      @Override
      public String os(){
        return res.body();
      }
    };
  }
  
  public static Object handleResponse(HttpRequest req) throws IOException, ClassNotFoundException, InterruptedException {    
    HttpResponse<InputStream> res = client.send(req, HttpResponse.BodyHandlers.ofInputStream());    
    Integer statusCode = res.statusCode();
    HttpHeaders headers = res.headers(); //Map<String, List<String>> headers = res.headers().map();
    
    int contentLength = Integer.parseInt(headers.firstValue("Content-Length").orElse("0"));
    String contentType = headers.firstValue("Content-Type").orElse("null");
    if(contentLength == 0 && contentType.equals("null")){
      return statusCode;
    }
    
    if(contentType.equals("application/x-java-serialized-object")){          
      Class objClass = Class.forName(headers.firstValue("Class-Name").get());
      Object obj;
      try(ObjectInputStream ois = new ObjectInputStream(res.body())){
        obj = objClass.cast(ois.readObject());
      }     
      if(statusCode != 200 && obj instanceof Exception){    
        if(obj instanceof IOException)
          throw (IOException)obj;
        
        ((Exception)obj).printStackTrace();
      }
      return obj;
    }
    if(contentType.equals("text/plain")){          
      String content;
      try(InputStream ois = res.body()){
        content = new String(ois.readAllBytes());
      }
      return content;
    }
    if(contentType.equals("application/octet-stream")){
      return res.body();
    }
    throw new IOException("Could not parse response, unknown Content-Type: \"" + contentType + "\"");
  }
}
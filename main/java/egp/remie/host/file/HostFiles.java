package egp.remie.host.file;

import egp.remie.host.*;
import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.nio.file.*;
import java.net.*;
import java.net.http.*;

public class HostFiles {
  
  //private static final HttpClient cli = HttpClient.newHttpClient();
  
  public static void move(HostFile originFl, HostFile targetFl) throws IOException, InterruptedException {
    if(isLocalFile(originFl) && isLocalFile(targetFl)){
      Files.move(originFl.getPath(), targetFl.getPath());
      return;
    }
    Host originHost = originFl.getHost();
    Host targetHost = targetFl.getHost();
    if(originHost.equals(targetHost)){
      String originStrPath = originFl.getPath().toString();
      String targetStrPath = targetFl.getPath().toString();
      endpointPost("/move", originHost, new String[]{originStrPath, targetStrPath});
      return;
    }
  }
    
  public static void write(HostFile fl, byte[] content) throws IOException, InterruptedException {
    boolean success = false;
    if(isLocalFile(fl)){
      Files.write(fl.getPath(), content);
      return;
    }
    String pathName = fl.getPath().toString();
    String strContent = new String(content);    
    endpointPost("/write", fl.getHost(), new String[]{pathName, strContent});      
  }
  
  public static void createFile(HostFile fl) throws IOException, InterruptedException {
    
    if(isLocalFile(fl)){
      Files.write(fl.getPath(), new byte[0], StandardOpenOption.CREATE_NEW); //learning_note: its possible to use many StandardOptions as StandardCharsets     
      return;
    }
    endpointPost("/create/file", fl.getHost(), new String[]{fl.getPath().toString()});
  }
  
  public static void createDirectory(HostFile fl) throws IOException, InterruptedException {
    if(isLocalFile(fl)){
      Files.createDirectory(fl.getPath()); //learning_note: its possible to use many StandardOptions as StandardCharsets
      return;
    }
    endpointPost("/create/directory", fl.getHost(), new String[]{fl.getPath().toString()});
  }
  
  public static void delete(HostFile fl) throws IOException, InterruptedException {
    if(isLocalFile(fl)){
      //System.out.println("delete action disabled");
      Files.delete(fl.getPath()); //learning_note: its possible to use many StandardOptions as StandardCharsets
      return;
    }
    endpointPost("/delete", fl.getHost(), new String[]{fl.getPath().toString()});
  }
  
  public static Stream<HostFile> list(HostFile fl) throws IOException, InterruptedException{
    if(isLocalFile(fl)){
      return Files.list(fl.getPath())
        .map(HostFile::new);
    }
    try{
      return Arrays.<String>asList(endpointQuery("/list", fl, String[].class)).stream()
        .map(p -> new HostFile(fl.getHost(), p));      
    }catch(ClassNotFoundException ex){
      ex.printStackTrace();
    }
    return null;
  }
  
  public static boolean isDirectory(HostFile fl){
    if(isLocalFile(fl))
      return Files.isDirectory(fl.getPath());
    try {
      return endpointQuery("/is/directory", fl, Boolean.class);
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return false;
  }
  
  public static Path getAbsolutePathOf(HostFile fl) throws IOException, InterruptedException {
    if(fl.getHost() == null)
      return fl.getPath().toAbsolutePath();
    try{
      return Paths.get(endpointQuery("/absolute/path", fl, String.class));
    }catch(ClassNotFoundException ex){
      ex.printStackTrace();
    }
    return null;
  }
  
  public static String readString(HostFile fl) throws IOException, InterruptedException {    
    if (isLocalFile(fl)){      
      return Files.readString(fl.getPath());
    } 
    String content = null;
    try {
      content = endpointQuery("/read", fl, String.class);      
    }catch(ClassNotFoundException ex) {
      ex.printStackTrace();
    }
        
    return content;
  } 
  
  public static boolean exists(HostFile fl){
    if(isLocalFile(fl))
      return Files.exists(fl.getPath()); 
    try{
      String pathName = fl.getPath().toString();
      HttpClient cli = HttpClient.newHttpClient();
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(fl.getHost().domain() + "/exists"))
        .header("Content-type","application/octet-stream")
        .POST(HttpRequest.BodyPublishers.ofByteArray(pathName.getBytes()))
        .build();
     
      HttpResponse<String> res = cli.send(req, HttpResponse.BodyHandlers.ofString());
      return Boolean.parseBoolean(res.body());
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return false;
  }
  
  public static boolean isLocalFile(HostFile fl){
    return fl.getHost() == null;
  }
  
  public static void rename(HostFile fl, String name) throws IOException, InterruptedException {
    Path oldPath = fl.getPath();
    Path path = oldPath.resolveSibling(name);
    if(isLocalFile(fl)){
      Files.move(oldPath, path);
      fl.setPath(path);
      return;
    }
    endpointPost("/move", fl.getHost(), new String[]{oldPath.toString(), path.toString()});  
  }
  
  public static void endpointPost(String endpoint, Host host, String[] values) throws IOException, InterruptedException {
    HttpClient cli = HttpClient.newHttpClient();
    HttpRequest req = HttpRequest.newBuilder()
      .uri(URI.create(host.domain() + endpoint))
      .POST(HttpRequest.BodyPublishers.ofByteArray(Server.serialize(values)))
      .build();      
    HttpResponse<byte[]> res = cli.send(req, HttpResponse.BodyHandlers.ofByteArray());
   // try{     
      //res.statusCode();
      int contentLength = Integer.parseInt(res.headers().firstValue("Content-Length")
        .orElse("0"));
      if(contentLength == 0)  
        return; 
    //}catch( ex){
    //  ex.printStackTrace();
    //}
    ObjectInputStream oos = new ObjectInputStream(new ByteArrayInputStream(res.body()));    
    cli.close();
    try{
      Object obj = oos.readObject();
      if(obj instanceof IOException){
        throw (IOException)obj;
      }      
    }catch(ClassNotFoundException ex){
      ex.printStackTrace();
    }
  }
  public static <T> T endpointQuery(String endpoint, HostFile fl, Class<T> type) throws IOException, ClassNotFoundException, InterruptedException {
    HttpClient cli = HttpClient.newHttpClient();
    HttpRequest req = HttpRequest.newBuilder()
      .uri(URI.create(fl.getHost().domain() + endpoint))
      .POST(HttpRequest.BodyPublishers.ofByteArray(fl.getPath().toString().getBytes()))
      .build();
    HttpResponse<byte[]> res = cli.send(req, HttpResponse.BodyHandlers.ofByteArray());
    ObjectInputStream oos = new ObjectInputStream(new ByteArrayInputStream(res.body()));
    Object obj = oos.readObject(); 
    cli.close();
    if(obj instanceof IOException){
      IOException ex = (IOException)obj;
      ex.getMessage();
      throw ex;
    }
    return type.cast(obj);
  }
}
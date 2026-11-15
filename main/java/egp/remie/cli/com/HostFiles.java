package egp.remie.cli.com;

import egp.remie.host.*;
import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.nio.file.*;
import java.net.*;
import java.net.http.*;

public class HostFiles {
  
  //private static final HttpClient cli = HttpClient.newHttpClient();
    
  public static void write(HostFile fl, byte[] content) throws IOException, InterruptedException {
    boolean success = false;
    if(isLocalFile(fl)){
      Files.write(fl.getPath(), content);
      return;
    }
    String pathName = fl.getPath().toString();
    String strContent = new String(content);    
    endpointPost("/write", fl, new String[]{pathName, strContent});      
  }
  
  public static void createFile(HostFile fl) throws IOException, InterruptedException {
    
    if(isLocalFile(fl)){
      Files.write(fl.getPath(), new byte[0], StandardOpenOption.CREATE_NEW); //learning_note: its possible to use many StandardOptions as StandardCharsets     
      return;
    }
    endpointPost("/create/file", fl, new String[]{fl.getPath().toString()});
  }
  
  public static void createDirectory(HostFile fl) throws IOException, InterruptedException {
    if(isLocalFile(fl)){
      Files.createDirectory(fl.getPath()); //learning_note: its possible to use many StandardOptions as StandardCharsets
      return;
    }
    endpointPost("/create/directory", fl, new String[]{fl.getPath().toString()});
  }
  
  public static void delete(HostFile fl) throws IOException, InterruptedException {
    if(isLocalFile(fl)){
      //System.out.println("delete action disabled");
      Files.delete(fl.getPath()); //learning_note: its possible to use many StandardOptions as StandardCharsets
      return;
    }
    endpointPost("/delete", fl, new String[]{fl.getPath().toString()});
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
  
  public static void rename(HostFile file, String name) throws IOException {
    if(isLocalFile(file)){
      Path oldPath = file.getPath();
      Path path = oldPath.resolveSibling(name);
      Files.move(oldPath, path);
      file.setPath(path);
      return;
    }
    return;
  }
  
  public static void endpointPost(String endpoint, HostFile fl, String[] values) throws IOException, InterruptedException {
    HttpClient cli = HttpClient.newHttpClient();
    HttpRequest req = HttpRequest.newBuilder()
      .uri(URI.create(fl.getHost().domain() + endpoint))
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
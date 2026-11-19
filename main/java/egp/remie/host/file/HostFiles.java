package egp.remie.host.file;

import egp.remie.host.*;
import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.nio.file.*;
import java.net.*;
import java.net.http.*;

public class HostFiles {
  
  private static final HttpClient client = HttpClient.newHttpClient();
  
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
      try{        
        HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(originHost + "/move"))
          .header("Content-Type","text/plain")
          .header("Content-Disposition", targetFl.getPath().toString())
          .POST(HttpRequest.BodyPublishers.ofString(originFl.getPath().toString()))
          .build();
          
        int statusCode = (Integer)handleResponse(req);
      }catch(ClassNotFoundException ex ){
        ex.printStackTrace();
      }
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
    
    try {      
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(fl.getHost() + "/write"))
        .header("Content-Type","application/octet-stream")
        .header("Content-Disposition", fl.getPath().toString())
        .POST(HttpRequest.BodyPublishers.ofByteArray(content))
        .build();
        
      int statusCode = (Integer)handleResponse(req);
    }catch(ClassNotFoundException ex) {
      ex.printStackTrace();
    }     
  }
  
  public static void createFile(HostFile fl) throws IOException, InterruptedException {
    
    if(isLocalFile(fl)){
      Files.write(fl.getPath(), new byte[0], StandardOpenOption.CREATE_NEW); //learning_note: its possible to use many StandardOptions as StandardCharsets     
      return;
    }
    
    try {      
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(fl.getHost() + "/create/file"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      int statusCode = (Integer)handleResponse(req);
    }catch(ClassNotFoundException ex) {
      ex.printStackTrace();
    }
  }
  
  public static void createDirectory(HostFile fl) throws IOException, InterruptedException {
    if(isLocalFile(fl)){
      Files.createDirectory(fl.getPath()); //learning_note: its possible to use many StandardOptions as StandardCharsets
      return;
    }
    
    try {      
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(fl.getHost() + "/create/directory"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      int statusCode = (Integer)handleResponse(req);
    }catch(ClassNotFoundException ex) {
      ex.printStackTrace();
    }
  }
  
  public static void delete(HostFile fl) throws IOException, InterruptedException {
    if(isLocalFile(fl)){
      //System.out.println("delete action disabled");
      Files.delete(fl.getPath()); //learning_note: its possible to use many StandardOptions as StandardCharsets
      return;
    }
    
    try {      
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(fl.getHost() + "/delete"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      int statusCode = (Integer)handleResponse(req);
      
    }catch(ClassNotFoundException ex){
      ex.printStackTrace();
    }
  }
  
  private static Object handleResponse(HttpRequest req) throws IOException, ClassNotFoundException, InterruptedException {    
    HttpResponse<InputStream> res = client.send(req, HttpResponse.BodyHandlers.ofInputStream());    
    Integer statusCode = res.statusCode();
    HttpHeaders headers = res.headers(); //Map<String, List<String>> headers = res.headers().map();
    int contentLength = Integer.parseInt(headers.firstValue("Content-Length").get());
    
    if(contentLength == -1){
      return statusCode;
    }
    String contentType = headers.firstValue("Content-Type").get();
    
    if(contentType.equals("application/x-java-serialized-object")){          
      Class objClass = Class.forName(headers.firstValue("Class-Name").get());
      Object obj;
      try(ObjectInputStream ois = new ObjectInputStream(res.body())){
        obj = objClass.cast(ois.readObject());
      }     
      if(statusCode != 200 && obj instanceof Exception){    
        if(obj instanceof IOException)
          throw (IOException)obj;
      }
      return obj;
    }
    if(contentType.equals("text/plain")){          
      String content;
      try(ObjectInputStream ois = new ObjectInputStream(res.body())){
        content = new String(ois.readAllBytes());
      }
      return content;
    }
    if(contentType.equals("application/octet-stream")){     
      try(ObjectInputStream ois = new ObjectInputStream(res.body())){
        return Optional.of(ois);
      }
    }
    return null;
  }
  
  public static HostFile[] list(HostFile fl) throws IOException, InterruptedException {
    HostFile[] files;
    if(isLocalFile(fl)){
      Stream<Path> stream = Files.list(fl.getPath());
      files = stream
        .map(HostFile::new)
        .toArray(HostFile[]::new);
      stream.close();
      return files;
    }
    try{
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(fl.getHost() + "/list"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      Object ob = handleResponse(req);
      
      return Arrays.stream((String[])ob)
        .map(HostFile::new)
        .toArray(HostFile[]::new);
        
    }catch(ClassNotFoundException ex){
      ex.printStackTrace();
    }
    return null;
  }
  
  public static boolean isDirectory(HostFile fl) throws InterruptedException {
    if(isLocalFile(fl))
      return Files.isDirectory(fl.getPath());
    
    try {
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(fl.getHost() + "/is/directory"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      Boolean isDir = (Boolean)handleResponse(req);
      return isDir;
      
    }catch(ClassNotFoundException | IOException | InterruptedException ex){
      ex.printStackTrace();
    }
    return false;
  }
  
  public static Path getAbsolutePathOf(HostFile fl) throws IOException, InterruptedException {
    if(fl.getHost() == null)
      return fl.getPath().toAbsolutePath();
    try{      
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(fl.getHost() + "/absolute/path"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      String strPath = (String)handleResponse(req);
      return Paths.get(strPath);
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
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(fl.getHost() + "/read/string"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      content = (String)handleResponse(req);
      
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
    move(fl, new HostFile(fl.getHost(), path));
  }
  
  /*public static void endpointPost(String endpoint, Host host, String[] values) throws IOException, InterruptedException {
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
  }*/
}
package egp.remie.host.file;

import egp.remie.host.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.io.*;
import java.nio.file.*;
import java.net.*;
import java.net.http.*;

public class HostFiles {
  
  public static boolean equalHosts(HostFile fl0, HostFile fl1){
    Host host0 = fl0.getHost();
    String hostDomain0 = (host0 == null ? "" : host0.domain());
    Host host1 = fl1.getHost();
    String hostDomain1 = (host1 == null ? "" : host1.domain());
    
    return hostDomain0.equals(hostDomain1);
  }
  
  public static boolean areHostEquals(HostFile fl0, HostFile fl1){
    if(isLocalFile(fl0) && isLocalFile(fl1))
      return true;
    
    return equalHosts(fl0, fl1);
  }
  
  public static void copyAll(HostFile originFl, HostFile targetFl, Consumer<Integer> consumer) throws IOException, InterruptedException{
    if(isDirectory(originFl)){
      if(HostFiles.areHostEquals(originFl, targetFl)){
        Path originPath = originFl.getPath();
        Path targetParentPath = targetFl.getPath().getParent();
        if(originPath.equals(targetParentPath)){
          throw new IOException("Can not copy folder to it self");
        }
      }
      
      if(!HostFiles.exists(targetFl))
          createDirectory(targetFl);
      
      for(HostFile fl : HostFiles.list(originFl)){
        copyAll(fl, targetFl.resolve(fl.getFileName()), consumer);
      }
      return;
    }
    if(!HostFiles.exists(targetFl)){
      copy(originFl, targetFl, consumer);
      return;
    }
    long originSize = HostFiles.sizeOf(originFl);
    long targetSize = HostFiles.sizeOf(targetFl);
    
    if(originSize != targetSize){
      System.out.println(originSize);
      System.out.println(targetSize);
      delete(targetFl);
      copy(originFl, targetFl, consumer);
    }
  }
  
  public static Long sizeOf(HostFile fl) throws IOException, InterruptedException {
    if(isLocalFile(fl)){
        return Files.size(fl.getPath()); 
    }
    try{
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(fl.getHost().domain() + "/size"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      return (Long)Hosts.handleResponse(req);
      
    }catch(ClassNotFoundException ex){
      ex.printStackTrace();
    }
    return null;
  }
  
  public static void moveAll(HostFile originFl, HostFile targetFl, Consumer<Integer> consumer) throws IOException, InterruptedException{
    if(isDirectory(originFl)){
      if(HostFiles.areHostEquals(originFl, targetFl)){
        Path originPath = originFl.getPath();
        Path targetParentPath = targetFl.getPath().getParent();
        if(originPath.equals(targetParentPath)){
          throw new IOException("Can not move folder to it self");
        }
      }
      createDirectory(targetFl);
      for(HostFile fl : HostFiles.list(originFl)){
        moveAll(fl, targetFl.resolve(fl.getFileName()), consumer);
      }
      delete(originFl);
      return;
    }
    move(originFl, targetFl, consumer);
  }
  
  public static void move(HostFile originFl, HostFile targetFl, Consumer<Integer> consumer) throws IOException, InterruptedException {
    if(isLocalFile(originFl) && isLocalFile(targetFl)){
      Files.move(originFl.getPath(), targetFl.getPath());
      return;
    }
    Host originHost = originFl.getHost();
    Host targetHost = targetFl.getHost();
    if(originHost != null){
      if(originHost.equals(targetHost)){
        String originStrPath = originFl.getPath().toString();
        String targetStrPath = targetFl.getPath().toString();
        try{        
          HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(originHost.domain() + "/move"))
            .header("Content-Type","text/plain")
            .header("Content-Disposition", targetFl.getPath().toString())
            .POST(HttpRequest.BodyPublishers.ofString(originFl.getPath().toString()))
            .build();
            
          int statusCode = (Integer)Hosts.handleResponse(req);
        }catch(ClassNotFoundException ex ){
          ex.printStackTrace();
        }
        return;
      }  
    } 
    copy(originFl, targetFl, consumer);
    delete(originFl);
  }
  
  public static void copy(HostFile originFl, HostFile targetFl, Consumer<Integer> consumer) throws IOException, InterruptedException {
    if(isLocalFile(originFl) && isLocalFile(targetFl)){
      Files.copy(originFl.getPath(), targetFl.getPath());
      return;
    }
    Host originHost = originFl.getHost();
    Host targetHost = targetFl.getHost();
    if(originHost != null){
      if(originHost.equals(targetHost)){
        String originStrPath = originFl.getPath().toString();
        String targetStrPath = targetFl.getPath().toString();
        try{        
          HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(originHost.domain()  + "/copy"))
            .header("Content-Type","text/plain")
            .header("Content-Disposition", targetFl.getPath().toString())
            .POST(HttpRequest.BodyPublishers.ofString(originFl.getPath().toString()))
            .build();
            
          int statusCode = (Integer)Hosts.handleResponse(req);
          
        }catch(ClassNotFoundException ex ){
          ex.printStackTrace();
        }
        return;
      }  
    } 
    if(isLocalFile(originFl)){
      try{        
        HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(targetHost.domain()  + "/write?overwrite=false&append=false"))
          .header("Content-Type","application/octet-stream")
          .header("Content-Disposition", targetFl.getPath().toString())
          .POST(HttpRequest.BodyPublishers.ofInputStream(()->{
            try{
              return new FileInputStream(originFl.getPath().toFile());
            }catch(IOException ex){
              ex.printStackTrace();
            }
            return null;
          }))
          .build();
          
        int statusCode = (Integer)Hosts.handleResponse(req);
      }catch(ClassNotFoundException ex ){
        ex.printStackTrace();
      }
      return;
    }
    try{        
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(originHost.domain()  + "/read"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(originFl.getPath().toString()))
        .build();
        
      try(InputStream is = (InputStream)Hosts.handleResponse(req)){
        try(FileOutputStream fos = new FileOutputStream(targetFl.getPath().toFile(), true)){
          int read = 0;
          byte[] buffer = new byte[8192];
          
          while((read = is.read(buffer)) > 0){
            fos.write(buffer, 0, read);
            fos.flush();
          }
        }
      }
    }catch(ClassNotFoundException ex ){
      ex.printStackTrace();
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
        .uri(URI.create(fl.getHost().domain() + "/write?overwrite=true&append=false"))
        .header("Content-Type","application/octet-stream")
        .header("Content-Disposition", fl.getPath().toString())
        .POST(HttpRequest.BodyPublishers.ofByteArray(content))
        .build();
        
      int statusCode = (Integer)Hosts.handleResponse(req);
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
        .uri(URI.create(fl.getHost().domain() + "/create/file"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      int statusCode = (Integer)Hosts.handleResponse(req);
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
        .uri(URI.create(fl.getHost().domain() + "/create/directory"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      int statusCode = (Integer)Hosts.handleResponse(req);
    }catch(ClassNotFoundException ex) {
      ex.printStackTrace();
    }
  }
  
  public static void deleteAll(HostFile fl){
    
  }
  
  public static void delete(HostFile fl) throws IOException, InterruptedException {
    if(isLocalFile(fl)){
      //System.out.println("delete action disabled");
      if(!HostFiles.isDirectory(fl)){
        Files.delete(fl.getPath());
        return;        
      }
      Path path = fl.getPath();
      try(Stream<Path> stream = Files.walk(path)){
        stream.sorted(Comparator.reverseOrder())
          .forEach(p -> {
            try{
              Files.delete(p);
            }catch(IOException ex){
              throw new RuntimeException(ex);
            }
          });
      }
      return;
    }
    
    try {      
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(fl.getHost().domain() + "/delete"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      int statusCode = (Integer)Hosts.handleResponse(req);
      
    }catch(ClassNotFoundException ex){
      ex.printStackTrace();
    }
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
        .uri(URI.create(fl.getHost().domain() + "/list"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      Object ob = Hosts.handleResponse(req);
      
      return Arrays.stream((String[])ob)
        .map((s)-> new HostFile(fl.getHost(), s))
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
        .uri(URI.create(fl.getHost().domain() + "/is/directory"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      return (Boolean)Hosts.handleResponse(req);
      
    }catch(ClassNotFoundException | IOException | InterruptedException ex){
      ex.printStackTrace();
    }
    return false;
  }
  
  public static Path getAbsolutePathOf(HostFile fl) throws IOException, InterruptedException {
    if(isLocalFile(fl))
      return fl.getPath().toAbsolutePath();
    try{      
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(fl.getHost().domain() + "/absolute/path"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      String strPath = (String)Hosts.handleResponse(req);
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
        .uri(URI.create(fl.getHost().domain() + "/read/string"))
        .header("Content-Type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(fl.getPath().toString()))
        .build();
        
      content = (String)Hosts.handleResponse(req);
      
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
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(fl.getHost().domain() + "/exists"))
        .header("Content-type","text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(pathName))
        .build();
     
      return (Boolean)Hosts.handleResponse(req);
      
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return false;
  }
  
  public static boolean isLocalFile(HostFile fl){
    Host host = fl.getHost();
    String domain = host.domain().toLowerCase();
    
    return 
      domain.isEmpty() ||
      domain.contains("http://localhost") ||
      domain.contains("http://127.0.0.1");
  }
  
  public static void rename(HostFile fl, String name) throws IOException, InterruptedException {
    Path oldPath = fl.getPath();
    Path path = oldPath.resolveSibling(name);
    if(isLocalFile(fl)){
      Files.move(oldPath, path);
      fl.setPath(path);
      return;
    }
    move(fl, new HostFile(fl.getHost(), path), (c)->{});
  }
}
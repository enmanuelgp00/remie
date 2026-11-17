package egp.remie.host;

import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.file.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.*;
import java.util.stream.*;

public class Server {
  public Server(int port){
    System.out.println("#OS: " + System.getProperty("os.name"));
    try{
      HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
      server.createContext("/exists", pathExistsHandler);
      server.createContext("/absolute/path", absolutePathHandler);
      server.createContext("/read", readFileHandler);
      server.createContext("/write", writeFileHandler);
      server.createContext("/is/directory", isDirectoryHandler);
      server.createContext("/list", listFilesHandler);
      server.createContext("/delete", deleteFileHandler);
      server.createContext("/create/file", createFileHandler);
      server.createContext("/create/directory", createDirectoryHandler);
      server.createContext("/move", moveFileHandler);
      server.setExecutor(Executors.newFixedThreadPool(5));
      server.start();
      System.out.println("#Listening at http:/localhost:" + port);
    }catch(IOException ex){
      ex.printStackTrace();
    }
  }
 private HttpHandler deleteFileHandler = (exchange) -> {
    byte[] response = new byte[0];
    try{
      String[] file;  
      ObjectInputStream ois = new ObjectInputStream(exchange.getRequestBody());
      file = (String[])ois.readObject();
      ois.close();
      String pathName = handleOSPathSlash(file[0]);
      Path path = Paths.get(pathName);
      System.out.println("Deleting: " + path);
      Files.delete(path);
    }catch(Exception ex){
      ex.printStackTrace();
      response = serialize(ex);
    };
    
    exchange.sendResponseHeaders(200, response.length);
    OutputStream os = exchange.getResponseBody();
      os.write(response);
      os.flush();
    exchange.close();
  }; 
  private HttpHandler createDirectoryHandler = (exchange) -> {
    byte[] response = new byte[0];
    try{
      String[] file;  
      ObjectInputStream ois = new ObjectInputStream(exchange.getRequestBody());
      file = (String[])ois.readObject();
      ois.close();
      String strPath = handleOSPathSlash(file[0]);
      Path path = Paths.get(strPath);
      System.out.println("Creating: " + strPath);
      Files.createDirectory(path);
    }catch(Exception ex){
      ex.printStackTrace();
      response = serialize(ex);
    };
    
    exchange.sendResponseHeaders(200, response.length);
    OutputStream os = exchange.getResponseBody();
      os.write(response);
      os.flush();
    exchange.close();
  }; 
  private HttpHandler createFileHandler = (exchange) -> {
    byte[] response = new byte[0];
    try{
      String[] file;  
      ObjectInputStream ois = new ObjectInputStream(exchange.getRequestBody());
      file = (String[])ois.readObject();
      ois.close();
      String strPath = handleOSPathSlash(file[0]);
      Path path = Paths.get(strPath);
      System.out.println("Creating: " + strPath);
      Files.write(path, new byte[0], StandardOpenOption.CREATE_NEW);
    }catch(Exception ex){
      ex.printStackTrace();
      response = serialize(ex);
    };
    
    exchange.sendResponseHeaders(200, response.length);
    OutputStream os = exchange.getResponseBody();
      os.write(response);
      os.flush();
    exchange.close();
  }; 
  private HttpHandler moveFileHandler = (exchange) -> {
    byte[] response = new byte[0];
    try{
      String[] pathNames;  
      ObjectInputStream ois = new ObjectInputStream(exchange.getRequestBody());
      pathNames = (String[])ois.readObject();
      ois.close();
      String origin = handleOSPathSlash(pathNames[0]);
      String target = handleOSPathSlash(pathNames[1]);
      System.out.println("Moving:\t" + origin + "\n\t" + target);
      Files.move(Paths.get(origin), Paths.get(target));
    }catch(Exception ex){
      ex.printStackTrace();
      response = serialize(ex);
    };
    
    exchange.sendResponseHeaders(200, response.length);
    OutputStream os = exchange.getResponseBody();
      os.write(response);
      os.flush();
    exchange.close();
  }; 
  private HttpHandler writeFileHandler = (exchange) -> {
    byte[] response = new byte[0];
    try{
      String[] file;  
      ObjectInputStream ois = new ObjectInputStream(exchange.getRequestBody());
      file = (String[])ois.readObject();
      ois.close();
      String strPath = handleOSPathSlash(file[0]);
      String content = file[1];
      Path path = Paths.get(strPath);
      System.out.println("Writing: " + strPath);
      Files.write(path, content.getBytes());
    }catch(Exception ex){
      ex.printStackTrace();
      response = serialize(ex);
    };
    
    exchange.sendResponseHeaders(200, response.length);
    OutputStream os = exchange.getResponseBody();
      os.write(response);
      os.flush();
    exchange.close();
  }; 
  private HttpHandler absolutePathHandler = (exchange) -> {
    Path path = readFilePathFrom(exchange).toAbsolutePath();
    System.out.println("Getting AbsolutePath: " + path);
    byte[] response = serialize(path.toString());
    exchange.sendResponseHeaders(200, response.length);
    OutputStream os = exchange.getResponseBody();
      os.write(response);
      os.flush();
    exchange.close();
  };
  private HttpHandler readFileHandler = (exchange) -> {
    Path path = readFilePathFrom(exchange);
    System.out.println("Reading: " + path);
    String fileContent = "";
    byte[] response;
    try(FileInputStream fis = new FileInputStream(path.toFile())){
      fileContent = new String(fis.readAllBytes());
      response = serialize(fileContent);
    }catch(IOException ex){
      ex.printStackTrace();
      response = serialize(ex);
    }
    exchange.sendResponseHeaders(200, response.length);
    OutputStream os = exchange.getResponseBody();
      os.write(response);
      os.flush();
    exchange.close();
  };
  private HttpHandler listFilesHandler = (exchange) -> {
    byte[] response;
    Path path = readFilePathFrom(exchange);
    try(Stream<Path> stream = Files.list(path)){
      System.out.println("Listing: " + path);
      String[] fileNames = stream 
        .map(Path::toAbsolutePath)
        .map(Path::toString)
        .collect(Collectors.toList()).toArray(new String[0]);
      
      response = serialize(fileNames);
      exchange.sendResponseHeaders(200, response.length);
      
    }catch(Exception ex){
      ex.printStackTrace();
      response = serialize(ex);
    }
    
    OutputStream os = exchange.getResponseBody();
      os.write(response);
      os.flush();
    exchange.close();
  };
  private HttpHandler isDirectoryHandler = (exchange) -> {
    Path path = readFilePathFrom(exchange);
    
    Boolean isDirectory = Files.isDirectory(path);
    byte[] response = serialize(isDirectory);
    exchange.sendResponseHeaders(200, response.length);
    exchange.getResponseBody()
      .write(response);
    exchange.close();
  };
  private HttpHandler pathExistsHandler = (exchange) -> {
    Path path = readFilePathFrom(exchange);
    String pathExists = String.valueOf(Files.exists(path));
    exchange.sendResponseHeaders(200, pathExists.length());
    exchange.getResponseBody()
      .write(pathExists.getBytes());
    exchange.close();
  };
  private Path readFilePathFrom(HttpExchange exchange) throws IOException {
    InputStream is = exchange.getRequestBody();
    String strPath = new String(is.readAllBytes());
    strPath = handleOSPathSlash(strPath);
    return Paths.get(strPath);
  }
  
  private String handleOSPathSlash(String strPath){
    String os = System.getProperty("os.name");
    if(os.toLowerCase().contains("windows"))
      return strPath.replace("/", "\\");
    else 
      return strPath.replace("\\", "/");
  }  
  public static byte[] serialize(Object obj) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(obj);
    baos.close();
    return baos.toByteArray();
  }
}

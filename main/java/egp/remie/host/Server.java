package egp.remie.host;

import egp.remie.host.controller.*;
import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class Server {
  
  public Server(int port){
    int userPool = 0; // auto
    System.out.println("#OS: " + System.getProperty("os.name"));
    try{
      HttpServer server = HttpServer.create(new InetSocketAddress(port), userPool);
      server.createContext("/size", FileController.size);
      server.createContext("/delete", FileController.delete);
      server.createContext("/read", FileController.read);
      server.createContext("/read/string", FileController.readString);
      server.createContext("/absolute/path", FileController.absolutePath);
      server.createContext("/exists", FileController.exists);
      server.createContext("/write", FileController.write);
      server.createContext("/is/directory", FileController.isDirectory);
      server.createContext("/list", FileController.list);
      server.createContext("/create/file", FileController.createFile);
      server.createContext("/create/directory", FileController.createDirectory);
      server.createContext("/move", FileController.move);
      server.createContext("/shell/create", ShellController.create);
      server.createContext("/shell/is", ShellController.is);
      server.createContext("/shell/os", ShellController.os);
      server.setExecutor(Executors.newFixedThreadPool(5));
      server.start();
      System.out.println("#Listening at http:/localhost:" + port);
      
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  public static Process createShellProcess(Path path) throws IOException {
    String os = System.getProperty("os.name").toLowerCase();
    String command;
    if(os.contains("windows")){
      command = "cmd";      
    } else if(os.contains("linux")){
      command = "sh";
    } else {
      throw new RuntimeException("Can not create Shell. Unknown Operation System: " + os);
    }
    
    ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectErrorStream(true);
    pb.directory(path.toFile());
    Process proc = pb.start();
    return proc;
  }
  
  public static void sendStringResponse(int code, HttpExchange exchange, String res){    
    exchange.getResponseHeaders().set("Content-Type", "text/plain");
    sendByteArrayResponse(200, exchange, res.getBytes());
  }
  
  public static void sendByteArrayResponse(int code, HttpExchange exchange, byte[] res){    
    try(OutputStream os = exchange.getResponseBody()){
      exchange.sendResponseHeaders(code, res.length);
      os.write(res);
      os.flush();
      
    } catch(IOException ex){
      ex.printStackTrace();
    }
  }
  
  public static boolean closedByUnsupportedMediaType(HttpExchange exchange, String... mediaTypes){     
    String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
    if(contentType == null) contentType = "";
    
    Set<String> mediaTypeSet = Set.of(mediaTypes);
    if(!mediaTypeSet.contains(contentType)){
      sendUnsupportedMediaType(exchange);
      exchange.close();
      return true;
    }
    return false; 
  }
    
  public static void sendURLParamsExceptionResponse(HttpExchange exchange, String message){
    sendObjectResponse(400, exchange, new URLParamsException(message));
  }
  public static void sendBadRequestResponse(HttpExchange exchange, String message){
    sendObjectResponse(400, exchange, new BadRequestException());
  }
  
  public static void sendUnsupportedMediaType(HttpExchange exchange){
    String message = exchange.getRequestHeaders().getFirst("Content-Type");
    sendObjectResponse(415, exchange, new UnsupportedMediaTypeException(message));
  }
  
  public static <T extends Object> void sendObjectResponse(int statusCode, HttpExchange exchange, T obj){
    try{
      byte[] res = serialize(obj);
      Headers headers = exchange.getResponseHeaders();
      headers.set("Content-Type","application/x-java-serialized-object");
      headers.add("Class-Name", obj.getClass().getName()); 
      sendByteArrayResponse(statusCode, exchange, res);
      
    }catch(IOException ex){
      ex.printStackTrace();
    };
  }
  
  public static Map<String, String> parseURLParams(HttpExchange exchange){
    Map<String, String> params = new HashMap<>();
    String query = exchange.getRequestURI().getQuery();
    if(query == null)
      return params;
    String[] pairs = query.split("&");
    String field;
    String value;
    for(String pair : pairs){
      int eqIndex = pair.lastIndexOf("=");
      if(eqIndex == -1 || pair.length() == eqIndex + 1){
        params.put(decodeURLParam(pair), null);
        continue;
      }
      field = pair.substring(0, eqIndex);
      value = pair.substring(eqIndex + 1);
      params.put(decodeURLParam(field), decodeURLParam(value));
    }
    return params;
  }
  
  public static boolean isValidBooleanName(String name){
    String namelc = name.toLowerCase();
    return namelc.equals("false") || namelc.equals("true");
  }
  
  public static String decodeURLParam(String urlParam){
    return URLDecoder.decode(urlParam, StandardCharsets.UTF_8);
  }
  
  public static Path handleOSPath(String strPath){
    String os = System.getProperty("os.name");
    String osStrPath = "";
    if(os.toLowerCase().contains("windows"))
      osStrPath = strPath.replace("/", "\\");
    else 
      osStrPath = strPath.replace("\\", "/");
    return Paths.get(osStrPath);
  }  
  
  public static byte[] serialize(Object obj) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(obj);
    baos.close();
    return baos.toByteArray();
  }
}

package egp.remie.host.controller;

import static egp.remie.host.Server.*;
import egp.remie.host.MethodHandler;
import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class FileController {
  
  public static HttpHandler delete = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;      
      
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());        
        Path path = handleOSPath(rawPathStr);
        System.out.println("Deleting: " + path);
          
        if(Files.isDirectory(path)){
          Files.delete(path);
        } else {         
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
        }
        exchange.sendResponseHeaders(200, -1);
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
  
  public static HttpHandler createDirectory = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;            
      
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());        
        Path path = handleOSPath(rawPathStr);
        System.out.println("Create dir: " + path);
        Files.createDirectory(path);
        exchange.sendResponseHeaders(200, -1);
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
    
  public static HttpHandler createFile = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;      
      
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());        
        Path path = handleOSPath(rawPathStr);
        System.out.println("Create file: " + path);
        Files.write(path, new byte[0], StandardOpenOption.CREATE_NEW);
        exchange.sendResponseHeaders(200, -1);
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
    
  public static HttpHandler move = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;      
      
      String contentDisposition = exchange.getRequestHeaders().getFirst("Content-Disposition");      
      if(contentDisposition == null || contentDisposition.isEmpty()){
        sendBadRequestResponse(exchange, "Header not found: Content-Disposition");
        return;
      }
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());  
        Path originPath = handleOSPath(rawPathStr);
        Path targetPath = handleOSPath(contentDisposition);
        System.out.println("Move path:\n\t" + originPath + "\n\t" + targetPath);
        Files.move(originPath, targetPath);
        exchange.sendResponseHeaders(200, -1);
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
    
  public static HttpHandler write = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain", "application/octet-stream", "application/x-java-serialized-object"))
        return;      
      
      String targetStrPath = exchange.getRequestHeaders().getFirst("Content-Disposition");
      if(targetStrPath == null){
        sendBadRequestResponse(exchange, "Header not found: Content-Disposition");
        return;
      }
      Path targetPath = handleOSPath(targetStrPath);
      
      boolean overwrite = false;
      boolean append = false;
      
      try{
        Map<String, String> urlParams = parseURLParams(exchange);
        String strOverride = urlParams.get("overwrite"); 
        String strAppend = urlParams.get("append");
        
        if(strOverride == null || strAppend == null){
          sendURLParamsExceptionResponse(exchange, "URL params not found: (boolean) overwrite, (boolean) append");
          return;
        }
        
        if(!isValidBooleanName(strOverride) || !isValidBooleanName(strAppend)){
          sendURLParamsExceptionResponse(exchange, "URL params most be boolean names");
          return;          
        }
        
        overwrite = Boolean.parseBoolean(strOverride);
        append = Boolean.parseBoolean(strAppend);
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendObjectResponse(500, exchange, ex);
        return;
      }
      
      try(InputStream is = exchange.getRequestBody()){        
        if(!overwrite){
          if(Files.exists(targetPath))
            throw new FileAlreadyExistsException(targetPath.toString());
        }
        
        byte[] buffer = new byte[8192];    
        int read = 0;
        
        System.out.println("Write: " + targetPath);
        try(FileOutputStream fos = new FileOutputStream(targetPath.toFile(), true)){ // appends to the end of the file
          if(!append){
            fos.write((byte)0);
          }
          while((read = is.read(buffer)) > -1){
            fos.write(buffer, 0, read);
            fos.flush();
          }
        }
        exchange.sendResponseHeaders(200, -1);
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
  public static HttpHandler absolutePath = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;      
      
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());        
        Path path = handleOSPath(rawPathStr);
        System.out.println("Absolute path: " + path);
        
        String res = path.toAbsolutePath().toString();
        sendStringResponse(200, exchange, res);
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
    
  public static HttpHandler readString = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;      
      
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());        
        Path path = handleOSPath(rawPathStr);
        System.out.println("Reading string: " + path);
        
        String str = Files.readString(path);
        sendStringResponse(200, exchange, str);
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendObjectResponse(500, exchange, ex);        
      };
      
      exchange.close();  
    })
    .build();
    
  public static HttpHandler read = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;      
      
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());        
        Path path = handleOSPath(rawPathStr);
        System.out.println("Reading: " + path);
        
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/octet-stream");
        
        byte[] buffer = new byte[8192];
        int read = 0;
        
        try(FileInputStream fis = new FileInputStream(path.toFile())){          
          try(OutputStream os = exchange.getResponseBody()){
            long size = fis.getChannel().size();
            exchange.sendResponseHeaders(200, size);
            while((read = fis.read(buffer)) > -1){
              os.write(buffer, 0, read);
              os.flush();
            };  
          }
        }        
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendObjectResponse(500, exchange, ex);        
      };
      
      exchange.close();  
    })
    .build();
    
  public static HttpHandler list = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;      
      
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());        
        Path path = handleOSPath(rawPathStr);
        
        //System.out.println("List: " + path);        
        try(Stream<Path> stream = Files.list(path)){
          String[] fileNames = stream.map(Path::toString)
            .toArray(String[]::new);
          sendObjectResponse(200, exchange, fileNames);         
        }
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
    
  public static HttpHandler isDirectory = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;      
      
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());        
        Path path = handleOSPath(rawPathStr);
        //System.out.println("Is Directory: " + path);
        
        boolean isDir = Files.isDirectory(path);
        sendObjectResponse(200, exchange, isDir);   
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
    
  public static HttpHandler exists = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;      
      
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());        
        Path path = handleOSPath(rawPathStr);
        System.out.println("Exits path: " + path);
        
        boolean exists = Files.exists(path);
        sendObjectResponse(200, exchange, exists);  
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
   
}
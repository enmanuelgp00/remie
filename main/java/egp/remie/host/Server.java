package egp.remie.host;

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
      server.createContext("/delete", deleteFileHandler);
      server.createContext("/read", readFileHandler);
      server.createContext("/absolute/path", absolutePathHandler);
      server.createContext("/exists", pathExistsHandler);
      server.createContext("/write", writeFileHandler);
      server.createContext("/is/directory", isDirectoryHandler);
      server.createContext("/list", listFilesHandler);
      server.createContext("/create/file", createFileHandler);
      server.createContext("/create/directory", createDirectoryHandler);
      server.createContext("/move", moveFileHandler);
      server.setExecutor(Executors.newFixedThreadPool(5));
      server.start();
      System.out.println("#Listening at http:/localhost:" + port);
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  private void sendByteArrayResponse(int code, HttpExchange exchange, byte[] res){    
    try(OutputStream os = exchange.getResponseBody()){
      exchange.sendResponseHeaders(code, res.length);
      os.write(res);
      os.flush();
      
    } catch(IOException ex){
      ex.printStackTrace();
    }
  }
  
  private boolean closedByUnsupportedMediaType(HttpExchange exchange, String... mediaTypes){     
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
  
  private void sendBadRequestResponse(HttpExchange exchange, String message){
    sendExceptionObjectResponse(400, exchange, new BadRequestException());
  }
  
  private void sendUnsupportedMediaType(HttpExchange exchange){
    String message = exchange.getRequestHeaders().getFirst("Content-Type");
    sendExceptionObjectResponse(415, exchange, new UnsupportedMediaTypeException(message));
  }
  
  private void sendExceptionObjectResponse(int statusCode, HttpExchange exchange, Exception exRes){
    try{
      byte[] res = serialize(exRes);
      Headers headers = exchange.getResponseHeaders();
      headers.set("Content-Type","application/x-java-serialized-object");
      headers.add("Class-Name", exRes.getClass().getName()); 
      sendByteArrayResponse(statusCode, exchange, res);
      
    }catch(IOException ex){
      ex.printStackTrace();
    };
  }
  
  private HttpHandler deleteFileHandler = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;      
      
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());        
        Path path = handleOSPath(rawPathStr);
        System.out.println("Deleting: " + path);
        //Files.delete(path);
        exchange.sendResponseHeaders(200, -1);
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendExceptionObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
    
  
  private HttpHandler createDirectoryHandler = MethodHandler.builder()
    .post((exchange)->{
      System.out.println("entring post body");
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
        sendExceptionObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
    
  private HttpHandler createFileHandler = MethodHandler.builder()
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
        sendExceptionObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
    
  private HttpHandler moveFileHandler = MethodHandler.builder()
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
        Path path = handleOSPath(rawPathStr);
        System.out.println("Absolute path: " + path);
        
        String res = path.toAbsolutePath().toString();
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        sendByteArrayResponse(200, exchange, res.getBytes());
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendExceptionObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
    
  private Map<String, String> parseURLParams(HttpExchange exchange){
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
  private boolean isValidBooleanName(String name){
    String namelc = name.toLowerCase();
    return namelc.equals("false") || namelc.equals("true");
  }
  private HttpHandler writeFileHandler = MethodHandler.builder()
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
          sendBadRequestResponse(exchange, "URL params not found: (boolean) overwrite, (boolean) append");
          return;
        }
        
        if(!isValidBooleanName(strOverride) || !isValidBooleanName(strAppend)){
          sendBadRequestResponse(exchange, "URL params most be boolean names");
          return;          
        }
        
        overwrite = Boolean.parseBoolean(strOverride);
        append = Boolean.parseBoolean(strAppend);
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendExceptionObjectResponse(500, exchange, ex);
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
        sendExceptionObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
  
  private String decodeURLParam(String urlParam){
    return URLDecoder.decode(urlParam, StandardCharsets.UTF_8);
  }
    
  private HttpHandler absolutePathHandler = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;      
      
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());        
        Path path = handleOSPath(rawPathStr);
        System.out.println("Absolute path: " + path);
        
        String res = path.toAbsolutePath().toString();
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        sendByteArrayResponse(200, exchange, res.getBytes());
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendExceptionObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
    
  private HttpHandler readFileHandler = MethodHandler.builder()
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
        sendExceptionObjectResponse(500, exchange, ex);        
      };
      
      exchange.close();  
    })
    .build();
    
  private HttpHandler listFilesHandler = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;      
      
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());        
        Path path = handleOSPath(rawPathStr);
        
        System.out.println("List: " + path);        
        try(Stream<Path> stream = Files.list(path)){
          String[] fileNames = stream.map(Path::toString)
            .toArray(String[]::new);
          Headers headers = exchange.getResponseHeaders();
          headers.set("Content-Type", "application/x-java-serialized-object");
          headers.add("Class-Name", fileNames.getClass().getName());
          sendByteArrayResponse(200, exchange, serialize(fileNames));          
        }
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendExceptionObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
    
  private HttpHandler isDirectoryHandler = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;      
      
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());        
        Path path = handleOSPath(rawPathStr);
        System.out.println("Is Directory: " + path);
        
        boolean exists = Files.isDirectory(path);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/x-java-serialized-object");
        headers.add("Class-Name", Boolean.class.getName());
        sendByteArrayResponse(200, exchange, serialize(exists));
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendExceptionObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
    
  private HttpHandler pathExistsHandler = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;      
      
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());        
        Path path = handleOSPath(rawPathStr);
        System.out.println("Exits path: " + path);
        
        boolean exists = Files.exists(path);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/x-java-serialized-object");
        headers.add("Class-Name", Boolean.class.getName());
        sendByteArrayResponse(200, exchange, serialize(exists));
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendExceptionObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
    
  private Path handleOSPath(String strPath){
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

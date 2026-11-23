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
import java.util.concurrent.*;

public class ShellController {
  private static volatile Map<Long, Process> processMap = new HashMap<>();
  
  public static final HttpHandler destroy = MethodHandler.builder()
    .get((exchange)->{
      Map<String, String> URLparams = parseURLParams(exchange);
      long id = 0;
      try{
        id = Long.parseLong(URLparams.get("id"));
      }catch(Exception ex){
        ex.printStackTrace();
        sendURLParamsExceptionResponse(exchange, "Param not found or not a number: id");
      }
      try{        
        Process proc = processMap.get(id);
        boolean finished = proc.waitFor(5, TimeUnit.SECONDS);
        int exitCode = proc.exitValue();
        sendObjectResponse(200, exchange, exitCode);
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
  
  public static final HttpHandler os = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain")){
        return;
      }
      Map<String, String> URLparams = parseURLParams(exchange);
      long id = 0;
      try{
        id = Long.parseLong(URLparams.get("id"));
      }catch(Exception ex){
        ex.printStackTrace();
        sendURLParamsExceptionResponse(exchange, "Param not found or not a number: id");
      }
      try{        
        Process proc = processMap.get(id);
        OutputStream os = proc.getOutputStream();
        byte[] command = exchange.getRequestBody().readAllBytes();
        os.write(command);
        os.flush();
        exchange.sendResponseHeaders(200, -1);
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
    
  public static final HttpHandler is = MethodHandler.builder()
    .get((exchange)->{
      //new Thread(()->{
        Map<String, String> URLparams = parseURLParams(exchange);
        long id = 0;
        try{
          id = Long.parseLong(URLparams.get("id"));
        }catch(Exception ex){
          ex.printStackTrace();
          sendURLParamsExceptionResponse(exchange, "Param not found or not a number: id");
        }
        try{        
          Process proc = processMap.get(id);
          exchange.getResponseHeaders().set("Content-Type","application/octet-stream");
          exchange.sendResponseHeaders(200, 0);
          try(OutputStream os = exchange.getResponseBody()){
            try(InputStream is = proc.getInputStream()){
              int read;
              byte[] buffer = new byte[1024];
              os.write(new byte[0]); // had to send some data to allow the httpresponse to continue other wise it keeps hanging
              os.flush();
              while((read = is.read(buffer)) > -1){
                os.write(buffer, 0, read);
                os.flush();
              }
            }
          } catch(IOException ex){
            ex.printStackTrace();
          }
            
          System.out.println(proc);
          
        }catch(Exception ex){
          ex.printStackTrace();
          sendObjectResponse(500, exchange, ex);
        };
        
        exchange.close();        
      //}).start();
    })
    .build();
    
  public static final HttpHandler create = MethodHandler.builder()
    .post((exchange)->{
      if(closedByUnsupportedMediaType(exchange, "text/plain"))
        return;            
      
      try(InputStream is = exchange.getRequestBody()){        
        String rawPathStr = new String(is.readAllBytes());        
        Path path = handleOSPath(rawPathStr);
        
        Process proc = createShellProcess(path);
        Long pid = proc.pid();
        System.out.println("Create shell: " + pid);
        processMap.put(pid, proc);
        sendObjectResponse(200, exchange, pid);
        
      }catch(Exception ex){
        ex.printStackTrace();
        sendObjectResponse(500, exchange, ex);
      };
      
      exchange.close();
    })
    .build();
}

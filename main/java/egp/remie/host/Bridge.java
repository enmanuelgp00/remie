package egp.remie.host;

import com.sun.net.httpserver.*;
import egp.remie.host.file.*;
import java.net.*;
import java.net.http.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class Bridge {
  private final HttpClient client = HttpClient.newHttpClient();
  private Host host = null;
  
  public Bridge(int port, Host host){
    this.host = host;
    int userPool = 0; // auto
    
    try{
      HttpServer server = HttpServer.create(new InetSocketAddress(port), userPool);
      server.createContext("/", home);
      server.setExecutor(Executors.newFixedThreadPool(5));
      server.start();
      System.out.println("#Bridge at http:/localhost:" + port);
      
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  private Consumer<HttpExchange> handleRequestMethod(BiFunction<HttpRequest.Builder, HttpExchange, HttpRequest> handleMethod){
    return (exchange)->{      
      try{  
        Headers cliReqHeaders = exchange.getRequestHeaders();
        List<String> reqHeaders = new ArrayList<>();
        Set<String> reservedKeys = Set.of(new String[]{"upgrade", "accept", "host", "content-length", "expect", "connection"});
        for(String key : cliReqHeaders.keySet()){
          if(reservedKeys.contains(key.toLowerCase()))
            continue;
          cliReqHeaders.get(key).forEach(value -> {
            reqHeaders.add(key);
            reqHeaders.add(value);            
          });
        }
        HttpRequest serverReq = handleMethod.apply(HttpRequest.newBuilder()
            .uri(URI.create(host.domain() + exchange.getRequestURI()))
            .headers(reqHeaders.toArray(new String[0])
          ), exchange);
        
        HttpResponse<InputStream> serverRes = client.send(serverReq, HttpResponse.BodyHandlers.ofInputStream());
        HttpHeaders headers = serverRes.headers();
        Map<String, List<String>> headersMap = headers.map();
        exchange.getResponseHeaders().clear();
        for(String key : headersMap.keySet()){
          headersMap.get(key).forEach(value -> {
            exchange.getResponseHeaders().add(key, value);
          });
        }
        //System.out.println(exchange.getResponseHeaders());
        int contentLength = Integer.parseInt(headers.firstValue("Content-Length").orElse("0"));
        exchange.sendResponseHeaders(serverRes.statusCode(), contentLength);
        byte[] buffer = new byte[8192];
        int read;
        try(OutputStream os = exchange.getResponseBody()){
          try(InputStream is = serverRes.body()){
            while((read = is.read(buffer)) > -1){
              os.write(buffer, 0, read);
              os.flush();
            }
          }
        }
      }catch(Exception ex){
        ex.printStackTrace();
      }
      exchange.close();
    };
  }
  
  HttpHandler home = MethodHandler.builder()
    .post(handleRequestMethod((builder, exchange)->{
      return builder
        .POST(HttpRequest.BodyPublishers.ofInputStream(()-> exchange.getRequestBody() ))
        .build();
    }))
    .get(handleRequestMethod((builder, exchange)->{
      return builder
        .GET()
        .build();
    }))
    .put(handleRequestMethod((builder, exchange)->{
      return builder
        .PUT(HttpRequest.BodyPublishers.ofInputStream(()-> exchange.getRequestBody() ))
        .build();
    }))
    .build();
}
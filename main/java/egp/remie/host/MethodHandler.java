package egp.remie.host;

import com.sun.net.httpserver.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.function.*;

public class MethodHandler implements HttpHandler {
  private final Consumer<HttpExchange> getConsumer;
  private final Consumer<HttpExchange> postConsumer;
  private final Consumer<HttpExchange> updateConsumer;
  private final Consumer<HttpExchange> on405Consumer;
  
  private MethodHandler(Builder builder){
    getConsumer = builder.getConsumer;
    postConsumer = builder.postConsumer;
    updateConsumer = builder.updateConsumer;
    on405Consumer = builder.on405Consumer;
  }
  
  @Override
  public void handle(HttpExchange exch){
    String method = exch.getRequestMethod();
    switch(method){
      case "GET": getConsumer.accept(exch);
        break;
      case "POST": postConsumer.accept(exch);
        break;
      case "UPDATE": updateConsumer.accept(exch);
        break;
      default:
        on405Consumer.accept(exch);
    }
  }
  
  public static Builder builder(){
    return new Builder();
  }
  
  public static class Builder{        
    private Consumer<HttpExchange> getConsumer;
    private Consumer<HttpExchange> postConsumer;
    private Consumer<HttpExchange> updateConsumer;
    private Consumer<HttpExchange> on405Consumer;
    
    public Builder(){
      getConsumer = createMethodNotAllowedConsumer();
      postConsumer = createMethodNotAllowedConsumer();
      updateConsumer = createMethodNotAllowedConsumer();
      on405Consumer = createMethodNotAllowedConsumer();
    }
    
    private Consumer<HttpExchange> createMethodNotAllowedConsumer(){
      return (exchange)->{
        try{
          String message = String.format("Can not %s: %s", exchange.getRequestMethod(), exchange.getRequestURI());
          byte[] res = Server.serialize(new MethodNotAllowedException(message));
          exchange.getResponseHeaders().set("Content-Type", "application/x-java-serialized-object");
          exchange.sendResponseHeaders(405, res.length);
          exchange.getResponseBody().write(res);
        }catch(IOException ex){
          ex.printStackTrace();
        };
        exchange.close();
      };
    }
    
    public Builder get(Consumer<HttpExchange> getConsumer){
      this.getConsumer = getConsumer;
      return this;
    }
    public Builder update(Consumer<HttpExchange> updateConsumer){
      this.updateConsumer = updateConsumer;
      return this;
    }
    public Builder post(Consumer<HttpExchange> postConsumer){
      this.postConsumer = postConsumer;
      return this;
    }
    public MethodHandler build(){
      return new MethodHandler(this); 
    }
  }
}
  
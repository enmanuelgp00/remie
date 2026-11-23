package egp.remie.host;

import com.sun.net.httpserver.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import egp.remie.host.Server;

public class MethodHandler implements HttpHandler {
  private final Consumer<HttpExchange> getConsumer;
  private final Consumer<HttpExchange> postConsumer;
  private final Consumer<HttpExchange> putConsumer;
  private final Consumer<HttpExchange> on405Consumer;
  
  private MethodHandler(Builder builder){
    getConsumer = builder.getConsumer;
    postConsumer = builder.postConsumer;
    putConsumer = builder.putConsumer;
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
      case "PUT": putConsumer.accept(exch);
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
    private Consumer<HttpExchange> putConsumer;
    private Consumer<HttpExchange> on405Consumer;
    
    public Builder(){
      getConsumer = createMethodNotAllowedConsumer();
      postConsumer = createMethodNotAllowedConsumer();
      putConsumer = createMethodNotAllowedConsumer();
      on405Consumer = createMethodNotAllowedConsumer();
    }
    
    private Consumer<HttpExchange> createMethodNotAllowedConsumer(){
      return (exchange)->{
        String message = String.format("Can not %s: %s", exchange.getRequestMethod(), exchange.getRequestURI());
        Server.sendObjectResponse(405, exchange, new MethodNotAllowedException(message));
        exchange.close();
      };
    }
    
    public Builder get(Consumer<HttpExchange> getConsumer){
      this.getConsumer = getConsumer;
      return this;
    }
    public Builder put(Consumer<HttpExchange> putConsumer){
      this.putConsumer = putConsumer;
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
  
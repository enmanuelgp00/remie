package egp.remie.host.file;

import java.io.*;
import java.net.*;
import java.net.http.*;

public class Hosts {
  public static byte[] serialize(Object obj) throws IOException {    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(obj);
    baos.close();
    return baos.toByteArray();
  }
  
  public static Host get(String uriName) throws IOException, InterruptedException {
    URI hostUri = URI.create(uriName);
    HttpClient cli = HttpClient.newHttpClient();
    HttpRequest req = HttpRequest.newBuilder()
      .uri(hostUri)
      .GET()
      .build();    
      cli.send(req, HttpResponse.BodyHandlers.ofByteArray());
    
    return new Host(){
      @Override
      public String domain(){
        return uriName;
      }
    };
  }
}
package egp.remie.host;

import java.io.*;
import java.net.*;
import java.net.http.*;

public class Client {
  public Client(){
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest req = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:8080"))
      .GET()
      .build();
    
    try{
      HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
      System.out.println(res.body());
    }catch(IOException | InterruptedException ex){
      ex.printStackTrace();
    }
  }
  public static void main(String[] args){
    new Client();
  }
}
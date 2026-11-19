package egp.remie.host;

import java.io.*;

public class BadRequestException extends IOException {
  public BadRequestException(){
    super();
  }
  public BadRequestException(String message){
    super(message);
  }
}
package egp.remie.host;

import java.io.*;

public class URLParamsException extends BadRequestException {
  public URLParamsException(){
    super();
  }
  public URLParamsException(String message){
    super(message);
  }
}
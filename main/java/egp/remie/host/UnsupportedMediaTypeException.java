package egp.remie.host;

import java.io.*;

public class UnsupportedMediaTypeException extends IOException {
  public UnsupportedMediaTypeException(){
    super();
  }
  public UnsupportedMediaTypeException(String message){
    super(message);
  }
}
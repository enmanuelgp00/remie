package egp.remie.host;

import java.io.*;

public class MethodNotAllowedException extends IOException {
  public MethodNotAllowedException(){
    super();
  }
  public MethodNotAllowedException(String message){
    super(message);
  }
}
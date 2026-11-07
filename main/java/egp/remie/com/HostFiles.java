package egp.remie.com;

import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.nio.file.*;

public class HostFiles {
  
  public static Stream<HostFile> list(HostFile fl) throws IOException {
    if(isLocalFile(fl)){
      return Files.list(fl.getPath())
        .map(HostFile::new);
    }
    return null;
  }
  
  public static boolean isDirectory(HostFile fl){
    if(isLocalFile(fl))
      return Files.isDirectory(fl.getPath());
    return false;
  }
  
  public static String readString(HostFile fl) throws IOException {    
    if (isLocalFile(fl)){      
      return Files.readString(fl.getPath());
    }
    return null;
  }
  
  public static boolean exists(HostFile fl){
    if(isLocalFile(fl))
      return Files.exists(fl.getPath());    
    return false;
  }
  
  public static boolean isLocalFile(HostFile fl){
    return fl.getHost().isEmpty();
  }
}
package egp.remie.com;

import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.nio.file.*;

public class HostFiles {
  
  public static void write(HostFile hostFile, String text) throws IOException {
    if(isLocalFile(hostFile)){
      Files.write(hostFile.getPath(), text.getBytes());
    }
    return;
  }
  
  public static void createFile(HostFile hostFile) throws IOException {
    if(isLocalFile(hostFile)){
      Files.write(hostFile.getPath(), "".getBytes(), StandardOpenOption.CREATE_NEW); //learning_note: its possible to use many StandardOptions as StandardCharsets
    }
    return;
  }
  
  public static void createDirectory(HostFile hostFile) throws IOException {
    if(isLocalFile(hostFile)){
      Files.createDirectory(hostFile.getPath()); //learning_note: its possible to use many StandardOptions as StandardCharsets
    }
    return;
  }
  
  public static void delete(HostFile hostFile) throws IOException {
    if(isLocalFile(hostFile)){
      //System.out.println("delete action disabled");
      Files.delete(hostFile.getPath()); //learning_note: its possible to use many StandardOptions as StandardCharsets
    }
    return;
  }
  
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
  
  public static void rename(HostFile file, String name) throws IOException {
    if(isLocalFile(file)){
      Path oldPath = file.getPath();
      Path path = oldPath.resolveSibling(name);
      Files.move(oldPath, path);
      file.setPath(path);
    }
    return;
  }
}
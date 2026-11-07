package egp.remie.com;

import java.nio.file.*;

public class HostFile {
  
  private Path filePath;
  private final String host;
  
  public HostFile(Path path, String host){
    this.filePath = filePath;
    this.host = host;
  }
  
  public HostFile(String strPth, String host){
    this.filePath = Paths.get(strPth);
    this.host = host;
  }
  
   public HostFile(Path filePath){
    this.filePath = filePath;
    this.host = "";
  }
  
  public HostFile(String strPth){
    this.filePath = Paths.get(strPth);
    this.host = "";
  }
  
  
  public Path getPath(){
    return this.filePath;
  }
  
  public String getHost(){
    return host;
  }
  
  @Override
  public String toString(){
    return getPath()
      .toAbsolutePath()
      .normalize()
      .getFileName()
      .toString();
  }
  
  @Override
  public boolean equals(Object obj){
    if(!(obj instanceof HostFile))
      return false;
    HostFile hostFile = (HostFile)obj;
    return (
      this.filePath.equals(hostFile.getPath()) &&
      this.host.equals(hostFile.getHost())
    );
  }
}
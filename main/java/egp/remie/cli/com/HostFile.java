package egp.remie.cli.com;

import java.nio.file.*;

public class HostFile {
  private final Host host;
  private Path path;
  
  public HostFile(Host host, Path path){
    this.host = host;
    this.path = path;
  }
  
  public HostFile(Path path){
    this.host = null;
    this.path = path;
  }
  
  public HostFile(Host host, String strPath){
    this.host = host;
    if(host != null){
      
    }
    this.path = Paths.get(strPath);
  }
  
  public HostFile(String strPath){
    this.host = null;
    this.path = Paths.get(strPath);
  }
  
  public void setPath(Path path){
    this.path = path;    
  }
  
  public Path getPath(){
    return path;
  }
  
  public Host getHost(){
    return host;
  }
  
  @Override
  public String toString(){
    Path fileName = getPath()
      .normalize()
      .getFileName();
    if(fileName == null)
      fileName = Paths.get("/");
    
    String name = fileName.toString();    
    //if(getHost() != null)
      //name = String.format("[%s]%s", getHost().domain(), name);
    return name;
  }
  
  @Override
  public boolean equals(Object obj){
    if(!(obj instanceof HostFile))
      return false;
    HostFile hostFile = (HostFile)obj;    
    Host thisHost = getHost();
    Host otherHost = hostFile.getHost();
    boolean areBothNull = thisHost == null && otherHost == null;
    boolean oneIsNull = thisHost == null || otherHost == null;
    if(areBothNull || oneIsNull)
      return getPath().equals(hostFile.getPath());
    
    return thisHost.domain().equals(otherHost.domain()) && 
      getPath().equals(hostFile.getPath());
  }
}
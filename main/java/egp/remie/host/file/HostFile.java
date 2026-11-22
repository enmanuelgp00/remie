package egp.remie.host.file;

import java.io.*;
import java.nio.file.*;

public class HostFile {
  private final Host host;
  private Path path;
  
  public HostFile(Host host, Path path){
    this.host = (host == null ? Hosts.THIS_COMPUTER : host);
    this.path = path;
    handlePathDotName();
  }
  
  public HostFile(Path path){
    this.host = Hosts.THIS_COMPUTER;
    this.path = path;
    handlePathDotName();
  }
  
  public HostFile(Host host, String strPath){
    this.host = (host == null ? Hosts.THIS_COMPUTER : host);
    this.path = Paths.get(strPath);
    handlePathDotName();
    
  }
  
  public HostFile(String strPath){
    this.host = Hosts.THIS_COMPUTER;
    this.path = Paths.get(strPath);
    handlePathDotName();
  }
  
  public void setPath(Path path){
    this.path = path;    
  }
  
  private void handlePathDotName(){ 
    String name = toString();
    if(name.equals(".") || name.equals("..")){      
      try{
        Path path = HostFiles.getAbsolutePathOf(this).normalize();
        setPath(path);
      }catch(IOException | InterruptedException ex){
        ex.printStackTrace();
      }
    }
  }
  
  public Path getPath(){
    return path;
  }
  
  public Host getHost(){
    return host;
  }
  
  @Override
  public String toString(){
    Path fileName = getPath().getFileName();
    if(fileName == null)
      fileName = Paths.get("/");
    
    String name = fileName.toString();    
    //if(getHost() != null)
      //name = String.format("[%s]%s", getHost().domain(), name);
    return name;
  }
  
  public String getFileName(){
    return path.getFileName().toString();
  }
  
  public HostFile resolve(String name){
    return new HostFile(getHost(), getPath().resolve(name));
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
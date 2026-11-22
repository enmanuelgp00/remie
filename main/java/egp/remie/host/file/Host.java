package egp.remie.host.file;

import java.net.*;

public abstract class Host {
  public abstract String os();
  public abstract String domain();
  @Override
  public boolean equals(Object obj){
    if(!(obj instanceof Host))
      return false;
    Host otherHost = (Host)obj;
    return this.domain().equals(otherHost.domain());
  }
}
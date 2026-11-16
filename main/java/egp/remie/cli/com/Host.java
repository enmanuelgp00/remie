package egp.remie.cli.com;

import java.net.*;

public abstract class Host {
  public abstract String domain();
  @Override
  public boolean equals(Object obj){
    if(!(obj instanceof Host))
      return false;
    Host otherHost = (Host)obj;
    return this.domain().equals(otherHost.domain());
  }
}
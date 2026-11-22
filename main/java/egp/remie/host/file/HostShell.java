package egp.remie.host.file;

import java.io.*;

public interface HostShell {
  public long getId();
  public InputStream getInputStream();
  public OutputStream getOutputStream();
  public HostFile getWorkingDirectory();
}
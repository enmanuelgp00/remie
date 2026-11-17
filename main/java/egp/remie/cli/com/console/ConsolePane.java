package egp.remie.cli.com.console;

import egp.remie.host.file.*;
import egp.remie.cli.custome.*;
import javax.swing.*;
import java.awt.*;

public class ConsolePane extends CustomeTextPane {
  private HostFile context;
  
  public ConsolePane(HostFile dir) {
    setFont(new Font("monospaced", Font.BOLD, 16));
    setForeground(Color.WHITE);
    setCaretColor(Color.WHITE);
    setBackground(Color.BLACK);
    context = dir;
  }
  
  public HostFile getContext(){
    return context;
  }
  
  public void setContext(HostFile dir) {
    this.context = dir;
  }
}
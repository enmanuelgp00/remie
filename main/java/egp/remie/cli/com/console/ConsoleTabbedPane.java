package egp.remie.cli.com.console;

import egp.remie.cli.custome.*;
import egp.remie.host.file.*;
import javax.swing.*;
import java.util.*;

public class ConsoleTabbedPane extends CustomeTabbedPane {
  private final Map<ConsolePane, Integer> consoleMap = new HashMap<>();
  
  public ConsoleTabbedPane() {
    setTabPlacement(JTabbedPane.TOP);
  }
  public void removeConsole(ConsolePane console){
    removeTabAt(consoleMap.get(console));
  }
  public void createConsoleTab(HostFile fl){
    ConsolePane console = new ConsolePane(fl, this);
    consoleMap.put(console, getTabCount());
    String host = ":localhost";
    if(!HostFiles.isLocalFile(fl)){
      host = ":" + fl.getHost().domain();
    }
    add(host, new CustomeScrollPane(console));
  }
}

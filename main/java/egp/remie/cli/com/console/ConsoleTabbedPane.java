package egp.remie.cli.com.console;

import egp.remie.cli.custome.*;
import egp.remie.host.file.*;
import javax.swing.*;

public class ConsoleTabbedPane extends CustomeTabbedPane {
  public ConsoleTabbedPane() {
    setTabPlacement(JTabbedPane.RIGHT);
    createConsoleTab(new HostFile(null, "."));
    createConsoleTab(new HostFile(null, "."));
    createConsoleTab(new HostFile(null, "."));
    createConsoleTab(new HostFile(null, "."));
  }
  public void createConsoleTab(HostFile fl){
    add("Console", new ConsolePane(fl));
  }
}

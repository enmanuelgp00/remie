package egp.remie.cli.com.console;

import egp.remie.cli.custome.*;
import egp.remie.host.file.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

public class ConsoleTabbedPane extends CustomeTabbedPane {
  private final Map<ConsolePane, Integer> consoleMap = new HashMap<>();
  private final ConsoleTabPopupMenu tabPopupMenu;
  
  public ConsoleTabbedPane() {
    setTabPlacement(JTabbedPane.BOTTOM);
    tabPopupMenu = new ConsoleTabPopupMenu(this);
    addMouseListener(handleMouseListener);
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
  
  public ConsolePane getConsoleAt(int i){
    JScrollPane scroll = (JScrollPane)getComponentAt(i);
    return (ConsolePane)scroll.getViewport().getView();
  }
  
  public void closeConsoleAt(int i){
    ConsolePane console = getConsoleAt(i);
    console.exit();
  }
  
  public MouseAdapter handleMouseListener = new MouseAdapter(){
    @Override
    public void mouseClicked(MouseEvent e){
      int tabIndex = indexAtLocation(e.getX(), e.getY());
      if(tabIndex < 0)
        return;      
      if(SwingUtilities.isMiddleMouseButton(e)){
        removeTabAt(tabIndex);
        return;
      }
      if(SwingUtilities.isRightMouseButton(e)){
        tabPopupMenu.setTargetTabIndex(tabIndex);
        tabPopupMenu.show(ConsoleTabbedPane.this, e.getX(), e.getY());
        return;
      }
    }
  };
}

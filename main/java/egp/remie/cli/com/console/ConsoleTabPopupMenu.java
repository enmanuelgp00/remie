package egp.remie.cli.com.console;

import java.io.*;
import javax.swing.tree.*;
import egp.remie.cli.custome.*;
import egp.remie.host.file.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*; 

class ConsoleTabPopupMenu extends  JPopupMenu{
  private final ConsoleTabbedPane tabbedPane;
  private int targetTabIndex = -1;
  
  private JMenuItem newConsoleItem = new JMenuItem("New Console");
  private JMenuItem closeItem = new JMenuItem("Close");
  
  ConsoleTabPopupMenu(ConsoleTabbedPane tabbedPane){
    this.tabbedPane = tabbedPane;
    configItems();
    add(newConsoleItem);
    add(closeItem);
  }
  
  void configItems(){
    closeItem.addActionListener((e)-> tabbedPane.closeConsoleAt(getTargetTabIndex()));
    newConsoleItem.addActionListener(e -> {
      ConsolePane console = (ConsolePane)tabbedPane.getConsoleAt(targetTabIndex);
      tabbedPane.createConsoleTab(console.getWorkingDirectory());
    });
  }
  
  void setTargetTabIndex(int targetTabIndex){
    this.targetTabIndex = targetTabIndex;
  }
  
  int getTargetTabIndex(){
    return targetTabIndex;
  }
} 
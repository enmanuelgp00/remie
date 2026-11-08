package egp.remie.com.editor;

import java.io.*;
import javax.swing.tree.*;
import egp.remie.custome.*;
import egp.remie.com.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*; 

public class TabPopupMenu extends  JPopupMenu{
  private final TreeNodeHostFileTextEditorTabbedPane tabbedPane;
  private int targetTabIndex = -1;
  
  private JMenuItem renameItem = new JMenuItem("Rename");
  private JMenuItem closeItem = new JMenuItem("Close");
  
  public TabPopupMenu(TreeNodeHostFileTextEditorTabbedPane tabbedPane){
    this.tabbedPane = tabbedPane;
    configItems();
    add(renameItem);
    add(closeItem);
  }
  
  public void configItems(){
    closeItem.addActionListener((e)-> tabbedPane.closeTabAt(getTargetTabIndex()));
  }
  
  public void setTargetTabIndex(int targetTabIndex){
    this.targetTabIndex = targetTabIndex;
  }
  
  public int getTargetTabIndex(){
    return targetTabIndex;
  }
} 
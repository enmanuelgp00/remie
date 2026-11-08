package egp.remie.com.tree;

import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.*;
import egp.remie.com.*;
import egp.remie.custome.*;

public class FileNodePopupMenu extends JPopupMenu{
  private final WorkSpaceNodeTree tree;
  private DefaultMutableTreeNode targetNode;
  private JMenuItem openItem = new JMenuItem("Open");
  private JMenuItem renameItem = new JMenuItem("Rename");
  private JMenuItem deleteItem = new JMenuItem("Delete");
  
  public FileNodePopupMenu(WorkSpaceNodeTree tree){
    this.tree = tree;
    configureItems();
    add(openItem);
    add(renameItem);
    addSeparator();
    add(deleteItem);
  }
  
  private void configureItems(){
    openItem.addActionListener((e)->{ tree.getTextEditorTabbedPane().addTextEditorTabFrom(targetNode); });
    deleteItem.addActionListener(deleteFile);
  }
  
  public DefaultMutableTreeNode getTargetNode(){
    return targetNode;
  }
  
  public void setTargetNode(DefaultMutableTreeNode targetNode){
    this.targetNode = targetNode;
  } 
  
  private ActionListener deleteFile = (e)->{
    try{
      HostFile hostFile = (HostFile)targetNode.getUserObject();
      HostFiles.delete(hostFile);
    }catch(IOException ex){
      ex.printStackTrace();
    }
  };
}
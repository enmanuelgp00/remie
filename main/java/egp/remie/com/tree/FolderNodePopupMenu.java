package egp.remie.com.tree;

import javax.swing.*;
import javax.swing.tree.*;
import egp.remie.com.*;
import egp.remie.custome.*;

public class FolderNodePopupMenu extends JPopupMenu{
  private DefaultMutableTreeNode targetNode;
  private JMenuItem renameItem = new JMenuItem("Rename");
  private JMenu newFileMenu = new JMenu("New");
  private JMenuItem newFileItem = new JMenuItem("File");
  private JMenuItem newFolderItem = new JMenuItem("Folder");
  private JMenuItem deleteItem = new JMenuItem("Delete");
  
  public FolderNodePopupMenu(){
    add(renameItem);
    newFileMenu.add(newFileItem);
    newFileMenu.add(newFolderItem);
    add(newFileMenu);
    addSeparator();
    add(deleteItem);
  }
  
  public DefaultMutableTreeNode getTargetNode(){
    return targetNode;
  }
  
  public void setTargetNode(DefaultMutableTreeNode targetNode){
    this.targetNode = targetNode;
  }
}
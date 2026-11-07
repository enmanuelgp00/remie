package egp.remie.com.tree;

import javax.swing.*;
import javax.swing.tree.*;
import egp.remie.com.*;
import egp.remie.custome.*;

public class FolderNodePopupMenu extends JPopupMenu{
  private DefaultMutableTreeNode targetNode;
  private JMenuItem renameItem = new JMenuItem("Rename");
  private JMenuItem addFileItem = new JMenuItem("Add File");
  private JMenuItem addFolderItem = new JMenuItem("Add Folder");
  private JMenuItem deleteItem = new JMenuItem("Delete");
  
  public FolderNodePopupMenu(){
    add(renameItem);
    add(addFileItem);
    add(addFolderItem);
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
package egp.remie.com.tree;

import javax.swing.*;
import javax.swing.tree.*;
import egp.remie.com.*;
import egp.remie.custome.*;

public class FileNodePopupMenu extends JPopupMenu{
  private DefaultMutableTreeNode targetNode;
  private JMenuItem renameItem = new JMenuItem("Rename");
  private JMenuItem deleteItem = new JMenuItem("Delete");
  
  public FileNodePopupMenu(){
    add(renameItem);
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
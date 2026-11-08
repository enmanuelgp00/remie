package egp.remie.com.editor;

import javax.swing.*;
import javax.swing.tree.*;
import egp.remie.com.*;
import egp.remie.custome.*;

public class TextEditorPopupMenu extends JPopupMenu{
  private DefaultMutableTreeNode targetNode;
  private JMenuItem undoItem = new JMenuItem("Undo");
  private JMenuItem cutItem = new JMenuItem("Cut");
  private JMenuItem copyItem = new JMenuItem("Copy");
  private JMenuItem paseItem = new JMenuItem("Paste");
  private JMenuItem deleteItem = new JMenuItem("Delete");
  private JMenuItem selectAllItem = new JMenuItem("Select All");
  
  public TextEditorPopupMenu(){
    add(undoItem);
    addSeparator();
    add(cutItem);
    add(copyItem);
    add(paseItem);
    addSeparator();
    add(deleteItem);
    addSeparator();
    add(selectAllItem);
  }
  
  public DefaultMutableTreeNode getTargetNode(){
    return targetNode;
  }
  
  public void setTargetNode(DefaultMutableTreeNode targetNode){
    this.targetNode = targetNode;
  }
}
package egp.remie.com.editor;

import java.io.*;
import javax.swing.tree.*;
import egp.remie.custome.*;
import egp.remie.com.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*; 

public class TreeNodeHostFileTextEditor extends CustomeScrollPane {
  
  private DefaultMutableTreeNode treeNode;
  private CustomeTextArea textArea = new CustomeTextArea();
  private TextEditorPopupMenu textEditorPopupMenu = new TextEditorPopupMenu();
  
  public TreeNodeHostFileTextEditor(DefaultMutableTreeNode treeNode){
    super();
    setTreeNode(treeNode);
    setViewportView(textArea);
    textArea.addMouseListener(showPopupMenu);
  }
  
  private MouseAdapter showPopupMenu = new MouseAdapter(){
    @Override
    public void mouseClicked(MouseEvent e){
      if(!SwingUtilities.isRightMouseButton(e))
        return;
      textEditorPopupMenu.show(textArea, e.getX(), e.getY());
    }
  };
  
  public void setTreeNode(DefaultMutableTreeNode treeNode){
    this.treeNode = treeNode;
  }
  
  public DefaultMutableTreeNode getTreeNode(){
    return treeNode;
  }
  
  public void loadFileContent() throws IOException {    
    textArea.setText(HostFiles.readString(getHostFile()));
  }
  
  public HostFile getHostFile(){
    return (HostFile)getTreeNode().getUserObject();
  }
  
  public void commit(){
    
  }
  
}
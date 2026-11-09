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
    textArea.addKeyListener(commitOnControl_S);
  }
  
  public CustomeTextArea getTextArea(){
    return textArea;
  }
  
  private KeyAdapter commitOnControl_S = new KeyAdapter(){
    boolean isPressingControl = false;
    @Override
    public void keyPressed(KeyEvent e){
      switch(e.getKeyCode()){
        case KeyEvent.VK_CONTROL:
          if(!isPressingControl) isPressingControl = true;
          break;
        case KeyEvent.VK_S:
          if(isPressingControl) commit();
          break;
      }
    }
    @Override
    public void keyReleased(KeyEvent e){
      if(e.getKeyCode() == KeyEvent.VK_CONTROL)
        isPressingControl = false;
    }
  };
  
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
    try{
      HostFiles.write(getHostFile(), textArea.getText());      
    }catch(IOException ex){
      ex.printStackTrace();
    }
  }
  
}
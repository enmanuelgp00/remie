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
  private CustomeTextPane textPane = new CustomeTextPane();
  private TextEditorPopupMenu textEditorPopupMenu = new TextEditorPopupMenu();
  private String originalText;
  private TreeNodeHostFileTextEditorTabbedPane tabbedPane;
  private boolean editing = false;
  private String changeNotificationText = " *";
  
  public TreeNodeHostFileTextEditor(TreeNodeHostFileTextEditorTabbedPane tabbedPane, DefaultMutableTreeNode treeNode){
    super();
    this.tabbedPane = tabbedPane;
    setTreeNode(treeNode);
    setViewportView(textPane);
    getVerticalScrollBar()
      .setUnitIncrement(10);
    textPane.addMouseListener(showPopupMenu);
    textPane.addKeyListener(commitOnControl_S);
    textPane.addKeyListener(textChangeNotification);
  }
  
  public void notifyChangeOnTitle(){
    String fileName = getHostFile().toString();      
    if(isOriginalText()){
      setTabName(fileName);
      setEditing(false);
      return;
    }
    if(!isEditing()){
      setEditing(true);
      setTabName(fileName + changeNotificationText);
      return;        
    }
  }  
  public void updateTitle(){
    String fileName = getHostFile().toString();      
    if(isOriginalText()){
      setTabName(fileName);
      return;
    }
    setTabName(fileName + changeNotificationText);
  }
  public void loadFileText() throws IOException {  
    originalText = HostFiles.readString(getHostFile());
    textPane.setText(originalText);
  }  
  public void commit(){
    try{
      HostFiles.write(getHostFile(), textPane.getText());  
      originalText = textPane.getText();
      setEditing(false);
    }catch(IOException ex){
      ex.printStackTrace();
    }
  }

  private void setEditing(boolean editing){
    this.editing = editing;
  }  
  private void setTabName(String name){
    int i = tabbedPane.indexOfComponent(this);
    JLabel tabLabel = (JLabel)tabbedPane.getTabComponentAt(i);
    SwingUtilities.invokeLater(()->{
      tabLabel.setText(name);
    });
  }  
  public void setTreeNode(DefaultMutableTreeNode treeNode){
    this.treeNode = treeNode;
  }  
  
  public DefaultMutableTreeNode getTreeNode(){
    return treeNode;
  } 
  public CustomeTextPane getTextPane(){
    return textPane;
  } 
  public HostFile getHostFile(){
    return (HostFile)getTreeNode().getUserObject();
  }
  
  public boolean isEditing(){
    return editing;
  }  
  public boolean isOriginalText(){
    return originalText.equals(textPane.getText());
  }  
    
  private KeyAdapter textChangeNotification = new KeyAdapter(){ //learning_note: getKeyCode return 0 inside keyTyped method we most use getKeyChar
    @Override
    public void keyReleased(KeyEvent e){
      if(e.getKeyChar() == KeyEvent.CHAR_UNDEFINED)
        return;
      notifyChangeOnTitle();
    }
  };  
  private KeyAdapter commitOnControl_S = new KeyAdapter(){
    @Override
    public void keyPressed(KeyEvent e){
      if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S){
        commit();
      }
    }
  };  
  private MouseAdapter showPopupMenu = new MouseAdapter(){
    @Override
    public void mouseClicked(MouseEvent e){
      if(!SwingUtilities.isRightMouseButton(e))
        return;
      textEditorPopupMenu.show(textPane, e.getX(), e.getY());
    }
  };
}  
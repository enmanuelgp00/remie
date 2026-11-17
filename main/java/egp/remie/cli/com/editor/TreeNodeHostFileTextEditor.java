package egp.remie.cli.com.editor;

import java.io.*;
import javax.swing.tree.*;
import egp.remie.cli.custome.*;
import egp.remie.host.file.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.event.*; 
import java.awt.*; 

public class TreeNodeHostFileTextEditor extends JPanel {
  
  private long prevLineCount = 0;
  private JLabel currentLineLabel = new JLabel(" ");
  private JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 1));
  private CustomeScrollPane scroll = new CustomeScrollPane();
  private DefaultMutableTreeNode treeNode;
  private JPanel textPanel = new JPanel(new BorderLayout());
  private CustomeTextPane textPane = new CustomeTextPane();
  private JTextPane lineNumberPane = new JTextPane();
  private TextEditorPopupMenu textEditorPopupMenu = new TextEditorPopupMenu();
  private String originalText;
  private TreeNodeHostFileTextEditorTabbedPane tabbedPane;
  private boolean editing = false;
  private String changeNotificationText = " *";
  
  public TreeNodeHostFileTextEditor(TreeNodeHostFileTextEditorTabbedPane tabbedPane, DefaultMutableTreeNode treeNode){
    super(new BorderLayout());
    this.tabbedPane = tabbedPane;
    textPanel.add(lineNumberPane, BorderLayout.WEST);
    textPanel.add(textPane, BorderLayout.CENTER);
    
    lineNumberPane.setEditable(false);
    DefaultCaret lineNumCaret = (DefaultCaret)lineNumberPane.getCaret();
    lineNumCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE); // prevent thw scroll pane from scrolling down when swtting text
    
    scroll.setViewportView(textPanel);
    scroll.getVerticalScrollBar()
      .setUnitIncrement(10);      
    add(scroll, BorderLayout.CENTER);
    
    currentLineLabel.setFont(new Font("monospaced", Font.BOLD, 14));    
    statusBar.add(currentLineLabel);
    add(statusBar, BorderLayout.SOUTH);
    setTreeNode(treeNode);
    
    textPane.addMouseListener(textPaneMouseListenerHandler);
    textPane.addKeyListener(textPaneKeyListenerHandler);
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
  public void loadFileText() throws IOException, InterruptedException {  
    originalText = HostFiles.readString(getHostFile());
    textPane.setText(originalText);
    updateStatus();
  }  
  public void commit(){
    try{
      HostFiles.write(getHostFile(), textPane.getText().getBytes());  
      originalText = textPane.getText();
      setEditing(false);
    }catch(IOException | InterruptedException ex){
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
  public void setTextPaneTabSize(int tabSize){
    textPane.setTabSize(tabSize);
  }
  public void setTreeNode(DefaultMutableTreeNode treeNode){
    this.treeNode = treeNode;
  }  
  public void setTextPaneFont(Font font){
    textPane.setFont(font);
    lineNumberPane.setFont(font);
  }
  public void setTextPaneBackground(Color color){
    textPane.setBackground(color);
    //lineNumberPane.setBackground(color);  
  }
  public void setTextPaneForeground(Color color){
    textPane.setForeground(color);
    //lineNumberPane.setForeground(color);
  }
  public void setTextPaneCaretColor(Color color){
    textPane.setCaretColor(color);
  }
  public void enableTextPaneWordWrapping(boolean wrapping){
    textPane.enableWordWrapping(wrapping);
  }
  public void enableTextPaneAutoIndent(boolean autoIndent){
    textPane.enableAutoIndent(autoIndent);
  }
  public void enableTextPaneTabInsertionAsSpace(boolean asSpace){
    textPane.enableTabInsertionAsSpace(asSpace);
  }
  
  
  public DefaultMutableTreeNode getTreeNode(){
    return treeNode;
  } 
  public JTextPane getLineNumberPane(){
    return lineNumberPane;
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
  private long getTextLineCount(){
    return textPane.getText().chars().filter(c -> c == '\n').count() + 1;
  }
  
  
  private MouseAdapter textPaneMouseListenerHandler = new MouseAdapter(){
    @Override
    public void mousePressed(MouseEvent e){
      updateStatus();
    }
    @Override
    public void mouseClicked(MouseEvent e){
      showPopupMenu(e);
    }
  };

  private KeyAdapter textPaneKeyListenerHandler = new KeyAdapter(){ //learning_note: getKeyCode return 0 inside keyTyped method we most use getKeyChar
    @Override
    public void keyPressed(KeyEvent e){
      commitOnControl_S(e);
    }
    @Override
    public void keyReleased(KeyEvent e){
      textChangeNotification(e);
      updateStatus();
    }
  };        
  
  private void textChangeNotification(KeyEvent e){
    if(e.getKeyChar() == KeyEvent.CHAR_UNDEFINED || e.isControlDown())
      return;
    notifyChangeOnTitle();
  }
  
  
  private void commitOnControl_S(KeyEvent e){
    if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S){
      commit();
   		notifyChangeOnTitle();
    }
  }
  
  private void showPopupMenu(MouseEvent e){
    if(!SwingUtilities.isRightMouseButton(e))
      return;
    textEditorPopupMenu.show(textPane, e.getX(), e.getY());
  }
  
  private void updateStatus(){
    try {      
      int caretPos = textPane.getCaretPosition();
      Document doc = textPane.getDocument();
      String beforeCaretText = doc.getText(0, caretPos);
      int bCaretLastLf = beforeCaretText.lastIndexOf('\n');
      int column = caretPos - bCaretLastLf;
      long lfCountOnPos = beforeCaretText.chars().filter(c -> c == '\n').count(); //learning_note charts returns an stream
      long lfCount = getTextLineCount();
      currentLineLabel.setText(String.format("ln:%2d/%2d col:%2d pos:%2d", ++lfCountOnPos, lfCount, column, caretPos));
      if(prevLineCount != lfCount){
        prevLineCount = lfCount;
        int width = String.valueOf(lfCount).length();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lfCount; i++ ){
          sb.append(String.format("%" + width + "d\n", i));
        }
        lineNumberPane.setText(sb.toString());
      }
      
    } catch(BadLocationException ex){
      ex.printStackTrace();
    }
  }
}      
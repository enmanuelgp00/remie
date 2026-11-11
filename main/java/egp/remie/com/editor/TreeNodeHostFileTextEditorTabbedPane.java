package egp.remie.com.editor;

import egp.remie.com.*;
import egp.remie.user.*;
import egp.remie.custome.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

public class TreeNodeHostFileTextEditorTabbedPane extends CustomeTabbedPane {
  public Map<DefaultMutableTreeNode, Integer> treeNodeMap = new HashMap<DefaultMutableTreeNode, Integer>();
  public TabPopupMenu tabPopupMenu;
  public Font textEditorFont;
  public Color textEditorBackground;
  public Color textEditorForeground;
  private Boolean lineWrapping = false;
  
  public TreeNodeHostFileTextEditorTabbedPane(){
    
    textEditorFont = UserPreferences.getEditorFont()
      .orElse(new Font("monospaced", Font.PLAIN, 16));
    textEditorBackground = UserPreferences.getEditorBackground()
      .orElse(Color.WHITE);
    textEditorForeground = UserPreferences.getEditorForeground()
      .orElse(Color.BLACK);
    lineWrapping = UserPreferences.getEditorLineWrapping()
      .orElse(true);
      
    tabPopupMenu = new TabPopupMenu(this);
    selectTabOnlyOnLeftClick();
    addMouseListener(handleMouseListener);
  }  
  public void closeTabAt(int tabIndex){    
    TreeNodeHostFileTextEditor tabTextEditor = (TreeNodeHostFileTextEditor)getComponentAt(tabIndex);
    treeNodeMap.remove(tabTextEditor.getTreeNode());
    removeTabAt(tabIndex);
  }  
  public void addTextEditorTabFrom(DefaultMutableTreeNode treeNode){
    if(treeNodeMap.keySet().contains(treeNode)){
      int tabIndex = treeNodeMap.get(treeNode);
      setSelectedComponent(getComponentAt(tabIndex));
      return;      
    }
    try {
      TreeNodeHostFileTextEditor editor = new TreeNodeHostFileTextEditor(this, treeNode);
      editor.loadFileText();
      loadEditorStyle(editor);      
      add(editor);
      int tabIndex = getTabCount() - 1;
      String tabName = editor.getHostFile().toString();
      
      treeNodeMap.put(treeNode, tabIndex);
      setTabComponentAt(tabIndex, new JLabel(tabName));      
      setSelectedComponent(editor);
    }catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  private void selectTabOnlyOnLeftClick(){
    Arrays.asList(getMouseListeners())
      .stream()
      .filter(l -> l.getClass().getName().contains("BasicTabbedPaneUI$Handler"))
      .findFirst()
      .ifPresent(l -> {
        //System.out.println(l.getClass().getName());
        removeMouseListener(l);
        addMouseListener(new MouseAdapter(){
          @Override
          public void mousePressed(MouseEvent e){
            if(SwingUtilities.isLeftMouseButton(e)){
              l.mousePressed(e);
            }
          }
        });
      });
  }
  
  public Font getTextEditorFont(){
    return textEditorFont;
  }  
  public Color getTextEditorBackground(){
    return textEditorBackground;
  }  
  public Color getTextEditorForeground(){
    return textEditorForeground;
  }
  public int getTabIndexByNode(DefaultMutableTreeNode treeNode){
    Integer index = treeNodeMap.get(treeNode);
    if(index == null)
      return -1;
    return index;
  }  
  public TreeNodeHostFileTextEditor getTextEditorByNode(DefaultMutableTreeNode treeNode){
    int i = getTabIndexByNode(treeNode);
    if(i == -1)
      return null;
    return (TreeNodeHostFileTextEditor)getComponentAt(i);
  }
  public List<TreeNodeHostFileTextEditor> getTextEditors(){
    List<TreeNodeHostFileTextEditor> editors = new ArrayList<TreeNodeHostFileTextEditor>();
    for(int i = 0; i < getTabCount(); i++){
      TreeNodeHostFileTextEditor textEditor = (TreeNodeHostFileTextEditor)getComponentAt(i);
      editors.add(textEditor);
    }    
    return editors;
  }  
  public boolean isLineWrappingEnabled(){
    return lineWrapping;
  }
  public void refreshEditorTextPane(){
    getTextEditors()
      .forEach(ed -> {
        CustomeTextPane textPane = ed.getTextPane();
        textPane.revalidate();
        textPane.repaint();
      });
    
  }
  
  private void loadEditorStyle(TreeNodeHostFileTextEditor editor){    
    CustomeTextPane textArea = editor.getTextPane();
    textArea.setFont(textEditorFont);
    textArea.setBackground(textEditorBackground);
    textArea.setForeground(textEditorForeground);
    textArea.setCaretColor(textEditorForeground);
    textArea.enableLineWrapping(isLineWrappingEnabled());
  }
  public void enableEditorLineWrapping(boolean lineWrapping){
    this.lineWrapping = lineWrapping;
    getTextEditors()
      .forEach(ed -> {
        ed.getTextPane().enableLineWrapping(isLineWrappingEnabled());
      });
  }
  public void setTextEditorFont(Font font){
    textEditorFont = font;    
    getTextEditors()
      .forEach(e -> e.getTextPane().setFont(font));
  }
  public void setTextEditorBackground(Color background){
    textEditorBackground = background;
    getTextEditors()
      .forEach(e -> e.getTextPane().setBackground(background));
  }
  public void setTextEditorForeground(Color foreground){
    textEditorForeground = foreground;
    getTextEditors()
      .forEach(e -> e.getTextPane().setForeground(foreground));
  }  
  
  public MouseAdapter handleMouseListener = new MouseAdapter(){
    @Override
    public void mouseClicked(MouseEvent e){
      int tabIndex = indexAtLocation(e.getX(), e.getY());
      if(tabIndex < 0)
        return;      
      if(SwingUtilities.isMiddleMouseButton(e)){
        closeTabAt(tabIndex);
        return;
      }
      if(SwingUtilities.isRightMouseButton(e)){
        tabPopupMenu.setTargetTabIndex(tabIndex);
        tabPopupMenu.show(TreeNodeHostFileTextEditorTabbedPane.this, e.getX(), e.getY());
        return;
      }
    }
  };
   
}
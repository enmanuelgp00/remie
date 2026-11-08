package egp.remie.com.editor;

import egp.remie.com.*;
import egp.remie.custome.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class TreeNodeHostFileTextEditorTabbedPane extends CustomeTabbedPane {
  public Map<DefaultMutableTreeNode, Integer> treeNodeMap = new HashMap<DefaultMutableTreeNode, Integer>();
  public TabPopupMenu tabPopupMenu;
  
  public TreeNodeHostFileTextEditorTabbedPane(){
    tabPopupMenu = new TabPopupMenu(this);
    addMouseListener(handleMouseListener);
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
    TreeNodeHostFileTextEditor editor = new TreeNodeHostFileTextEditor(treeNode);
    try {
      editor.loadFileContent();
      treeNodeMap.put(treeNode, getTabCount());
      add(editor.getHostFile().toString(), editor);
      setSelectedComponent(editor);
    }catch(Exception ex) {
      ex.printStackTrace();
    }
  }
}
package egp.remie;

import egp.remie.custome.*;
import egp.remie.com.*;
import egp.remie.com.editor.*;
import egp.remie.com.tree.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;

public class Main{
  final TreeNodeHostFileTextEditorTabbedPane editorTabbedPane = new TreeNodeHostFileTextEditorTabbedPane();
  
  public Main(){
    CustomeFrame frame = new CustomeFrame();
    HostFileTreeNode tree = new HostFileTreeNode(new HostFile(".")); 
    tree.addMouseListener(onDoubleClickAddEditorTab(tree));     
    WorkSpaceTreePane workSpace = new WorkSpaceTreePane();
    workSpace.addTree(tree);   
    frame.add(new CustomeSplitPane(
      CustomeSplitPane.HORIZONTAL_SPLIT,
      workSpace,
      editorTabbedPane));
    frame.setVisible(true);
  }
  
  public static void main(String[] args){
    SwingUtilities.invokeLater(()->{
      new Main();
    });
  }
  
  private MouseAdapter onDoubleClickAddEditorTab(HostFileTreeNode tree){
    return new MouseAdapter(){
      @Override
      public void mouseClicked(MouseEvent e){
        if(e.getClickCount() < 2 || !SwingUtilities.isLeftMouseButton(e))
          return; 
        TreePath treePath = tree.getPathForLocation(e.getX(), e.getY());
        if(treePath == null)
          return;
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
        HostFile hostFile = (HostFile)treeNode.getUserObject();
        if(HostFiles.isDirectory(hostFile))
          return;
        editorTabbedPane.addEditorTab(treeNode);        
      }
    };
  }
}
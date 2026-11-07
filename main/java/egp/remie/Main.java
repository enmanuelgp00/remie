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
  
  public Main(){
    CustomeFrame frame = new CustomeFrame();
    TreeNodeHostFileTextEditorTabbedPane editorTabbedPane = new TreeNodeHostFileTextEditorTabbedPane();

    WorkSpaceNodeTree tree = new WorkSpaceNodeTree(
      new WorkSpaceName("my computer"), editorTabbedPane);
    HostFileTreeNode treeNode0 = new HostFileTreeNode(new HostFile("."), tree); 
    HostFileTreeNode treeNode1 = new HostFileTreeNode(new HostFile("../../.."), tree);
    tree.addHostFileTreeNode(treeNode0);  
    tree.addHostFileTreeNode(treeNode1);  
    
    frame.add(new CustomeSplitPane(CustomeSplitPane.HORIZONTAL_SPLIT,
      tree, editorTabbedPane));
    frame.setVisible(true);
  }
  
  public static void main(String[] args){
    SwingUtilities.invokeLater(()->{
      new Main();
    });
  }
  
  
}
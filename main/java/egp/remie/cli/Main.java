package egp.remie.cli;

import egp.remie.cli.custome.*;
import egp.remie.cli.com.*;
import egp.remie.cli.com.editor.*;
import egp.remie.cli.com.tree.*;
import egp.remie.cli.com.frame.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;

public class Main{
  
  public Main(){
    TreeNodeHostFileTextEditorTabbedPane editorTabbedPane = new TreeNodeHostFileTextEditorTabbedPane();
    WorkSpaceNodeTree tree = new WorkSpaceNodeTree("My Computer", editorTabbedPane);
    HostFileTreeNode treeNode0 = new HostFileTreeNode(new HostFile("."), tree); 
    tree.addHostFileTreeNode(treeNode0);  
    MainFrame frame = new MainFrame(tree, editorTabbedPane);
 
    frame.setVisible(true);
  }
  
  public static void main(String[] args){
    SwingUtilities.invokeLater(()->{
      new Main();
    });
  }
}
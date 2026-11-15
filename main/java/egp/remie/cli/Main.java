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
import java.io.*;

public class Main{
  
  public Main(HostFile hostfile){
    TreeNodeHostFileTextEditorTabbedPane editorTabbedPane = new TreeNodeHostFileTextEditorTabbedPane();
    WorkSpaceNodeTree tree = new WorkSpaceNodeTree("My Computer", editorTabbedPane);
    HostFileTreeNode treeNode0 = new HostFileTreeNode(hostfile, tree); 
    tree.addHostFileTreeNode(treeNode0);  
    MainFrame frame = new MainFrame(tree, editorTabbedPane);
 
    frame.setVisible(true);
  }
  
  public static void main(String[] args){
    SwingUtilities.invokeLater(()->{
      HostFile fl = new HostFile("."); 
      switch(args.length){
        case 1:
          fl = new HostFile(args[0]);
          break;
        case 2:
          try{
            System.out.println("Trying connect to the host");
            Host host = Hosts.get(args[0]);
            fl = new HostFile(host, args[1]);
          }catch(IOException | InterruptedException ex){
            ex.printStackTrace();
          }
          break;
      }  
      new Main(fl);
    });
  }
}
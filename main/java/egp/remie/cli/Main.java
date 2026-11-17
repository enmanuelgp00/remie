package egp.remie.cli;

import egp.remie.cli.custome.*;
import egp.remie.host.file.*;
import egp.remie.cli.com.console.*;
import egp.remie.cli.com.editor.*;
import egp.remie.cli.com.tree.*;
import egp.remie.cli.com.frame.*;
import egp.remie.host.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Main{
  
  public Main(HostFile hostFile){
    TreeNodeHostFileTextEditorTabbedPane editorTabbedPane = new TreeNodeHostFileTextEditorTabbedPane();
    ConsoleTabbedPane consoleTabbedPane = new ConsoleTabbedPane();
    WorkSpaceNodeTree tree = new WorkSpaceNodeTree("My Computer", editorTabbedPane, consoleTabbedPane);
    
    if(HostFiles.isDirectory(hostFile)){
      HostFileTreeNode treeNode0 = new HostFileTreeNode(hostFile, tree); 
      tree.addHostFileTreeNode(treeNode0);        
    } else {
      editorTabbedPane.createTextEditorTab(new DefaultMutableTreeNode(hostFile));
    }
    MainFrame frame = new MainFrame(tree);
 
    frame.setVisible(true);
  }
  
  public static void main(String[] args){
    SwingUtilities.invokeLater(()->{
      if(args.length < 2){
        printHowToUse();
        return;
      }
      String parm = args[0];
      String value = args[1];
      
      if(args.length == 2){
        if(parm.equals("--listen") || parm.equals("-l")){
          launchServer(value);
          return;
        }
        if(parm.equals("-f")){
          new Main(new HostFile(value));
          return;
        }
        printHowToUse();
        return;
      }
      if(args.length == 3){   
        String hostName = args[1];
        String fileName = args[2];
           
        if(!parm.equals("-f")){
          printHowToUse();
          return;
        }
        try{
          Host host = Hosts.get(hostName);
          new Main(new HostFile(host, fileName));
        }catch(Exception ex){
          ex.printStackTrace();
        }
      }
    });
  }
  public static void launchServer(String port){
    try{
      new Server(Integer.parseInt(port));      
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }  
  public static void printHowToUse(){
    System.out.println("#How to use: ");
    System.out.println("  --listen <PORT>");
    System.out.println("  -f <FILE>");
    System.out.println("  -f http://<HOST>:<PORT> <FILE>");
  }
}
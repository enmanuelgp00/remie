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
import java.util.*;
import java.util.List;

public class Main{
  
  public Main(HostFile[] files){
    TreeNodeHostFileTextEditorTabbedPane editorTabbedPane = new TreeNodeHostFileTextEditorTabbedPane();
    ConsoleTabbedPane consoleTabbedPane = new ConsoleTabbedPane();
    consoleTabbedPane.createConsoleTab(files[0]);
    WorkSpaceNodeTree tree = new WorkSpaceNodeTree("My Computer", editorTabbedPane, consoleTabbedPane);
    
    for(HostFile fl : files){
      try{     
        if(HostFiles.isDirectory(fl)){
          HostFileTreeNode treeNode0 = new HostFileTreeNode(fl, tree); 
          tree.addHostFileTreeNode(treeNode0);        
        } else {
          editorTabbedPane.createTextEditorTab(new DefaultMutableTreeNode(fl));
        } 
      }catch(InterruptedException ex){
        ex.printStackTrace();
      }
    }
    
    MainFrame frame = new MainFrame(tree);
 
    frame.setVisible(true);
  }
  
  public static void main(String[] args){
    List<String> argList = Arrays.asList(args);
    
    boolean usingServerSwitch = argList.contains("-l");
    boolean usingClientSwitch = argList.contains("-f");
    boolean usingBridgeSwitch = argList.contains("-b");
    
    if(usingClientSwitch && usingServerSwitch){
      System.out.println("#Only one switch can be used at a time ");
      printHowToUse();
      System.exit(1);
    }
    
    if(usingServerSwitch){
      new Server(Integer.parseInt(args[1]));
      return;
    }
    
    if(usingBridgeSwitch){
      int port = Integer.parseInt(args[1]);
      String hostName = args[2];
      try{
        
        System.out.println("#Checking host connection");
        Host host = Hosts.get(hostName);
        System.out.println("#Creating bridge");
        new Bridge(port, host);
      }catch(InterruptedException | IOException ex){
        ex.printStackTrace();
      }
      return;      
    }
    
    List<HostFile> files = new ArrayList<>();
    String param;
    String arg;
    String flpath;
    String hostName;
    String afterNextArg;
    for(int i = 0; i < args.length; i++){
      arg = args[i];
      if(arg.equals("-f")){
        if(i + 1 < args.length){ // check if has next arg
          hostName = null;
          flpath = args[i+1];
          
          if(i + 2 < args.length){ // check if has host name is defined
            afterNextArg = args[i+2];
            if(!afterNextArg.equals("-f")){
              hostName = args[i+1];
              flpath = args[i+2]; 
            }     
          }
          if(hostName == null){
            files.add(new HostFile(flpath));
            continue; 
          }
          try{
            System.out.println("#Connecting to: " + hostName);
            Host host = Hosts.get(hostName);
            files.add(new HostFile(host, flpath));
          }catch(InterruptedException | IOException ex){
            ex.printStackTrace();
          }
          continue;
        }
        System.out.println("#Expected host and file name");
        printHowToUse();
        continue;
      }
    }
    new Main(files.toArray(new HostFile[0]));
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
    System.out.println("  -l <PORT>");
    System.out.println("  -f <FILE> [ -f http://<HOST>:<PORT> <FILE> ]");
  }
}
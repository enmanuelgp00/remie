package egp.remie.cli.com.tree;

import egp.remie.host.file.*;
import egp.remie.cli.custome.*;
import egp.remie.cli.com.tree.name.*;
import java.io.*;
import java.util.List;
import javax.swing.tree.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.stream.*;

public class HostFileTreeNode extends DefaultMutableTreeNode {

  private HostFile hostFileRoot;
  private final WorkSpaceNodeTree tree;
  private boolean hostFileSyncing = false;
  private int hostFileSyncInterval = 1000;
  
  public HostFileTreeNode(HostFile hostFileRoot, WorkSpaceNodeTree tree){
    super(hostFileRoot);
    this.tree = tree;
    WorkSpaceNodeTree.addLoadingNodeTo(getTreeModel(), this);
    this.hostFileRoot = hostFileRoot;
    setAutoHostFileSyncInterval(1000);
    setAutoHostFileSyncEnabled(true);
  }
  
  public DefaultTreeModel getTreeModel(){
    return (DefaultTreeModel)tree.getModel();
  }
  
  public void setAutoHostFileSyncInterval(int interval){
    this.hostFileSyncInterval = interval;
  }
  
  public void setAutoHostFileSyncEnabled(boolean syncing){
    if(syncing)
      startSyncingHostFiles();
    hostFileSyncing = syncing;
  }
  
  public boolean isAutoHostFileSyncEnabled(){
    return hostFileSyncing;
  }
  
  public int getAutoHostFileSyncInterval(){
    return hostFileSyncInterval;
  }
  
  private void startSyncingHostFiles(){
    if(hostFileSyncing)
      return;
    Thread sync = new Thread(()->{
      while(getParent() != null || !isAutoHostFileSyncEnabled()){
        
        try{
          Thread.sleep(getAutoHostFileSyncInterval());
          syncAllHostFiles();          
        }catch(Exception e){
          e.printStackTrace();
        }
      }
      setAutoHostFileSyncEnabled(false);
    });
    sync.start();
  }
  
  public void syncAllHostFiles(){
    syncHostFileTree(this);
  }
  
  public void syncHostFileTree(DefaultMutableTreeNode parentNode){
    if(parentNode.getChildCount() == 1){
      DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode)parentNode.getFirstChild();
      if(firstChild.getUserObject() instanceof LoadingNodeMessage)
        return;
    }
    HostFile parentNodeFile = (HostFile)parentNode.getUserObject();
    try{
      if(!HostFiles.isDirectory(parentNodeFile))
        return;
    }catch(InterruptedException ex ){
      ex.printStackTrace();
      return;
    }
    try(Stream<HostFile> fileStream = Arrays.stream(HostFiles.list(parentNodeFile))){ 
      List<HostFile> filesInDir = fileStream.collect(Collectors.toList());
      List<DefaultMutableTreeNode> extraNodes = Collections.list(parentNode.children()).stream()
        .map(DefaultMutableTreeNode.class::cast)
        .collect(Collectors.toList());
      
      if(filesInDir.size() > 0){
        filesInDir.forEach(fl -> {
          DefaultMutableTreeNode[] initNodes = extraNodes.toArray(new DefaultMutableTreeNode[extraNodes.size()]);
          DefaultMutableTreeNode usingNode;
          Object obj;
          HostFile nodeFl;
          for(int i = 0; i < initNodes.length; i++){
            usingNode = initNodes[i];
            obj = usingNode.getUserObject();
            if(obj instanceof HostFile){
              nodeFl = (HostFile)obj;
              if(nodeFl.equals(fl)){
                extraNodes.remove(usingNode);
                return;
              }
            }
          }
          tree.insertHostFileNode(fl, parentNode);
        });
      }
      DefaultMutableTreeNode node;
      Object obj;
      for(int i = 0; i < extraNodes.size(); i++){
        node = extraNodes.get(i);
        if(i == 0 && filesInDir.size() == 0){
          if(!(node.getUserObject() instanceof EmptyFolderMessage)){
            node.setUserObject(new EmptyFolderMessage());
            node.removeAllChildren();             
            getTreeModel().nodeStructureChanged(node);
          }
          continue;
        }
        getTreeModel().removeNodeFromParent(node);
      }
        
      if(parentNode.isLeaf()){
        //WorkSpaceNodeTree.addLoadingNodeTo(getTreeModel(), parentNode);
        WorkSpaceNodeTree.addEmptyFolderMessageTo(getTreeModel(), parentNode);
        return;
      }    
      Enumeration<TreeNode> nodeChildrenEnum = parentNode.children();
      while(nodeChildrenEnum.hasMoreElements()){
        DefaultMutableTreeNode nodeChild = (DefaultMutableTreeNode)nodeChildrenEnum.nextElement();
        if(nodeChild.getUserObject() instanceof HostFile)
          syncHostFileTree(nodeChild);
      }    
    }catch(IOException | InterruptedException ex){
      if(!(ex instanceof java.nio.file.AccessDeniedException))
        ex.printStackTrace();
    }    
  }
}
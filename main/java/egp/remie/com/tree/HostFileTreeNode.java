package egp.remie.com.tree;

import egp.remie.com.*;
import java.io.*;
import java.util.List;
import javax.swing.tree.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.stream.*;
import egp.remie.custome.*;

public class HostFileTreeNode extends DefaultMutableTreeNode {
  
  private HostFile hostFileRoot;
  private final WorkSpaceNodeTree tree;
  private boolean hostFileSyncing = false;
  private int hostFileSyncInterval = 1000;
  
  public HostFileTreeNode(HostFile hostFileRoot, WorkSpaceNodeTree tree){
    super(hostFileRoot);
    WorkSpaceNodeTree.addLoadingDummyTo(this);
    this.tree = tree;
    this.hostFileRoot = hostFileRoot;
    setHostFileSyncInterval(1000);
    setHostFileSyncEnabled(true);
  }
  
  public DefaultTreeModel getTreeModel(){
    return (DefaultTreeModel)tree.getModel();
  }
  
  public void setHostFileSyncInterval(int interval){
    this.hostFileSyncInterval = interval;
  }
  
  public void setHostFileSyncEnabled(boolean syncing){
    startSyncingHostFiles();
    hostFileSyncing = syncing;
  }
  
  public boolean isHostFileSyncEnabled(){
    return hostFileSyncing;
  }
  
  public int getHostFileSyncInterval(){
    return hostFileSyncInterval;
  }
  
  private void startSyncingHostFiles(){
    Thread sync = new Thread(()->{
      while(isHostFileSyncEnabled()){
        try{
          Thread.sleep(getHostFileSyncInterval());
          syncAllHostFiles();          
        }catch(Exception e){
          e.printStackTrace();
        }
      }
    });
    sync.start();
  }
  
  public void syncAllHostFiles(){
    syncHostFileTree(this);
  }
  
  public void syncHostFileTree(DefaultMutableTreeNode parentNode){
    if(parentNode.isLeaf())
      return;
    DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode)parentNode.getFirstChild();
    if(!(firstChild.getUserObject() instanceof HostFile))
      return;
    HostFile parentNodeFile = (HostFile)parentNode.getUserObject();
    if(!HostFiles.isDirectory(parentNodeFile))
      return;
    try{   
      List<HostFile> nodeHFiles = Collections.list(parentNode.children()) //learning_note: returns Enumeration<TreeNode>
        .stream()
        .map(child -> (HostFile)((DefaultMutableTreeNode)child).getUserObject())
        .collect(Collectors.toCollection(ArrayList::new)); //learning_note: Collectors.toCollection(LinkedHashSet::new)   same as .toSet()      
      List<HostFile> nodeExtraHFiles = new ArrayList<HostFile>(nodeHFiles);
      Set<HostFile> fetchedHostFiles = HostFiles.list(parentNodeFile)
        .collect(Collectors.toCollection(HashSet::new));
      fetchedHostFiles.forEach((fl)->{
        if(nodeExtraHFiles.contains(fl)){ //learning_note: it seems List.contains uses the equals(Object method) method 
          nodeExtraHFiles.remove(fl);
          return;
        }
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(fl);
        if(HostFiles.isDirectory(fl))
          WorkSpaceNodeTree.addLoadingDummyTo(treeNode);
        nodeHFiles.add(fl);
        nodeHFiles.sort(Comparator.comparing(HostFile::toString));
        int index = nodeHFiles.indexOf(fl);
        getTreeModel().insertNodeInto(treeNode, parentNode, index);//learning_note: evoids reloaing all the parent tree
      });   
      nodeExtraHFiles.forEach((fl)->{
        int i = 0;        
        while (i < parentNode.getChildCount()){
          DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)parentNode.getChildAt(i++);
          HostFile childHFile = (HostFile)childNode.getUserObject();
          if(childHFile.equals(fl))
            getTreeModel().removeNodeFromParent(childNode);
        }
      });      
      Enumeration<TreeNode> nodeChildren = parentNode.children();
      while(nodeChildren.hasMoreElements()){
        DefaultMutableTreeNode nodeChild = (DefaultMutableTreeNode)nodeChildren.nextElement();
        syncHostFileTree(nodeChild);
      }    
    }catch(IOException ex){
      ex.printStackTrace();
    }    
  }
}
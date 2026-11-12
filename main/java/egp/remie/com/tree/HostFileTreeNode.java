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
    this.tree = tree;
    WorkSpaceNodeTree.addLoadingDummyTo(getTreeModel(), this);
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
    if(parentNode.getChildCount() > 0){
      //DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode)parentNode.getFirstChild();
      //if(!(firstChild.getUserObject() instanceof HostFile))
        //return;
    }
    HostFile parentNodeFile = (HostFile)parentNode.getUserObject();
    if(!HostFiles.isDirectory(parentNodeFile))
      return;
    try{   
      Map<Object, DefaultMutableTreeNode> extraObjectNodeMap = new HashMap<Object, DefaultMutableTreeNode>();      
      Collections.list(parentNode.children()).stream() //learning_note: returns Enumeration<TreeNode>
        .map(DefaultMutableTreeNode.class::cast)
        .forEach(node -> extraObjectNodeMap.put(node.getUserObject(), node));
      
      List<HostFile> nodeHostFiles = extraObjectNodeMap.keySet().stream()
        .filter(obj -> obj instanceof HostFile)
        .map(HostFile.class::cast)
        .collect(Collectors.toCollection(ArrayList::new));
        /*.stream()
        .map(c -> ((DefaultMutableTreeNode)c).getUserObject())
        .collect(Collectors.toCollection(ArrayList::new));*/ //learning_note: Collectors.toCollection(LinkedHashSet::new)   same as .toSet()      
      /*List<HostFile> nodeHFiles = nodeExtraHFiles.stream()
        .filter(obj -> obj instanceof HostFile)
        .map(HostFile.class::cast)
        .collect(Collectors.toList());*/
      Set<HostFile> fetchedHostFiles = HostFiles.list(parentNodeFile)
        .collect(Collectors.toCollection(HashSet::new));
      fetchedHostFiles.forEach((fl)->{
        if(nodeHostFiles.contains(fl)){ 
          extraObjectNodeMap.remove(nodeHostFiles.get(nodeHostFiles.indexOf(fl)));
          return;
        }
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(fl);
        if(HostFiles.isDirectory((HostFile)fl)){
          WorkSpaceNodeTree.addLoadingDummyTo(getTreeModel(), treeNode);
        }
        nodeHostFiles.add(fl);
        nodeHostFiles.sort(Comparator.comparing(HostFile::toString));
        int index = nodeHostFiles.indexOf(fl);
        getTreeModel().insertNodeInto(treeNode, parentNode, index);//learning_note: evoids reloaing all the parent tree
      });   
      //System.out.println(extraObjectNodeMap);
      extraObjectNodeMap.keySet()
        .forEach((obj)->{
          DefaultMutableTreeNode node = extraObjectNodeMap.get(obj);
          if(fetchedHostFiles.size() == 0){
            if(obj instanceof EmptyFolderName)
              return;
            node.setUserObject(new EmptyFolderName());
            getTreeModel().nodeChanged(node);
            return;
          }
          getTreeModel().removeNodeFromParent(node);
        });  
      if(parentNode.isLeaf()){
        //WorkSpaceNodeTree.addLoadingDummyTo(getTreeModel(), parentNode);
        WorkSpaceNodeTree.addEmptyFolderNameTo(getTreeModel(), parentNode);
        return;
      }    
      Enumeration<TreeNode> nodeChildren = parentNode.children();
      while(nodeChildren.hasMoreElements()){
        DefaultMutableTreeNode nodeChild = (DefaultMutableTreeNode)nodeChildren.nextElement();
        if(nodeChild.getUserObject() instanceof HostFile)
          syncHostFileTree(nodeChild);
      }    
    }catch(IOException ex){
      ex.printStackTrace();
    }    
  }
}
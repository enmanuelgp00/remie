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
          SwingUtilities.invokeLater(()->{
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(fl);            
            List<DefaultMutableTreeNode> currentNodes = Collections.list(parentNode.children()).stream()
              .map(DefaultMutableTreeNode.class::cast)
              .collect(Collectors.toList());
            currentNodes.add(node);
            currentNodes
              .sort(Comparator
                  .comparing(ob -> {
                    DefaultMutableTreeNode n = (DefaultMutableTreeNode)ob;
                    HostFile f = (HostFile)n.getUserObject();
                    try{
                      return !HostFiles.isDirectory(f);
                    }catch(InterruptedException ex){
                      ex.printStackTrace();
                      return false;
                    }
                  })
                  .thenComparing(ob -> ((DefaultMutableTreeNode)ob).getUserObject().toString()) //uses String.compareTo()
                  //.reversed()
              );              
            getTreeModel().insertNodeInto(node, parentNode, currentNodes.indexOf(node)); 
          });         
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
      
      /*
      Map<Object, DefaultMutableTreeNode> extraObjectNodeMap = new HashMap<Object, DefaultMutableTreeNode>();      
      Collections.list(parentNode.children()).stream() //learning_note: returns Enumeration<TreeNode>
        .map(DefaultMutableTreeNode.class::cast)
        .forEach(node -> extraObjectNodeMap.put(node.getUserObject(), node));
      
      List<HostFile> nodeHostFiles = extraObjectNodeMap.keySet().stream()
        .filter(obj -> obj instanceof HostFile)
        .map(HostFile.class::cast)
        .collect(Collectors.toCollection(ArrayList::new));
      Set<HostFile> fetchedHostFiles = HostFiles.list(parentNodeFile)
        .collect(Collectors.toCollection(HashSet::new));
      fetchedHostFiles.forEach((fl)->{
        System.out.println("fetched file: " + fl);
      });
      nodeHostFiles.forEach((fl)->{
        System.out.println("node file: " + fl);
      });
      fetchedHostFiles.forEach((fl)->{
        if(nodeHostFiles.contains(fl)){ 
          System.out.println("node contains: " + fl);
          for(Object k : new ArrayList<Object>(extraObjectNodeMap.keySet())){  
            if(k.equals(fl)){
              System.out.println("extra remove: " + k);
              extraObjectNodeMap.remove(k);
            }
          }
          return;
        }
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(fl);
        if(HostFiles.isDirectory((HostFile)fl)){
          WorkSpaceNodeTree.addLoadingNodeTo(getTreeModel(), treeNode);
        }
        nodeHostFiles.add(fl);
        nodeHostFiles.sort(Comparator.comparing(HostFile::toString));
        int index = nodeHostFiles.indexOf(fl);
        System.out.println("node inserting: " + fl);
        //SwingUtilities.invokeLater(()->{
          getTreeModel().insertNodeInto(treeNode, parentNode, index);//learning_note: evoids reloaing all the parent tree          
        //});
      });   
      extraObjectNodeMap.keySet()
        .forEach((obj)->{
          DefaultMutableTreeNode node = extraObjectNodeMap.get(obj);
          if(fetchedHostFiles.size() == 0){
            if(obj instanceof EmptyFolderMessage)
              return;
            node.setUserObject(new EmptyFolderMessage());
            getTreeModel().nodeChanged(node);
            return;
          }
          System.out.println("extra file: " + node.getUserObject());
          SwingUtilities.invokeLater(()->{
            getTreeModel().removeNodeFromParent(node);
          });
        });  
        */
        
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
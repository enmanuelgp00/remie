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

public class HostFileTree extends CustomeTree {
  
  private HostFile hostFileRoot;
  private DefaultTreeModel treeModel;
  private boolean hostFileSyncing = false;
  private int hostFileSyncInterval = 1000;
  private FolderNodePopupMenu folderNodePopupMenu = new FolderNodePopupMenu();
  private FileNodePopupMenu fileNodePopupMenu = new FileNodePopupMenu();
  
  public HostFileTree(HostFile hostFileRoot){
    this.hostFileRoot = hostFileRoot;
    addTreeWillExpandListener(lazyTreeNodeExpansion);
    addTreeExpansionListener(expandSingleChildFolderTreeNode);
    addMouseListener(nodePopupMenuOnRightClick);
    setHostFileSyncInterval(1000);
    setHostFileSyncEnabled(true);
    createDefaultTreeModel(hostFileRoot);
  }
  
  private MouseAdapter nodePopupMenuOnRightClick = new MouseAdapter(){
    @Override
    public void mouseClicked(MouseEvent e){
      if(!SwingUtilities.isRightMouseButton(e))
        return;
      TreePath treePath = getPathForLocation(e.getX(), e.getY());      
      if(treePath == null)
        return;
      DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
      HostFile fl = (HostFile)treeNode.getUserObject();
      if(HostFiles.isDirectory(fl)){
        folderNodePopupMenu.setTargetNode(treeNode);
        folderNodePopupMenu.show(HostFileTree.this, e.getX(), e.getY());
        return;
      }
      fileNodePopupMenu.setTargetNode(treeNode);
      fileNodePopupMenu.show(HostFileTree.this, e.getX(), e.getY());
    }
  };
  
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
    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)treeModel.getRoot();
    syncHostFileTree(rootNode);
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
          addLoadingDummy(treeNode);
        nodeHFiles.add(fl);
        nodeHFiles.sort(Comparator.comparing(HostFile::toString));
        int index = nodeHFiles.indexOf(fl);
        treeModel.insertNodeInto(treeNode, parentNode, index);//learning_note: evoids reloaing all the parent tree
      });   
      nodeExtraHFiles.forEach((fl)->{
        int i = 0;        
        while (i < parentNode.getChildCount()){
          DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)parentNode.getChildAt(i++);
          HostFile childHFile = (HostFile)childNode.getUserObject();
          if(childHFile.equals(fl))
            treeModel.removeNodeFromParent(childNode);
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
  
  private void createDefaultTreeModel(HostFile hostFileRoot){    
    DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(hostFileRoot);
    addLoadingDummy(treeNode);
    treeModel =  new DefaultTreeModel(treeNode);    
    setModel(treeModel); 
    collapsePath(new TreePath(treeModel.getRoot()));  
    expandPath(new TreePath(treeModel.getRoot()));
  }
  
  public TreeExpansionListener expandSingleChildFolderTreeNode = new TreeExpansionListener(){
    @Override
    public void treeExpanded(TreeExpansionEvent e){
      DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
      if(parentNode.getChildCount() > 1)
      return;
      DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode)parentNode.getFirstChild();
      HostFile fl = (HostFile)firstChild.getUserObject();
      if(HostFiles.isDirectory(fl)){
        TreePath childPath = new TreePath(firstChild.getPath());
        expandPath(childPath); 
      }
    }
    @Override
    public void treeCollapsed(TreeExpansionEvent e){ }
  };
  
  public TreeWillExpandListener lazyTreeNodeExpansion = new TreeWillExpandListener(){
    @Override
    public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
      TreePath treePath = e.getPath();
      DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
      expandTreeNode(parentNode, e);
    }
    @Override
    public void treeWillCollapse(TreeExpansionEvent e){}
  };  
  
  private void expandTreeNode(DefaultMutableTreeNode parentNode, TreeExpansionEvent event) throws ExpandVetoException {
    final DefaultMutableTreeNode firstChildNode = (DefaultMutableTreeNode)parentNode.getFirstChild();
      Object obj = firstChildNode.getUserObject();
      if(!(obj instanceof DummyNode))
        return;
      DummyNode dummyNode = (DummyNode)obj;
      startLoadingAnimation(firstChildNode);
      HostFile parentFile = (HostFile)parentNode.getUserObject();
      List<HostFile> hfiles = new ArrayList<HostFile>();
      try{          
        hfiles = HostFiles.list(parentFile)
          .collect(Collectors.toList());
      }catch(IOException ex){
        dummyNode.setLoading(false); 
        throw new ExpandVetoException(event);      
      }
      if(hfiles.size() == 0){ 
        dummyNode.setLoading(false); 
        throw new ExpandVetoException(event);        
      }
      parentNode.remove(firstChildNode);
      hfiles.forEach((fl)->{
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(fl);
        if(HostFiles.isDirectory(fl))
          addLoadingDummy(childNode);
        parentNode.add(childNode);
      });    
      dummyNode.setLoading(false);    
      treeModel.nodeStructureChanged(parentNode); 
  }
  
  public void addLoadingDummy(DefaultMutableTreeNode treeNode){
    DefaultMutableTreeNode child = new DefaultMutableTreeNode(new DummyNode());
    treeNode.add(child);    
  }
  
  public void startLoadingAnimation(DefaultMutableTreeNode treeNode){
    Thread animation = new Thread(()->{
      Object obj;
      if(!((obj = treeNode.getUserObject()) instanceof DummyNode))
        return;
      DummyNode dummy = (DummyNode)obj;
      if(dummy.isRunning())
        return;
      dummy.setIsRunning(true);
      while(dummy.isLoading()){
        System.out.println("dummy animetion running");
        try{
          Thread.sleep(300);
          dummy.nextSprite();
          treeModel.nodeChanged(treeNode);        
        }catch(Exception ex){ex.printStackTrace();}
      };
      dummy.setIsRunning(false);
    });    
    animation.start();
  }
}
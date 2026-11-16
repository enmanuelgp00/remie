package egp.remie.cli.com.tree.popup;

import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import egp.remie.cli.com.tree.*;
import egp.remie.cli.com.*;
import egp.remie.cli.custome.*;
import java.util.List;
import java.util.stream.*;
import java.util.*;

public class FolderNodePopupMenu extends JPopupMenu{
  private final String EXPAND_NAME = "Expand";
  private final String COLLAPSE_NAME = "Collapse";
  private DefaultMutableTreeNode targetNode;
  private final WorkSpaceNodeTree tree;
  private JMenuItem renameItem = new JMenuItem("Rename");
  private JMenuItem removeItem = new JMenuItem("Remove");
  private JMenuItem expandCollapseItem = new JMenuItem("Unknown Expand State");
  
  private JMenu newFileMenu = new JMenu("New");
  private JMenuItem newFileItem = new JMenuItem("File");
  private JMenuItem newFolderItem = new JMenuItem("Folder");
  private JMenuItem deleteItem = new JMenuItem("Delete");
  
  public FolderNodePopupMenu(WorkSpaceNodeTree tree){
    this.tree = tree;
    tree.getModel().addTreeModelListener(renameIfChildTarget);
    configureItems();
    addItems();
  }
  
  private void addItems(){
    add(renameItem);
    add(expandCollapseItem);
    add(removeItem);
    addSeparator();
    newFileMenu.add(newFileItem);
    newFileMenu.add(newFolderItem);
    add(newFileMenu);
    addSeparator();
    add(deleteItem);
  }
  public void configureItems(){
    renameItem.addActionListener(triggerRenameNode);
    removeItem.addActionListener(removeTreeNode);
    expandCollapseItem.addActionListener(expandOrCollapse);
    newFileItem.addActionListener(createNewFile);
    newFolderItem.addActionListener(createNewFolder);
    deleteItem.addActionListener(deleteFolder);
  }
  
  private ActionListener triggerRenameNode = (e)->{
  	triggerRenameAction(targetNode);
  };
  
  private TreeModelListener renameIfChildTarget = new TreeModelListener(){
    @Override
    public void treeNodesInserted(TreeModelEvent e){
      DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)e.getTreePath().getLastPathComponent();
      if(parentNode == targetNode){
        int childIndex = e.getChildIndices()[0];
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)parentNode.getChildAt(childIndex);
        triggerRenameAction(treeNode);
      }
    }
    @Override
    public void treeNodesChanged(TreeModelEvent e){
      setTargetNode(null);
      DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)e.getTreePath().getLastPathComponent();
      int childIndex = e.getChildIndices()[0];
      int childCount = parentNode.getChildCount();
      if(childCount == 1)
        return;
      DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)parentNode.getChildAt(childIndex);
      List<DefaultMutableTreeNode> sortedChildren = Collections.list(parentNode.children()).stream()
        .map(DefaultMutableTreeNode.class::cast)
        .collect(Collectors.toList());
      sortedChildren.sort(Comparator.comparing(DefaultMutableTreeNode::toString));
        
      DefaultTreeModel model = (DefaultTreeModel)tree.getModel();      
      SwingUtilities.invokeLater(()->{
        model.removeNodeFromParent(childNode);
        /*int index = sortedChildren.indexOf(childNode);
        if(index > childCount)
          index = childCount - 1;
        model.insertNodeInto(parentNode, childNode, index);*/
      });
    }
    @Override
    public void treeStructureChanged(TreeModelEvent e){}
    @Override
    public void treeNodesRemoved(TreeModelEvent e){}
  };
  
  private void triggerRenameAction(DefaultMutableTreeNode node){
    TreePath path = new TreePath(node.getPath());
    JTree tree = getTree();
    //tree.setSelectionPath(path);
    SwingUtilities.invokeLater(()->{
      tree.startEditingAtPath(path);
    });
  }
  
  private ActionListener removeTreeNode = (e)->{
    ((DefaultTreeModel)getTree().getModel()).removeNodeFromParent(getTargetNode());
    setTargetNode(null);
  };
  
  public WorkSpaceNodeTree getTree(){
    return tree;
  }
  
  private ActionListener deleteFolder = (e)->{
    try{
      HostFile hostFile = (HostFile)targetNode.getUserObject();
      HostFiles.delete(hostFile);
    }catch(IOException | InterruptedException ex){
      ex.printStackTrace();
    }
    setTargetNode(null);
  };
  
   private ActionListener createNewFolder = (e)->{
    try{
      HostFile parentHostFile = (HostFile)targetNode.getUserObject();
      Path filePath = parentHostFile.getPath().resolve("New Folder");
      HostFile dir = new HostFile(parentHostFile.getHost(), filePath);
      HostFiles.createDirectory(dir);
      /*if()
      triggerRenameAction(parentPath);*/
    }catch(IOException | InterruptedException ex){
      ex.printStackTrace();
    }
  };
  
  private ActionListener createNewFile = (e)->{
    try{
      HostFile parentHostFile = (HostFile)targetNode.getUserObject();
      Path filePath = parentHostFile.getPath().resolve("New File");
      HostFiles.createFile(new HostFile(parentHostFile.getHost(), filePath));
      //triggerRenameAction();
    }catch(IOException | InterruptedException ex){
      ex.printStackTrace();
    }
  };
  
  private ActionListener expandOrCollapse = new ActionListener(){
    @Override
    public void actionPerformed(ActionEvent e){
      TreePath treePath = new TreePath(targetNode.getPath());
      if(expandCollapseItem.getText().equals(EXPAND_NAME)){
        tree.expandPath(treePath);
        return;
      }
      tree.collapsePath(treePath);
    }
  };
  
  
  @Override
  public void show(Component com0, int x, int y){
    TreePath path = tree.getPathForLocation(x, y);
    setTargetNode((DefaultMutableTreeNode)path.getLastPathComponent());
    if(tree.isExpanded(new TreePath(targetNode.getPath()))){
      expandCollapseItem.setText(COLLAPSE_NAME);
    } else {
      expandCollapseItem.setText(EXPAND_NAME);
    }
    
    if(getTargetNode() instanceof HostFileTreeNode){
      removeItem.setVisible(true);
    } else {
      removeItem.setVisible(false);
    }
    super.show(com0, x, y);
  }
  
  public DefaultMutableTreeNode getTargetNode(){
    return targetNode;
  }
  
  private void setTargetNode(DefaultMutableTreeNode targetNode){
    this.targetNode = targetNode;
  }
}
package egp.remie.com.tree.popup;

import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import egp.remie.com.tree.*;
import egp.remie.com.*;
import egp.remie.custome.*;

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
  	triggerRenameAction(new TreePath(targetNode.getPath()));
  };
  
  private void triggerRenameAction(TreePath treePath){
    getTree().startEditingAtPath(treePath);
  }
  
  private ActionListener removeTreeNode = (e)->{
    ((DefaultTreeModel)getTree().getModel()).removeNodeFromParent(getTargetNode());
  };
  
  public WorkSpaceNodeTree getTree(){
    return tree;
  }
  
  private ActionListener deleteFolder = (e)->{
    try{
      HostFile hostFile = (HostFile)targetNode.getUserObject();
      HostFiles.delete(hostFile);
    }catch(IOException ex){
      ex.printStackTrace();
    }
  };
  
   private ActionListener createNewFolder = (e)->{
    try{
      HostFile parentHostFile = (HostFile)targetNode.getUserObject();
      Path filePath = parentHostFile.getPath().resolve("New Folder");
      HostFile dir = new HostFile(filePath);
      HostFiles.createDirectory(dir);
      /*if()
      triggerRenameAction(parentPath);*/
    }catch(IOException ex){
      ex.printStackTrace();
    }
  };
  
  private ActionListener createNewFile = (e)->{
    try{
      HostFile parentHostFile = (HostFile)targetNode.getUserObject();
      Path filePath = parentHostFile.getPath().resolve("New File");
      HostFiles.createFile(new HostFile(filePath));
      //triggerRenameAction();
    }catch(IOException ex){
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
  
  public void setTargetNode(DefaultMutableTreeNode targetNode){
    this.targetNode = targetNode;
  }
}
package egp.remie.com.tree;

import egp.remie.com.*;
import egp.remie.com.editor.*;
import egp.remie.com.tree.name.*;
import egp.remie.com.tree.popup.*;
import egp.remie.custome.*;
import java.io.*;
import java.util.List;
import javax.swing.tree.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.stream.*;

public class WorkSpaceNodeTree extends CustomeTree {

  private TreeNodeHostFileTextEditorTabbedPane editorTabbedPane;
  private final DefaultTreeModel treeModel;
  private final DefaultMutableTreeNode rootTreeNode; 
  private final FolderNodePopupMenu folderNodePopupMenu;
  private final FileNodePopupMenu fileNodePopupMenu;
  private final TreeCellEditor cellEditor;
  private final WorkSpacePopupMenu workSpacePopupMenu;
  private final DefaultTreeCellRenderer treeCellRenderer;
  
  public WorkSpaceNodeTree(String workSpaceName, TreeNodeHostFileTextEditorTabbedPane editorTabbedPane){
    this.editorTabbedPane = editorTabbedPane;
    this.rootTreeNode = new DefaultMutableTreeNode(new WorkSpaceName(workSpaceName));
    this.treeModel = new DefaultTreeModel(rootTreeNode);
    folderNodePopupMenu = new FolderNodePopupMenu(this);
    fileNodePopupMenu = new FileNodePopupMenu(this);
    workSpacePopupMenu = new WorkSpacePopupMenu(this);
    
    setModel(treeModel);
    addTreeWillExpandListener(lazyTreeNodeExpansion);
    addTreeExpansionListener(autoExpandSingleChildFolderTreeNode); 
    addMouseListener(nodePopupMenuOnRightClick);  
    addMouseListener(addTextEditorTabOnDoubleClick);
    setPreferredSize(new Dimension(120, 0));
    setEditable(true);
    treeCellRenderer = createTreeCellRenderer();
    setCellRenderer(treeCellRenderer);
    cellEditor = (TreeCellEditor)getCellEditor();
    cellEditor.addCellEditorListener(renameHostFileOnChange);
    setCellEditor(cellEditor);
  }  
  
  private DefaultTreeCellRenderer createTreeCellRenderer(){
    return new DefaultTreeCellRenderer(){
      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object userObj, boolean isSelected, boolean isExpanded, boolean isLeaf, int row, boolean hasFocus){
        JLabel label = (JLabel)super.getTreeCellRendererComponent(tree, userObj, isSelected, isExpanded, isLeaf, row, hasFocus);        
        if(row == 0){
          label.setIcon(UIManager.getIcon("FileView.computerIcon"));
          return label;
        }
        TreePath treePath = tree.getPathForRow(row);
        if(treePath == null)
          return label;
                  
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
        if(treeNode instanceof HostFileTreeNode){
          label.setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
          return label;
        }  
        
        if(treeNode.getUserObject() instanceof EmptyFolderMessage){
          label.setIcon(null);
          label.setFont(label.getFont().deriveFont(Font.ITALIC));
          label.setForeground(Color.GRAY);
        } else {
          label.setFont(label.getFont().deriveFont(Font.PLAIN));          
        }
        return label;
      }
    };
  }
  
  private DefaultTreeCellEditor createCellEditor(){
    return new DefaultTreeCellEditor(this, (DefaultTreeCellRenderer)getCellRenderer()){
      @Override
      public boolean stopCellEditing(){
        System.out.println("stop cell editing");
        return true;
      }
    };
  }
  
  private CellEditorListener renameHostFileOnChange = new CellEditorListener(){
    @Override
    public void editingStopped(ChangeEvent e){
      String name = (String)cellEditor.getCellEditorValue();
      TreePath selectedPath = getSelectionPath(); //learning_note: can use tree.getLastSelectedPathComponent();
      if(selectedPath == null)
        return;
      DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
      Object obj = treeNode.getUserObject();
      if(!(obj instanceof HostFile))
        return;
      HostFile file = (HostFile)obj;
      try{
        HostFiles.rename(file, name); 
        treeModel.nodeChanged(treeNode);
        TreeNodeHostFileTextEditor textEditor = editorTabbedPane.getTextEditorByNode(treeNode);
        if(textEditor != null)
          textEditor.updateTitle();
      }catch(IOException ex){
        ex.printStackTrace();
      }
      cellEditor.cancelCellEditing(); // had to use it, other ways it would summit the String content to the tree node user object
    }
    @Override
    public void editingCanceled(ChangeEvent e){
      
    }
  };
  
  public void setTextEditorTabbedPane(TreeNodeHostFileTextEditorTabbedPane editorTabbedPane){
    this.editorTabbedPane = editorTabbedPane;
  }
  
  public TreeNodeHostFileTextEditorTabbedPane getTextEditorTabbedPane(){
    return editorTabbedPane;
  }
  
  public DefaultMutableTreeNode getRootTreeNode(){
    return rootTreeNode;
  }
  
  public void addHostFileTreeNode(HostFileTreeNode treeNode){
    treeModel.insertNodeInto(treeNode, rootTreeNode, rootTreeNode.getChildCount());
  }
  
  private MouseAdapter addTextEditorTabOnDoubleClick = new MouseAdapter(){
    @Override
    public void mouseClicked(MouseEvent e){
      if(e.getClickCount() < 2 || !SwingUtilities.isLeftMouseButton(e))
        return; 
      TreePath treePath = getPathForLocation(e.getX(), e.getY());
      if(treePath == null)
        return;
      DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
      Object obj = treeNode.getUserObject();
      if(!(obj instanceof HostFile))
        return;        
      if(HostFiles.isDirectory((HostFile)obj))
        return;
      editorTabbedPane.addTextEditorTabFrom(treeNode);        
    }
  };
  
  private MouseAdapter nodePopupMenuOnRightClick = new MouseAdapter(){
    @Override
    public void mouseClicked(MouseEvent e){
      if(!SwingUtilities.isRightMouseButton(e))
        return;
      TreePath treePath = getPathForLocation(e.getX(), e.getY());      
      if(treePath == null)
        return;
      DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
      Object obj = treeNode.getUserObject();
      if(obj instanceof WorkSpaceName){
        workSpacePopupMenu.show(WorkSpaceNodeTree.this, e.getX(), e.getY());
        return;
      }
      if(!(obj instanceof HostFile))
        return;      
      HostFile fl = (HostFile)obj;
      if(HostFiles.isDirectory(fl)){
        folderNodePopupMenu.setTargetNode(treeNode);
        folderNodePopupMenu.show(WorkSpaceNodeTree.this, e.getX(), e.getY());
        return;
      }
      fileNodePopupMenu.setTargetNode(treeNode);
      fileNodePopupMenu.show(WorkSpaceNodeTree.this, e.getX(), e.getY());
    }
  };
   
  public TreeWillExpandListener lazyTreeNodeExpansion = new TreeWillExpandListener(){
    @Override
    public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
      TreePath treePath = e.getPath();
      DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
      hostFilesFetchPathExpansion(parentNode, e);
    }
    @Override
    public void treeWillCollapse(TreeExpansionEvent e){}
  };  
  
  public TreeExpansionListener autoExpandSingleChildFolderTreeNode = new TreeExpansionListener(){
    @Override
    public void treeExpanded(TreeExpansionEvent e){
      DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
      if(parentNode.getChildCount() > 1)
        return;
      DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode)parentNode.getFirstChild();
      Object obj = firstChild.getUserObject();
      if(!(obj instanceof HostFile))
        return;
      if(HostFiles.isDirectory((HostFile)obj)){
        TreePath childPath = new TreePath(firstChild.getPath());
        expandPath(childPath); 
      }
    }
    @Override
    public void treeCollapsed(TreeExpansionEvent e){ }
  };  
  
  private void hostFilesFetchPathExpansion(DefaultMutableTreeNode parentNode, TreeExpansionEvent event) throws ExpandVetoException {
    final DefaultMutableTreeNode firstChildNode = (DefaultMutableTreeNode)parentNode.getFirstChild();
    Object obj = firstChildNode.getUserObject();
    if(!(obj instanceof LoadingNodeMessage))
      return;    
    LoadingNodeMessage loadingMessage = (LoadingNodeMessage)obj;
    loadingMessage.startAnimation();
    HostFile parentFile = (HostFile)parentNode.getUserObject();
    List<HostFile> hfiles = new ArrayList<HostFile>();
    try{          
      hfiles = HostFiles.list(parentFile)
        .collect(Collectors.toList());
    }catch(IOException ex){
      loadingMessage.stopAnimation(); 
      throw new ExpandVetoException(event);      
    }
    if(hfiles.size() == 0){ 
      loadingMessage.stopAnimation();
      firstChildNode.setUserObject(new EmptyFolderMessage());
      treeModel.nodeChanged(firstChildNode);
      return;
      //throw new ExpandVetoException(event);        
    }
    parentNode.remove(firstChildNode);
    hfiles.forEach((fl)->{
      DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(fl);
      if(HostFiles.isDirectory(fl))
        addLoadingNodeTo(treeModel, childNode);
      parentNode.add(childNode);
    });    
    loadingMessage.stopAnimation();    
    treeModel.nodeStructureChanged(parentNode); 
  }
  static void addLoadingNodeTo(DefaultTreeModel treeModel, DefaultMutableTreeNode parentNode){
    DefaultMutableTreeNode loadingNode = new DefaultMutableTreeNode();
    LoadingNodeMessage loadingNodeMessage = new LoadingNodeMessage(treeModel, loadingNode);
    loadingNode.setUserObject(loadingNodeMessage);
    treeModel.insertNodeInto(loadingNode, parentNode, 0);
  }
  static void addEmptyFolderMessageTo(DefaultTreeModel treeModel, DefaultMutableTreeNode parentNode){
    treeModel.insertNodeInto(new DefaultMutableTreeNode(new EmptyFolderMessage()), parentNode, 0);
  }
}
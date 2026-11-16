package egp.remie.cli.com.tree;

import egp.remie.cli.com.*;
import egp.remie.cli.com.editor.*;
import egp.remie.cli.com.tree.name.*;
import egp.remie.cli.com.tree.popup.*;
import egp.remie.cli.custome.*;
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
  private final List<TreePath> openingPaths = new ArrayList<TreePath>();
  
  public WorkSpaceNodeTree(String workSpaceName, TreeNodeHostFileTextEditorTabbedPane editorTabbedPane){
    this.editorTabbedPane = editorTabbedPane;
    this.rootTreeNode = new DefaultMutableTreeNode(new WorkSpaceName(workSpaceName));
    this.treeModel = new DefaultTreeModel(rootTreeNode);
    setModel(treeModel);
    folderNodePopupMenu = new FolderNodePopupMenu(this);
    fileNodePopupMenu = new FileNodePopupMenu(this);
    workSpacePopupMenu = new WorkSpacePopupMenu(this);
    setBackground(new Color(34, 34, 34));
    
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
    addMouseMotionListener(moveNodes);
    addMouseListener(moveNodes);
  }  
  
  private MouseAdapter moveNodes = new MouseAdapter(){
    DefaultMutableTreeNode originNode = null; 
    DefaultMutableTreeNode targetParent = null;
    
    @Override
    public void mousePressed(MouseEvent e){
      TreePath path = getPathForLocation(e.getX(), e.getY());
      if(path == null){
        originNode = null;
        return;
      }
      originNode = (DefaultMutableTreeNode)path.getLastPathComponent();
      Object obj = originNode.getUserObject();
      if(!(obj instanceof HostFile)){
        originNode = null;
        return;
      }
    }
    @Override
    public void mouseDragged(MouseEvent e){
      
    }
    @Override
    public void mouseReleased(MouseEvent e){
      TreePath path = getPathForLocation(e.getX(), e.getY());
      if(path == null || originNode == null){
        targetParent = null;
        return;
      }
      targetParent = (DefaultMutableTreeNode)path.getLastPathComponent();
      Object obj = targetParent.getUserObject();
      if(originNode == targetParent){
        originNode = targetParent = null;
        return;
      }
      if(!(obj instanceof HostFile))
        return;
      HostFile parentFl = (HostFile)obj;
      if(!HostFiles.isDirectory(parentFl))
        return;      
      HostFile originFl = (HostFile)originNode.getUserObject();
      HostFile targetFl = new HostFile(
        parentFl.getHost(),
        parentFl.getPath().resolve(originFl.getPath().getFileName()));
      try{
        HostFiles.move(originFl, targetFl);
        DefaultMutableTreeNode originParent = (DefaultMutableTreeNode)originNode.getParent();
        if(originParent.getChildCount() == 1){
          originNode.setUserObject(new EmptyFolderMessage());
          treeModel.nodeChanged(originNode);
        } else {
        treeModel.removeNodeFromParent(originNode);  
        }
      }catch(IOException ex){
        ex.printStackTrace();
      }
    }
  };
  
  private DefaultTreeCellRenderer createTreeCellRenderer(){
    return new DefaultTreeCellRenderer(){
      {
        setTextNonSelectionColor(Color.WHITE);
        setBackgroundNonSelectionColor(new Color(34, 34, 34));
        //setFont(new Font("arial", Font.PLAIN, 12));
      }
      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object userObj, boolean isSelected, boolean isExpanded, boolean isLeaf, int row, boolean hasFocus){
        JLabel label = (JLabel)super.getTreeCellRendererComponent(tree, userObj, isSelected, isExpanded, isLeaf, row, hasFocus);                
        Font labelFont = label.getFont();
        
        if(labelFont.getStyle() != Font.PLAIN)
          label.setFont(labelFont.deriveFont(Font.PLAIN));    
        if(!selected){            
          if(isLeaf)
            label.setForeground(Color.WHITE);
          else
            label.setForeground(new Color(200, 200, 229));
        }
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
        Object obj = treeNode.getUserObject();
        if(obj instanceof NodeName){
          label.setIcon(null);
          label.setFont(label.getFont().deriveFont(Font.ITALIC));
          if(obj instanceof ErrorMessage)
            label.setForeground(Color.RED);
          else 
            label.setForeground(Color.GRAY);
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
      }catch(IOException | InterruptedException ex){
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
      hostFilesFetchPathExpansion(treePath);
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
    public void treeCollapsed(TreeExpansionEvent e){
      DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
      if(parentNode.getChildCount() > 1)
        return;
      DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode)parentNode.getFirstChild();
      Object obj = firstChild.getUserObject();
      if(obj instanceof LoadingNodeMessage){
        LoadingNodeMessage message = (LoadingNodeMessage)obj;
        message.stopAnimation();
      }
    }
  };  
  
  private void hostFilesFetchPathExpansion(TreePath treePath) {
    if(openingPaths.contains(treePath)){
      return;
    }
    openingPaths.add(treePath);
    Thread th = new Thread(()->{      
      DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
      final DefaultMutableTreeNode firstChildNode = (DefaultMutableTreeNode)parentNode.getFirstChild();
      Object obj = firstChildNode.getUserObject();
      if(!(obj instanceof LoadingNodeMessage)){
        return;    
      }
      LoadingNodeMessage loadingMessage = (LoadingNodeMessage)obj;
      loadingMessage.startAnimation();
      HostFile parentFile = (HostFile)parentNode.getUserObject();
      List<HostFile> hfiles = new ArrayList<HostFile>();
      try{          
        hfiles = HostFiles.list(parentFile)
          .collect(Collectors.toList());
      }catch(IOException | InterruptedException ex){
        loadingMessage.stopAnimation();
        firstChildNode.setUserObject(new ErrorMessage(ex.getClass().getName()));
        
        treeModel.nodeChanged(firstChildNode);
        return;     
      }
      if(hfiles.size() == 0){ 
        loadingMessage.stopAnimation();
        firstChildNode.setUserObject(new EmptyFolderMessage());
        treeModel.nodeChanged(firstChildNode);
        return;      
      }
      parentNode.remove(firstChildNode);
      hfiles.forEach((fl)->{
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(fl);
        if(HostFiles.isDirectory(fl))
          addLoadingNodeTo(treeModel, childNode);
        parentNode.add(childNode);
      });    
      loadingMessage.stopAnimation(); 
      SwingUtilities.invokeLater(()->{
        treeModel.nodeStructureChanged(parentNode); 
      });
    });
    th.start();
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
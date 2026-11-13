package egp.remie.cli.com.frame;

import egp.remie.cli.custome.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import egp.remie.cli.com.tree.*;
import egp.remie.cli.com.editor.*;

public class MainMenuBar extends JMenuBar {
  private WorkSpaceNodeTree tree;
  private TreeNodeHostFileTextEditorTabbedPane editorTabbedPane;    
  private StyleFormFrame selectFontFrame;
  
  private JMenu fileColumn = new JMenu("File");
  private JMenuItem newItem = new JMenuItem("New");
  private JMenuItem openItem = new JMenuItem("Open...");
  private JMenuItem saveItem = new JMenuItem("Save");
  private JMenuItem saveAsItem = new JMenuItem("Save As...");
  private JMenuItem exitItem = new JMenuItem("Exit");
  
  private JMenu editColumn = new JMenu("Edit");
  private JMenuItem undoItem = new JMenuItem("Undo");
  private JMenuItem cutItem = new JMenuItem("Cut");
  private JMenuItem copyItem = new JMenuItem("Copy");
  private JMenuItem paseItem = new JMenuItem("Paste");
  private JMenuItem deleteItem = new JMenuItem("Delete");
  private JMenuItem selectAllItem = new JMenuItem("Select All");
  
  private JMenu settingColumn = new JMenu("Settings");
  private JMenuItem styleItem = new JMenuItem("Style");
  
  private JMenu searchColumn = new JMenu("Search");
  
  public MainMenuBar(WorkSpaceNodeTree tree, TreeNodeHostFileTextEditorTabbedPane editorTabbedPane){
    selectFontFrame = new StyleFormFrame(editorTabbedPane); 
    selectFontFrame.setLocationRelativeTo(this);
    this.tree = tree;
    this.editorTabbedPane = editorTabbedPane;
    configColumns();
    addItems();
  }
  
  private void configColumns(){
    configFormatColumn();
    configEditColumn();
    configFileColumn();
  }
  
  private void addItems(){
    add(fileColumn);
    add(editColumn);
    add(searchColumn);
    add(settingColumn);
  }
  
  private void configFormatColumn(){
    styleItem.addActionListener((e)->{
      selectFontFrame.setVisible(true);
    });
    settingColumn.add(styleItem);
  }
  
  private void configEditColumn(){
    editColumn.add(undoItem);
    editColumn.addSeparator();
    editColumn.add(cutItem);
    editColumn.add(copyItem);
    editColumn.add(paseItem);
    editColumn.addSeparator();
    editColumn.add(deleteItem);
    editColumn.addSeparator();
    editColumn.add(selectAllItem);
  }
  
  private void configFileColumn(){  
    addFileColumnItemsListeners();    
    fileColumn.add(newItem); 
    fileColumn.add(openItem); 
    fileColumn.add(saveItem); 
    fileColumn.add(saveAsItem); 
    fileColumn.addSeparator();
    exitItem.addActionListener((e)->System.exit(0));
    fileColumn.add(exitItem);
  }
  private void addFileColumnItemsListeners(){
    saveItem.addActionListener(e->{
      TreeNodeHostFileTextEditor editor = (TreeNodeHostFileTextEditor)editorTabbedPane.getSelectedComponent();
      if(editor.isEditing())
        editor.commit();
        editor.updateTitle();
    });
  }
}
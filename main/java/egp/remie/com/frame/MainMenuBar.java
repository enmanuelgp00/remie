package egp.remie.com.frame;

import egp.remie.custome.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import egp.remie.com.tree.*;
import egp.remie.com.editor.*;

public class MainMenuBar extends JMenuBar {
  private WorkSpaceNodeTree tree;
  private TreeNodeHostFileTextEditorTabbedPane editorTabbedPane;    
  private SelectFontFrame selectFontFrame;
  
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
  
  private JMenu formatColumn = new JMenu("Format");
  private JCheckBox wordWrapItem = new JCheckBox("Word Wrap");
  private JMenuItem fontItem = new JMenuItem("Font...");
  
  public MainMenuBar(WorkSpaceNodeTree tree, TreeNodeHostFileTextEditorTabbedPane editorTabbedPane){
    selectFontFrame = new SelectFontFrame(editorTabbedPane); 
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
    add(formatColumn);
  }
  
  private void configFormatColumn(){
    wordWrapItem.setHorizontalTextPosition(SwingConstants.LEADING);
    formatColumn.add(wordWrapItem);
    fontItem.addActionListener((e)->{
      selectFontFrame.setVisible(true);
    });
    formatColumn.add(fontItem);
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
    fileColumn.add(newItem); 
    fileColumn.add(openItem); 
    fileColumn.add(saveItem); 
    fileColumn.add(saveAsItem); 
    fileColumn.addSeparator();
    exitItem.addActionListener((e)->System.exit(0));
    fileColumn.add(exitItem);
  }
}
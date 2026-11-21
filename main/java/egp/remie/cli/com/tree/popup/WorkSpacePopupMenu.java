package egp.remie.cli.com.tree.popup;

import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.tree.*;
import java.awt.event.*;
import egp.remie.host.file.*;
import egp.remie.cli.com.tree.*;
import egp.remie.cli.com.frame.*;
import egp.remie.cli.custome.*;

public class WorkSpacePopupMenu extends JPopupMenu {
  private final WorkSpaceNodeTree tree;
  private final JMenuItem workSpaceCreatorItem = new JMenuItem("Add Work Space");
  
  public WorkSpacePopupMenu(WorkSpaceNodeTree tree){
    this.tree = tree;
    workSpaceCreatorItem.addActionListener((e)->{
      tree.displayWorkSpaceForm();
    });
    add(workSpaceCreatorItem);
  }
  
}
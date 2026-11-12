package egp.remie.com.tree;

import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.tree.*;
import java.awt.event.*;
import egp.remie.com.*;
import egp.remie.com.frame.*;
import egp.remie.custome.*;

class WorkSpacePopupMenu extends JPopupMenu {
  private final WorkSpaceNodeTree tree;
  private WorkSpaceFormFrame formFrame;
  private final JMenuItem workSpaceCreatorItem = new JMenuItem("Add Work Space");
  
  WorkSpacePopupMenu(WorkSpaceNodeTree tree){
    this.tree = tree;
    formFrame = new WorkSpaceFormFrame(tree);
    workSpaceCreatorItem.addActionListener((e)->{
      formFrame.setVisible(true);
    });
    add(workSpaceCreatorItem);
  }
  
}
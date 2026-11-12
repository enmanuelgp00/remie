package egp.remie.com.tree;

import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.tree.*;
import java.awt.event.*;
import egp.remie.com.*;
import egp.remie.custome.*;

class WorkSpacePopupMenu extends JPopupMenu {
  private final WorkSpaceNodeTree tree;
  private final JMenuItem newWorkSpaceItem = new JMenuItem("New Work Space");
  
  WorkSpacePopupMenu(WorkSpaceNodeTree tree){
    this.tree = tree;
    add(newWorkSpaceItem);
  }
}
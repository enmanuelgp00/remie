package egp.remie.com.tree;

import egp.remie.com.*;
import javax.swing.tree.*;
import javax.swing.*;
import java.awt.*;
import egp.remie.custome.*;

public class WorkSpaceTreePane extends CustomeScrollPane {
  
  private final Box workSpaceTreeBox = Box.createVerticalBox();
  
  public WorkSpaceTreePane(){
    super();
    workSpaceTreeBox.setBackground(Color.WHITE);
    workSpaceTreeBox.setOpaque(true);
    setViewportView(workSpaceTreeBox);
  }
  
  public void addTree(HostFileTreeNode tree){
    tree.setAlignmentX(0.0f);
    workSpaceTreeBox.add(tree);
  }
  
}
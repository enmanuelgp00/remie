package egp.remie.com.frame;

import egp.remie.custome.*;
import egp.remie.user.*;
import egp.remie.com.*;
import egp.remie.com.tree.*;
import egp.remie.com.editor.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;

public class WorkSpaceFormFrame extends CustomeFrame {
  private final WorkSpaceNodeTree tree;
  private final JLabel pathLabel = new JLabel("Local path or Host URI");
  private final CustomeTextField pathField = new CustomeTextField();
  private final JButton openButton = new JButton("Open");
  
  
  public WorkSpaceFormFrame(WorkSpaceNodeTree tree){
    this.tree = tree;
    setTitle("Open Work Space");
    setLocationRelativeTo(tree);
    setResizable(false);
    setSize(347, 100);
    
    pathField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    pathField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    pathField.setFont(UserPreferences.getEditorFont().orElse(new Font("monospaced", Font.PLAIN, 16)));
    openButton.setMaximumSize(new Dimension(0, Integer.MAX_VALUE));
    openButton.addActionListener(openWorkSpace);
    
    Box inputBox = Box.createHorizontalBox();
    inputBox.add(pathField);
    inputBox.add(openButton);
    
    Box container = Box.createVerticalBox();
    container.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
    container.add(pathLabel);
    container.add(inputBox);
    Arrays.asList(container.getComponents())
      .forEach(c -> ((JComponent)c).setAlignmentX(0.0f));
    add(container);
  } 
  
  private WorkSpaceNodeTree getWorkSpaceNodeTree(){
    return tree;
  }
  
  private ActionListener openWorkSpace = (e)->{
    WorkSpaceNodeTree tree = getWorkSpaceNodeTree();
    tree.addHostFileTreeNode(
      new HostFileTreeNode(new HostFile(pathField.getText()), tree)
      );
  };
  
}    
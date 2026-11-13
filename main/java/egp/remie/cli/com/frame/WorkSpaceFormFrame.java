package egp.remie.cli.com.frame;

import egp.remie.cli.custome.*;
import egp.remie.cli.user.*;
import egp.remie.cli.com.*;
import egp.remie.cli.com.tree.*;
import egp.remie.cli.com.editor.*;
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
  private final JLabel errorHost = new JLabel("Error Host");
  private final JLabel errorPath = new JLabel("Error Path");
  private final CustomeTextField hostField = new CustomeTextField();
  private final CustomeTextField pathField = new CustomeTextField();
  private final JButton openButton = new JButton("Open");
  
  
  public WorkSpaceFormFrame(WorkSpaceNodeTree tree){
    this.tree = tree;
    setTitle("Open Work Space");
    setLocationRelativeTo(tree);
    setResizable(false);
    setSize(347, 200);
    
    styleComponents();
    
    Box buttonBox = Box.createHorizontalBox();
    buttonBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    buttonBox.add(Box.createHorizontalGlue());
    buttonBox.add(openButton);
    
    Box container = Box.createVerticalBox();
    container.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
    container.add(errorHost);
    container.add(hostField);
    container.add(Box.createRigidArea(new Dimension(3, 3)));
    container.add(errorPath);
    container.add(pathField);
    container.add(buttonBox);
    Arrays.asList(container.getComponents())
      .forEach(c -> ((JComponent)c).setAlignmentX(0.0f));
    add(container);
  } 
  
  private void styleComponents(){
    errorHost.setText(" ");
    errorPath.setText(" ");
    errorHost.setForeground(Color.RED);
    errorPath.setForeground(Color.RED);
    
    hostField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    //hostField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    hostField.setFont(UserPreferences.getEditorFont().orElse(new Font("monospaced", Font.PLAIN, 16)));
    hostField.setHint("Host URI");
    
    pathField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    //pathField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    pathField.setFont(UserPreferences.getEditorFont().orElse(new Font("monospaced", Font.PLAIN, 16)));
    pathField.setHint("File Path");
    
    //openButton.setMaximumSize(new Dimension(0, Integer.MAX_VALUE));
    openButton.addActionListener(openWorkSpace);
  }
  
  private WorkSpaceNodeTree getWorkSpaceNodeTree(){
    return tree;
  }
  
  private ActionListener openWorkSpace = (e)->{
    WorkSpaceNodeTree tree = getWorkSpaceNodeTree();
    HostFile rootFile = new HostFile(pathField.getText(), hostField.getText());
    if(!HostFiles.isHostAvailable(rootFile)){
      errorHost.setText("Host is not available");
      return;
    }
    errorHost.setText(" ");
    if(!HostFiles.exists(rootFile)){
      errorPath.setText("Path does not exist");
      return;
    }
    errorPath.setText(" ");
    hostField.setText("");
    hostField.setText("");
    tree.addHostFileTreeNode(
      new HostFileTreeNode(rootFile, tree)
      );
  };
  
}    
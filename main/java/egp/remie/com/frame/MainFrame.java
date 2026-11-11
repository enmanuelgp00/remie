package egp.remie.com.frame;

import egp.remie.custome.*;
import egp.remie.com.tree.*;
import egp.remie.com.editor.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends CustomeFrame {
  
  public MainFrame(WorkSpaceNodeTree tree, TreeNodeHostFileTextEditorTabbedPane editorTabbedPane){
    setTitle("Remote Mini");
    setSize(1024, 768);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setJMenuBar(new MainMenuBar(tree, editorTabbedPane));
    tree.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    
    add(new CustomeSplitPane(
      CustomeSplitPane.HORIZONTAL_SPLIT,
      new CustomeScrollPane(tree),
      editorTabbedPane));
  }
}
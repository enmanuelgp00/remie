package egp.remie.com.frame;

import egp.remie.custome.*;
import egp.remie.com.tree.*;
import egp.remie.com.editor.*;
import javax.swing.*;

public class MainFrame extends CustomeFrame {
  
  public MainFrame(WorkSpaceNodeTree tree, TreeNodeHostFileTextEditorTabbedPane editorTabbedPane){
    setTitle("Remote Mini");
    setSize(1024, 768);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setJMenuBar(new MainMenuBar(tree, editorTabbedPane));
    add(new CustomeSplitPane(
      CustomeSplitPane.HORIZONTAL_SPLIT,
      tree, editorTabbedPane));
  }
}
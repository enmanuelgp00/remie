package egp.remie.cli.com.frame;

import egp.remie.cli.custome.*;
import egp.remie.cli.com.tree.*;
import egp.remie.cli.com.editor.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class MainFrame extends CustomeFrame {
  
  public MainFrame(WorkSpaceNodeTree tree, TreeNodeHostFileTextEditorTabbedPane editorTabbedPane){
    setTitle("Remote Mini");
    setSize(1474, 768);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setJMenuBar(new MainMenuBar(tree, editorTabbedPane));
    
    tree.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    tree.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
    CustomeScrollPane treeScroll = new CustomeScrollPane(tree);
    treeScroll.setPreferredSize(new Dimension(250, Integer.MAX_VALUE));
    
    tree.addComponentListener(new ComponentAdapter(){
      @Override
      public void componentResized(ComponentEvent e){
        treeScroll.getHorizontalScrollBar().setValue(tree.getSize().width);
      }
    });
    add(new CustomeSplitPane(
      CustomeSplitPane.HORIZONTAL_SPLIT,
      treeScroll,
      editorTabbedPane));
  }
}
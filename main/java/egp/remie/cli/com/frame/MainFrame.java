package egp.remie.cli.com.frame;

import egp.remie.cli.custome.*;
import egp.remie.cli.com.console.*;
import egp.remie.cli.com.tree.*;
import egp.remie.cli.com.editor.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class MainFrame extends CustomeFrame {
  
  public MainFrame(WorkSpaceNodeTree tree){
    setTitle("Remote Mini");
    setSize(1474, 768);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    TreeNodeHostFileTextEditorTabbedPane editorTabbedPane = tree.getTextEditorTabbedPane();
    editorTabbedPane.setPreferredSize(new Dimension(250, Integer.MAX_VALUE));
    ConsoleTabbedPane consoleTabbedPane = tree.getConsoleTabbedPane();
    setJMenuBar(new MainMenuBar(tree));
    
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
    add(new QuickAccessBar(editorTabbedPane), BorderLayout.NORTH);
    add(new CustomeSplitPane(CustomeSplitPane.HORIZONTAL_SPLIT,
      treeScroll,
      new CustomeSplitPane(JSplitPane.VERTICAL_SPLIT, editorTabbedPane, consoleTabbedPane)),
    BorderLayout.CENTER);
  }
}
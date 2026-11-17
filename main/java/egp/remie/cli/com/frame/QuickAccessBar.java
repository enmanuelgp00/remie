package egp.remie.cli.com.frame;

import javax.swing.*;
import egp.remie.cli.com.editor.*;
import java.awt.*;
import java.awt.event.*;

public class QuickAccessBar extends JPanel {
  private TreeNodeHostFileTextEditorTabbedPane editorTabbedPane = null;
  private final JButton closeAllEditorTabs = new JButton("Close All Tabs");
  
  public QuickAccessBar(TreeNodeHostFileTextEditorTabbedPane editorTabbedPane) {
    super(new FlowLayout(FlowLayout.LEFT, 3, 3));
    this.editorTabbedPane = editorTabbedPane;
    closeAllEditorTabs.addActionListener(closeAllEditorTabsAction);
    add(closeAllEditorTabs);
  }
  
  private ActionListener closeAllEditorTabsAction = (e)->{
    SwingUtilities.invokeLater(()->{
      editorTabbedPane.removeAllTabs();
    });
  };
}
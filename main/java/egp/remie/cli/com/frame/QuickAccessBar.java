package egp.remie.cli.com.frame;

import javax.swing.*;
import egp.remie.cli.com.editor.*;
import java.awt.*;
import java.awt.event.*;

public class QuickAccessBar extends JPanel {
  private TreeNodeHostFileTextEditorTabbedPane editorTabbedPane = null;
  private final Button closeAllEditorTabs = new Button("Close Tabs");
  
  public QuickAccessBar(TreeNodeHostFileTextEditorTabbedPane editorTabbedPane) {
    super(new FlowLayout(FlowLayout.LEFT, 3, 3));
    this.editorTabbedPane = editorTabbedPane;
    //closeAllEditorTabs.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 7));
    closeAllEditorTabs.addActionListener(closeAllEditorTabsAction);
    add(closeAllEditorTabs);
  }
  
  private ActionListener closeAllEditorTabsAction = (e)->{
    SwingUtilities.invokeLater(()->{
      editorTabbedPane.removeAllTabs();
    });
  };
}
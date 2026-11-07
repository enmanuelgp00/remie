package egp.remie.com.editor;

import egp.remie.com.*;
import egp.remie.custome.*;
import javax.swing.*;
import javax.swing.tree.*;

public class TreeNodeHostFileTextEditorTabbedPane extends CustomeTabbedPane {
  public TreeNodeHostFileTextEditorTabbedPane(){
    
  }
  
  public void addEditorTab(DefaultMutableTreeNode treeNode){
    TreeNodeHostFileTextEditor editor = new TreeNodeHostFileTextEditor(treeNode);
    try {
      editor.loadFileContent();
      add(editor.getHostFile().toString(), editor);
    }catch(Exception ex) {
      ex.printStackTrace();
    }
  }
}
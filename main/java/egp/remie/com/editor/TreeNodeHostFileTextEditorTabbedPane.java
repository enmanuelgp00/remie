package egp.remie.com.editor;

import egp.remie.com.*;
import egp.remie.custome.*;
import javax.swing.*;

public class HostFileTextEditorTabbedPane extends CustomeTabbedPane {
  public HostFileTextEditorTabbedPane(){
    
  }
  
  public void addEditorTab(HostFile hostFile){
    HostFileTextEditor editor = new HostFileTextEditor(hostFile);
    
    try {
      editor.loadFileContent();
      add(hostFile.toString(), editor);
      
    }catch(Exception ex) {
      ex.printStackTrace();
    }
  }
}
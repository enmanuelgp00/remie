package egp.remie.com.editor;

import java.io.*;
import egp.remie.custome.*;
import egp.remie.com.*;
import javax.swing.*;

public class TreeNodeHostFileTextEditor extends CustomeScrollPane {
  
  private DefaultMutableTreeNode treeNode;
  private CustomeTextArea textArea = new CustomeTextArea();
  private TextEditorPopupMenu textEditorPopupMenu 
  
  public TreeNodeHostFileTextEditor(HostFile hostFile){
    super();
    setTreeNode(hostFile);
    setViewportView(textArea);    
  }
  
  public void setTreeNode(HostFile hostFile){
    this.hostFile = hostFile;
  }
  
  public void loadFileContent() throws IOException {    
    textArea.setText(HostFiles.readString(hostFile));
  }
  
  public void commit(){
    
  }
  
}
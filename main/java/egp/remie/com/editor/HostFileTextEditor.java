package egp.remie.com.editor;

import java.io.*;
import egp.remie.custome.*;
import egp.remie.com.*;
import javax.swing.*;

public class HostFileTextEditor extends CustomeScrollPane {
  
  private HostFile hostFile;
  private CustomeTextArea textArea = new CustomeTextArea();
  
  public HostFileTextEditor(HostFile hostFile){
    super();
    setHostFile(hostFile);
    setViewportView(textArea);    
  }
  
  public void setHostFile(HostFile hostFile){
    this.hostFile = hostFile;
  }
  
  public void loadFileContent() throws IOException {    
    textArea.setText(HostFiles.readString(hostFile));
  }
  
  public void commit(){
    
  }
  
}
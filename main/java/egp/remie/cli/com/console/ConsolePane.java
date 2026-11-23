package egp.remie.cli.com.console;

import egp.remie.host.file.*;
import egp.remie.cli.custome.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ConsolePane extends CustomeTextPane {
  private final HostFile workingDir;
  private HostShell shell = null;
  private final ConsoleTabbedPane tabbedPane;
  private ActionMap am = null;
  private volatile StringBuilder command = new StringBuilder();
  
  public ConsolePane(HostFile workingDir, ConsoleTabbedPane tabbedPane) {
    setFont(new Font("Consolas", Font.PLAIN, 16));
    setForeground(Color.WHITE);
    setCaretColor(Color.WHITE);
    setBackground(Color.BLACK);
    setDocument(constraintDoc);
    am = getActionMap();
    //am.get("caret-up").setEnabled(false);
    //am.get("caret-down").setEnabled(false);
    
    this.tabbedPane = tabbedPane;
    this.workingDir = workingDir;
    
    try{
      shell = Hosts.createShell(workingDir);
      Thread th = new Thread(()->{
        try(InputStream is = shell.getInputStream()){
          int read;
          byte[] buffer = new byte[1024];
          Document doc = getDocument();
          String strIn;
          while((read = is.read(buffer)) > -1){
            strIn = new String(buffer, 0, read);
            if(strIn.equals(command.toString())){
              command.setLength(0);
              continue;
            }
            doc.insertString(doc.getLength(), strIn, null);
            setCaretPosition(doc.getLength());
            command.setLength(0);
          }
          tabbedPane.removeConsole(this);          
          
        }catch(Exception ex){
          ex.printStackTrace();
        }
      });
      th.start();
      addKeyListener(new KeyAdapter(){
        @Override
        public void keyTyped(KeyEvent e){
          if(e.getKeyChar() == KeyEvent.VK_ENTER){
            try{
              
              shell.getOutputStream().write(command.append('\n').toString().getBytes());
              shell.getOutputStream().flush();
              
            }catch(Exception ex){
              ex.printStackTrace();
            }
            return;
          }
          if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE){
            return;
          }
          command.append(e.getKeyChar()); //command.deleteCharAt(command.length() - 1);
        }
      });
    }catch(Exception ex){
      ex.printStackTrace();
    }
    
  }
  
  public void exit(){
    try {
      int exitValue = Hosts.stopShell(shell);      
    }catch(InterruptedException | IOException ex){
      ex.printStackTrace();
    }
  }
  
  public HostFile getWorkingDirectory(){
    return workingDir;
  }  
  @Override
  public Dimension getPreferredSize(){
    Dimension size = getUI().getPreferredSize(this); //learning_note: getUI().getPreferredSize() returns the ideal size required by the text component based on its current content and font
    Dimension parentSize = getParent().getSize();
    int width = Math.max(size.width, parentSize.width);
    int height = Math.max(size.height, parentSize.height);
    
    return new Dimension(width, height);
  }
  private DefaultStyledDocument constraintDoc = new DefaultStyledDocument(){
    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
      int pos = getDocument().getLength();
      setCaretPosition(pos);
      super.insertString(pos, str, attr);
    }
    @Override
    public void remove(int offset, int length) throws BadLocationException {
      Document doc = getDocument();
      int originLength = doc.getLength() - command.length();
      setCaretPosition(doc.getLength());
      if(offset < originLength){
        return;
      }
      super.remove(offset, length);
      command.setLength(0);
      command.append(doc.getText(originLength, doc.getLength() - originLength));
    }
    
  };
}
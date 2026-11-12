package egp.remie.custome;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.image.*;
import java.awt.*;

public class CustomeTextPane extends JTextPane {
  private int tabSize = -1;
  private boolean stringInsertEnabled = true;
  private boolean wordWrapping = false;
  private boolean autoIndentEnabled = true;
  
  public CustomeTextPane(){
    setTabSize(4);
    setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    setDocument(new DefaultStyledDocument(){
      @Override
      public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException{
        String insertion = str;
        if(str.equals("\n") && hasAutoIndenting()){
          int indentSpaceCount = getIndentSpaceCount();
          StringBuffer sb = new StringBuffer();
          sb.append("\n");
          for(int i = 0; i < indentSpaceCount; i++){
            sb.append(" ");
          }
          insertion = sb.toString();
        }
        super.insertString(offset, insertion, attr);
      }
    });
  }    
  
  private int getIndentSpaceCount(){
    return getIndentCharCount(' ');
  }
  private int getIndentTabCount(){
    return getIndentCharCount('\t');
  }
  private int getIndentCharCount(char indentChar){
    String str = "";
    try{
      str = getDocument().getText(0, getCaretPosition());
    }catch(Exception ex){ ex.printStackTrace();}
    int indentCharCount = 0;
    char ch;
    for(int i = str.length() - 1; i > -1; i--){
      ch = str.charAt(i);
      if(ch == '\n')
        break;
      if(ch == indentChar){
        indentCharCount++;
        //System.out.println(String.format("'%c' == '%c' : %d", ch, indentChar, indentCharCount));
      } else {
        indentCharCount = 0;
        //System.out.println(String.format("'%c' != '%c' : %d", ch, indentChar, indentCharCount));
      }
    }
    return indentCharCount;
  }
  
  public boolean hasAutoIndenting(){
    return autoIndentEnabled;
  }
  @Override
  public void setFont(Font font){
    super.setFont(font);
    updateTabStop();
  }
  @Override
  public void setDocument(Document doc){
    super.setDocument(doc);
    updateTabStop();
  }
  
  public void enableAutoIndent(boolean enabled){
    this.autoIndentEnabled = enabled;
  }
  public void enableWordWrapping(boolean wordWrapping){
    this.wordWrapping = wordWrapping;
  }
  public void setTabSize(int tabSize){
    this.tabSize = tabSize;    
    Document doc = getDocument();
    if(doc == null)
      return;
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics g = image.createGraphics();
    g.setFont(getFont());
    FontMetrics fMetrics = g.getFontMetrics();
    int spaceWidth = fMetrics.charWidth(' ');
    int tabWidth = tabSize * spaceWidth;
    TabStop[] tabStops = new TabStop[50];
    for(int i = 0; i < tabStops.length; i++){
      int tabStopsAt = (i + 1) * tabWidth;
      tabStops[i] = new TabStop(tabStopsAt);
    }
    SimpleAttributeSet tabSizeAttributes = new SimpleAttributeSet();
    StyleConstants.setTabSet(tabSizeAttributes, new TabSet(tabStops));
    int docLength = doc.getLength();
    boolean isReplacing = false; // false means merging attributes
    getStyledDocument().setParagraphAttributes(0, docLength, tabSizeAttributes, isReplacing);
  }
  private void updateTabStop(){
    setTabSize(getTabSize());
  }
  public int getTabSize(){
    return tabSize;
  }
  @Override
  public boolean getScrollableTracksViewportWidth(){ //learning_note: handles the line wrapping
    return wordWrapping;
  }  
  public boolean hasWordWrapping(){
    return wordWrapping;
  }
  @Override
  public Dimension getPreferredSize(){
    Dimension size = getUI().getPreferredSize(this); //learning_note: getUI().getPreferredSize() returns the ideal size required by the text component based on its current content and font
    Dimension parentSize = getParent().getSize();
    int width = Math.max(size.width, parentSize.width);
    int height = Math.max(size.height, parentSize.height);
    
    return new Dimension(width, height);
  }
  
}
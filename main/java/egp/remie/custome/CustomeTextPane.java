package egp.remie.custome;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.image.*;
import java.awt.*;

public class CustomeTextPane extends JTextPane {
  private int tabSize = -1;
  
  private boolean wordWrapping = false;
  
  public CustomeTextPane(){
    setTabSize(4);
    setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
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
    TabStop[] tabStops = new TabStop[10];
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
    if(size.width > parentSize.width)
      return size;
    return parentSize;
  }
  
}
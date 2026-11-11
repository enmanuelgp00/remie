package egp.remie.custome;

import javax.swing.*;
import java.awt.*;

public class CustomeTextPane extends JTextPane {
  
  private boolean lineWrapping = false;
  
  public CustomeTextPane(){
    setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
  }

  @Override
  public boolean getScrollableTracksViewportWidth(){ //learning_note: handles the line wrapping
    return lineWrapping;
  }
  
  @Override
  public Dimension getPreferredSize(){
    Dimension size = getUI().getPreferredSize(this); //learning_note: getUI().getPreferredSize() returns the ideal size required by the text component based on its current content and font
    Dimension parentSize = getParent().getSize();
    if(size.width > parentSize.width)
      return size;
    return parentSize;
  }
  
  public void enableLineWrapping(boolean lineWrapping){
    this.lineWrapping = lineWrapping;
  }
}
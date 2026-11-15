package egp.remie.cli.custome;

import javax.swing.*;
import java.awt.*;

public class CustomeTextField extends JTextField{
  private String hint = "";
  
  public CustomeTextField(){ }
  public CustomeTextField(String str){
    super(str);
  }
  
  public String getHint(){
    return hint;
  }
  
  public void setHint(String hint){
    this.hint = hint;
  }
  
  @Override
  public void paintComponent(Graphics g){
    super.paintComponent(g);
    if(!getText().isEmpty()){
      return;
    }
    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setFont(getFont().deriveFont(Font.ITALIC));
    g2.setColor(Color.GRAY);
    Insets insets = getInsets();
    FontMetrics metrics = g2.getFontMetrics();
    int charHeight = metrics.getHeight(); 
    int baselineYPos = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent(); //learning_note: metrics.getHeight() = .getDescent() + .getAscent();
    int xPos = insets.left;
    g2.drawString(getHint(), xPos, baselineYPos);
  }
}

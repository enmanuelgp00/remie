package egp.remie.custome;

import javax.swing.*;
import java.awt.*;

public class CustomeTextArea extends JTextArea {
  private final Font font = new Font("monospaced", Font.PLAIN, 16);
  
  public CustomeTextArea(){
    setFont(font);
  }
}
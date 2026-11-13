package egp.remie.cli.com.frame;

import egp.remie.cli.custome.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import egp.remie.cli.com.tree.*;
import egp.remie.cli.com.editor.*;
import java.util.List;
import java.util.*;

public class ColorInputPane extends Box {
  private Color value = null;
  private CustomeTextField input = new CustomeTextField();
  private JButton moreButton = new JButton("...");
  private List<ColorChangeListener> colorChangeListeners = new ArrayList<ColorChangeListener>();
  private Color color = null;
  private Color wrongInputColor = Color.RED;
  private Color validInputColor = Color.BLACK;
  
  public ColorInputPane(String title){
    super(BoxLayout.Y_AXIS);
    //setBorder(BorderFactory.createTitledBorder(title));
    add(new JLabel(title));
    input.addKeyListener(textValueAsColor);
    input.setFont(new Font("monospaced", Font.PLAIN, 16));
    input.setPreferredSize(new Dimension(100, 30));
    input.setMaximumSize(new Dimension(100, 30));
    input.setMinimumSize(new Dimension(100, 30));
    add(input);
    Arrays.asList(getComponents())
      .forEach(c -> ((JComponent)c).setAlignmentX(0.0f));
  }
  
  public Color getSelectedColor(){
    return color;
  }
  
  public void setSelectedColor(Color color){
    int red = color.getRed();
    int green = color.getGreen();
    int blue = color.getBlue();
    String colorHex = String.format("#%02x%02x%02x",red, green, blue);
    input.setHint(colorHex);
    input.setText(colorHex);
  }
  
  private KeyAdapter textValueAsColor = new KeyAdapter(){
    @Override
    public void keyReleased(KeyEvent e){
      String inputText = input.getText();
      if(!isValidInput(inputText)){
        input.setForeground(wrongInputColor);
        return;
      }
      input.setForeground(validInputColor);
      try{
        color = Color.decode(inputText);
        colorChangeListeners.forEach(l -> l.colorChanged(getSelectedColor()));
      }catch(Exception ex){}
    }
  };
  
  private boolean isValidInput(String inputText){
    int base = 16;
    char ch;
    String hexName = "";
    
    if(inputText.startsWith("#"))
      hexName = inputText.substring(1);
    else if(inputText.startsWith("0x"))
      hexName = inputText.substring(2);
    
    if(hexName.length() != 6)
      return false;
    
    for(int i = 0; i < hexName.length(); i++){
      ch = hexName.charAt(i);
      if(!(Character.digit(ch, base) > -1))
        return false;
    }
    return true;
  }
  
  public JTextField getTextField(){
    return input;
  }
  
  public void addColorChangeListener(ColorChangeListener listener){
    colorChangeListeners.add(listener);
  }
  
  @FunctionalInterface
  public interface ColorChangeListener{
    public void colorChanged(Color color);
  }
}
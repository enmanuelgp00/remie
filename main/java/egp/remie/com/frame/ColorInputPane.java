package egp.remie.com.frame;

import egp.remie.custome.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import egp.remie.com.tree.*;
import egp.remie.com.editor.*;
import java.util.List;
import java.util.*;

public class ColorInputPane extends Box {
  private Color value = null;
  private CustomeTextField input = new CustomeTextField();
  private JButton moreButton = new JButton("...");
  private List<ColorChangeListener> colorChangeListeners = new ArrayList<ColorChangeListener>();
  private Color color = null;
  
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
    String colorHex = String.format("#%x%x%x",red, green, blue);
    input.setHint(colorHex);
    input.setText(colorHex);
  }
  
  private KeyAdapter textValueAsColor = new KeyAdapter(){
    @Override
    public void keyPressed(KeyEvent e){
      String inputText = input.getText();
      try{
        color = Color.decode(inputText + (char)e.getKeyCode());
        colorChangeListeners.forEach(l -> l.colorChanged(getSelectedColor()));
      }catch(Exception ex){}
    }
    @Override
    public void keyReleased(KeyEvent e){
      String inputText = input.getText();
      try{
        color = Color.decode(inputText);
        colorChangeListeners.forEach(l -> l.colorChanged(getSelectedColor()));
      }catch(Exception ex){}
    }
  };
  
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
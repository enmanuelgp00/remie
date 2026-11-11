package egp.remie.custome;

import javax.swing.*;
import java.awt.*;

public class CustomeTree extends JTree {
  public CustomeTree(){
    
  }
  @Override
  public Dimension getPreferredSize(){
    return getUI().getPreferredSize(this); // to considere the node labels as width
  }
}
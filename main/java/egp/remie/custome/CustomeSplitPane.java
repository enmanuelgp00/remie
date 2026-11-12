package egp.remie.custome;

import javax.swing.*;
import java.awt.*;

public class CustomeSplitPane extends JSplitPane {
  public CustomeSplitPane(int horitation, JComponent com0, JComponent com1){
    super(horitation, com0, com1);
    setBackground(new Color(34, 34, 34));
  }
}
package egp.remie.custome;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.plaf.metal.*;
import java.awt.*;

public class CustomeTree extends JTree {
  
  public CustomeTree(){
    //setBackground(new Color(34, 34, 34));
    //DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer)getCellRenderer();
    //darkTreeCellRenderer();
  }
  //Has Methos
  //Set Methods
  //Get Methods
  public void darkTreeCellRenderer(){
    setCellRenderer(new DefaultTreeCellRenderer(){ // used for ui only, can not implement listerner, use CellEditor instead
      {
        /*
        setTextSelectionColor();
        setBackgroundSelectionColor();
        setBorderSelectionColor();
        setOpenIcon()
        setClosedIcon()
        setLeafIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        setLeafIcon(MetalIconFactory.getTreeLeafIcon());
        setOpenIcon(UIManager.getIcon("Tree.expandedIcon"));
        setClosedIcon(UIManager.getIcon("Tree.collapsedIcon"));*/
        setTextNonSelectionColor(Color.WHITE);
        setBackgroundNonSelectionColor(new Color(34, 34, 34));
        setFont(new Font("arial", Font.PLAIN, 12));
      }
      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean isLeaf, int row, boolean hasFocus){
        JLabel label = (JLabel)super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, hasFocus);        
        if(selected)
          return label;
        
        if(isLeaf)
          label.setForeground(Color.WHITE);
        else
          label.setForeground(new Color(200, 200, 229));
        
        return label;
      }
    });
  }
  @Override
  public Dimension getPreferredSize(){
    return getUI().getPreferredSize(this); // to considere the node labels as width
  }
  
}
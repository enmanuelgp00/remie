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

public class FontFieldInputPane extends Box {
  private JList<String> listPane;
  private JTextField searchField = new JTextField();
  private JLabel label;
  private boolean focusingSearchField = false;
  private CustomeScrollPane listScroll;
  
  public FontFieldInputPane(String title, String[] values){
    super(BoxLayout.Y_AXIS);
    listPane = new JList<String>(values);  
    listScroll = new CustomeScrollPane(listPane);  
    label = new JLabel(title);
    searchField.setMaximumSize(new Dimension(1000, 1000));
    setMaximumSize(new Dimension(1000, 1000)); //learing_note: it seems that applying max size the component size performs as inside a jpanel
    searchField.addKeyListener(searchListValue);
    searchField.addFocusListener(trackTextFieldFocus);
    listPane.addListSelectionListener(syncSearchFieldText);
    addComponents();
  }
  
  private KeyAdapter searchListValue = new KeyAdapter(){
    @Override
    public void keyTyped(KeyEvent e){
      ListModel<String> model = listPane.getModel();
      for(int i = 0; i < model.getSize(); i++){
        String value = model.getElementAt(i);
        if(value.toLowerCase().contains(searchField.getText().toLowerCase())){
          listPane.setSelectedIndex(i);
          Point indexLocation = listPane.indexToLocation(i);
          listScroll.getViewport().setViewPosition(indexLocation);
          return;
        }
      }
    }
  };
  
  public void setSelectedName(String name){
    ListModel<String> model = listPane.getModel();
    for(int i = 0; i < model.getSize(); i++){
      String value = model.getElementAt(i);
      if(value.toLowerCase().equals(name.toLowerCase())){
        listPane.setSelectedIndex(i);
        Point indexLocation = listPane.indexToLocation(i);
        listScroll.getViewport().setViewPosition(indexLocation);
      }
    }
  }
  
  private FocusListener trackTextFieldFocus = new FocusListener(){
    @Override
    public void focusGained(FocusEvent e){
      setHasSearchFieldFocus(true);      
    }
    @Override
    public void focusLost(FocusEvent e){
      setHasSearchFieldFocus(false);
    }
  };
  
  private void setHasSearchFieldFocus(boolean focusingSearchField){
    this.focusingSearchField = focusingSearchField;
  }
  
  private boolean hasSearchFieldFocus(){
    return focusingSearchField;
  }
  
  private ListSelectionListener syncSearchFieldText = new ListSelectionListener(){
    @Override
    public void valueChanged(ListSelectionEvent e){
      if(!hasSearchFieldFocus())
        searchField.setText(listPane.getSelectedValue());
    }
  };
  
  public JList<String> getListPane(){
    return listPane;
  }
  
  public void addListSelectionListener(ListSelectionListener listener){
    listPane.addListSelectionListener(listener);
  }
  
  private void addComponents(){
    listPane.setVisibleRowCount(7);
    add(label);
    add(searchField);
    add(listScroll);
    Arrays.asList(getComponents())
      .forEach(c -> ((JComponent)c).setAlignmentX(0.0f));
  }
}
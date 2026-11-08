package egp.remie.com.frame;

import egp.remie.custome.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import egp.remie.com.tree.*;
import egp.remie.com.editor.*;
import java.util.*;

public class SelectFontFrame extends CustomeFrame {
  private TreeNodeHostFileTextEditorTabbedPane editorTabbedPane;  
  
  public SelectFontFrame(TreeNodeHostFileTextEditorTabbedPane editorTabbedPane){
    
    Dimension margin = new Dimension(13, 13);
    
    this.editorTabbedPane = editorTabbedPane;
    setResizable(false);
    setTitle("Select Font");
    setSize(400, 300);
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String[] fontFamilyNames = ge.getAvailableFontFamilyNames(); //Font[] osFonts = ge.getAllFonts();
    
    JTextField searchFontNameField = new JTextField();
    Box selectFontNameBox = Box.createVerticalBox();
    for(String fontName : fontFamilyNames){
      JLabel fontLabel = new JLabel(fontName);      
      fontLabel.setFont(new Font("monospaced", Font.PLAIN, 14)); //fontLabel.setFont(font.deriveFont(Font.PLAIN, 16));
      fontLabel.setAlignmentX(0.0f);
      selectFontNameBox.add(fontLabel);
    }
    Box selectFontNameMarginedBox = Box.createHorizontalBox();
    selectFontNameMarginedBox.add(Box.createRigidArea(new Dimension(3, 3)));
    selectFontNameMarginedBox.add(selectFontNameBox);
    selectFontNameMarginedBox.add(Box.createRigidArea(new Dimension(3, 3)));
    CustomeScrollPane fontNamesScroll = new CustomeScrollPane(selectFontNameMarginedBox);
    Box fontNamesBox = Box.createVerticalBox();
    JLabel fontNamesBoxLabel = new JLabel("Names");
    fontNamesBoxLabel.setAlignmentX(0.0f);
    searchFontNameField.setAlignmentX(0.0f);
    fontNamesScroll.setAlignmentX(0.0f);
    fontNamesBox.add(fontNamesBoxLabel);
    fontNamesBox.add(searchFontNameField);
    fontNamesBox.add(fontNamesScroll);
    
    JTextField searchFontSizeField = new JTextField();
    Box selectFontSizeBox = Box.createVerticalBox();
    for(int i = 8; i < 28; i+=2){
      JLabel fontSizeLabel = new JLabel(String.format("%02d", i));
      fontSizeLabel.setFont(new Font("monospaced", Font.PLAIN, 14));
      selectFontSizeBox.add(fontSizeLabel);
    }
    Box selectFontSizeMarginedBox = Box.createHorizontalBox();
    selectFontSizeMarginedBox.setPreferredSize(new Dimension(50, 200));
    selectFontSizeMarginedBox.add(Box.createRigidArea(new Dimension(3, 3)));
    selectFontSizeMarginedBox.add(selectFontSizeBox);
    selectFontSizeMarginedBox.add(Box.createRigidArea(new Dimension(3, 3)));
    CustomeScrollPane fontSizeScroll = new CustomeScrollPane(selectFontSizeMarginedBox);
    Box fontSizeBox = Box.createVerticalBox();
    JLabel fontSizeBoxLabel = new JLabel("Size");
    fontSizeScroll.setAlignmentX(0.0f);
    searchFontSizeField.setAlignmentX(0.0f);
    fontSizeBoxLabel.setAlignmentX(0.0f);
    fontSizeBox.add(fontSizeBoxLabel);
    fontSizeBox.add(searchFontSizeField);
    fontSizeBox.add(fontSizeScroll);
    
    JTextField searchFontStyleField = new JTextField();
    searchFontStyleField.setMaximumSize(new Dimension(110, 0));
    Box selectFontStyleBox = Box.createVerticalBox();
    String[] styleNames = new String[]{"Regular", "Italic", "Bold", "Bold Italic"};
    int[] styles = {Font.PLAIN, Font.ITALIC, Font.BOLD, Font.BOLD | Font.ITALIC};
    for(int i = 0; i < styles.length; i++){
      JLabel fontStyleLabel = new JLabel(styleNames[i]);
      fontStyleLabel.setFont(new Font("monospaced", styles[i], 14));
      selectFontStyleBox.add(fontStyleLabel);
    }
    Box selectFontStyleMarginedBox = Box.createHorizontalBox();
    selectFontStyleMarginedBox.setPreferredSize(new Dimension(110, 50));
    selectFontStyleMarginedBox.add(Box.createRigidArea(new Dimension(3, 3)));
    selectFontStyleBox.setAlignmentY(0.0f);
    selectFontStyleMarginedBox.add(selectFontStyleBox);
    selectFontStyleMarginedBox.add(Box.createRigidArea(new Dimension(3, 3)));
    CustomeScrollPane fontStyleScroll = new CustomeScrollPane(selectFontStyleMarginedBox);
    Box fontStyleBox = Box.createVerticalBox();
    JLabel fontStyleBoxLabel = new JLabel("Style");
    fontStyleScroll.setAlignmentX(0.0f);
    searchFontStyleField.setAlignmentX(0.0f);
    fontStyleBoxLabel.setAlignmentX(0.0f);
    fontStyleBox.add(fontStyleBoxLabel);
    fontStyleBox.add(searchFontStyleField);
    fontStyleBox.add(fontStyleScroll);
    
    Box fontFielsBox = Box.createHorizontalBox();
    fontFielsBox.add(Box.createRigidArea(margin));
    fontFielsBox.add(fontNamesBox);
    fontFielsBox.add(Box.createRigidArea(margin));
    fontFielsBox.add(fontStyleBox);
    fontFielsBox.add(Box.createRigidArea(margin));
    fontFielsBox.add(fontSizeBox);
    fontFielsBox.add(Box.createRigidArea(margin));
    
    JButton cancelButton = new JButton("Cancel");
    JButton acceptButton = new JButton("OK");
    Box buttonsBox = Box.createHorizontalBox();
    buttonsBox.add(Box.createHorizontalGlue());
    buttonsBox.add(cancelButton);
    buttonsBox.add(Box.createRigidArea(margin));
    buttonsBox.add(acceptButton);
    buttonsBox.add(Box.createRigidArea(margin));
    
    Box mainContainer = Box.createVerticalBox();     
    mainContainer.add(Box.createRigidArea(margin));
    mainContainer.add(fontFielsBox);
    mainContainer.add(Box.createVerticalGlue());
    mainContainer.add(Box.createRigidArea(margin));
    mainContainer.add(buttonsBox);
    mainContainer.add(Box.createRigidArea(margin));
    
    add(mainContainer);
  }
}
package egp.remie.com.frame;

import egp.remie.custome.*;
import egp.remie.user.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import egp.remie.com.tree.*;
import egp.remie.com.editor.*;
import java.util.List;
import java.util.*;
import java.util.stream.*;

public class StyleFormFrame extends CustomeFrame {
  private TreeNodeHostFileTextEditorTabbedPane editorTabbedPane;
  private CustomeTextPane sampleArea = new CustomeTextPane();
  private Font editorOldFont = null;
  private Color editorOldBackground = null;
  private Color editorOldForeground = null;
  private Boolean editorOldLineWrapping = null;
  
  private final int MARGIN = 13;
  
  private FontFieldInputPane sizeInputPane = null; 
  private FontFieldInputPane styleInputPane = null;
  private FontFieldInputPane nameInputPane = null;
  
  private JButton cancelButton = null;
  private JButton acceptButton = null;
  private JButton applyButton = null;
  
  private ColorInputPane backgroundInputColor = new ColorInputPane("Background");
  private ColorInputPane foregroundInputColor = new ColorInputPane("Foreground");
  
  private List<String> fontStyleNames;
  private int[] fontStyles;    
  private List<String> fontSizeNames = new ArrayList<String>();
  
  private JCheckBox lineWrapCheckBox = new JCheckBox("Line Wrap");
  private JCheckBox autoIndexCheckBox = new JCheckBox("Auto Indent");
  
  public StyleFormFrame(TreeNodeHostFileTextEditorTabbedPane editorTabbedPane){
    this.editorTabbedPane = editorTabbedPane;    
    setResizable(false);
    setTitle("Style");
    setSize(390, 430);
    
    setAlwaysOnTop(true);
    loadTextEditorStyle();
    configureSample();
    
    fontStyleNames = Arrays.asList(new String[]{"Regular", "Italic", "Bold", "Bold Italic"});
    fontStyles = new int[]{Font.PLAIN, Font.ITALIC, Font.BOLD, Font.BOLD | Font.ITALIC};
    
    List<Integer> fontSizeList = new ArrayList<Integer>();
    for(int i = 8; i < 28; i+=2){ fontSizeList.add(i); }
    
    fontSizeNames = fontSizeList.stream().map(i -> {
      return String.format("%02d", i);
      }).collect(Collectors.toList());
    
    nameInputPane = new FontFieldInputPane("Name:", getMonospacedFontNames());
    styleInputPane = new FontFieldInputPane("Style:", fontStyleNames.toArray(new String[0]));
    sizeInputPane = new FontFieldInputPane("Size:", fontSizeNames.toArray(new String[0]));
    
    applyButton = new JButton("Apply");
    acceptButton = new JButton("OK");
    cancelButton = new JButton("Cancel");
    
    addButtonActionListeners();
    addFontInputPaneListSelectionListeners(); 
    addColorInputPaneListeners();   
    updateInputFieldsFirstValues();
    
    MarginedBox fontFieldSection = new MarginedBox(MARGIN, BoxLayout.X_AXIS);  
    fontFieldSection.setAlignmentX(0.0f);
    fontFieldSection.add(nameInputPane);
    fontFieldSection.add(styleInputPane);
    fontFieldSection.add(sizeInputPane);
    
    MarginedBox selectColorColumn = new MarginedBox(MARGIN, BoxLayout.Y_AXIS);  
    selectColorColumn.ignoreInitialMargin();
    selectColorColumn.ignoreLastMargin();
    selectColorColumn.add(backgroundInputColor);
    selectColorColumn.add(foregroundInputColor);
    Arrays.asList(selectColorColumn.getComponents())
      .forEach(c -> ((JComponent)c).setAlignmentX(0.0f));
    
    CustomeTabbedPane sampleTabbedPane = new CustomeTabbedPane();
    sampleTabbedPane.add("Example.txt", new CustomeScrollPane(sampleArea));
    
    MarginedBox colorAndSampleSection = new MarginedBox(MARGIN, BoxLayout.X_AXIS); 
    colorAndSampleSection.setAlignmentX(0.0f);
    colorAndSampleSection.add(selectColorColumn);
    colorAndSampleSection.add(sampleTabbedPane);
    Arrays.asList(colorAndSampleSection.getComponents())
      .forEach(c -> ((JComponent)c).setAlignmentY(0.0f));
    
    MarginedBox buttonSection = new MarginedBox(MARGIN, BoxLayout.X_AXIS);
    buttonSection.setAlignmentX(0.0f);
    buttonSection.add(cancelButton);
    buttonSection.add(Box.createHorizontalGlue());
    buttonSection.add(acceptButton);
    buttonSection.add(applyButton);
        
    MarginedBox lineFormatSection = new MarginedBox(MARGIN, BoxLayout.X_AXIS);
    lineFormatSection.setAlignmentX(0.0f);
    lineFormatSection.add(Box.createHorizontalGlue());
    lineFormatSection.add(lineWrapCheckBox);
    lineFormatSection.add(autoIndexCheckBox);
    lineWrapCheckBox.addItemListener(toggleLineWrapping);
    autoIndexCheckBox.addItemListener((e)->{});
    lineWrapCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
    autoIndexCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
    
    MarginedBox mainContainer = new MarginedBox(MARGIN, BoxLayout.Y_AXIS);
    mainContainer.add(fontFieldSection);
    mainContainer.add(lineFormatSection);
    mainContainer.add(colorAndSampleSection);
    mainContainer.add(Box.createVerticalGlue());
    mainContainer.add(buttonSection);
    add(mainContainer);
  } 
  
  private String[] getMonospacedFontNames(){
    GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();  //ge.getAvailableFontFamilyNames(); Font[] osFonts = ge.getAllFonts();
    List<String> families = Arrays.asList(gEnv.getAvailableFontFamilyNames());
    List<String> monos = families.stream() // Font.getFamily
      .map(n -> new Font(n, Font.PLAIN, 10))
      .filter(f -> canFontDisplayString(f, "abc"))
      .filter(this::isMonospacedFont)
      .map(Font::getFamily)
      .collect(Collectors.toList());
    return monos.toArray(new String[0]);
  }
  
  private boolean canFontDisplayString(Font font, String str){
    int indexNotDisplayable = font.canDisplayUpTo(str);
    if(indexNotDisplayable == -1)
      return true;
    return false;
  }
  
  private boolean isMonospacedFont(Font font){
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics g = image.createGraphics();
    FontMetrics metrics = g.getFontMetrics(font);
    char[] test = new char[] {'.', '1', 'W', 'm'};
    int firstWidth = metrics.charWidth(test[0]);
    int charWidth;
    for(int i = 1; i < test.length; i++){
      charWidth = metrics.charWidth(test[i]);
      if(charWidth != firstWidth){
        g.dispose();
        return false;
      }
    }
    return true;
  }
  
  private void addColorInputPaneListeners(){    
    backgroundInputColor.addColorChangeListener(color->sampleArea.setBackground(color));
    foregroundInputColor.addColorChangeListener(color->sampleArea.setForeground(color));
  }
  
  private void addButtonActionListeners(){
    acceptButton.addActionListener((e)-> {    
      applyStyleToTextEditor();
      commitPreferences();
      dispose();
    });
    cancelButton.addActionListener((e)-> {
      restoreOldTextEditorStyle();
      dispose();
    });
    applyButton.addActionListener((e)->{
      applyStyleToTextEditor();
    });
  }
  
  private void restoreOldTextEditorStyle(){
    editorTabbedPane.setTextEditorFont(editorOldFont); 
    editorTabbedPane.setTextEditorBackground(editorOldBackground); 
    editorTabbedPane.setTextEditorForeground(editorOldForeground); 
    editorTabbedPane.enableEditorLineWrapping(editorOldLineWrapping); 
    editorTabbedPane.refreshEditorTextPane();
  }
  
  private void commitPreferences(){
    Font font = sampleArea.getFont();
    Color background = sampleArea.getBackground();
    Color foreground = sampleArea.getForeground();
    boolean lineWrapping = lineWrapCheckBox.isSelected();
    
    if(!(font.equals(editorOldFont)))
      UserPreferences.setEditorFont(font);
    
    if(!(background.equals(editorOldBackground)))
      UserPreferences.setEditorBackground(background);
    
    if(!(foreground.equals(editorOldForeground)))
      UserPreferences.setEditorForeground(foreground);
    
    if(lineWrapping != editorOldLineWrapping)
      UserPreferences.setEditorLineWrapping(lineWrapping);
    
    UserPreferences.commit();
  }
  
  private void applyStyleToTextEditor(){    
      editorTabbedPane.setTextEditorFont(sampleArea.getFont());
      editorTabbedPane.setTextEditorBackground(sampleArea.getBackground());
      editorTabbedPane.setTextEditorForeground(sampleArea.getForeground());
      editorTabbedPane.refreshEditorTextPane();
  }
  
  private void addFontInputPaneListSelectionListeners(){
    nameInputPane.addListSelectionListener(changeSampleLabelFontName);
    styleInputPane.addListSelectionListener(changeSampleLabelFontStyle);
    sizeInputPane.addListSelectionListener(changeSampleLabelFontSize);
  }
  
  private void configureSample(){
    sampleArea.setOpaque(true);    
    sampleArea.setFont(editorOldFont);
    sampleArea.setText("This is an example");
    sampleArea.enableLineWrapping(true);
  }
  private ItemListener toggleLineWrapping = new ItemListener(){
    @Override
    public void itemStateChanged(ItemEvent e){
      editorTabbedPane.enableEditorLineWrapping(e.getStateChange() == ItemEvent.SELECTED);
    }
  };
  private ListSelectionListener changeSampleLabelFontSize = (e)-> {
    try{
      float fontSize = Float.parseFloat(sizeInputPane.getListPane().getSelectedValue());
      Font oldFont = sampleArea.getFont();
      sampleArea.setFont(oldFont.deriveFont(fontSize));        
    }catch(Exception ex){
      ex.printStackTrace();
    }
  };  
  private ListSelectionListener changeSampleLabelFontStyle = (e)-> {
    try{
      int fontStyleIndex = fontStyleNames.indexOf(styleInputPane.getListPane().getSelectedValue());
      Font oldFont = sampleArea.getFont();
      sampleArea.setFont(oldFont.deriveFont(fontStyles[fontStyleIndex]));  
    }catch(Exception ex){
      ex.printStackTrace();
    }
  };  
  private ListSelectionListener changeSampleLabelFontName = (e)-> {
    String fontName = nameInputPane.getListPane().getSelectedValue();
    Font oldFont = sampleArea.getFont();
    sampleArea.setFont(new Font(fontName, oldFont.getStyle(), oldFont.getSize()));
  };
  
  private class MarginedBox extends Box{    
    private int margin = 0;
    private boolean ignoringInitialMargin = false;
    private boolean ignoringLastMargin = false;
    public MarginedBox(int margin, int orientation){
      super(orientation);
      this.margin = margin;
    }
    public void ignoreInitialMargin(){
      ignoringInitialMargin = true;
    }
    @Override
    public Component add(Component com0){
      List<Component> components = Arrays.asList(getComponents());
      if(components.size() == 0 && !ignoringInitialMargin)
        super.add(Box.createRigidArea(new Dimension(margin, margin)));
      
      if(components.size() > 0 && !(components.getLast() instanceof Box.Filler)){ //learning_note: Box.Filler is a RigidArea;
        super.add(Box.createRigidArea(new Dimension(margin, margin)));
      }
      super.add(com0);
      if(!ignoringLastMargin)
        super.add(Box.createRigidArea(new Dimension(margin, margin)));
      return com0;
    }
    public void ignoreLastMargin(){
      ignoringLastMargin = true;
    }
  }
  
  private void updateInputFieldsFirstValues(){
    int styleIndex = fontStyles[editorOldFont.getStyle()];
    styleInputPane.setSelectedName(fontStyleNames.get(styleIndex));
    nameInputPane.setSelectedName(editorOldFont.getName());
    sizeInputPane.setSelectedName(String.valueOf(editorOldFont.getSize()));
    backgroundInputColor.setSelectedColor(editorOldForeground);
    foregroundInputColor.setSelectedColor(editorOldForeground);
    lineWrapCheckBox.setSelected(editorOldLineWrapping);
  } 
  
  public void loadTextEditorStyle(){    
    editorOldFont = editorTabbedPane.getTextEditorFont();
    editorOldBackground = editorTabbedPane.getTextEditorBackground();
    editorOldForeground = editorTabbedPane.getTextEditorForeground();
    editorOldLineWrapping = editorTabbedPane.isLineWrappingEnabled();
  }
  public void updateSampleStyle(){
      sampleArea.setFont(editorOldFont);
      sampleArea.setBackground(editorOldBackground);
      sampleArea.setForeground(editorOldForeground);
      sampleArea.setCaretColor(editorOldForeground);    
  }
  @Override
  public void setVisible(boolean settingVisible){
    if(!isVisible()){
      loadTextEditorStyle();
    }
    
    if(settingVisible && !isVisible()){
      updateSampleStyle();
      updateInputFieldsFirstValues();
    }
    
    super.setVisible(settingVisible);
  }
  
}
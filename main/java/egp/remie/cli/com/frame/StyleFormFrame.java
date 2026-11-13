package egp.remie.cli.com.frame;

import egp.remie.cli.custome.*;
import egp.remie.cli.user.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import egp.remie.cli.com.tree.*;
import egp.remie.cli.com.editor.*;
import java.util.List;
import java.util.*;
import java.util.stream.*;

public class StyleFormFrame extends CustomeFrame {
  private TreeNodeHostFileTextEditorTabbedPane editorTabbedPane;
  private CustomeTextPane sampleArea = new CustomeTextPane();

  private Font editorOldFont = null;
  private Color editorOldBackground = null;
  private Color editorOldForeground = null;
  private Boolean editorOldWordWrapping = null;
  private Boolean editorOldUsingSpaceAsTab = null;
  private Integer editorOldTabSize = null;
  private Boolean editorOldAutoIndent = null;
  private Boolean editorOldTabAsSpace = null;
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
  
  private JCheckBox wordWrapCheckBox = new JCheckBox("Line Wrap");
  private JCheckBox autoIndentCheckBox = new JCheckBox("Auto Indent");
  private JCheckBox tabAsSpaceCheckBox = new JCheckBox("Tab Space");
  private JTextField tabSizeField = new JTextField("");
  
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
    
    wordWrapCheckBox.addItemListener(toggleLineWrapping);
    autoIndentCheckBox.addItemListener(toggleAutoIndent);
    tabAsSpaceCheckBox.addItemListener(toggleTabAsSpace);
    tabSizeField.addKeyListener(tabSizeListener);
    
    MarginedBox fontFieldSection = new MarginedBox(MARGIN, BoxLayout.X_AXIS);  
    fontFieldSection.add(nameInputPane);
    fontFieldSection.add(styleInputPane);
    fontFieldSection.add(sizeInputPane);
    
    Box tabSizePane = Box.createHorizontalBox();
    tabSizeField.setPreferredSize(new Dimension(23, 23));
    tabSizeField.setMaximumSize(new Dimension(23, 23));
    tabSizeField.setMinimumSize(new Dimension(23, 23));
    tabSizeField.setHorizontalAlignment(SwingConstants.CENTER);
    tabSizePane.add(tabSizeField);
    tabSizePane.add(Box.createRigidArea(new Dimension(3, 3)));
    tabSizePane.add(new JLabel("Tab Size"));
    Insets checkBoxInsets = tabAsSpaceCheckBox.getInsets();
    tabSizePane.setBorder(BorderFactory.createEmptyBorder(
      checkBoxInsets.top, 
      0, 
      checkBoxInsets.bottom, 
      checkBoxInsets.right));
    
    CustomeTabbedPane sampleTabbedPane = new CustomeTabbedPane();
    sampleTabbedPane.add("example", new CustomeScrollPane(sampleArea));    
    
    //MarginedBox spacingPane = new MarginedBox(MARGIN, BoxLayout.X_AXIS);
    Box spacingPane = Box.createHorizontalBox();
    spacingPane.add(Box.createRigidArea(new Dimension(MARGIN, MARGIN)));
    spacingPane.add(tabSizePane);
    spacingPane.add(tabAsSpaceCheckBox);
    spacingPane.add(wordWrapCheckBox);
    spacingPane.add(autoIndentCheckBox);
    spacingPane.add(Box.createHorizontalGlue());
    
    MarginedBox colorPane = new MarginedBox(MARGIN, BoxLayout.Y_AXIS);
    colorPane.ignoreInitialMargin();
    colorPane.add(Box.createVerticalGlue());
    colorPane.add(backgroundInputColor);
    colorPane.add(foregroundInputColor);
    Arrays.asList(colorPane.getComponents())
      .forEach(c -> ((JComponent)c).setAlignmentX(0.0f));
    
    //wordWrapCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
    //autoIndentCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
    //tabAsSpaceCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
     
    MarginedBox colorSampleSection = new MarginedBox(MARGIN, BoxLayout.X_AXIS);
    colorSampleSection.add(colorPane);
    colorSampleSection.add(sampleTabbedPane);
    Arrays.asList(colorSampleSection.getComponents())
      .forEach(c -> ((JComponent)c).setAlignmentY(0.0f));
    
    MarginedBox buttonSection = new MarginedBox(MARGIN, BoxLayout.X_AXIS);
    buttonSection.add(cancelButton);
    buttonSection.add(Box.createHorizontalGlue());
    buttonSection.add(acceptButton);
    buttonSection.add(applyButton);
    
    MarginedBox mainContainer = new MarginedBox(MARGIN, BoxLayout.Y_AXIS);
    mainContainer.add(fontFieldSection);
    mainContainer.add(spacingPane);
    mainContainer.add(colorSampleSection);
    //mainContainer.skipMarginWithNextComponent();
    mainContainer.add(Box.createVerticalGlue());
    mainContainer.add(buttonSection);
    Arrays.asList(mainContainer.getComponents())
      .forEach(c -> ((JComponent)c).setAlignmentX(0.5f));
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
    editorTabbedPane.enableTextEditorWordWrapping(editorOldWordWrapping); 
    editorTabbedPane.enableTextEditorAutoIndenting(editorOldAutoIndent); 
    editorTabbedPane.setTextEditorTabSize(editorOldTabSize); 
    editorTabbedPane.refreshEditorTextPane();
  }
  
  private void commitPreferences(){
    Font font = sampleArea.getFont();
    Color background = sampleArea.getBackground();
    Color foreground = sampleArea.getForeground();
    boolean wordWrapping = sampleArea.hasWordWrapping();
    int tabSize = sampleArea.getTabSize();
    boolean autoIndent = sampleArea.hasAutoIndenting();
    boolean tabAsSpace = sampleArea.isTabInsertionAsSpaceEnabled();
    
    if(!(font.equals(editorOldFont)))
      UserPreferences.setEditorFont(font);
    if(!(background.equals(editorOldBackground)))
      UserPreferences.setEditorBackground(background);
    if(!(foreground.equals(editorOldForeground)))
      UserPreferences.setEditorForeground(foreground);
    if(wordWrapping != editorOldWordWrapping)
      UserPreferences.setEditorLineWrapping(wordWrapping);
    if(tabSize != editorOldTabSize)
      UserPreferences.setEditorTabSize(tabSize);   
    if(autoIndent != editorOldAutoIndent)
      UserPreferences.setEditorAutoIndenting(autoIndent);
    if(tabAsSpace != editorOldTabAsSpace)
      UserPreferences.setEditorTabAsSpace(tabAsSpace);
    
    UserPreferences.commit();
  }
  
  
  private void addFontInputPaneListSelectionListeners(){
    nameInputPane.addListSelectionListener(changeSampleLabelFontName);
    styleInputPane.addListSelectionListener(changeSampleLabelFontStyle);
    sizeInputPane.addListSelectionListener(changeSampleLabelFontSize);
  }
  
  private void configureSample(){
    sampleArea.setOpaque(true);    
    sampleArea.setFont(editorOldFont);
    sampleArea.setText("void greet(char[] n){\n\tprintf(\"Hey, how are you \" + n);\n}");
    sampleArea.enableWordWrapping(true);
  }
  
  private KeyListener tabSizeListener = new KeyAdapter(){
    @Override
    public void keyReleased(KeyEvent e){
      try{
        int size = Integer.parseInt(tabSizeField.getText());
        sampleArea.setTabSize(size);
        sampleArea.repaint();
      }catch(Exception ex){
        
      }
    }
  };
  private ItemListener toggleLineWrapping = new ItemListener(){
    @Override
    public void itemStateChanged(ItemEvent e){
      sampleArea.enableWordWrapping(e.getStateChange() == ItemEvent.SELECTED);
      sampleArea.revalidate();
      sampleArea.repaint();
    }
  };
  private ItemListener toggleTabAsSpace = new ItemListener(){
    @Override
    public void itemStateChanged(ItemEvent e){
      sampleArea.enableTabInsertionAsSpace(e.getStateChange() == ItemEvent.SELECTED);
    }
  };
  private ItemListener toggleAutoIndent = new ItemListener(){
    @Override
    public void itemStateChanged(ItemEvent e){
      sampleArea.enableAutoIndent(e.getStateChange() == ItemEvent.SELECTED);
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
    private boolean isSkipingComponentMargin = false;
    
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
      
      if(components.size() > 0 && 
        !(components.getLast() instanceof Box.Filler) && !isSkipingComponentMargin){ //learning_note: Box.Filler is a RigidArea;
        super.add(Box.createRigidArea(new Dimension(margin, margin)));
      }
      super.add(com0);
      isSkipingComponentMargin = false;
      if(!ignoringLastMargin){
        super.add(Box.createRigidArea(new Dimension(margin, margin)));
      }
      return com0;
    }
    public void ignoreLastMargin(){
      ignoringLastMargin = true;
    }
    public void skipMarginWithNextComponent(){
      Component comp = Arrays.asList(getComponents()).getLast();
      if(comp instanceof Box.Filler)
        remove(comp);
      isSkipingComponentMargin = true;
    }
  }
  
  private void applyStyleToTextEditor(){    
      editorTabbedPane.setTextEditorFont(sampleArea.getFont());
      editorTabbedPane.setTextEditorBackground(sampleArea.getBackground());
      editorTabbedPane.setTextEditorForeground(sampleArea.getForeground());
      editorTabbedPane.enableTextEditorWordWrapping(sampleArea.hasWordWrapping()); 
      editorTabbedPane.enableTextEditorAutoIndenting(sampleArea.hasAutoIndenting()); 
      editorTabbedPane.setTextEditorTabSize(sampleArea.getTabSize()); 
      editorTabbedPane.enableTextEditorTabAsSpace(sampleArea.isTabInsertionAsSpaceEnabled());
      editorTabbedPane.refreshEditorTextPane();
  }
  
  private void updateInputFieldsFirstValues(){
    int styleIndex = fontStyles[editorOldFont.getStyle()];
    styleInputPane.setSelectedName(fontStyleNames.get(styleIndex));
    nameInputPane.setSelectedName(editorOldFont.getName());
    sizeInputPane.setSelectedName(String.valueOf(editorOldFont.getSize()));
    backgroundInputColor.setSelectedColor(editorOldBackground);
    foregroundInputColor.setSelectedColor(editorOldForeground);
    wordWrapCheckBox.setSelected(editorOldWordWrapping);
    autoIndentCheckBox.setSelected(editorOldAutoIndent);
    tabSizeField.setText(String.valueOf(editorOldTabSize));
    tabAsSpaceCheckBox.setSelected(editorOldTabAsSpace);
  } 
  
  public void loadTextEditorStyle(){    
    editorOldFont = editorTabbedPane.getTextEditorFont();
    editorOldBackground = editorTabbedPane.getTextEditorBackground();
    editorOldForeground = editorTabbedPane.getTextEditorForeground();
    editorOldWordWrapping = editorTabbedPane.isTextEditorWordWrappingEnabled();
    editorOldTabSize = editorTabbedPane.getTextEditorTabSize();
    editorOldAutoIndent = editorTabbedPane.isTextEditorAutoIndentEnabled();
    editorOldTabAsSpace = editorTabbedPane.isTextEditorTabAsSpaceEnabled();
  }
  public void updateSampleStyle(){
      sampleArea.setFont(editorOldFont);
      sampleArea.setBackground(editorOldBackground);
      sampleArea.setForeground(editorOldForeground);
      sampleArea.setCaretColor(editorOldForeground);  
      sampleArea.enableWordWrapping(editorOldWordWrapping);  
      sampleArea.setTabSize(editorOldTabSize);
      sampleArea.enableAutoIndent(editorOldAutoIndent);
      sampleArea.enableTabInsertionAsSpace(editorOldTabAsSpace);
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
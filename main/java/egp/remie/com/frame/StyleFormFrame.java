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
  private CustomeTextArea sampleArea = new CustomeTextArea();
  private Font editorOldFont = null;
  private Color editorOldBackground = null;
  private Color editorOldForeground = null;
  
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
  
  public StyleFormFrame(TreeNodeHostFileTextEditorTabbedPane editorTabbedPane){
    this.editorTabbedPane = editorTabbedPane;    
    loadTextEditorStyle();
    setResizable(false);
    setTitle("Style");
    setSize(400, 370);
    configureSampleLable();
    setAlwaysOnTop(true);
    
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
    loadSampleAreaStyleIntoInputs();
    
    MarginedBox fontFielsPane = new MarginedBox(MARGIN, BoxLayout.X_AXIS);  
    fontFielsPane.setAlignmentX(0.0f);
    fontFielsPane.add(nameInputPane);
    fontFielsPane.add(styleInputPane);
    fontFielsPane.add(sizeInputPane);
    
    MarginedBox selectColorPane = new MarginedBox(MARGIN, BoxLayout.Y_AXIS);  
    selectColorPane.ignoreInitialMargin();
    selectColorPane.ignoreLastMargin();
    selectColorPane.add(backgroundInputColor);
    selectColorPane.add(foregroundInputColor);
    Arrays.asList(selectColorPane.getComponents())
      .forEach(c -> ((JComponent)c).setAlignmentX(0.0f));
    
    CustomeTabbedPane sampleTabbedPane = new CustomeTabbedPane();
    sampleTabbedPane.add("Example.txt", new CustomeScrollPane(sampleArea));
    
    MarginedBox colorAndSamplePane = new MarginedBox(MARGIN, BoxLayout.X_AXIS); 
    colorAndSamplePane.setAlignmentX(0.0f);
    colorAndSamplePane.add(selectColorPane);
    colorAndSamplePane.add(sampleTabbedPane);
    Arrays.asList(colorAndSamplePane.getComponents())
      .forEach(c -> ((JComponent)c).setAlignmentY(1.0f));
    
    MarginedBox buttonPane = new MarginedBox(MARGIN, BoxLayout.X_AXIS);
    buttonPane.setAlignmentX(0.0f);
    buttonPane.add(cancelButton);
    buttonPane.add(Box.createHorizontalGlue());
    buttonPane.add(acceptButton);
    buttonPane.add(applyButton);
    
    MarginedBox mainContainer = new MarginedBox(MARGIN, BoxLayout.Y_AXIS);
    mainContainer.add(fontFielsPane);
    mainContainer.add(colorAndSamplePane);
    mainContainer.add(Box.createVerticalGlue());
    mainContainer.add(buttonPane);
    add(mainContainer);
  } 
  
  private String[] getMonospacedFontNames(){
    GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();  //ge.getAvailableFontFamilyNames(); Font[] osFonts = ge.getAllFonts();
    List<String> families = Arrays.asList(gEnv.getAvailableFontFamilyNames());
    List<String> monos = families.stream() // Font.getFamily
      .filter(f -> isMonospacedFont(new Font(f, Font.PLAIN, 10)))
      .collect(Collectors.toList());
    return monos.toArray(new String[0]);
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
      writeStylePreferences();
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
  }
  
  private void writeStylePreferences(){
    Font font = sampleArea.getFont();
    Color background = sampleArea.getBackground();
    Color foreground = sampleArea.getForeground();
    
    if(!(font.equals(editorOldFont)))
      UserPreferences.writeTextEditorFont(font);
    
    if(!(background.equals(editorOldBackground)))
      UserPreferences.writeBackground(background);
    
    if(!(foreground.equals(editorOldForeground)))
      UserPreferences.writeForeground(foreground);
  }
  
  private void applyStyleToTextEditor(){    
      editorTabbedPane.setTextEditorFont(sampleArea.getFont());
      editorTabbedPane.setTextEditorBackground(sampleArea.getBackground());
      editorTabbedPane.setTextEditorForeground(sampleArea.getForeground());
  }
  
  private void addFontInputPaneListSelectionListeners(){
    nameInputPane.addListSelectionListener(changeSampleLabelFontName);
    styleInputPane.addListSelectionListener(changeSampleLabelFontStyle);
    sizeInputPane.addListSelectionListener(changeSampleLabelFontSize);
  }
  
  private void configureSampleLable(){
    sampleArea.setOpaque(true);    
    sampleArea.setFont(editorOldFont);
    sampleArea.setText("This is an example");
    sampleArea.setLineWrap(true);
    sampleArea.setWrapStyleWord(true);
  }
  
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
  
  private void loadSampleAreaStyleIntoInputs(){
    Font sampleAreaFont = sampleArea.getFont();
    nameInputPane.setSelectedName(sampleAreaFont.getName());
    int styleIndex = fontStyles[sampleAreaFont.getStyle()];
    styleInputPane.setSelectedName(fontStyleNames.get(styleIndex));
    sizeInputPane.setSelectedName(String.valueOf(sampleAreaFont.getSize()));
    backgroundInputColor.setSelectedColor(sampleArea.getBackground());
    foregroundInputColor.setSelectedColor(sampleArea.getForeground());
  } 
  
  public void loadTextEditorStyle(){    
    editorOldFont = editorTabbedPane.getTextEditorFont();
    editorOldBackground = editorTabbedPane.getTextEditorBackground();
    editorOldForeground = editorTabbedPane.getTextEditorForeground();
  }
  
  @Override
  public void setVisible(boolean settingVisible){
    if(!isVisible()){
      loadTextEditorStyle();
    }
    
    if(settingVisible && !isVisible()){
      sampleArea.setFont(editorOldFont);
      sampleArea.setBackground(editorTabbedPane.getTextEditorBackground());
      sampleArea.setForeground(editorTabbedPane.getTextEditorForeground());
      sampleArea.setCaretColor(editorTabbedPane.getTextEditorForeground());
      loadSampleAreaStyleIntoInputs();
    }
    
    super.setVisible(settingVisible);
  }
  
}
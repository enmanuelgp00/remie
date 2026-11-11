package egp.remie.user;

import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class UserPreferences{
  
  private static Map<Editor, Object> preferencesMap = fetchPreferencesMap();
  public static enum Editor {
    AUTO_INDENTING,
    LINE_WRAPPING,
    TAB_SIZE,
    FONT,
    BACKGROUND,
    FOREGROUND
  }
    
  public static Path getPreferencesPath(){
    return getUserHomePath().resolve("remie.user.ser");
  }  
  public static Path getUserHomePath(){
    return Paths.get(System.getProperty("user.home"));
  }  
    
  public static void setEditorAutoIndenting(boolean indenting){
    put(Editor.AUTO_INDENTING, indenting);
  }
  public static void setEditorLineWrapping(boolean wrapping){
    put(Editor.LINE_WRAPPING, wrapping);
  }
  public static void setEditorTabSize(int size){
    put(Editor.TAB_SIZE, size);
  }
  public static void setEditorFont(Font font){
    put(Editor.FONT, font);
  }  
  public static void setEditorBackground(Color bg){
    put(Editor.BACKGROUND, bg);
  }  
  public static void setEditorForeground(Color fg){
    put(Editor.FOREGROUND, fg);
  }      
  
  public static Optional<Boolean> getEditorAutoIndenting(){
    return getBoolean(Editor.AUTO_INDENTING);
  }
  public static Optional<Boolean> getEditorLineWrapping(){
    return getBoolean(Editor.LINE_WRAPPING);
  }
  public static Optional<Integer> getEditorTabSize(){
    return getInt(Editor.TAB_SIZE);
  }
  public static Optional<Font> getEditorFont(){
    return getFont(Editor.FONT);
  }  
  public static Optional<Color> getEditorBackground(){
    return getColor(Editor.BACKGROUND);
  }  
  public static Optional<Color> getEditorForeground(){
    return getColor(Editor.FOREGROUND);
  }      
  
  private static Optional<Boolean> getBoolean(Editor key){
    return get(key, Boolean.class);
  }
  private static Optional<Font> getFont(Editor key){
    return get(key, Font.class);
  }  
  private static Optional<Color> getColor(Editor key){
    return get(key, Color.class);
  }  
  private static Optional<Integer> getInt(Editor key){
    return get(key, Integer.class);
  }    public static <T> Optional<T> get(Editor key, Class<T> classType){
    return Optional.ofNullable(classType.cast(preferencesMap.get(key)));
  }  
  public static void put(Editor key, Object obj){
    preferencesMap.put(key, obj);
  }
  
  public static void commit(){
    try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getPreferencesPath().toFile()))){
      oos.writeObject(preferencesMap);      
    }catch(IOException ex){
      ex.printStackTrace();
    }
  }  
  @SuppressWarnings("unchecked")
  private static HashMap<Editor, Object> fetchPreferencesMap(){
    HashMap<Editor, Object> preferences = new HashMap<Editor, Object>();
    try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getPreferencesPath().toFile()))){
      preferences = (HashMap<Editor, Object>)ois.readObject();
    }catch(IOException | ClassNotFoundException ex){
      ex.printStackTrace();
    }
    return preferences;
  }
}
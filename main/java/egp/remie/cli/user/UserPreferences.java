package egp.remie.cli.user;

import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class UserPreferences{
  
  private static Map<Integer, Object> preferencesMap = fetchPreferencesMap();
  private static final int EDITOR_AUTO_INDENTING = 0;
  private static final int EDITOR_LINE_WRAPPING = 1;
  private static final int EDITOR_TAB_SIZE = 2;
  private static final int EDITOR_BACKGROUND = 3;
  private static final int EDITOR_FOREGROUND = 4;
  private static final int EDITOR_FONT = 5;
  private static final int EDITOR_TAB_AS_SPACE = 6;
  
  public static Path getPreferencesPath(){
    return getUserHomePath().resolve("remie.user.ser");
  }  
  public static Path getUserHomePath(){
    return Paths.get(System.getProperty("user.home"));
  }  
    
  public static void setEditorTabAsSpace(boolean tabAsSpace){
    put(EDITOR_TAB_AS_SPACE, tabAsSpace);
  }
  public static void setEditorAutoIndenting(boolean indenting){
    put(EDITOR_AUTO_INDENTING, indenting);
  }
  public static void setEditorLineWrapping(boolean wrapping){
    put(EDITOR_LINE_WRAPPING, wrapping);
  }
  public static void setEditorTabSize(int size){
    put(EDITOR_TAB_SIZE, size);
  }
  public static void setEditorFont(Font font){
    put(EDITOR_FONT, font);
  }  
  public static void setEditorBackground(Color bg){
    put(EDITOR_BACKGROUND, bg);
  }  
  public static void setEditorForeground(Color fg){
    put(EDITOR_FOREGROUND, fg);
  }      
  
  public static Optional<Boolean> getEditorTabAsSpace(){
    return getBoolean(EDITOR_TAB_AS_SPACE);
  }
  public static Optional<Boolean> getEditorAutoIndenting(){
    return getBoolean(EDITOR_AUTO_INDENTING);
  }
  public static Optional<Boolean> getEditorWordWrapping(){
    return getBoolean(EDITOR_LINE_WRAPPING);
  }
  public static Optional<Integer> getEditorTabSize(){
    return getInt(EDITOR_TAB_SIZE);
  }
  public static Optional<Font> getEditorFont(){
    return getFont(EDITOR_FONT);
  }  
  public static Optional<Color> getEditorBackground(){
    return getColor(EDITOR_BACKGROUND);
  }  
  public static Optional<Color> getEditorForeground(){
    return getColor(EDITOR_FOREGROUND);
  }      
  
  private static Optional<Boolean> getBoolean(int key){
    return get(key, Boolean.class);
  }
  private static Optional<Font> getFont(int key){
    return get(key, Font.class);
  }  
  private static Optional<Color> getColor(int key){
    return get(key, Color.class);
  }  
  private static Optional<Integer> getInt(int key){
    return get(key, Integer.class);
  }    public static <T> Optional<T> get(int key, Class<T> classType){
    return Optional.ofNullable(classType.cast(preferencesMap.get(key)));
  }  
  public static void put(int key, Object obj){
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
  private static HashMap<Integer, Object> fetchPreferencesMap(){
    HashMap<Integer, Object> preferences = new HashMap<Integer, Object>();
    try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getPreferencesPath().toFile()))){
      preferences = (HashMap<Integer, Object>)ois.readObject();
    }catch(IOException | ClassNotFoundException ex){
      ex.printStackTrace();
    }
    return preferences;
  }
}
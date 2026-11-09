package egp.remie.user;

import java.awt.*;
import java.io.*;
import java.nio.file.*;

public class UserPreferences{
    
  public static Path getBackgroundPath(){
    return getUserHomePath().resolve("remie.bg.obj");
  }   
  
  public static Path getForegroundPath(){
    return getUserHomePath().resolve("remie.fg.obj");
  }  
 
  public static Path getTextEditorFontPath(){
    return getUserHomePath().resolve("remie.te.font.obj");
  }
  
  public static Path getUserHomePath(){
    return Paths.get(System.getProperty("user.home"));
  }
  
  public static void writeTextEditorFont(Font font){
    try{      
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getTextEditorFontPath().toFile()));
      oos.writeObject(font);      
      oos.close();
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  public static Font readTextEditorFont(){
    Font font = null;
    try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getTextEditorFontPath().toFile()))){
      font = (Font)ois.readObject();
    }catch(Exception ex){
      ex.printStackTrace();      
    }
    return font;
  }
  
  public static void writeBackground(Color background){
    try{      
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getBackgroundPath().toFile()));
      oos.writeObject(background);      
      oos.close();
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  public static Color readBackground(){
    Color background = Color.BLACK;
    try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getBackgroundPath().toFile()))){
      background = (Color)ois.readObject();
    }catch(Exception ex){
      ex.printStackTrace();      
    }
    return background;
  }
  public static void writeForeground(Color foreground){
    try{      
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getForegroundPath().toFile()));
      oos.writeObject(foreground);      
      oos.close();
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  public static Color readForeground(){
    Color foreground = Color.WHITE;
    try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getForegroundPath().toFile()))){
      foreground = (Color)ois.readObject();
    }catch(Exception ex){
      ex.printStackTrace();      
    }
    return foreground;
  }
}
package egp.remie.com.tree;

class DummyNode{
  private boolean loading = false;
  private boolean running = false;
  private final String[] loadingSprites = new String[]{
    "ooo", "0oo", "00o", "o00",
    "oo0"};
  private int spriteIndex = 0;
  private int frameCount = 1;
  
  DummyNode(){ }
  
  @Override
  public String toString(){
    return loadingSprites[spriteIndex];
  }
  
  void nextSprite(){
    spriteIndex = frameCount++ % loadingSprites.length;
  }  
 
  void setIsRunning(boolean running){
    this.running = running;
  }
  
  boolean isRunning(){
    return running;
  }  
  
  void setLoading(boolean loading){
    this.loading = loading;
  }
  
  boolean isLoading(){
    return loading;
  }
}
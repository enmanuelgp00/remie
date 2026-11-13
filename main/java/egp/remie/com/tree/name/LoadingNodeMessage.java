package egp.remie.com.tree.name;

import javax.swing.tree.*;

public class LoadingNodeMessage {
  private boolean loading = false;
  private boolean running = false;
  private final String[] loadingSprites = new String[]{
    "ooo", "0oo", "00o", "o00",
    "oo0"};
  private int spriteIndex = 0;
  private int frameCount = 1;
  private final DefaultTreeModel treeModel;
  private final DefaultMutableTreeNode treeNode;
  
  public LoadingNodeMessage(DefaultTreeModel treeModel, DefaultMutableTreeNode treeNode){
    this.treeModel = treeModel;
    this.treeNode = treeNode;
  }
  
  @Override
  public String toString(){
    return loadingSprites[spriteIndex];
  }
  
  private void nextSprite(){
    spriteIndex = frameCount++ % loadingSprites.length;
  }  
 
  public void setAnimationRunning(boolean running){
    this.running = running;
  }
  
  public boolean isAnimationRunning(){
    return running;
  }  
  
  public DefaultMutableTreeNode getTreeNode(){
    return treeNode;
  }
  
  public DefaultTreeModel getTreeModel(){
    return treeModel;
  }
  
  public void stopAnimation(){
    setAnimationRunning(false);
  }
  
  public void startAnimation(){
    Thread animation = new Thread(()->{
      if(isAnimationRunning())
        return;
      setAnimationRunning(true);
      while(isAnimationRunning()){
        System.out.println("dummy animation running");
        try{
          Thread.sleep(300);
          nextSprite();
          getTreeModel().nodeChanged(getTreeNode());        
        }catch(Exception ex){ex.printStackTrace();}
      };
      setAnimationRunning(false);
    });    
    animation.start();
  }
}
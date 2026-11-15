package egp.remie.cli.com.tree.name;

import javax.swing.tree.*;

public class LoadingNodeMessage {
  private volatile boolean running = false;
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
 
  public synchronized void setAnimationRunning(boolean running){
    this.running = running;
  }
  
  public synchronized boolean isAnimationRunning(){
    return running;
  }  
  
  public DefaultMutableTreeNode getTreeNode(){
    return treeNode;
  }
  
  public DefaultTreeModel getTreeModel(){
    return treeModel;
  }
  
  public void stopAnimation(){
    //System.out.println("stoping animation");
    running = false;
  }
  
  public void startAnimation(){
    if(isAnimationRunning())
      return;
    Thread animation = new Thread(()->{
      int counter = 0;
      try{
        while(isAnimationRunning()){
          //System.out.println("dummy animation running " + counter++);
          Thread.sleep(300);
          nextSprite();
          getTreeModel().nodeChanged(getTreeNode());   
        };     
      }catch(Exception ex){
        ex.printStackTrace();
      }
      setAnimationRunning(false);
    });    
    setAnimationRunning(true);
    animation.start();
  }
}
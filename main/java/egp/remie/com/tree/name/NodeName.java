package egp.remie.com.tree.name;

public class NodeName {
  private final String name;
  public NodeName(String name){
    this.name = name;
  }
  
  public String getName(){
    return name;
  }
  @Override
  public String toString(){
    return name;
  }
}
package egp.remie.com.tree;

public class WorkSpaceName{
  private final String name;
  public WorkSpaceName(String name){
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
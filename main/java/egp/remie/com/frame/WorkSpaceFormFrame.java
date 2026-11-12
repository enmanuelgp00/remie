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

public class WorkSpaceFormFrame extends CustomeFrame {
  private final WorkSpaceNodeTree tree;
  public WorkSpaceFormFrame(WorkSpaceNodeTree tree){
    this.tree = tree;
  }

}
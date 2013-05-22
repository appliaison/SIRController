package ch.ethz.naro;

import java.util.EventObject;

public class LightHandler  extends java.util.EventObject {

  int cam, left, right;
  
  public LightHandler(Object source, int cam, int left, int right) {
    super(source);
    this.cam = cam;
    this.left =left;
    this.right = right;
  }
  
  public interface LightHandlerListener {
    public void handleLightEvent(LightHandler handler);
  }
  
}


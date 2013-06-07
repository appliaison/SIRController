package ch.ethz.naro;

public class ButtonHandler extends java.util.EventObject {
  
  boolean status;
  String name;

  public ButtonHandler(Object source, String name, boolean buttonStatus) {
    super(source);
    
    this.name = name;
    this.status = buttonStatus;
  }
  
  public interface ButtonHandlerListener {
    public void handleButtonEvent(ButtonHandler handler);
  }

}

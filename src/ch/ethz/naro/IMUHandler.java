package ch.ethz.naro;

public class IMUHandler extends java.util.EventObject {
  double angX, angY, angZ;
  
  public IMUHandler(Object source, double angX, double angY, double angZ ) {
    super(source);
    this.angX = angX;
    this.angY = angY;
    this.angZ = angZ;
  }
  
  public interface IMUhandlerListener {
    public void handleIMUEvent(IMUHandler handler);
  }
}

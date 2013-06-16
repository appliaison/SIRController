package ch.ethz.naro;

public class SensorHandler extends java.util.EventObject{

  double lLeft, lRight;
  
  public SensorHandler(Object source, double left, double right) {
    super(source);
    lLeft = left;
    lRight = right;
  }

  public interface SensorHandlerListener {
    public void handleSensorEvent(SensorHandler handler);
  }
}

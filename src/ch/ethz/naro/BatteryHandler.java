package ch.ethz.naro;

public class BatteryHandler extends java.util.EventObject{
  
  public double current;
  public double voltage;

  public BatteryHandler(Object source, double cur, double vol) {
    super(source);
    current = cur;
    voltage = vol;
  }
  
  public interface BatteryHandlerListener {
    public void handleBatteryEvent(BatteryHandler handler);
  }

}

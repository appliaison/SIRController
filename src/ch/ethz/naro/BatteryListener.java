package ch.ethz.naro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import android.util.Log;
import ch.ethz.naro.BatteryHandler.BatteryHandlerListener;
import ch.ethz.naro.SensorHandler.SensorHandlerListener;

public class BatteryListener extends AbstractNodeMain {
  
  // ---- Event handler ------------
  private List _listeners = new ArrayList();
  
  public synchronized void addEventListener(BatteryHandlerListener listener) {
    Log.i("Joy", "addEventListener called");
    _listeners.add(listener);
  }
  public synchronized void removeEventListener(BatteryHandlerListener listener) {
    //Log.i("Joy", "removeEventListener called");
    _listeners.remove(listener);
  }
  
  private synchronized void fireBatteryStatus(double cur, double vol) {
    //Log.i("IMU", "fireEvent called");
    BatteryHandler event = new BatteryHandler(this, cur, vol);
    
    Iterator i = _listeners.iterator();
    
    while(i.hasNext()) {
      ((BatteryHandlerListener) i.next()).handleBatteryEvent(event);
    }
  } 
  // ---------------

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("BatteryListener");
  }
  
  public void onStart(ConnectedNode connectedNode) {
    Subscriber<mbed_controller.SIRPowerSupply> battery = connectedNode.newSubscriber("Sensors/PowerSupply", mbed_controller.SIRPowerSupply._TYPE);
    
    battery.addMessageListener(new MessageListener<mbed_controller.SIRPowerSupply>() {
      
      @Override
      public void onNewMessage(mbed_controller.SIRPowerSupply message) {
        
        double batCur = message.getCurrent();
        double batVol = message.getVoltage();
        
        fireBatteryStatus(batCur, batVol);
      }
    });
  }

}

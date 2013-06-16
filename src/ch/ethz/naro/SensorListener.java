package ch.ethz.naro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mbed_controller.SIRDistances;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import android.util.Log;
import ch.ethz.naro.IMUHandler.IMUhandlerListener;
import ch.ethz.naro.SensorHandler.SensorHandlerListener;

public class SensorListener extends AbstractNodeMain {
  
  // ---- Event handler ------------
  private List _listeners = new ArrayList();
  
  public synchronized void addEventListener(SensorHandlerListener listener) {
    Log.i("Joy", "addEventListener called");
    _listeners.add(listener);
  }
  public synchronized void removeEventListener(SensorHandlerListener listener) {
    //Log.i("Joy", "removeEventListener called");
    _listeners.remove(listener);
  }
  
  private synchronized void fireDistance(double left, double right) {
    //Log.i("IMU", "fireEvent called");
    SensorHandler event = new SensorHandler(this, left, right);
    
    Iterator i = _listeners.iterator();
    
    while(i.hasNext()) {
      ((SensorHandlerListener) i.next()).handleSensorEvent(event);
    }
  } 
  // ---------------

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("SensorListener");
  }
  
  public void onStart(ConnectedNode connectedNode) {
    Subscriber<mbed_controller.SIRDistances> distance = connectedNode.newSubscriber("Sensors/Distance", mbed_controller.SIRDistances._TYPE);
    
    distance.addMessageListener(new MessageListener<mbed_controller.SIRDistances>() {
      
      @Override
      public void onNewMessage(mbed_controller.SIRDistances message) {
        double lLeft = message.getLowerleft();
        double lRight = message.getLowerright();
        
        fireDistance(lLeft, lRight);
      }
    });
  }

}

package ch.ethz.naro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ch.ethz.naro.DrivetrainHandler.DrivetrainHandlerListener;
import ch.ethz.naro.IMUHandler.IMUhandlerListener;

import android.util.Log;

public class DrivetrainListener extends AbstractNodeMain{
  
  // ---- Event handler ------------
  private List _listeners = new ArrayList();
  
  public synchronized void addEventListener(DrivetrainHandlerListener listener) {
    _listeners.add(listener);
  }
  public synchronized void removeEventListener(DrivetrainHandlerListener listener) {
    _listeners.remove(listener);
  }
  
  private synchronized void fireValues(double[] pos, double[] speed, double[] tor, double gearRatio, double wheelR) {
    //Log.i("IMU", "fireEvent called");
    DrivetrainHandler event = new DrivetrainHandler(this, pos, speed, tor, gearRatio, wheelR);
    
    Iterator i = _listeners.iterator();
    
    while(i.hasNext()) {
      ((DrivetrainHandlerListener) i.next()).handleDrivetrainEvent(event);
    }
  } 
  // ---------------

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("DrivetrainListener");
  }
  
  public void onStart(ConnectedNode connectedNode) {

    Subscriber<base_controller.DrivetrainData> subscriber 
      = connectedNode.newSubscriber("/sir/base_controller/drivetrain_data", base_controller.DrivetrainData._TYPE);
    
    subscriber.addMessageListener(new MessageListener<base_controller.DrivetrainData>() {
      @Override
      public void onNewMessage(base_controller.DrivetrainData msg) {
        
        fireValues(msg.getPositions(), msg.getSpeeds(), msg.getTorques(), msg.getGearRatio(), msg.getWheelRadius());

      }
    });
    
  }

}

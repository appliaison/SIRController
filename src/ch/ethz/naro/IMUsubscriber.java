package ch.ethz.naro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import ch.ethz.naro.IMUHandler.IMUhandlerListener;

import android.util.Log;

public class IMUsubscriber extends AbstractNodeMain {
  
  // ---- Event handler ------------
  private List _listeners = new ArrayList();
  
  public synchronized void addEventListener(IMUhandlerListener listener) {
    Log.i("Joy", "addEventListener called");
    _listeners.add(listener);
  }
  public synchronized void removeEventListener(IMUhandlerListener listener) {
    //Log.i("Joy", "removeEventListener called");
    _listeners.remove(listener);
  }
  
  private synchronized void fireOrientation(double x, double y , double z) {
    //Log.i("IMU", "fireEvent called");
    IMUHandler event = new IMUHandler(this, x, y, z);
    
    Iterator i = _listeners.iterator();
    
    while(i.hasNext()) {
      Log.i("Subscriber", "shoooot!");
      ((IMUhandlerListener) i.next()).handleIMUEvent(event);
    }
  } 
  // ---------------

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("IMUsubscriber");
  }
  
  public void onStart(ConnectedNode connectedNode) {
    Log.i("ROS", "IMU listener started");
    
    Subscriber<geometry_msgs.Vector3> subscriber = connectedNode.newSubscriber("/Sensors/AngOrientation", geometry_msgs.Vector3._TYPE);
    
    subscriber.addMessageListener(new MessageListener<geometry_msgs.Vector3>() {
      @Override
      public void onNewMessage(geometry_msgs.Vector3 message) {
        
        //Log.i("IMU", "got msg");
        
        double angleX = message.getX();
        double angleY = message.getY();
        double angleZ = message.getZ();
        
        fireOrientation(angleX, angleY, angleZ);
      }
    });
  }

}

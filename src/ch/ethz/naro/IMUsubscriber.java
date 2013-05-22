package ch.ethz.naro;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import android.util.Log;

public class IMUsubscriber extends AbstractNodeMain {

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("IMUsubscriber");
  }
  
  public void onStart(ConnectedNode connectedNode) {
    Log.i("ROS", "IMU listener started");
    Subscriber<mbed_controller.SIRImu> subscriber = connectedNode.newSubscriber("/Sensors/Imu", mbed_controller.SIRImu._TYPE);
    subscriber.addMessageListener(new MessageListener<mbed_controller.SIRImu>() {
      @Override
      public void onNewMessage(mbed_controller.SIRImu message) {
        double avX = message.getAngvlX();
      }
    });
  }

}

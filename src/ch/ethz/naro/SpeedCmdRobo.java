package ch.ethz.naro;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import android.util.Log;

import ch.ethz.naro.JoyHandler.JoyHandlerListener;

public class SpeedCmdRobo extends AbstractNodeMain implements JoyHandlerListener {
	
	private Publisher<geometry_msgs.Twist> publisherRobo;
	private Publisher<mbed_controller.SIRsetCAM> publisherCamera;

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("SpeedCmdRobo");
	}
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		publisherRobo = connectedNode.newPublisher("sir/base_controller/cmd_vel", geometry_msgs.Twist._TYPE);
		publisherCamera = connectedNode.newPublisher("sir/camera/pan_tilt", mbed_controller.SIRsetCAM._TYPE);
	}

  @Override
  public void handleJoyEvent(JoyHandler handler) {
    // Send speed cmd to robot
    if(handler.name == "JoyRobot") {
      float x = handler.x;
      float y = handler.y;
  
      float v = y*y*y;
      float omega = x*x*x;
      
      // generate Twist Message  
      geometry_msgs.Twist twist = publisherRobo.newMessage();
      twist.getLinear().setX(v);      // set linear speed
      twist.getAngular().setZ(omega); // set angular speed
      publisherRobo.publish(twist);
    }
    // Send position cmd to camera
    if(handler.name == "JoyCamera") {
      float x = handler.x;
      float y = handler.y;
  
      float tilt = y*y*y;
      float pan = x*x*x;
      
      // generate Camera Message  
      mbed_controller.SIRsetCAM camera = publisherCamera.newMessage();
      camera.setPan(pan);
      camera.setTilt(tilt);

      publisherCamera.publish(camera);
    }
      
  }

}

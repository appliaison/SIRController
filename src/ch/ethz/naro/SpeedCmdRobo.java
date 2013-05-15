package ch.ethz.naro;

import java.sql.Array;
import java.util.Arrays;
import java.util.List;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import android.util.Log;

import ch.ethz.naro.JoyHandler.JoyHandlerListener;

public class SpeedCmdRobo extends AbstractNodeMain implements JoyHandlerListener {
	
	private Publisher<geometry_msgs.Twist> publisherRobo;
	private Publisher<sensor_msgs.JointState> publisherCamera;

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("SpeedCmdRobo");
	}
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		publisherRobo = connectedNode.newPublisher("sir/base_controller/cmd_vel", geometry_msgs.Twist._TYPE);
		publisherCamera = connectedNode.newPublisher("sir/camera/pan_tilt", sensor_msgs.JointState._TYPE);
	}

  @Override
  public void handleJoyEvent(JoyHandler handler) {
    // Send speed cmd to robot
    if(handler.name == "JoyRobot") {
      Log.i("Deb", "Got robo cmd");
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
      Log.i("Deb", "Got Camera msg");
      float x = handler.x;
      float y = handler.y;
  
      double tilt = y*y*y;
      double pan = x*x*x;
      
      // generate JointState Message  
      sensor_msgs.JointState state = publisherCamera.newMessage();
      List<String> names = Arrays.asList("pan", "tilt"); // set names
      state.setName(names);
      double[] positions = new double[] {pan, tilt};
      state.setPosition(positions);
      publisherCamera.publish(state);
    }
    
    
  }

}

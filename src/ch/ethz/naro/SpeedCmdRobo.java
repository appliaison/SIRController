package ch.ethz.naro;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import android.util.Log;

import ch.ethz.naro.JoyHandler.JoyHandlerListener;

public class SpeedCmdRobo extends AbstractNodeMain implements JoyHandlerListener {
  
  private static final String iTag = "Joy msg";
	
	private Publisher<geometry_msgs.Twist> publisherRobo;
	private Publisher<mbed_controller.SIRsetCAM> publisherCamera;
	
	private float speedX;
	private float speedZ;
	
	private float _camPosPan;
	private float _camPosTilt;

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("SpeedCmdRobo");
	}
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {

	  publisherRobo = connectedNode.newPublisher("sir/base_controller/cmd_vel", geometry_msgs.Twist._TYPE);
		publisherCamera = connectedNode.newPublisher("mbed_com/pan_tilt", mbed_controller.SIRsetCAM._TYPE);
		
		// set initial camera position to [0.5; 0.5]
		mbed_controller.SIRsetCAM initCamera = publisherCamera.newMessage();
		initCamera.setPan(0.5f);
		_camPosPan = 0.5f;
		initCamera.setTilt(0.5f);
		_camPosTilt = 0.5f;
		
		publisherCamera.publish(initCamera);
		
	}

  @Override
  public void handleJoyEvent(JoyHandler handler) {
    // Send speed cmd to robot
    if(handler.name == "JoyRobot") {
      float x = handler.x;
      float y = handler.y;
  
      this.speedX = y*y*y;
      this.speedZ = x*x*x;
      
      // generate Twist Message  
      geometry_msgs.Twist twist = publisherRobo.newMessage();
      twist.getLinear().setX(speedX);      // set linear speed
      twist.getAngular().setZ(speedZ); // set angular speed
      
      publisherRobo.publish(twist);
    }
    
    // Send position cmd to camera
    if (publisherCamera != null) { // check if null
      if(handler.name == "JoyCamera") {
        float speedPan = handler.x;
        float speedTilt = handler.y;
        
        float maxStep = 0.1f;
    
        // check if position in Range [0,1]
        // Pan
        if(_camPosPan>=0 && _camPosPan<=1) {
          // set new position
          _camPosPan += maxStep*speedPan;
          // check if out of boundes
          if(_camPosPan<0) {
            _camPosPan = 0;
          } else if(_camPosPan>1) {
            _camPosPan = 1;
          }
        }
        // Tilt
        if(_camPosTilt>=0 && _camPosTilt<=1) {
          // set new position
          _camPosTilt += maxStep*speedTilt;
          // check if out of boundes
          if(_camPosTilt<0) {
            _camPosTilt = 0;
          } else if(_camPosTilt>1) {
            _camPosTilt = 1;
          }
        }
        
        // generate Camera Message  
        mbed_controller.SIRsetCAM camera = publisherCamera.newMessage();
        camera.setPan(_camPosPan);
        camera.setTilt(_camPosTilt);
  
        publisherCamera.publish(camera);
      }
    } 
  }
  
  

}

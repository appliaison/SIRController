package ch.ethz.naro;

import java.util.Timer;
import java.util.TimerTask;

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
	
	private float _camPosPan;
	private float _camPosTilt;
	
	private Timer mTimer;
	private tTask_class tTask;

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("SpeedCmdRobo");
	}
	
	// ------
	// Task manager for constant publishing
	private class tTask_class extends TimerTask {
	  private float spdX;
	  private float spdZ;
	  
	  private Publisher<geometry_msgs.Twist> publisherRobo;
	  
	  public tTask_class(Publisher<geometry_msgs.Twist> d) {
	    this.spdX = 0;
	    this.spdZ = 0;
	    this.publisherRobo = d;
	  }
	  
	  public void setNewSpeeds(float spdx, float spdz) {
	    synchronized(this) {
	      this.spdX = spdx;
	      this.spdZ = -spdz; // negative sign for intuitive turning 
	    }
	  }
	  
    @Override
    public void run() {
      geometry_msgs.Twist twist = publisherRobo.newMessage();
      synchronized(this) {
        twist.getLinear().setX(this.spdX);      // set linear speed
        twist.getAngular().setZ(this.spdZ); // set angular speed
      }
      publisherRobo.publish(twist);
    }
	}
	//---------
	
	// when ROS connected:
	@Override
	public void onStart(final ConnectedNode connectedNode) {

	  publisherRobo = connectedNode.newPublisher("sir/base_controller/cmd_vel", geometry_msgs.Twist._TYPE);
		publisherCamera = connectedNode.newPublisher("sir/mbed_com/pan_tilt", mbed_controller.SIRsetCAM._TYPE);
		
		// set initial camera position to [0.5; 0.5]
		mbed_controller.SIRsetCAM initCamera = publisherCamera.newMessage();
		initCamera.setPan(0.5f);
		_camPosPan = 0.5f;
		initCamera.setTilt(0.5f);
		_camPosTilt = 0.5f;
		
		publisherCamera.publish(initCamera);
		
		tTask = new tTask_class(publisherRobo);
		
		mTimer = new Timer();
		mTimer.schedule(tTask, 0, 20); // publish with const. 50 Hz
		
	}

	// handle input from Joystick
  @Override
  public void handleJoyEvent(JoyHandler handler) {
    // Send speed cmd to robot
    if(publisherRobo != null) {
      if(handler.name == "JoyRobot") {
        float x = handler.x;
        float y = handler.y;
        this.tTask.setNewSpeeds(y*y*y, x*x*x);
      }
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

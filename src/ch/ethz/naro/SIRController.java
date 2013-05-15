package ch.ethz.naro;

import java.util.EventObject;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import ch.ethz.naro.VirtualJoystick;

import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SIRController extends SIRActivity{

  private VirtualJoystick joy1;
	private VirtualJoystick joy2;
	private GLSurfaceView cubeView;
	
	private SpeedCmdRobo speedChatter;
	
	private RajawaliTutRenderer cubeRenderer;

	public SIRController() {
    super("SIRController", "SIRController");
  }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
	
		RelativeLayout blLay = (RelativeLayout) findViewById(R.id.bottom_l);
		RelativeLayout brLay = (RelativeLayout) findViewById(R.id.bottom_r);
		RelativeLayout tmLay = (RelativeLayout) findViewById(R.id.top_m);
		
    // -------- Add Rajawali Surface ----------
	
    cubeRenderer = new RajawaliTutRenderer(this);
    cubeRenderer.setSurfaceView(mSurfaceView);
    super.setRenderer(cubeRenderer);
    tmLay.addView(mLayout); // mLayout from RajawaliActivity

    // -----------
		// Init ROS Nodes
		speedChatter = new SpeedCmdRobo();

		// ----------- Implement Joystick -----

		joy1 = new VirtualJoystick(blLay,150, 150, 100, "JoyRobot");
		joy1.addEventListener(speedChatter);
		joy2 = new VirtualJoystick(brLay,150, 150, 100, "JoyCamera");
		joy2.addEventListener(speedChatter);
    
		// ------------------------------------------		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	// ------ Init ROS -------
  @Override
  protected void init(NodeMainExecutor nodeMainExecutor) {
    NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(
        InetAddressFactory.newNonLoopback().getHostAddress(), getMasterUri());
    
    nodeMainExecutor.execute(speedChatter, nodeConfiguration);
  }

}
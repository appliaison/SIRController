package ch.ethz.naro;

import org.ros.address.InetAddressFactory;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import ch.ethz.naro.VirtualJoystick;

import android.os.Bundle;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class SIRController extends SIRActivity{

  private VirtualJoystick joy1;
	private VirtualJoystick joy2;
	
	private LightControl light;
	
	private RobotModelRenderer cubeRenderer;
	
	private SpeedCmdRobo speedChatter;
	private LightPublisher lightChatter;
	private IMUsubscriber imuSub;
	
	private Plot testPlot; // Plot

	public SIRController() {
    super("SIRController", "SIRController");
  }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		
		
	// Get different layouts
		RelativeLayout blLay = (RelativeLayout) findViewById(R.id.bottom_l);
		RelativeLayout brLay = (RelativeLayout) findViewById(R.id.bottom_r);
		FrameLayout tmLay = (FrameLayout) findViewById(R.id.tab_1);
		FrameLayout tab2 = (FrameLayout) findViewById(R.id.tab_2);
		RelativeLayout trLay = (RelativeLayout) findViewById(R.id.top_r);
		
    // -------- Add Model Surface ----------
		
    cubeRenderer = new RobotModelRenderer(this);
    cubeRenderer.setSurfaceView(mSurfaceView);
    super.setRenderer(cubeRenderer);
    tmLay.addView(mLayout); // mLayout from RajawaliActivity
    
    // -----------
    
    // -------- Add IMU Listener ----------
    imuSub = new IMUsubscriber();
    imuSub.addEventListener(cubeRenderer);
    // ------------------------------------
    
    
		// Init ROS Nodes
		speedChatter = new SpeedCmdRobo();
		lightChatter = new LightPublisher();

		// ----------- Implement Joystick -----
		joy1 = new VirtualJoystick(blLay,200, 230, 150, "JoyRobot");
		joy1.addEventListener(speedChatter);
		joy2 = new VirtualJoystick(brLay,200, 230, 150, "JoyCamera");
		joy2.addEventListener(speedChatter);
		// ------------------------------------------	
		
		// ---------- Implement Lightcontrol ---------
		light = new LightControl(trLay);
		light.addEventListener(lightChatter);
		// -------------------------------------------
		
		// ---------- Plot ------------------------
		testPlot = new Plot(tab2);
		// ----------------------------------------

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
    nodeMainExecutor.execute(lightChatter, nodeConfiguration);
    nodeMainExecutor.execute(imuSub, nodeConfiguration);
  }

}
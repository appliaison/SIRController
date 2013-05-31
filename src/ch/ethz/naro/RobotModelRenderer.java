
package ch.ethz.naro;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ch.ethz.naro.IMUHandler.IMUhandlerListener;

import android.content.Context;
import android.util.Log;
import rajawali.BaseObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.SimpleMaterial;
import rajawali.parser.AParser.ParsingException;
import rajawali.parser.StlParser;
import rajawali.renderer.RajawaliRenderer;

public class RobotModelRenderer extends RajawaliRenderer implements IMUhandlerListener {

	private BaseObject3D mObject;
	private DirectionalLight mLight;
	
	// init orientation robot
	private float angleX;
	private float angleY;
	private float angleZ;

	public RobotModelRenderer(Context context) {
		super(context);
		setFrameRate(60);
	}

	protected void initScene() {
	  // set background-color
	  getCurrentScene().setBackgroundColor(0x000000);
	  
	  // create light
	  mLight = new DirectionalLight(1f, 0.2f, -1.0f); // set the direction
	  mLight.setLookAt(0.0f, 0.0f, 0.0f);
	  mLight.setColor(1.0f, 1.0f, 1.0f);
	  mLight.setPower(5);
		
		// parse object from .stl
    StlParser stlParser = new StlParser(mContext.getResources(), mTextureManager, R.raw.model_sir_stl);
    try {
      stlParser.parse();
    } catch (ParsingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    mObject = stlParser.getParsedObject();
    
    SimpleMaterial simple = new SimpleMaterial();
    simple.setUseColor(true);
    mObject.setMaterial(simple);
    // set object color
    mObject.setColor(0xffffff);
    // add light
    mObject.addLight(mLight);
    
    // place object in the origin
    mObject.setX(0);
    mObject.setY(0);
    mObject.setZ(0);
    
    // set init orientation
    angleX = 0;
    angleY = 90;
    angleZ = 90;
    mObject.setRotation(angleX, angleY, angleZ);

    // scale objact
    mObject.setScale(0.007f);
    mObject.setDoubleSided(true);
		addChild(mObject);

		// set camera
		getCurrentCamera().setZ(4);
		getCurrentCamera().setLookAt(0, 0, 0);
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
	}

	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		
		/*
		mObject.setRotX(mObject.getRotX() + 1);
		mObject.setRotY(mObject.getRotY() + 1);
		*/

		mObject.setRotation(angleX, angleY, angleZ);

	}

	// get Robot-Orientation from ROS
  @Override
  public void handleIMUEvent(IMUHandler handler) {
    
    Log.i("IMU", "got msg model");
    this.angleX = (float) handler.angX;
    this.angleY = (float) handler.angY;
    this.angleZ = (float) handler.angZ;  
  }
}


package ch.ethz.naro;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ch.ethz.naro.IMUHandler.IMUhandlerListener;

import android.content.Context;
import android.util.Log;
import rajawali.BaseObject3D;
import rajawali.lights.ALight;
import rajawali.lights.DirectionalLight;
import rajawali.lights.PointLight;
import rajawali.lights.SpotLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.materials.GouraudMaterial;
import rajawali.materials.PhongMaterial;
import rajawali.materials.SimpleMaterial;
import rajawali.parser.AParser.ParsingException;
import rajawali.parser.StlParser;
import rajawali.renderer.RajawaliRenderer;

public class RobotModelRenderer extends RajawaliRenderer implements IMUhandlerListener {

	private BaseObject3D mObject;
	//private SpotLight light;

	
	// orientation robot
	private float angleX;
	private float angleY;
	private float angleZ;

	public RobotModelRenderer(Context context) {
		super(context);
		setFrameRate(60);
	}

	protected void initScene() {
	  // set background-color
	  getCurrentScene().setBackgroundColor(0xffffff);
	  
		// parse object from .stl
    StlParser stlParser = new StlParser(mContext.getResources(), mTextureManager, R.raw.model_sir_stl);
    try {
      stlParser.parse();
    } catch (ParsingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    // create Object from .stl file
    mObject = stlParser.getParsedObject();
    
    // add material to object
    GouraudMaterial material = new GouraudMaterial();
    material.setUseColor(true);
    material.setAmbientColor(230);
    mObject.setMaterial(material);
    
    // set object color
    mObject.setColor(0x000000);

    // create light
    SpotLight light = new SpotLight(0,-1,0);
    light.setPosition(0, 10, 10);
    light.setLookAt(0, 0, 0);
    light.setPower(1);
    mObject.addLight(light); // add Light
    
    // place object in the origin
    mObject.setX(0);
    mObject.setY(0);
    mObject.setZ(0);
    
    // set init orientation
    angleX = 0;
    angleY = 0;
    angleZ = 0;

    // scale objact
    mObject.setScale(0.007f);
    mObject.setDoubleSided(true);
		addChild(mObject);

		// set camera
		getCurrentCamera().setPosition(4, 0, 0);
		getCurrentCamera().setLookAt(0, 0, 0);
		getCurrentCamera().setRotZ(90);
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
	}

	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		
		// update rotation on IMU
		mObject.setRotation(angleX, angleY, angleZ);

	}

	// get Robot-Orientation from ROS
  @Override
  public void handleIMUEvent(IMUHandler handler) {
    
    //Log.i("IMU", "got msg model");
    this.angleX = (float) handler.angX;
    this.angleY = (float) handler.angY;
    this.angleZ = (float) -handler.angZ;  
  }
}

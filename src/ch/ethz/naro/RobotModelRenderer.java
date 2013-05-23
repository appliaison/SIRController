
package ch.ethz.naro;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import rajawali.BaseObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.SimpleMaterial;
import rajawali.parser.AParser.ParsingException;
import rajawali.parser.StlParser;
import rajawali.renderer.RajawaliRenderer;

public class RobotModelRenderer extends RajawaliRenderer {

	private BaseObject3D mObject;

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
    
    mObject = stlParser.getParsedObject();
    
    SimpleMaterial simple = new SimpleMaterial();
    simple.setUseColor(true);
    mObject.setMaterial(simple);
    // set object color
    mObject.setColor(0x000000);
    
    // place object in the origin
    mObject.setX(0);
    mObject.setY(0);
    mObject.setZ(0);

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
		mObject.setRotY(mObject.getRotY() + 1);
		mObject.setRotZ(mObject.getRotZ() + 1);

	}
}

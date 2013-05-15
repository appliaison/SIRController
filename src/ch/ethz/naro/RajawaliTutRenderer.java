
package ch.ethz.naro;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import rajawali.BaseObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.parser.AParser.ParsingException;
import rajawali.parser.ObjParser;
import rajawali.parser.StlParser;
import rajawali.primitives.Sphere;
import rajawali.renderer.RajawaliRenderer;

public class RajawaliTutRenderer extends RajawaliRenderer {
	private DirectionalLight mLight;
	private BaseObject3D mSphere;
	private BaseObject3D mObject;

	public RajawaliTutRenderer(Context context) {
		super(context);
		setFrameRate(60);
	}

	protected void initScene() {
		mLight = new DirectionalLight(1f, 0.2f, -1.0f); // set the direction
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(3);

		Bitmap bg = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.skysphere);
		DiffuseMaterial material = new DiffuseMaterial();
		mSphere = new Sphere(1, 18, 18);
		mSphere.setMaterial(material);
		mSphere.addLight(mLight);
		mSphere.addTexture(mTextureManager.addTexture(bg));
		//addChild(mSphere); //Queue an addition task for mSphere
		
		ObjParser objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.simple_model_obj);
		//StlParser stlParser = new StlParser(mContext.getResources(), mTextureManager, R.raw.simple_cube_stl);
		try {
			//stlParser.parse();
			objParser.parse();
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mObject = objParser.getParsedObject();
		//mObject = stlParser.getParsedObject();
		mObject.setLight(mLight);
		//mObject.setRotY(400);
		//mObject.setRotZ(400);
		//mObject.getX();
		mObject.setX(0);
		mObject.setY(0);
		mObject.setZ(0);
		addChild(mObject);

		getCurrentCamera().setZ(0);
		//getCurrentCamera().setY(20);
		getCurrentCamera().setX(40);
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

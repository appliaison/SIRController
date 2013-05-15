package ch.ethz.naro;

import com.google.common.base.Preconditions;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ConfigurationInfo;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.ros.android.MasterChooser;
import org.ros.android.NodeMainExecutorService;
import org.ros.android.NodeMainExecutorServiceListener;
import org.ros.exception.RosRuntimeException;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

import rajawali.renderer.RajawaliRenderer;
import rajawali.util.RajLog;

import java.net.URI;
import java.net.URISyntaxException;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

// ----
// * Own Main Activity which combines: RosActivity & RajawaliActivity
// 

public abstract class SIRActivity extends Activity {

  // ---- Ros ----------
  private static final int MASTER_CHOOSER_REQUEST_CODE = 0;

  private final ServiceConnection nodeMainExecutorServiceConnection;
  private final String notificationTicker;
  private final String notificationTitle;

  private NodeMainExecutorService nodeMainExecutorService;
  
  // ----- Rajawali ---------
  protected GLSurfaceView mSurfaceView;
  protected FrameLayout mLayout;
  protected boolean mMultisamplingEnabled = false;
  protected boolean mUsesCoverageAa;
  private RajawaliRenderer mRajRenderer;
  protected boolean checkOpenGLVersion = true;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      mSurfaceView = new GLSurfaceView(this);
      
      ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
      if(checkOpenGLVersion) {
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        if(info.reqGlEsVersion < 0x20000)
          throw new Error("OpenGL ES 2.0 is not supported by this device");
      }
      mSurfaceView.setEGLContextClientVersion(2);
      mLayout = new FrameLayout(this);
      mLayout.addView(mSurfaceView);
      
      if(mMultisamplingEnabled)
        createMultisampleConfig();
      
      setContentView(mLayout);
  }
  
  protected void createMultisampleConfig() {
    final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
    final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;
    
      mSurfaceView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
      int[] configSpec = new int[] { 
          EGL10.EGL_RED_SIZE, 5,
          EGL10.EGL_GREEN_SIZE, 6,
          EGL10.EGL_BLUE_SIZE, 5,
          EGL10.EGL_DEPTH_SIZE, 16,
          EGL10.EGL_RENDERABLE_TYPE, 4,
          EGL10.EGL_SAMPLE_BUFFERS, 1,
          EGL10.EGL_SAMPLES, 2,
          EGL10.EGL_NONE
      };

      int[] result = new int[1];
      if(!egl.eglChooseConfig(display, configSpec, null, 0, result)) {
        RajLog.e("Multisampling configuration 1 failed.");
      }
      
      if(result[0] <= 0) {
        // no multisampling, check for coverage multisampling
        configSpec = new int[] {
          EGL10.EGL_RED_SIZE, 5,
          EGL10.EGL_GREEN_SIZE, 6,
          EGL10.EGL_BLUE_SIZE, 5,
          EGL10.EGL_DEPTH_SIZE, 16,
          EGL10.EGL_RENDERABLE_TYPE, 4,
          EGL_COVERAGE_BUFFERS_NV, 1,
          EGL_COVERAGE_SAMPLES_NV, 2,
          EGL10.EGL_NONE
        };
        
        if(!egl.eglChooseConfig(display, configSpec, null, 0, result)) {
          RajLog.e("Multisampling configuration 2 failed. Multisampling is not possible on your device.");
        }
        
        if(result[0] <= 0) {
          configSpec = new int[] {
            EGL10.EGL_RED_SIZE, 5,
            EGL10.EGL_GREEN_SIZE, 6, 
            EGL10.EGL_BLUE_SIZE, 5,
            EGL10.EGL_DEPTH_SIZE, 16,
            EGL10.EGL_RENDERABLE_TYPE, 4,
            EGL10.EGL_NONE
          };

          if(!egl.eglChooseConfig(display, configSpec, null, 0, result)) {
            RajLog.e("Multisampling configuration 3 failed. Multisampling is not possible on your device.");
          }

          if(result[0] <= 0) {
            throw new RuntimeException("Couldn't create OpenGL config.");
          }
        } else {
          mUsesCoverageAa = true;
        }
      }
      EGLConfig[] configs = new EGLConfig[result[0]];
      if(!egl.eglChooseConfig(display, configSpec, configs, result[0], result)) {
        throw new RuntimeException("Couldn't create OpenGL config.");
      }
      
      int index = -1;
      int[] value = new int[1];
      for(int i=0; i<configs.length; ++i) {
        egl.eglGetConfigAttrib(display, configs[i], EGL10.EGL_RED_SIZE, value);
        if(value[0] == 5) {
          index = i;
          break;
        }
      }

      EGLConfig config = configs.length > 0 ? configs[index] : null;
      if(config == null) {
        throw new RuntimeException("No config chosen");
      }
      
      return config;
    }
  });
  }
  
  protected void setGLBackgroundTransparent(boolean transparent) {
    if(transparent) {
          mSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
          mSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
          mSurfaceView.setZOrderOnTop(true);
    } else {
          mSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
          mSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
          mSurfaceView.setZOrderOnTop(false);
    }
  }
  
  protected void setRenderer(RajawaliRenderer renderer) {
    mRajRenderer = renderer;
    mSurfaceView.setRenderer(renderer);
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    mSurfaceView.onResume();
  }
  
  @Override
  protected void onPause() {
    super.onPause();
    mSurfaceView.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
  }
  
  private void unbindDrawables(View view) {
    if (view.getBackground() != null) {
        view.getBackground().setCallback(null);
    }
    if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
            unbindDrawables(((ViewGroup) view).getChildAt(i));
        }
        ((ViewGroup) view).removeAllViews();
    }
}
  // -----------------------------
  
  // ----------------- ROS -------------
  private final class NodeMainExecutorServiceConnection implements ServiceConnection {
    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
      nodeMainExecutorService = ((NodeMainExecutorService.LocalBinder) binder).getService();
      nodeMainExecutorService.addListener(new NodeMainExecutorServiceListener() {
        @Override
        public void onShutdown(NodeMainExecutorService nodeMainExecutorService) {
          SIRActivity.this.finish();
        }
      });
      startMasterChooser();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
  };

  protected SIRActivity(String notificationTicker, String notificationTitle) {
    super();
    this.notificationTicker = notificationTicker;
    this.notificationTitle = notificationTitle;
    nodeMainExecutorServiceConnection = new NodeMainExecutorServiceConnection();
  }

  @Override
  protected void onStart() {
    super.onStart();
    startNodeMainExecutorService();
  }

  private void startNodeMainExecutorService() {
    Intent intent = new Intent(this, NodeMainExecutorService.class);
    intent.setAction(NodeMainExecutorService.ACTION_START);
    intent.putExtra(NodeMainExecutorService.EXTRA_NOTIFICATION_TICKER, notificationTicker);
    intent.putExtra(NodeMainExecutorService.EXTRA_NOTIFICATION_TITLE, notificationTitle);
    startService(intent);
    Preconditions.checkState(
        bindService(intent, nodeMainExecutorServiceConnection, BIND_AUTO_CREATE),
        "Failed to bind NodeMainExecutorService.");
  }

  @Override
  protected void onDestroy() {
    if (nodeMainExecutorService != null) {
      nodeMainExecutorService.shutdown();
      unbindService(nodeMainExecutorServiceConnection);
      // NOTE(damonkohler): The activity could still be restarted. In that case,
      // nodeMainExectuorService needs to be null for everything to be started
      // up again.
      nodeMainExecutorService = null;
      
      // ---- Rajawali
      super.onDestroy();
      mRajRenderer.onSurfaceDestroyed();
      unbindDrawables(mLayout);
      System.gc();
    }
    Toast.makeText(this, notificationTitle + " shut down.", Toast.LENGTH_SHORT).show();
    super.onDestroy();
  }

  /*
   * **
   * This method is called in a background thread once this {@link Activity} has
   * been initialized with a master {@link URI} via the {@link MasterChooser}
   * and a {@link NodeMainExecutorService} has started. Your {@link NodeMain}s
   * should be started here using the provided {@link NodeMainExecutor}.
   * 
   * @param nodeMainExecutor
   *          the {@link NodeMainExecutor} created for this {@link Activity}
   *
   **/
  protected abstract void init(NodeMainExecutor nodeMainExecutor);

  private void startMasterChooser() {
    Preconditions.checkState(getMasterUri() == null);
    // Call this method on super to avoid triggering our precondition in the
    // overridden startActivityForResult().
    super.startActivityForResult(new Intent(this, MasterChooser.class), 0);
  }

  public URI getMasterUri() {
    Preconditions.checkNotNull(nodeMainExecutorService);
    return nodeMainExecutorService.getMasterUri();
  }

  @Override
  public void startActivityForResult(Intent intent, int requestCode) {
    Preconditions.checkArgument(requestCode != MASTER_CHOOSER_REQUEST_CODE);
    super.startActivityForResult(intent, requestCode);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == MASTER_CHOOSER_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        if (data == null) {
          nodeMainExecutorService.startMaster();
        } else {
          URI uri;
          try {
            uri = new URI(data.getStringExtra("ROS_MASTER_URI"));
          } catch (URISyntaxException e) {
            throw new RosRuntimeException(e);
          }
          nodeMainExecutorService.setMasterUri(uri);
        }
        // Run init() in a new thread as a convenience since it often requires
        // network access.
        new AsyncTask<Void, Void, Void>() {
          @Override
          protected Void doInBackground(Void... params) {
            SIRActivity.this.init(nodeMainExecutorService);
            return null;
          }
        }.execute();
      } else {
        // Without a master URI configured, we are in an unusable state.
        nodeMainExecutorService.shutdown();
        finish();
      }
    }
  }
}

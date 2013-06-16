package ch.ethz.naro;

import org.ros.android.BitmapFromImage;

import ch.ethz.naro.VideoHandler.VideoHandlerListener;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class VideoWindow implements VideoHandlerListener {
  
  private ImageView view;
  
  private String TAG = "VideoWin";
  
  public VideoWindow(FrameLayout layout) {
    view = new ImageView(layout.getContext());
    layout.addView(view);   
  }

  @Override
  public void handleVideo(VideoHandler handler) {
    Log.i(TAG, "got video");
    sensor_msgs.Image image = handler.image;
    
    //BitmapFromImage from = new BitmapFromImage();
    //Bitmap bit = from.call(image);
    //view.setImageBitmap(bit);
  }

}

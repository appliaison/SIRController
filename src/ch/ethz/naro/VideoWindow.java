package ch.ethz.naro;

import org.ros.android.BitmapFromImage;

import ch.ethz.naro.VideoHandler.VideoHandlerListener;
import android.R.layout;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoWindow {
  
  private ImageView view;
  private TextView text;
  
  private String TAG = "VideoWin";
  
  private FrameLayout layout;
  
  public VideoWindow(FrameLayout layout) {
    view = new ImageView(layout.getContext());
    layout.addView(view);
  }

  public void setBitmap(Bitmap bit) {
    view.setImageBitmap(bit);
  }


}

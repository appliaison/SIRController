package ch.ethz.naro;

import android.graphics.Bitmap;

public class VideoHandler extends java.util.EventObject {


  public sensor_msgs.CompressedImage image;
  
  public VideoHandler(Object source, sensor_msgs.CompressedImage img) {
    super(source);
    this.image = img;
  }
  
  public interface VideoHandlerListener {
    public void handleVideo(VideoHandler handler);
  }

}

package ch.ethz.naro;

public class VideoHandler extends java.util.EventObject {


  public sensor_msgs.Image image;
  
  public VideoHandler(Object source, sensor_msgs.Image img) {
    super(source);
    this.image = img;
  }
  
  public interface VideoHandlerListener {
    public void handleVideo(VideoHandler handler);
  }

}

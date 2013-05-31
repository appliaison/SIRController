package ch.ethz.naro;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import android.util.Log;

import ch.ethz.naro.LightHandler.LightHandlerListener;

public class LightPublisher extends AbstractNodeMain implements LightHandlerListener {
  
  private Publisher<mbed_controller.SIRsetLEDs> publisherLight;

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("LightPublisher");
  }
  
  @Override
  public void onStart(final ConnectedNode connectedNode) {
    Log.i("Light", "Publisher reg");
    publisherLight = connectedNode.newPublisher("/mbed_com/leds", mbed_controller.SIRsetLEDs._TYPE);
  }

  @Override
  public void handleLightEvent(LightHandler handler) {

    byte cam = (byte) (handler.cam & 0xFF);
    byte left = (byte) (handler.left & 0xFF);
    byte right = (byte) (handler.right  & 0xFF);
    
    if (publisherLight != null) {
    // generate msg
    mbed_controller.SIRsetLEDs ledMSG = publisherLight.newMessage();
    ledMSG.setLEDCAM(cam);
    ledMSG.setLEDLEFT(left);
    ledMSG.setLEDRIGHT(right);
    
    publisherLight.publish(ledMSG);
    } 
  }

}

package ch.ethz.naro;

import mbed_controller.ResetGyroResponse;

import org.ros.exception.RemoteException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import android.util.Log;

import ch.ethz.naro.ButtonHandler.ButtonHandlerListener;

public class CallService extends AbstractNodeMain implements ButtonHandlerListener {

  private ServiceClient<mbed_controller.ResetGyroRequest, mbed_controller.ResetGyroResponse> serviceIMU;

  private ServiceResponseListener<mbed_controller.ResetGyroResponse> serviceListener;
  
  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("CallService");
  }
  
  @Override
  public void onStart(final ConnectedNode connectedNode) {
    try {
      serviceIMU = connectedNode.newServiceClient("/ResetGyro", "mbed_controller/ResetGyro");
      Log.i("IMUService","Service created");
      
    } catch (ServiceNotFoundException e) {
      e.printStackTrace();
      Log.i("IMUService", "catch..., mhm?");
      Log.i("IMUService", e.getMessage());
      
    }
    
     // create costum service listener
     serviceListener = new ServiceResponseListener<ResetGyroResponse>() {
      
      @Override
      public void onSuccess(ResetGyroResponse response) {
        Log.i("IMUService", "call: success!");
        
      }
      
      @Override
      public void onFailure(RemoteException e) {
        Log.i("IMUService", "call: failure");
      }
    };
  
  }

  @Override
  public void handleButtonEvent(ButtonHandler handler) {
    // if IMUreset called
    if(handler.name == "IMUreset") {
      
      if(serviceIMU != null) {
        try {
          mbed_controller.ResetGyroRequest request = serviceIMU.newMessage();
          serviceIMU.call(request, serviceListener);
        } catch (Exception e) {
          Log.i("IMUservice", "catch called");
          e.printStackTrace();
        }
        
      }
    }
    
  }

}

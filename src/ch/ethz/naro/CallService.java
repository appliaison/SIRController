package ch.ethz.naro;

import mbed_controller.ResetGyroRequest;
import mbed_controller.ResetGyroResponse;

import org.ros.exception.RemoteException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.internal.message.RawMessage;
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
  
  private mbed_controller.ResetGyroRequest request;
  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("CallService");
  }
  
  @Override
  public void onStart(final ConnectedNode connectedNode) {
    try {
      serviceIMU = connectedNode.newServiceClient("ResetGyro", "mbed_controller/ResetGyro");
      Log.i("IMUService","Service created");
      
    } catch (ServiceNotFoundException e) {
      e.printStackTrace();
      Log.i("IMUService", "catch..., mhm?");
      Log.i("IMUService", e.getMessage());
      
    }
    
    request = new ResetGyroRequest() {
      
      @Override
      public RawMessage toRawMessage() {
        // TODO Auto-generated method stub
        return null;
      }
    };
    
     serviceListener = new ServiceResponseListener<ResetGyroResponse>() {
      
      @Override
      public void onSuccess(ResetGyroResponse response) {
        // TODO Auto-generated method stub
        Log.i("IMUService", "success!");
        
      }
      
      @Override
      public void onFailure(RemoteException e) {
        // TODO Auto-generated method stub
        Log.i("IMUService", "failure");
      }
    };
    
  }

  @Override
  public void handleButtonEvent(ButtonHandler handler) {
    // if IMUreset called
    if(handler.name == "IMUreset") {
      Log.i("IMU", "Got event");
      
      if(serviceIMU != null) {
        Log.i("IMUService", "try to call...");
        try {
          serviceIMU.call(request, serviceListener);
          Log.i("IMUService", "called!");
        } catch (Exception e) {
          Log.i("IMUservice", "catch called");
          //Log.i("IMUservice", e.getMessage());    
        }
        
      } else {
        Log.i("IMUservice", "where is my Service?");
      }
    }
    
  }

}

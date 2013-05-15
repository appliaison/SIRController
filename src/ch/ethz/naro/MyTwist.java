package ch.ethz.naro;

import org.ros.internal.message.RawMessage;

import geometry_msgs.Twist;
import geometry_msgs.Vector3;

public class MyTwist implements Twist {
  
  Vector3 linear;
  Vector3 angular;

  @Override
  public RawMessage toRawMessage() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Vector3 getLinear() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setLinear(Vector3 value) {
    // TODO Auto-generated method stub
    linear = value;
  }

  @Override
  public Vector3 getAngular() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setAngular(Vector3 value) {
    // TODO Auto-generated method stub
    angular = value;
    
  }

}

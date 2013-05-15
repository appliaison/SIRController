package ch.ethz.naro;

import org.ros.internal.message.RawMessage;

import geometry_msgs.Vector3;

public class MyVector3 implements Vector3{
  static final java.lang.String _TYPE = "geometry_msgs/Vector3";
  static final java.lang.String _DEFINITION = "# This represents a vector in free space. \n\nfloat64 x\nfloat64 y\nfloat64 z";
  // Init linear
  private Double _lx;
  private Double _ly;
  private Double _lz;
  
  // Init angular
  private Double _ax;
  private Double _ay;
  private Double _az;
  
  public void setLinear(Double u, Double v, Double w) {
    _lx = u;
    _ly = v;
    _lz = w;
  }
  
  public void setAngular(Double u, Double v, Double w) {
    _ax = u;
    _ay = v;
    _az = w;
  }

  @Override
  public RawMessage toRawMessage() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public double getX() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void setX(double value) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public double getY() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void setY(double value) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public double getZ() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void setZ(double value) {
    // TODO Auto-generated method stub
    
  }
}
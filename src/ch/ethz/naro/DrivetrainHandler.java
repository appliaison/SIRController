package ch.ethz.naro;

import sun.security.util.Length;

public class DrivetrainHandler extends java.util.EventObject {
  
  int nMot;
  double wR, gR, gearR, gearReff;
  double[] pos, spe, tor;
  double[] speed = new double[4];
  double[][] dynamic = new double[3][4];
  
  public DrivetrainHandler(Object source, double[] pos, double[] spe, double[] tor, double gearR, double wR) {
    super(source);
    
    this.gearR = gearR;
    this.wR = wR;
    
    this.pos = pos;
    this.spe = spe;
    this.tor = tor;
    
    // speed ->cm/s
    for(int i=0; i<4; i++) {
      speed[i] = spe[i]/gearR*Math.PI*2*wR*100;
    }
    
    // ajust direction
    speed[0] = -speed[0];
    speed[3] = -speed[3];
    this.tor[0] = -tor[0];
    this.tor[3] = -tor[3];
    
    this.dynamic[0] = pos;
    this.dynamic[1] = speed;
    this.dynamic[2] = tor;
    
  }
  
  public interface DrivetrainHandlerListener {
    public void handleDrivetrainEvent(DrivetrainHandler handler);
  }

}

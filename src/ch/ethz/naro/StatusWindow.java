package ch.ethz.naro;

import com.sun.org.apache.bcel.internal.generic.CPInstruction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ch.ethz.naro.BatteryHandler.BatteryHandlerListener;
import ch.ethz.naro.SensorHandler.SensorHandlerListener;

public class StatusWindow implements SensorHandlerListener, BatteryHandlerListener{
  
  private Activity _activity;
  private Fillbar distLeft;
  private Fillbar distRight;
  private Fillbar batteryStat;
  private TextView batteryStatus;
  
  public StatusWindow(Activity activity, RelativeLayout layout) {
    Context context = layout.getContext();
    _activity = activity;
    
    // Distance display
    TextView distance = new TextView(context);
    distance.setText("Distance Sensors [cm]:");
    layout.addView(distance);
    
    distLeft = new Fillbar(layout);
    distLeft.setRange(0, 30);
    distLeft.setSize(30, 200);
    distLeft.setValue(15);
    distLeft.setPosition(10, 40);
    distRight = new Fillbar(layout);
    distRight.setRange(0,30);
    distRight.setSize(30, 200);
    distRight.setValue(0);
    distRight.setPosition(130, 40);
    
    // battery display
    TextView battery = new TextView(context);
    battery.setText("Battery Status");
    battery.setY(260);
    layout.addView(battery);
    
    batteryStatus = new TextView(context);
    batteryStatus.setText("No V");
    batteryStatus.setY(310);
    batteryStatus.setX(130);
    layout.addView(batteryStatus);
    
    batteryStat = new Fillbar(layout);
    batteryStat.setRange(12, 17);
    batteryStat.setPosition(10, 300);
    batteryStat.setValue(17);
    batteryStat.setBackground(Color.BLACK);
    batteryStat.setColor(Color.YELLOW);
    
    String value = Float.toString(batteryStat.getValue());
    Log.i("status", value);
    
  }

  @Override
  public void handleSensorEvent(SensorHandler handler) {
    
    final float distL = (float) handler.lLeft;
    final float distR = (float) handler.lRight;
    
    //Log.i("Status", Float.toString(distL));
    
    _activity.runOnUiThread(new Runnable() {
      
      @Override
      public void run() {
        distLeft.setValue(distL);
        distRight.setValue(distR);
      }
    });
    
  }

  @Override
  public void handleBatteryEvent(BatteryHandler handler) {
    final float volt = (float) handler.voltage;
    _activity.runOnUiThread(new Runnable() {
      
      @Override
      public void run() {
        batteryStat.setValue(volt);
        String text = Float.toString(volt);
        batteryStatus.setText(text+" V");
      }
    });
    
  }

}

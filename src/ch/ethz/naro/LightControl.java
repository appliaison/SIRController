package ch.ethz.naro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.ethz.naro.LightHandler.LightHandlerListener;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class LightControl implements OnSeekBarChangeListener{
  
  // ---- Event handler ------------
  private List _listeners = new ArrayList();
  
  public synchronized void addEventListener(LightHandlerListener listener) {
    //Log.i("Joy", "addEventListener called");
    _listeners.add(listener);
  }
  public synchronized void removeEventListener(LightHandlerListener listener) {
    //Log.i("Joy", "removeEventListener called");
    _listeners.remove(listener);
  }
  
  private synchronized void fireLightValues(int cam, int left, int right) {
    //Log.i("Joy", "fireEvent called");
    LightHandler event = new LightHandler(this, cam, left, right);
    Iterator i = _listeners.iterator();
    
    while(i.hasNext()) {
      ((LightHandlerListener) i.next()).handleLightEvent(event);
    }
  } 
  // ---------------
  
  // Slider for the LEDs
  SeekBar _sliderCam;
  SeekBar _sliderLeft;
  SeekBar _sliderRight;
  
  int _valueCam;
  int _valueLeft;
  int _valueRight;
  
  // Initialization
  public LightControl(RelativeLayout layout) {
    // init Variables
    Context context = layout.getContext();
    int sliderTop = 60;
    int sliderDistance = 70;
    int maxValue = 255;
    int paddingLabel = 6;
    int paddingLeft = 30;
    
    // Create title
    TextView title = new TextView(context);
    title.setText("Light Control");
    title.setTypeface(null, Typeface.BOLD);
    title.setX(paddingLeft);
    
    // Create labels
    TextView labelCam = new TextView(context);
    labelCam.setText("LED Cam");
    labelCam.setY(sliderTop-paddingLabel);
    labelCam.setX(paddingLeft);
    TextView labelLeft = new TextView(context);
    labelLeft.setText("LED right");
    labelLeft.setY(sliderTop+sliderDistance-paddingLabel);
    labelLeft.setX(paddingLeft);
    TextView labelRight = new TextView(context);
    labelRight.setText("LED left");
    labelRight.setY(sliderTop+2*sliderDistance-paddingLabel);
    labelRight.setX(paddingLeft);
    
    // create Slider
    _sliderCam = new SeekBar(context);
    _sliderCam.setId(1); // Cam
    _sliderCam.setY(sliderTop);
    _sliderCam.setMax(maxValue);
    _sliderLeft = new SeekBar(context);
    _sliderLeft.setId(2); // Left LED
    _sliderLeft.setY(40);
    _sliderLeft.setY(sliderTop+sliderDistance); // set position from top
    _sliderLeft.setMax(maxValue);
    
    _sliderRight = new SeekBar(context);
    _sliderRight.setId(3); // right LED
    _sliderRight.setY(sliderTop+2*sliderDistance);
    _sliderRight.setMax(maxValue);
    
    // set Listeners
    _sliderCam.setOnSeekBarChangeListener(this);
    _sliderLeft.setOnSeekBarChangeListener(this);
    _sliderRight.setOnSeekBarChangeListener(this);
    
    
    // set Slider layout
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    
    // add Views
    layout.addView(title);
    layout.addView(_sliderCam, params);
    layout.addView(_sliderLeft, params);
    layout.addView(_sliderRight, params);
    layout.addView(labelCam);
    layout.addView(labelLeft);
    layout.addView(labelRight);
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    // read values from bars
    _valueCam = _sliderCam.getProgress();
    _valueLeft = _sliderLeft.getProgress();
    _valueRight = _sliderRight.getProgress();
    
    // send Event
    fireLightValues(_valueCam, _valueLeft, _valueRight);
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    // TODO Auto-generated method stub
    
  }

}

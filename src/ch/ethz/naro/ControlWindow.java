package ch.ethz.naro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.ethz.naro.ButtonHandler.ButtonHandlerListener;
import ch.ethz.naro.LightHandler.LightHandlerListener;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ControlWindow implements OnSeekBarChangeListener, OnClickListener{
    
  // ---- LightEvent handler ------------
  private List _listeners = new ArrayList();
  
  public synchronized void addEventListener(LightHandlerListener listener) {
    _listeners.add(listener);
  }
  public synchronized void removeEventListener(LightHandlerListener listener) {
    _listeners.remove(listener);
  }
  
  private synchronized void fireLightValues(int cam, int left, int right) {
    LightHandler event = new LightHandler(this, cam, left, right);
    Iterator i = _listeners.iterator();
    
    while(i.hasNext()) {
      ((LightHandlerListener) i.next()).handleLightEvent(event);
    }
  }
  // ---------------
  // ---- Button handler ------------
  
  private List _bListeners = new ArrayList();
  
  public synchronized void addEventListener(ButtonHandlerListener listener) {
    _bListeners.add(listener);
  }
  public synchronized void removeEventListener(ButtonHandlerListener listener) {
    _bListeners.remove(listener);
  }
  
  private synchronized void fireIMUreset(String name,boolean button) {
    ButtonHandler event = new ButtonHandler(this, name, button);
    Iterator i = _bListeners.iterator();
    
    while(i.hasNext()) {
      ((ButtonHandlerListener) i.next()).handleButtonEvent(event);
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
  public ControlWindow(RelativeLayout layout) {
    // init Variables
    Context context = layout.getContext();
    
    // -------- Light Control ---------
    int maxValue = 255;
    
    // Create title
    TextView title = new TextView(context);
    title.setText("Light Control");
    title.setTypeface(null, Typeface.BOLD);
    
    // Create labels
    TextView labelCam = new TextView(context);
    labelCam.setText("LED Cam");
    TextView labelLeft = new TextView(context);
    labelLeft.setText("LED right");
    TextView labelRight = new TextView(context);
    labelRight.setText("LED left");
    
    // create Slider
    _sliderCam = new SeekBar(context);
    _sliderCam.setId(1); // Cam
    _sliderCam.setMax(maxValue);
    _sliderLeft = new SeekBar(context);
    _sliderLeft.setId(2); // Left LED
    _sliderLeft.setMax(maxValue);
    
    _sliderRight = new SeekBar(context);
    _sliderRight.setId(3); // right LED
    _sliderRight.setMax(maxValue);
    
    // set Listeners
    _sliderCam.setOnSeekBarChangeListener(this);
    _sliderLeft.setOnSeekBarChangeListener(this);
    _sliderRight.setOnSeekBarChangeListener(this);
    
    // END ----- light control
    
    // ----- RESET IMU ------
    
    Button imuReset = new Button(context);
    imuReset.setText("Reset IMU");
    imuReset.setOnClickListener(this);
    // END ----- reset IMU --
        
    // create GridLayout
    GridLayout grid = new GridLayout(context);
    grid.setColumnCount(2);
    grid.setRowCount(5);
    
    // add Views
    GridLayout.LayoutParams span2 = new GridLayout.LayoutParams();
    span2.columnSpec = GridLayout.spec(0, 2);
    
    int widthColumn2 = 300;
    
    GridLayout.LayoutParams param1 = new GridLayout.LayoutParams();
    param1.width = widthColumn2;
    GridLayout.LayoutParams param2 = new GridLayout.LayoutParams();
    param2.width = widthColumn2;
    GridLayout.LayoutParams param3 = new GridLayout.LayoutParams();
    param3.width = widthColumn2;
    
    grid.addView(title, span2);
    grid.addView(labelCam);
    grid.addView(_sliderCam, param1);
    grid.addView(labelLeft);
    grid.addView(_sliderLeft, param2);
    grid.addView(labelRight);
    grid.addView(_sliderRight, param3);
    grid.addView(imuReset);
    
    // add grid-layout
    layout.addView(grid);
  }
  

  // ---- Seekbar events ----
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
  
  // ---- Button events ----
  @Override
  public void onClick(View v) {
    // fire IMU reset when clicked
    Log.i("IMU", "Button clicked");
    fireIMUreset("IMUreset", true);
    
    
  }

}


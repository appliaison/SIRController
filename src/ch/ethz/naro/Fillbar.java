package ch.ethz.naro;

import ch.ethz.naro.SensorHandler.SensorHandlerListener;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Fillbar{
  
  private Activity _activity;
  
  private float _height;
  private float _width;
  private float _minRange;
  private float _maxRange;
  private float _rangeConv;
  
  private int _fillColor;
  private int _backgroundColor;
  
  private float _defaultValue = 50;
  private float _value;
  
  private ImageView fillbar;
  private ImageView status;
    
  private static String TAG = "Fillbar";
  
  public Fillbar(Activity acc, RelativeLayout layout, float width, float height) {
    // Init variables
    _activity = acc;
    _width = width;
    _height = height;
    _fillColor = Color.CYAN;
    _backgroundColor = Color.YELLOW;
    _value = _defaultValue;
    // set default range
    setRange(0,100);
    
    // craete outer bounding
    fillbar = new ImageView(layout.getContext());
    fillbar.setImageBitmap(createOuterRec());
    fillbar.setX(100);
    fillbar.setY(100);
    layout.addView(fillbar);
    
    // init filling
    status = new ImageView(layout.getContext());
    status.setImageBitmap(createFilling(_defaultValue));
    status.setX(100);
    status.setY(100);
    layout.addView(status);
       
  }
  
  private Bitmap createOuterRec() {
    Bitmap bit = Bitmap.createBitmap((int)_width, (int) _height, Bitmap.Config.ARGB_8888);
    Paint paint = new Paint();
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(_backgroundColor);
    Canvas canvas = new Canvas(bit);
    
    canvas.drawRect(0, 0, _width, _height, paint);
    
    return bit;
  }
  
  
  private Bitmap createFilling(float fil) {
    
    Bitmap bit = Bitmap.createBitmap((int)_width, (int) _height, Bitmap.Config.ARGB_8888);
    Paint paint = new Paint();
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(_fillColor);
    Canvas canvas = new Canvas(bit);
    
    // check fil status
    float value;
    if(fil > _maxRange) {value = _maxRange;}
    else if(fil < _minRange) {value = _minRange;}
    else {value = fil;}
    
    float height = value*_rangeConv;
    
    // draw rect
    canvas.drawRect(0, _height-height, _width, _height, paint);
    return bit;
  }
  
  public void setRange(float min, float max) {
    // set Range
    _minRange = min;
    _maxRange = max;
    // set range conversion factor
    _rangeConv = _height/(_maxRange-_minRange);
  }
  
  public void setValue(float value) {
    _value = value;
    // update filling with new value
    status.setImageBitmap(createFilling(value));
  }
  
  public void setColor(int color) {
    _fillColor = color;
    status.setImageBitmap(createFilling(_value)); // update filling
  }
  
  public void setBackground(int color) {
    _backgroundColor = color;
    fillbar.setImageBitmap(createOuterRec()); // update
  }
  
  public void setSize(float width, float height) {
    _width = width;
    _height = height;
    // update
    fillbar.setImageBitmap(createOuterRec());
    status.setImageBitmap(createFilling(_value));
  }
}

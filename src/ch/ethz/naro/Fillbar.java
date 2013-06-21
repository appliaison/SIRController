package ch.ethz.naro;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Fillbar{
  
  private float _height;
  private float _width;
  private float _minRange;
  private float _maxRange;
  
  private int _fillColor;
  private int _backgroundColor;
  
  private float _defaultValue = 50;
  private float _value;
  
  private Boolean _label;
  private String _labelTop;
  private String _labelBot;
  
  private Float _posX;
  private Float _posY;
  
  private ImageView fillbar;
  private ImageView status;
  
  private RelativeLayout _layout;
    
  private static String TAG = "Fillbar";
  
  public Fillbar(RelativeLayout layout) {
    // Init variables
    _layout = layout;
    _width = 30;
    _height = 100;
    _fillColor = Color.CYAN;
    _backgroundColor = Color.BLUE;
    _value = _defaultValue;
    _posX = 0.0f;
    _posY = 0.0f;
    // set default range
    setRange(0,100);
    // set labels
    _label = true;
    _labelBot = Float.toString(_minRange);
    _labelTop = Float.toString(_maxRange);
    
    fillbar = new ImageView(_layout.getContext());
    status = new ImageView(_layout.getContext());
    // create Rectangles
    updateView();
    
    layout.addView(fillbar);
    layout.addView(status);
       
  }
  
  private Bitmap createOuterRec() {
    int textWidth = 0;
    if(_label) {textWidth = 100; }
    Bitmap bit = Bitmap.createBitmap((int)_width + textWidth, (int) _height, Bitmap.Config.ARGB_8888);
    Paint paint = new Paint();
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(_backgroundColor);
    Canvas canvas = new Canvas(bit);
    
    canvas.drawRect(0, 0, _width, _height, paint);
    
    if(_label) {
    // add label
      Paint textStyle = new Paint();
      textStyle.setStyle(Paint.Style.FILL);
      textStyle.setColor(Color.BLACK);
      int textSize = 20;
      textStyle.setTextSize(textSize);
      
      int padding = 5;
      canvas.drawText(_labelBot, _width+padding, _height, textStyle); // Label Bottom
      canvas.drawText(_labelTop, _width+padding, textSize, textStyle);
    }
    
    return bit;
  }
  
  private Bitmap createFilling(float fil) {
    
    Bitmap bit = Bitmap.createBitmap((int)_width, (int) _height, Bitmap.Config.ARGB_8888);
    Paint paint = new Paint();
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(_fillColor);
    Canvas canvas = new Canvas(bit);
    
    // check fil status
    float height;
    if(fil > _maxRange) {height = _height;}
    else if(fil < _minRange) {height = 0;}
    else {height=(_value-_minRange)*_height/(_maxRange-_minRange);}
    
    // draw rect
    canvas.drawRect(0, _height-height, _width, _height, paint);
    return bit;
  }
  
  private void updateView() {
    // craete outer bounding
    fillbar.setImageBitmap(createOuterRec());
    fillbar.setX(_posX);
    fillbar.setY(_posY);

    // set filling
    status.setImageBitmap(createFilling(_value));
    status.setX(_posX);
    status.setY(_posY);

  }
  
  public void setRange(float min, float max) {
    // set Range
    _minRange = min;
    _maxRange = max;
    
    _labelBot = Float.toString(_minRange);
    _labelTop = Float.toString(_maxRange);
    
    //updateView();
  }
  
  public void setValue(float value) {
    _value = value;
    // update filling with new value
    updateView();
  }
  
  public float getValue() {
    return _value;
  }
  
  public void setColor(int color) {
    _fillColor = color;
    updateView();
  }
  
  public void setBackground(int color) {
    _backgroundColor = color;
    updateView();
  }
  
  public void setLabelTop(String label) {
    _labelTop = label;
    updateView();
  }
  
  public void setLabelBottom(String label) {
    _labelBot = label;
    updateView();
  }
  
  public void enableLabel(Boolean bol) {
    _label = bol;
    updateView();
  }
  
  public void setSize(float width, float height) {
    _width = width;
    _height = height;
    updateView();
  }
  
  public void setPosition(float x, float y) {
    _posX = x;
    _posY = y;
    updateView();
  }
}

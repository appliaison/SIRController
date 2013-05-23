package ch.ethz.naro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.ethz.naro.JoyHandler.JoyHandlerListener;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

public class VirtualJoystick implements OnCheckedChangeListener {
	
	// ---- Event handler ------------
	private List _listeners = new ArrayList();
	
	public synchronized void addEventListener(JoyHandlerListener listener) {
		//Log.i("Joy", "addEventListener called");
		_listeners.add(listener);
	}
	public synchronized void removeEventListener(JoyHandlerListener listener) {
		//Log.i("Joy", "removeEventListener called");
		_listeners.remove(listener);
	}
	
	private synchronized void firePosition() {
		//Log.i("Joy", "fireEvent called");
		JoyHandler event = new JoyHandler(this, getDx(), getDy(), _name);
		Iterator i = _listeners.iterator();
		
		while(i.hasNext()) {
			((JoyHandlerListener) i.next()).handleJoyEvent(event);
		}
	}	
	// ---------------
	
	// Member Variables
	private ImageView stick;
	private Bitmap pic;
	private Bitmap picRes;
	private RelativeLayout layout;
	private RelativeLayout.LayoutParams params;
	private Switch lockX;
	private Switch lockY;
	
	private int bitWidth;
	private int bitHeight;
	private int startPosX;
	private int startPosY;
	private int _radius;
	
	private boolean dragging = false;
	private boolean draggingPos = true;
	
	private boolean _moveX = true;
	private boolean _moveY = true;
	
	private int _posX;
	private int _posY;
	
	private String _name;
	
	// ---------
	
	// Constructor
	@SuppressLint("NewApi")
	public VirtualJoystick(RelativeLayout layout,int midPointx, int midPointy, int radius, String name) {		
		// set nobe size
		bitWidth = radius/2;
		bitHeight = bitWidth;
		
		// Set Startposition
		this.startPosX = midPointx;
		this.startPosY = midPointy;
		this._radius = radius;
	
		_posX = startPosX-bitWidth/2;
		_posY = startPosY-bitWidth/2;
		
		// Set name
		_name = name;
		
		// set context
		Context context = layout.getContext();
		
		// Create View
		this.layout = layout;
		stick = new ImageView(layout.getContext());
		
		// Create joystick bitmap
		pic = BitmapFactory.decodeResource(layout.getResources(), R.drawable.circle);
		picRes = Bitmap.createScaledBitmap(pic, bitWidth, bitHeight, false);
		stick.setImageBitmap(picRes);
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(_posX, _posY, 0, 0);
		stick.setLayoutParams(params);	
		
		// Draw Circle
		int wScreen = startPosX+radius;
		int hScreen= startPosY+radius;
		Bitmap pallet = Bitmap.createBitmap(wScreen, hScreen, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(pallet);
		Paint paint = new Paint(); 
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawCircle(startPosX, startPosY, radius, paint);
		canvas.drawLine(startPosX, startPosY+radius, startPosX, startPosY-radius, paint); // draw vertical line
		canvas.drawLine(startPosX+radius, startPosY, startPosX-radius, startPosY, paint); // draw hotizontal line
		
		ImageView circle = new ImageView(layout.getContext());
		circle.setImageBitmap(pallet);
		
		// Create switches for axis-lock
		// y-axis
		lockY = new Switch(layout.getContext());
		lockY.setX(startPosX-95);
		lockY.setY(startPosY-radius-70);
		lockY.setOnCheckedChangeListener(this);
		lockY.setId(2);
		lockY.setChecked(true);
		// x-axis
		lockX = new Switch(layout.getContext());
		lockX.setX(startPosX+radius+15-53); //-53 startdistanz from left 
		lockX.setY(startPosY-25);
		lockX.setRotation(90);
		lockX.setOnCheckedChangeListener(this);
		lockX.setId(1);
		lockX.setChecked(true);
		
		// Add graphics to view
		layout.addView(lockX);
		layout.addView(lockY);
		layout.addView(circle); // add Circle to Layout
		layout.addView(stick); // Add joystick to layout
		
		// make layout sensitive
		layout.setOnTouchListener(myListener);

	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		
		if(id==1) { // Lock/Unlock x
			lockAxis('x', isChecked);
	
		}
		if(id==2) { // Lock/Unlock y
			this.lockAxis('y', isChecked);
		}
		
	}	
	
	// Listen to touch
	OnTouchListener myListener = new OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event){
			updatePos(event);
		    return true;
		}};
	
	public void lockAxis(char axis, boolean bol) {
		if(axis == 'x') {this._moveX = bol;}
		if(axis == 'y') {this._moveY = bol;}
	}
	
	public float getDx() {
		return ((float) -(this.startPosX - this._posX-bitWidth/2))/_radius;
	}
	
	public float getDy() {
		return (float) (this.startPosY - this._posY-bitHeight/2)/_radius;
	}
	
	
	// ----- Update Joy position
	private void updatePos(MotionEvent event) {
		double tabX = event.getX();
		double tabY = event.getY();
		
	    int tabXint = (int) Math.round(event.getX());
	    int tabYint = (int) Math.round(event.getY());
	    
	    int posX = tabXint - bitWidth/2;
	    int posY = tabYint - bitHeight/2;
	    
		int disX = tabXint-startPosX;
		int disY = tabYint-startPosY;
		
		//drag drop 
		if ( event.getAction() == MotionEvent.ACTION_DOWN ){
			
			// start zone
			if(disX*disX + disY*disY<bitWidth*bitWidth) {
				dragging = true;
			}
		}else if ( event.getAction() == MotionEvent.ACTION_UP){
			dragging = false;
			draggingPos = true;
		}
			
		// bound to circle
		if(disX*disX + disY*disY>_radius*_radius) {
			draggingPos = false;
		}
		else {
			draggingPos = true;
		}
		
		//move joystick
		if(dragging) {
			//stay in circle if tab out of circle
			if(!draggingPos) {

				double dX = tabX - startPosX;
				double dY = tabY -startPosY;
				
				double num = Math.sqrt(1+Math.pow(dY/dX, 2));
				int dx = (int) Math.round(_radius/num);
				int dy = (int) Math.round(Math.sqrt(_radius*_radius - dx*dx));
				
				// Check position in quarter
				if(dX<0) {dx = -dx;}
				if(dY<0) {dy = -dy;}
				
				posX = startPosX + dx - bitWidth/2;
				posY = startPosY + dy - bitHeight/2;
			}
			// lock position to axis
			if(!_moveX) {posX = startPosX-bitWidth/2;}
			if(!_moveY) {posY = startPosY-bitWidth/2;}
			
			// set final position
			this._posX = posX;
			this._posY = posY;
			params.setMargins(_posX, _posY, 0, 0);
			stick.setLayoutParams(params);
		} 
		else { // if release, set position back
			_posX = startPosX-bitWidth/2;
			_posY = startPosY-bitHeight/2;
			params.setMargins(_posX, _posY, 0, 0);
			stick.setLayoutParams(params);
		}
		firePosition();
	}
}
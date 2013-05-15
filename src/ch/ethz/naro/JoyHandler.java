package ch.ethz.naro;

import java.util.EventObject;

public class JoyHandler  extends java.util.EventObject {

	float x,y;
	String name;
	
	public JoyHandler(Object source, float x, float y, String nam) {
		super(source);
		this.x = x;
		this.y = y;
		this.name = nam;
	}
	
	public interface JoyHandlerListener {
		public void handleJoyEvent(JoyHandler handler);
	}
	
}

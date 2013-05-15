package ch.ethz.naro;

import android.os.Bundle;
import android.content.ServiceConnection;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ros.android.RosActivity;
import org.ros.address.InetAddressFactory;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import ch.ethz.naro.R;


public class NaroMainActivity extends RosActivity {
  private ServiceConnection connection;
  private Chatter chatter;
  private Listener listener;
  private SpeedCmdRobo speed;
  private TextView text;
  
  private VirtualJoystick joy;
	
	public NaroMainActivity() {
	  super("Naro", "Naro");
	}
		
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    RelativeLayout joyLay = (RelativeLayout) findViewById(R.id.rel);
    
    text = (TextView) findViewById(R.id.text);
    text.setText("Bla");
    
    speed = new SpeedCmdRobo();
    
    joy = new VirtualJoystick(joyLay, 100, 100, 100, "Joystick");
    joy.addEventListener(speed);
    
    
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }

  @Override
  protected void init(NodeMainExecutor nodeMainExecutor) {
    chatter = new Chatter();
    listener = new Listener();
    
    NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(
      InetAddressFactory.newNonLoopback().getHostAddress(), getMasterUri());
    
    nodeMainExecutor.execute(chatter, nodeConfiguration);
    nodeMainExecutor.execute(listener, nodeConfiguration);
    nodeMainExecutor.execute(speed, nodeConfiguration);
  }
  
  public void onSendButtonClick(View view) {
    EditText messageEdit = (EditText)findViewById(R.id.messageEdit);
    chatter.sendMessage(messageEdit.getText().toString());
  }


}

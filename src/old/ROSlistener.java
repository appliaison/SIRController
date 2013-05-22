package old;

//import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import android.util.Log;
import android.widget.TextView;


public class ROSlistener extends AbstractNodeMain {
  
  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("listener");
  }
  
  @Override
  public void onStart(ConnectedNode connectedNode) {
    //final Log log = connectedNode.getLog();
    Log.i("Listener", "Started");
    Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber("tablet", std_msgs.String._TYPE);
    subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
      @Override
      public void onNewMessage(std_msgs.String message) {
        Log.i("Listener", "I heard: "+message.getData());
      }
    });
  }

}

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.Map.Entry;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.json.JSONObject;

public class MQTTOperations implements MqttCallback {

	public static String topicPrefix = "dev/";

	private String brokerUrl;
	private String brokerPort;
	private String serverId;
	private String username;
	private String password;
	private MqttClient subscriber;
	private MqttClient publisher;
	public boolean changeBroker = false;
	public String config_file;
	private Map<String,Gateway> gateways = new HashMap<String, Gateway>();
	
	public MQTTOperations(String brokerUrl, String brokerPort, String serverId,
			String username, String password) {
		MqttConnectOptions connOpt = new MqttConnectOptions();
		
		this.brokerUrl = brokerUrl;
		this.brokerPort = brokerPort;
		this.serverId = serverId;
		this.username = username;
		this.password = password;


		try {
			if (!this.username.isEmpty())
				connOpt.setUserName(this.username);
			if (!this.password.isEmpty())
				connOpt.setPassword(this.password.toCharArray());

			this.subscriber = new MqttClient(this.brokerUrl + ":"
					+ this.brokerPort, this.serverId);
			this.subscriber.setCallback(this);
			this.subscriber.connect(connOpt);
			this.subscriber.subscribe("GATEWAY_CONNECTED", 1);
			System.out.println("Topic devices subscribed");
			
			this.publisher = new MqttClient(this.brokerUrl + ":"
					+ this.brokerPort, this.serverId + "_pub");
			this.publisher.setCallback(this);
			this.publisher.connect(connOpt);
			
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}

	}

	@Override
	public void connectionLost(Throwable cause) {
		try {
			MqttConnectOptions connOpt = new MqttConnectOptions();

			this.subscriber = new MqttClient(this.brokerUrl + ":"
					+ this.brokerPort, this.serverId);
			this.subscriber.setCallback(this);
			this.subscriber.connect(connOpt);
			this.subscriber.subscribe("DEVICES_CONNECTED", 1);

			this.publisher = new MqttClient(this.brokerUrl + ":"
					+ this.brokerPort, this.serverId + "_publisher");
			this.publisher.setCallback(this);
			this.publisher.connect(connOpt);

			System.out.println("Topic devices subscribed");
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}

	}

	@Override
	public void messageArrived(final String topic, final MqttMessage message)
			throws Exception {

	       new Thread(new Runnable() {
				public void run() {
					if(topic.equals("GATEWAY_CONNECTED")) {
												
						String messageContent = new String(message.getPayload());
						String[] gateway_info = messageContent.split(" - ");
						
						Gateway g_new = new Gateway(gateway_info[0], gateway_info[1], Integer.parseInt(gateway_info[2]));
						
						if(gateways.containsKey(gateway_info[0])) {
							gateways.replace(gateway_info[0], g_new);			
						}else {
							gateways.put(gateway_info[0], g_new);
						}
						
						int highest = 0, lowest = 999999999;
						Gateway g_highest = null, g_lowest = null;
						
						for (Entry<String, Gateway> entry : gateways.entrySet()) {
							
							Gateway g = (Gateway) entry.getValue(); 
							
						    if(g.getQtdDevices()>highest){
						    	highest = g.getQtdDevices();
						    	g_highest = (Gateway) entry;
						    }
								
							if(g.getQtdDevices()<lowest) {
						    	lowest = g.getQtdDevices();
						    	g_lowest = (Gateway) entry;
							}								
						}
						
						if(highest>lowest+1) {
							
							g_highest.removeDevice();
							g_lowest.addDevice();
							
							byte[] b = (g_lowest.getIp()+" - "+g_lowest.getPort()+" - "+g_highest.getIp()+" - "+g_highest.getPort()).getBytes();
							try {
								publisher.publish("BALANCE_LOAD", b, 1, false);
							} catch (MqttException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
	       }).start();
		
	}
		
	public Map<String, Gateway> getGateways() {
		return gateways;
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub

	}
	

	public void disconnect() {
		try {
			this.subscriber.disconnect();
			this.publisher.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

	public Thread getThreadByName(String threadName) {
		for (Thread t : Thread.getAllStackTraces().keySet()) {
			if (t.getName().equals(threadName))
				return t;
		}
		return null;
	}
	
	public String getIpAddress() throws UnknownHostException, IOException {
    	Socket s = new Socket("192.168.0.1", 80);
    	String ip = s.getLocalAddress().getHostAddress();
    	s.close();
    	return ip;
	}
	
	public String getBrokerUrl() {
		return this.brokerUrl.replaceFirst("tcp://", "");
	}
	public String getBrokerPort() {
		return this.brokerPort;
		
	}

	
}

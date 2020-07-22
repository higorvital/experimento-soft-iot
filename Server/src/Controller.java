import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.xml.sax.SAXException;


public class Controller {
	private Properties config;
	private MQTTOperations mqtt;
	public Map<String,Gateway> gateways = new HashMap<String,Gateway>();
	public Map<String,Gateway> gateways_free = new HashMap<String,Gateway>();
	public Map<String,Gateway> gateways_overloaded = new HashMap<String,Gateway>();
	
	public Controller(Properties config) throws IOException, SAXException {
		this.config = config;
	}
	
	public Controller() {

	}
		
	public MQTTOperations connectDeviceMqtt(){
			this.mqtt = new MQTTOperations(this.config.getProperty("broker_mqtt.url"),
					 this.config.getProperty("broker_mqtt.port"),
					 this.config.getProperty("virtual_devices.id"),
					 this.config.getProperty("broker_mqtt.username"),
					 this.config.getProperty("broker_mqtt.password"));
		return mqtt;
	}	
}
package br.ufba.dcc.wiser.soft_iot.fog_gateway;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ControllerImpl{
	
	private String ip;
	private String port;
	private String user;
	private String pass;
	private String ip_up;
	private String port_up;
	private String user_up;
	private String pass_up;
	private String ip_server;
	private String port_server;
	private String user_server;
	private String pass_server;
	private boolean debugModeValue;
	private ClientMQTT clienteMQTT;
	private ClientMQTT clienteMQTT_UP;
	private ClientMQTT clienteMQTT_SERVER;
	public int qtd_G_Connecteds = 0;
	public Map<String,List<String>> topk_k_scoresByIdrequi = new HashMap<String,List<String>>();
	public int k = 0;
	
	private Map<String,Device> devices = new HashMap<String, Device>();
	private int sensorQuantity = 0;
	private HashSet<String> listOfDevices = new HashSet<String>();
	private boolean writeFile = false;

	
	public void start(){
			System.out.println("Opaa");
			clienteMQTT = new ClientMQTT("tcp://" + this.ip + ":" + this.port, user, pass);
		 	clienteMQTT.iniciar(); // PUBLISH
		 	clienteMQTT_UP = new ClientMQTT("tcp://" + this.ip_up + ":" + this.port_up, user_up, pass_up);
		 	clienteMQTT_UP.iniciar();
		 	clienteMQTT_SERVER = new ClientMQTT("tcp://" + this.ip_server + ":" + this.port_up, user_server, pass_server);
		 	clienteMQTT_SERVER.iniciar();
		 	clienteMQTT_SERVER.gatewayConnectedMessage("tcp://" +this.getIpAddress(),this.port, 0);
			System.out.println("QTD BOTTOMBROKERS: " + this.getQtdConnecteds());
		 	new Listener(this, clienteMQTT, clienteMQTT_UP, "TOP_K_HEALTH_RES/#", 1); // SUBSCRIBE
		 	new Listener(this, clienteMQTT, clienteMQTT_UP, "CONNECTED/#", 1);
        	new Listener(this, clienteMQTT_UP, clienteMQTT, "TOP_K_HEALTH/#", 1);

		 	new Listener(this, clienteMQTT, clienteMQTT_SERVER, "DEVICES_CONNECTED/#", 1); // SUBSCRIBE
        	new Listener(this, clienteMQTT_SERVER, clienteMQTT, "BALANCE_LOAD/#", 1);
		 	new Listener(this, clienteMQTT, clienteMQTT, "dev/#", 1); // SUBSCRIBE

        	
        	System.out.println("qtd BOTTOMBROKERS: " + this.getQtdConnecteds());	        
	}
	
	public void calculateTopK (final String id, final int k) {
		new Thread(new Runnable() {
			public void run() {
				System.out.println("OK .. agora vou calcular o TOP-K dos TOP-K's");
				Map<String, Integer> myMap = new HashMap<String, Integer>();
				for(String t: topk_k_scoresByIdrequi.get(id)) {
					t = t.replace("{", "");
					t = t.replace("}", "");
					t = t.replace(" ", "");
					String[] pairs = t.split(",");
					for (int i=0;i<pairs.length;i++) {
					    String pair = pairs[i];
					    String[] keyValue = pair.split("=");
					    myMap.put(keyValue[0], Integer.valueOf(keyValue[1]));
					}
				}
				
				
				Object[] a = myMap.entrySet().toArray();
				Arrays.sort(a, new Comparator<Object>() {
				    @SuppressWarnings("unchecked")
					public int compare(Object o1, Object o2) {
				        return ((Map.Entry<String, Integer>) o2).getValue()
				                   .compareTo(((Map.Entry<String, Integer>) o1).getValue());
				    }
				});
				Map<String,Integer> top_k = new HashMap<String,Integer>();
				// Pegando os k piores ...
				for (int i =0; i< k; i++) {
					Map.Entry<String, Integer> e = (Map.Entry<String, Integer>) a[i];
					top_k.put(e.getKey(), e.getValue());
					
				}
				System.out.println("TOP_K => " + top_k.toString());
				System.out.println("=========================================");
				byte[] b = top_k.toString().getBytes();					
				clienteMQTT_UP.publicar("TOP_K_HEALTH_RES/" + id, b, 1);
				
			}
		}).start();
	}
	
	public static void main(String[] args) throws JAXBException {

        InetAddress inetAddress;
			Socket socket = new Socket();
			try {
				socket.connect(new InetSocketAddress("google.com", 80));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			socket.getLocalAddress().toString().replace("/", "");


    }
	
	public void stop(){
		
	        this.clienteMQTT.finalizar();
	        this.clienteMQTT_UP.finalizar();
	    
	}
	
	private void printlnDebug(String str){
		if (debugModeValue)
			System.out.println(str);
	}

	// public void setStrJsonDevices(String strJsonDevices) {
	// 	this.strJsonDevices = strJsonDevices;
	// }

	public void setDebugModeValue(boolean debugModeValue) {
		this.debugModeValue = debugModeValue;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getIp_up() {
		return ip_up;
	}

	public void setIp_up(String ip_up) {
		this.ip_up = ip_up;
	}

	public String getPort_up() {
		return port_up;
	}

	public void setPort_up(String port_up) {
		this.port_up = port_up;
	}

	public String getPass_up() {
		return pass_up;
	}

	public void setPass_up(String pass_up) {
		this.pass_up = pass_up;
	}

	public String getUser_up() {
		return user_up;
	}

	public void setUser_up(String user_up) {
		this.user_up = user_up;
	}

	public String getIp_server() {
		return ip_server;
	}

	public void setIp_server(String ip_server) {
		this.ip_server = ip_server;
	}

	public String getPort_server() {
		return port_server;
	}

	public void setPort_server(String port_server) {
		this.port_server = port_server;
	}

	public String getPass_server() {
		return pass_server;
	}

	public void setPass_server(String pass_server) {
		this.pass_server = pass_server;
	}

	public String getUser_server() {
		return user_server;
	}

	public void setUser_server(String user_server) {
		this.user_server = user_server;
	}
	
	public void add_G_Connecteds() {
    	this.qtd_G_Connecteds++;
    }

	public void remove_G_Connecteds() {
    	this.qtd_G_Connecteds--;
    }

    public int getQtdConnecteds() {
    	return this.qtd_G_Connecteds;
    }
    
	public String getIpAddress() {
    	Socket s = new Socket();   	
    	String ip = "";
    	try {
			s.connect(new InetSocketAddress("google.com", 80));
			ip = s.getLocalAddress().toString().replace("/", "");

	    	s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return ip;
	}
	
	public int getSensorQuantity() {
		return this.sensorQuantity;
	}
	
	public void setSensorQuantity(int sensorQuantity) {
		this.sensorQuantity = sensorQuantity;
	}

	public HashSet<String> getListOfDevices() {
		return this.listOfDevices;
	}
	
	public void addListOfDevices(String device) {
		this.listOfDevices.add(device);
	}
	
	public void resetListOfDevices() {
		this.listOfDevices = new HashSet<String>();
	}


	public boolean getWriteFile() {
		return this.writeFile;
	}
	
	public void setWriteFile(boolean writeFile) {
		this.writeFile = writeFile;
	}
	
	public Map<String, Device> getDevices(){
		return this.devices;
	}
	
	public void addDevice(String key, Device value) {
		if(devices.containsKey(key)) {
			devices.replace(key, value);			
		}else {
			devices.put(key, value);
		}
	}
	
	public void removeDevice(String key) {
		devices.remove(key);		
	}

}

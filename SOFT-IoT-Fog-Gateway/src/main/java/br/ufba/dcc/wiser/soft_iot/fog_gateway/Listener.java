package br.ufba.dcc.wiser.soft_iot.fog_gateway;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;

import javax.naming.ServiceUnavailableException;
import javax.xml.bind.JAXBException;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.BundleException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Comparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.util.Map.Entry.*;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class Listener implements IMqttMessageListener {
	
	private boolean debugModeValue;
	private ControllerImpl impl;
	private ClientMQTT clienteMQTT;
	private ClientMQTT clienteMQTT_Send;
	private ShellCommand shell = new ShellCommand();


    public Listener(ControllerImpl impl, ClientMQTT clienteMQTT, ClientMQTT clienteMQTT_Send, String topico, int qos) {
        this.clienteMQTT = clienteMQTT;
        this.clienteMQTT_Send = clienteMQTT_Send;
        this.clienteMQTT.subscribe(qos, this, topico);
        this.impl = impl;
    }
    
    public Listener(ControllerImpl impl) {       
        this.impl = impl;
    }
    
    public static void main(String[] args) throws Exception {
    	ControllerImpl ctrl= new ControllerImpl();
    	Listener novo = new Listener(ctrl);
    	novo.messageArrived("TOP_K_HEALTH/1/1", new MqttMessage());
    }

    @Override
    public synchronized void messageArrived( final String topic, final MqttMessage message) throws Exception {
//        System.out.println("Mensagem recebida:");
//        System.out.println("\tTópico: " + topico);
//        System.out.println("\tMensagem: " + new String(mm.getPayload()));
//        System.out.println("");
    	System.out.println("Topico: "+ topic);
    	
    	final String [] params = topic.split("/");
    	System.out.println("=========================================");
    	
//    	this.topk_k_scoresByIdrequi.put(params[1], null);
        new Thread(new Runnable() {
			public void run() {
				String messageContent = new String(message.getPayload());
				System.out.println("topic: " + topic + "message: " + messageContent);
				if(params[0].equals("TOP_K_HEALTH_RES")) {
					System.out.println("-----_----------RECEBENDO MENSAGEM DE RESPOSTA - TOK_K_RES-----------");
					byte[] b = messageContent.getBytes();
					List<String> n = impl.topk_k_scoresByIdrequi.get(params[1]);
					n.add(messageContent);
					impl.topk_k_scoresByIdrequi.put(params[1], n);
					System.out.println("TOP-K RES REcebido: " + messageContent);
					System.out.println("um no calculou seu top-k");
					System.out.println("TOP_K SCORES: " + impl.topk_k_scoresByIdrequi.get(params[1]).size());
					System.out.println("TOP_K SCORES_Gateways: " + impl.getQtdConnecteds());
					if(impl.topk_k_scoresByIdrequi.get(params[1]).size() == impl.getQtdConnecteds()) {
						impl.calculateTopK(params[1], impl.k);
					}					
				}
				if(params[0].equals("TOP_K_HEALTH")) {
		        	impl.k = Integer.valueOf(params[2]);    		
					System.out.println("-----_----------RECEBENDO MENSAGEM - TOK_K_REQUI-----------");
					byte[] b = messageContent.getBytes();
					clienteMQTT_Send.publicar(topic, b, 1);
					List<String> n = new ArrayList<String>();
					impl.topk_k_scoresByIdrequi.put(params[1], n);
					System.out.println("REQUI TOP-K REcebido: " + messageContent);
				}
				if(params[0].equals("CONNECTED")) {
					impl.add_G_Connecteds();
					System.out.println("Bottom Brokers connecteds: " + impl.getQtdConnecteds());
				}
				if(params[0].equals("DEVICES_CONNECTED")) {
					try {
						shell.restartBundle("mvn:br.ufba.dcc.wiser.soft_iot/soft-iot-mapping-devices/1.0.0");
						shell.restartBundle("mvn:br.ufba.dcc.wiser.soft_iot/soft-iot-local-storage/1.0.0");
						shell.restartBundle("mvn:br.ufba.dcc.wiser.soft_iot/soft-iot-iot-service/1.0.0");
					} catch (BundleException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						String message_new = new String(message.getPayload());
						System.out.println("vem ate aqui");
						System.out.println(impl.getIp().replace("tcp://", "")+" - "+impl.getPort()+" "+impl.getDevices().size());
						String[] device_info = message_new.split(" - ");
						
						Device d_new = new Device(device_info[0], device_info[1], Integer.parseInt(device_info[2]));
						
						impl.addDevice(device_info[0], d_new);
						
						impl.setSensorQuantity(impl.getSensorQuantity() + Integer.parseInt(message_new.split(" - ")[2]));
						
						impl.setWriteFile(true);
						clienteMQTT_Send.gatewayConnectedMessage(getIpAddress().replace("tcp://", ""), impl.getPort(), impl.getDevices().size());

					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				if(params[0].equals("BALANCE_LOAD")) {
					try {

						System.out.println("balance load 1");
						String content = new String(message.getPayload());
						String[] gateway_info = content.split(" - ");
												
						int highest = -1;
						Device d_highest = null;
						
						if(gateway_info[2].equals(getIpAddress()) && gateway_info[3].equals(impl.getPort())) {

							System.out.println("balance load 2");
							System.out.println(impl.getDevices().size());

							for (Entry<String, Device> entry : impl.getDevices().entrySet()) {
								
								System.out.println("entrou for");
								
								Device d = (Device) entry.getValue(); 

								System.out.println(((Device) entry.getValue()).getIp());
							    if(d.getQtdSensors()>highest){
							    	highest = d.getQtdSensors();
							    	d_highest = (Device) entry.getValue();
							    }
							}
							
							System.out.println("balance load 3");
							
							
							impl.setSensorQuantity(impl.getSensorQuantity()-d_highest.getQtdSensors());
							impl.resetListOfDevices();
							
							
							impl.removeDevice(d_highest.getIp());
							System.out.println("balance load 4");
							
							byte[] b = (gateway_info[0]+" - "+gateway_info[1]+" - "+d_highest.getIp()+" - "+d_highest.getPort()).getBytes();
							clienteMQTT_Send.publicar("CHANGE_BROKER", b, 1);
							System.out.println("balance load 5");
							
							impl.setWriteFile(true);
						}
						
						shell.restartBundle("mvn:br.ufba.dcc.wiser.soft_iot/soft-iot-mapping-devices/1.0.0");
						shell.restartBundle("mvn:br.ufba.dcc.wiser.soft_iot/soft-iot-local-storage/1.0.0");
						shell.restartBundle("mvn:br.ufba.dcc.wiser.soft_iot/soft-iot-iot-service/1.0.0");
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BundleException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(params[0].equals("dev")) {										
					String deviceName = topic.split("/")[1];
					
					if(!impl.getListOfDevices().contains(deviceName)) {
						impl.addListOfDevices(deviceName);
					}

					if(impl.getWriteFile() && impl.getListOfDevices().size()==impl.getSensorQuantity()) {
						writeMappingDevicesFile();
						
						try {
							shell.restartBundle("mvn:br.ufba.dcc.wiser.soft_iot/soft-iot-mapping-devices/1.0.0");
							shell.restartBundle("mvn:br.ufba.dcc.wiser.soft_iot/soft-iot-local-storage/1.0.0");
							shell.restartBundle("mvn:br.ufba.dcc.wiser.soft_iot/soft-iot-iot-service/1.0.0");
							impl.remove_G_Connecteds();
							shell.restartBundle("inputstream:soft-iot-bottom-broker-1.0.0.jar");
						} catch (BundleException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}					
				}

			}
		}).start();
    }
    
	public String getIpAddress() throws UnknownHostException, IOException {
    	Socket s = new Socket();   	
		s.connect(new InetSocketAddress("google.com", 80));
		String ip = s.getLocalAddress().toString().replace("/", "");

    	s.close();
    	return ip;
	}
    
    public void saveMsg() {
    	
    	
    }
    
    private void printlnDebug(String str){
		if (debugModeValue)
			System.out.println(str);
	}
    
	public void writeMappingDevicesFile() {

		impl.setWriteFile(false);

		try {
			FileWriter writer = new FileWriter("/home/aluno/Documents/soft-iot/apache-servicemix-6.1.1/etc/br.ufba.dcc.wiser.soft_iot.gateway.mapping_devices.cfg", true);
			
			String[] devices_id = new String[impl.getListOfDevices().size()];			
			
            int i = 0;

			for (String s : impl.getListOfDevices()) {
				devices_id[i] = s;
				i++;
			}
			
	         for(int x=0; x < devices_id.length-1; x++){  
                 for(int y=1; y < (devices_id.length-x-1); y++){  
                      if((devices_id[y-1]).compareTo(devices_id[y])<0){  
                         //swap elements  
                         String temp = devices_id[y-1];  
                         devices_id[y-1] = devices_id[y];  
                         devices_id[y] = temp;  
                     }                            
                 }  
	         }			
			
            writer.write("DevicesConnected=[");
            
	         for(int x=0; x < devices_id.length; x++){  
		            writer.write("{\"id\":\""+devices_id[x]+"\", \"latitude\":53.290411, \"longitude\":-9.074406, \"sensors\":[{\"id\":\"temperatureSensor\", \"type\":\"Thermometer\", \"collection_time\":30000, \"publishing_time\": 60000}, {\"id\":\"humiditySensor\", \"type\":\"HumiditySensor\", \"collection_time\":30000, \"publishing_time\": 60000}]}");
					i++;	
					if(x<devices_id.length) {
			            writer.write(",");
					}

	         }			
/*
            i = 0;
			for (String s : impl.getListOfDevices()) {
	            writer.write("{\"id\":\""+s+"\", \"latitude\":53.290411, \"longitude\":-9.074406, \"sensors\":[{\"id\":\"temperatureSensor\", \"type\":\"Thermometer\", \"collection_time\":30000, \"publishing_time\": 60000}, {\"id\":\"humiditySensor\", \"type\":\"HumiditySensor\", \"collection_time\":30000, \"publishing_time\": 60000}]}");
				i++;	
				if(i<impl.getListOfDevices().size()) {
		            writer.write(",");
				}
			}
			*/
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("debugMode=false");			
            writer.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
}
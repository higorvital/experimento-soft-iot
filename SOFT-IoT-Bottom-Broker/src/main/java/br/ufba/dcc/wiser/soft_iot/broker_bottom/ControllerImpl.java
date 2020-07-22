package br.ufba.dcc.wiser.soft_iot.broker_bottom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufba.dcc.wiser.soft_iot.entities.*;

public class ControllerImpl implements Controller{
	
	private List<Device> listDevices; 
	private String ip;
	private String port;
	private String user;
	private String pass;
	private boolean debugModeValue;
	private  ClientMQTT clienteMQTT;
	private  ClientIotService clienteIot;
	
	
	public void start() throws Exception{
		//printlnDebug("Starting mapping of connected devices...");		
		// TODO Auto-generated method stub
			this.clienteIot = new ClientIotService();
		 	clienteMQTT = new ClientMQTT("tcp://"  + this.ip + ":" + this.port, this.user, this.pass);
//		 	clienteMQTT = new ClientMQTT("tcp://192.168.0.106:1884", this.user, this.pass);
	        clienteMQTT.iniciar();
	        String devices = clienteIot.getApiIot("http://localhost:8181/cxf/iot-service/devices");
//	        if(devices != null) System.out.println("Conectado com Broker de FOG com sucesso!!!");
        	this.loadConnectedDevices(devices);
        	new Listener(this, clienteMQTT, "TOP_K_HEALTH/#", 1);
        	System.out.println("Chegou 6");
        	
	        
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("ufbaino08".compareTo("ufbaino07"));
		

//    	ctrl.updateValuesSensors();
//    	System.out.print(ctrl.getListDevices().get(0).getSensors().get(0).getValue());
       
    }
	
	public void stop(){
		
	        this.clienteMQTT.finalizar();
	    
	}
	
	public void updateValuesSensors() {
		for (Device d: this.listDevices) {
			d.getLastValueSensors(this.clienteIot);
		}
	}
	
	
	private void loadConnectedDevices(String strDevices){
		List<Device> listDevices = new ArrayList<Device>();
		try {
			printlnDebug("JSON load:");
			printlnDebug(strDevices);
			JSONArray jsonArrayDevices = new JSONArray(strDevices);
			for (int i = 0; i < jsonArrayDevices.length(); i++){
				JSONObject jsonDevice = jsonArrayDevices.getJSONObject(i);
				ObjectMapper mapper = new ObjectMapper();
				Device device = mapper.readValue(jsonDevice.toString(), Device.class);
				listDevices.add(device);
				
				List<Sensor> listSensors = new ArrayList<Sensor>();
				JSONArray jsonArraySensors = jsonDevice.getJSONArray("sensors");
				for (int j = 0; j < jsonArraySensors.length(); j++){
					JSONObject jsonSensor = jsonArraySensors.getJSONObject(j);
					Sensor sensor = mapper.readValue(jsonSensor.toString(), Sensor.class);
					listSensors.add(sensor);
				}
				device.setSensors(listSensors);
			}
			
		} catch (JsonParseException e) {
			System.out.println("Verify the correct format of 'DevicesConnected' property in configuration file."); 
		} catch (JsonMappingException e) {
			System.out.println("Verify the correct format of 'DevicesConnected' property in configuration file.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.listDevices = listDevices;
		System.out.println("Qtd from devices: " + this.listDevices.size());
	}
	
	public Device getDeviceById(String deviceId){
		for (Device device : listDevices ){
			if(device.getId().contentEquals(deviceId))
				return device;
		}	
		return null;		
	}
	
	private void printlnDebug(String str){
		if (debugModeValue)
			System.out.println(str);
	}

	public List<Device> getListDevices() {
		return listDevices;
	}

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

}

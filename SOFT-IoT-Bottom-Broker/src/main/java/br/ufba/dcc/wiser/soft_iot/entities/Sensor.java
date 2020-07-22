package br.ufba.dcc.wiser.soft_iot.entities;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.ufba.dcc.wiser.soft_iot.broker_bottom.ClientIotService;


public class Sensor {
	private String id;
	private String type;
	private int collection_time;
	private int publishing_time;
	private int value; 
	

	
	public Sensor(){
		
	}
	
	public void getValue(ClientIotService temp, String id_device) {
//		System.out.println("Device de id: " + id_device + " e Sensor de Id: " + this.id);
		String response = temp.getApiIot("http://localhost:8181/cxf/iot-service/devices/" + id_device + "/" + this.id);
//		System.out.println("Resposta: " + response);
		if(response != null) {
			JSONObject json = new JSONObject(response);
			this.value = Integer.valueOf(json.getString("value"));
		}else {
			this.value = 0;
		}
		
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public int getCollection_time() {
		return collection_time;
	}

	public void setCollection_time(int collection_time) {
		this.collection_time = collection_time;
	}

	public int getPublishing_time() {
		return publishing_time;
	}

	public void setPublishing_time(int publishing_time) {
		this.publishing_time = publishing_time;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
	
	
}

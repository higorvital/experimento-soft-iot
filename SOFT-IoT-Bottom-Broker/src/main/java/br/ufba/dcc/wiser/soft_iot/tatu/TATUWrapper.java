package br.ufba.dcc.wiser.soft_iot.tatu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import br.ufba.dcc.wiser.soft_iot.entities.Device;
import br.ufba.dcc.wiser.soft_iot.entities.Sensor;
import br.ufba.dcc.wiser.soft_iot.entities.SensorData;



public final class TATUWrapper {
	
	public static String topicBase = "dev/";
	
	public static String getTATUFlowInfo(String sensorId, int collectSeconds, int publishSeconds){
		String msgStr = "FLOW " + "INFO " + sensorId + " {\"collect\":" + collectSeconds + ",\"publish\":" + publishSeconds + "}";
		
		return msgStr;
	}
	
	public static String getTATUFlowValue(String sensorId, int collectSeconds, int publishSeconds){
		String msgStr = "FLOW " + "VALUE " + sensorId + " {\"collect\":" + collectSeconds + ",\"publish\":" + publishSeconds + "}";
		
		return msgStr;
	}
	
	//{"CODE":"POST","METHOD":"FLOW","HEADER":{"NAME":"ufbaino04"},"BODY":{"temperatureSensor":["36","26"],"FLOW":{"publish":10000,"collect":5000}}}
	public static boolean isValidTATUAnswer(String answer){
		try{
			JSONObject json = new JSONObject(answer);
			if ((json.get("CODE").toString().contentEquals("POST"))
					&& json.getJSONObject("BODY") != null) {
				return true;
			}
		} catch (org.json.JSONException e) {
		}
		return false;
	}
	
	public static String getDeviceIdByTATUAnswer(String answer){
		JSONObject json = new JSONObject(answer);
		String deviceId = json.getJSONObject("HEADER").getString("NAME");
		
		return deviceId;
	}
	
	public static String getSensorIdByTATUAnswer(String answer){
		JSONObject json = new JSONObject(answer);
		Iterator<?> keys = json.getJSONObject("BODY").keys();
		String sensorId = keys.next().toString();
		while(sensorId.contentEquals("FLOW")){
			sensorId = keys.next().toString();
		}
		return sensorId;
	}
	
	public static List<SensorData> parseTATUAnswerToListSensorData(String answer,Device device, Sensor sensor, Date baseDate){
		List<SensorData> listSensorData = new ArrayList<SensorData>();
		try{
			JSONObject json = new JSONObject(answer);
			JSONArray sensorValues = json.getJSONObject("BODY").getJSONArray(
					sensor.getId());
			int collectTime = json.getJSONObject("BODY").getJSONObject("FLOW")
					.getInt("collect");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(baseDate);
			for (int i = 0; i < sensorValues.length(); i++) {
				Integer valueInt = sensorValues.getInt(i);
				String value = valueInt.toString();
				SensorData sensorData = new SensorData(device, sensor,value,calendar.getTime(),calendar.getTime());
				listSensorData.add(sensorData);
				calendar.add(Calendar.MILLISECOND, collectTime);
			}
		}catch(org.json.JSONException e){
		}
		return listSensorData;
	}

}
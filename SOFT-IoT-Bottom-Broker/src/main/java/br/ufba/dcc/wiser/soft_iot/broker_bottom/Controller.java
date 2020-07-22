package br.ufba.dcc.wiser.soft_iot.broker_bottom;

import java.util.List;

import br.ufba.dcc.wiser.soft_iot.entities.Device;



public interface Controller {
	
	List<Device> getListDevices();	
	
	Device getDeviceById(String deviceId);
	
}

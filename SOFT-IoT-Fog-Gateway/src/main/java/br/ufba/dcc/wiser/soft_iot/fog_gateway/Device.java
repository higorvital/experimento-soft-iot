package br.ufba.dcc.wiser.soft_iot.fog_gateway;

public class Device {
	
	private String ip;
	private String port;
	private int qtdSensors;
	
	public Device(String ip, String port, int qtdSensors) {
		this.ip = ip;
		this.port = port;
		this.qtdSensors = qtdSensors;
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public String getPort() {
		return this.port;
	}

	public int getQtdSensors() {
		return this.qtdSensors;
	}		
}
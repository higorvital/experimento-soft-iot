import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Gateway {
	
	private String ip;
	private String port;
	private int qtdDevices;
	
	public Gateway(String ip, String port, int qtdDevices) {
		this.ip = ip;
		this.port = port;
		this.qtdDevices = qtdDevices;
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public String getPort() {
		return this.port;
	}

	public int getQtdDevices() {
		return this.qtdDevices;
	}
		
	public void removeDevice() {
		this.qtdDevices--;
	}

	public void addDevice() {
		this.qtdDevices++;
	}
		
}
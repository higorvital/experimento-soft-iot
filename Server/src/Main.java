import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.xml.sax.SAXException;


public class Main {
	public static void main(String[] args) throws SAXException, IOException {
		if(args.length > 0){
			Properties config = new Properties();
			FileInputStream file = new FileInputStream(args[0]);
			config.load(file);

			Controller controller = new Controller(config);
			System.out.println("OPA");
			controller.connectDeviceMqtt();
			System.out.println("vem ate aqui");
		}else{
			System.out.println("To execute IoT Virtual Device you need pass gateways.properties path as parameter");
			System.exit(-1);
		}
		
	}
	

}

package no.ntnu.osnap.com.testing;

import java.util.concurrent.TimeoutException;
import no.ntnu.osnap.com.deprecated.ComLayer;

public class ProtocolTest {
    public static void main(String[] args) throws TimeoutException, InterruptedException {
        ComLayer arduino = new ComLayer();
		//arduino.setDaemon(true);
		arduino.start();
		
		boolean toggle = false;
        
        for (long i = 0; i < 1000; ++i){
            //arduino.print(new String(new char[250]));
            //arduino.newPrint(new SimpleDateFormat("HH:mm:ss").format(new Date()));
			//arduino.pulse(13);
			//int value = arduino.sensor(0);
            //System.out.println("value: " + value);
			//arduino.write(13, value);
            arduino.print(i + "s");
			arduino.print(i + "s", true);
			
			//Thread.sleep(100);
			
			//arduino.print(value + "   ", false);
			//System.out.println("0: " + arduino.sensor(0));
			//System.out.println("10: " + arduino.sensor(10));
			//System.out.println("20: " + arduino.sensor(20));
        }
		
		Thread.sleep(10000);
		arduino.stopThread();
		
		while (arduino.isAlive())
			Thread.sleep(100);
		
		arduino.close();
        
        //arduino.print("ZooPark");
        //arduino.print("D");
		System.out.println("Done");
    }
    
}

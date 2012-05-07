package no.ntnu.osnap.com.testing;

import java.util.concurrent.TimeoutException;
import no.ntnu.osnap.com.deprecated.ComLayer;

public class ProtocolTest {
    public static void main(String[] args) throws TimeoutException, InterruptedException {
        ComLayer arduino = new ComLayer();
		//arduino.setDaemon(true);
		arduino.start();
		
		boolean toggle = false;
        
        for (long i = 120; i < 1000; ++i){
			System.out.println("Printing " + (byte)i);
            arduino.data(new byte[]{(byte)i}, true);
			Thread.sleep(1000);
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.osnap.com.testing;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.ntnu.osnap.com.deprecated.ComLayer;

/**
 *
 * @author anders
 */
public class ProtocolTest {
    public static void main(String[] args) {
        ComLayer arduino = new ComLayer();
		arduino.setDaemon(true);
		arduino.start();
		
		boolean toggle = false;
        
        for (long i = 0; i < 10000000; ++i){
            //arduino.print(new String(new char[250]));
            //arduino.newPrint(new SimpleDateFormat("HH:mm:ss").format(new Date()));
			//arduino.pulse(13);
			int value = arduino.sensor(0);
            //System.out.println("value: " + value);
			//arduino.write(13, value);
            //arduino.print(i + "s");
			
			arduino.print(value + "   ", false);
        }
        
        //arduino.print("ZooPark");
        //arduino.print("D");
		System.out.println("Done");
    }
    
}

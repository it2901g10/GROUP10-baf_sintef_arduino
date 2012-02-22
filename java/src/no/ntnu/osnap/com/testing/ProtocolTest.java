/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.osnap.com.testing;

import java.text.SimpleDateFormat;
import java.util.Date;
import no.ntnu.osnap.com.deprecated.ComLayer;

/**
 *
 * @author anders
 */
public class ProtocolTest {
    public static void main(String[] args) {
        ComLayer arduino = new ComLayer();
        
        for (int i = 0; i < 100000000; ++i){
            //arduino.print(new String(new char[250]));
            arduino.print(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            System.out.println("value: " + arduino.sensor(0));
            //arduino.print(i + "s");
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                
            }*/
        }
        
        //arduino.print("ZooPark");
        //arduino.print("D");
    }
    
}

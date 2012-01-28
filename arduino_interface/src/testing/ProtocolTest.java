/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.util.logging.Level;
import java.util.logging.Logger;
import source.Protocol;

/**
 *
 * @author anders
 */
public class ProtocolTest {
    public static void main(String[] args) {
        Protocol arduino = new Protocol();
        
        for (int i = 0; i < 100; ++i){
            arduino.print("T: " + i + "s");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                
            }
        }
        
        //arduino.print("ZooPark");
        //arduino.print("D");
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import source.Protocol;

/**
 *
 * @author anders
 */
public class ProtocolTest {
    public static void main(String[] args) {
        Protocol arduino = new Protocol();
        arduino.print("ZooPark");
        //arduino.print("D");
    }
    
}

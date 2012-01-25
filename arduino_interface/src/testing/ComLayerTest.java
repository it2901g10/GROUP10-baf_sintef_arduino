/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.util.Scanner;
import source.ComLayer;

/**
 *
 * @author anders
 */
public class ComLayerTest {
    public static void main(String[] args) {
        ComLayer com = new ComLayer();
        
        Scanner in = new Scanner(System.in);
        
        while (true){
            while (in.hasNextLine()){
                com.sendMsg(in.nextLine());
            }
        }
    }
}

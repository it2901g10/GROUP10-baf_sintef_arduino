/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import debug.Debug;

/**
 *
 * @author anders
 */
public class DebugTest {
    public static void main(String[] args) {
        Debug.setDebugLevel(5);
        Debug.println("test1", 4);
        Debug.println("test2", 5);
        Debug.println("test3", 6);
    }
}

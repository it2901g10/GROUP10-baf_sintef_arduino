/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import debug.Log;
import java.util.logging.Level;

/**
 *
 * @author anders
 */
public class DebugTest {
    public static void main(String[] args) {
        //Log.setStdOut(true);
        Log.setLevel(Level.ALL);
        Log.addMessage(Level.SEVERE, "test1");
        Log.addMessage(Level.WARNING, "test2");
        Log.addMessage(Level.CONFIG, "test3");
        Log.printLog();
    }
}

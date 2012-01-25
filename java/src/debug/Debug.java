/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package debug;

/**
 *
 * @author anders
 */
public class Debug {
    private static int debugLevel = 0;
    
    public static void setDebugLevel(int level){
        debugLevel = level;
    }
    
    public static int getDebugLevel(){
        return debugLevel;
    }
    
    public static void print(String message, int level){
        if (level > debugLevel) return;
        
        System.out.print(message);
        // Output to logger
    }
    
    public static void println(String message, int level){
        print(message + "\n", level);
    }
}

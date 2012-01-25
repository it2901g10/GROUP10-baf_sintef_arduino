/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package java.src.debug;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xolan
 */
public class Log {
    
    private static final Log instance = new Log();
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private Level currentLevel = Level.OFF;  //Default is OFF
    private boolean stdout = false;
    
    //There should be only one instance of the logger...
    private Log() { }
    
    public static Log getInstance() {
        return instance;
    }
    
    public static void addMessage(Level level, String message) {
        getInstance().logger.log(level, message);
    }
    
    public static Level getLevel() {
        return getInstance().currentLevel;
    }
    
    public static void setLevel(Level level) {
        getInstance().currentLevel = level;
    }
    
    public static boolean getStdOut() {
        return getInstance().stdout;
    }
    
    public static void setStdOut(boolean foo) {
        getInstance().stdout = foo;
    }
    
    public static void toggleStdOut() {
        getInstance().stdout = !getInstance().stdout;
    }
    
}

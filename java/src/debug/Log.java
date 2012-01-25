package debug;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonas Svarvaa
 */
public class Log {
    
    private static final Log instance = new Log();
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private Level currentLevel = Level.OFF;  //Default is OFF
    private boolean stdout = false;
    private List<String> list = new ArrayList<String>();
    
    //There should be only one instance of the logger...
    private Log() { }
    
    public static Log getInstance() {
        return instance;
    }
    
    public static void addMessage(Level level, String message) {
        String messageString = System.currentTimeMillis() + " ["+getLevel()+"] " + message;
        getInstance().logger.log(level, messageString);
        getInstance().list.add(messageString);
        if(getStdOut()) {
            System.out.println(messageString);
        }
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
    
    public static List<String> getLog() {
        return getInstance().list;
    }
    
    public static void printLog() {
        System.out.println("### Log start ###");
        for(String s : getInstance().getLog()) {
            System.out.println(s);
        }
        System.out.println("### Log end ###");
    }
    
    public static void main(String[] args) {
        Log.setStdOut(true);
        Log.setLevel(Level.ALL);
        Log.addMessage(Level.ALL, "test");
        Log.printLog();
    }
    
}

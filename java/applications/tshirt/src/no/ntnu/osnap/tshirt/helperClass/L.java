package no.ntnu.osnap.tshirt.helperClass;

import android.util.Log;

public class L {
    
    private static String LOGTAG = "TshirtApp";
    public static void i(String message){
        Log.i(LOGTAG, message);
    }
    public static void e(String message){
        Log.e(LOGTAG, message);
    }
}

package no.ntnu.osnap.com.testing;

import java.util.logging.Level;
import java.util.logging.Logger;
import no.ntnu.osnap.com.BluetoothConnection;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class AndroidBluetoothTest extends Activity {
    private TextView GUI;
    private BluetoothConnection con;
    	    
    private void print(String line) {
		GUI.append(line);
    	Log.v("BluetoothTest", line);
    }
    
	private void printLine(String line) {
		print(line + "\n");
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.e("DEBUG", "onCreate");
    	//Initialize GUI, do first
        super.onCreate(savedInstanceState);
        GUI = new TextView(this);
        setContentView(GUI);
        
        //Start connection
		setTitle("program is running");
		try {
			con = new BluetoothConnection("00:10:06:29:00:48", this); //BluetoothBee address
			con.connect();
			printLine("Trying to connect: " + con.getAddress());
			
		} catch (Exception e) {
			printLine("Could not establish connection: " + e.getMessage());
		}
    }
        
    @Override
    public void onStart() {
    	super.onStart();
    	
    	if(con != null){
    		while(!con.isConnected()) {
				/*wait until we are connected*/
				try {
					Thread.sleep(10);
				} catch (InterruptedException ex) {}
			}
    		con.print("Hi");
			con.print("derp");
    	}
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    }
    

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
/*		try {
			con.disconnect();
		} catch (IOException e) {
			Log.e("ERROR", "Read this: " + e.getMessage());
		}
*/
        
    }

    
}

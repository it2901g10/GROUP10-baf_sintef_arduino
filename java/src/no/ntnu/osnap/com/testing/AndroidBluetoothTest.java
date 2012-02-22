package no.ntnu.osnap.test;

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
    	
    	//Initialize GUI, do first
        super.onCreate(savedInstanceState);
        GUI = new TextView(this);
        setContentView(GUI);
    }
        
    @Override
    public void onStart() {
    	super.onStart();

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

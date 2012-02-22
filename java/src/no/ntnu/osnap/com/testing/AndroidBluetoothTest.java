package no.ntnu.osnap.test;

import java.io.IOException;

import no.ntnu.osnap.com.BluetoothConnection;
import no.ntnu.osnap.com.BluetoothConnection.ConnectionState;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
    
 // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        		printLine(device.getName() + " (" + device.getAddress() + ")");
            }
        }
    };
    
    @Override
    public void onStart() {
    	super.onStart();

		setTitle("program is running");
		
		/*printLine("Initialize bluetooth");
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		adapter.enable();
		
	    // Register the BroadcastReceiver
		adapter.startDiscovery();
	    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	    registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy    
		*/
		
		try {
			con = new BluetoothConnection("00:10:06:29:00:48", getApplicationContext()); //BluetoothBee address
			con.connect();
			printLine("Trying to connect: " + con.getAddress());
//			while( con.getConnectionState() != ConnectionState.STATE_CONNECTED ){
//			}
//			printLine("Connection established! " + con.getAddress());
			
		} catch (Exception e) {
			printLine("Could not establish connection: " + e.getMessage());
		}
		
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	//this.finish();
    }
    

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
		try {
			con.disconnect();
		} catch (IOException e) {
			Log.e("ERROR", "Read this: " + e.getMessage());
		}
		
    }

    
}

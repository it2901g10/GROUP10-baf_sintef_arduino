package no.ntnu.osnap.test;

import java.util.ArrayList;

import no.ntnu.osnap.arduinointerface.Protocol;
import no.ntnu.osnap.bluetooth.ArduinoBluetoothAdapter;
import no.ntnu.osnap.bluetooth.ArduinoBluetoothAdapter.UnsupportedHardwareException;
import no.ntnu.osnap.bluetooth.ArduinoBluetoothAdapterListener;
import no.ntnu.osnap.bluetooth.ArduinoBluetoothConnection;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class AndroidBluetoothTest extends Activity implements ArduinoBluetoothAdapterListener {
    private TextView GUI;
    private ArduinoBluetoothAdapter adapter;
    private Protocol protocol;
    	    
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
        
        //Create the adapter
        try {
			adapter = new ArduinoBluetoothAdapter(this, this);
		} catch (UnsupportedHardwareException e) {
			e.printStackTrace();
			printLine("Unable to initialize bluetooth hardware: " + e.getMessage());
		}
    }
    
    @Override
    public void onStart() {
    	super.onStart();

		setTitle("program is running");		
    	
        //Start scanning for Arduino modules
    	try {
    		setTitle("Scanning for Arduino devices...");		
			adapter.scanArduinoDevices();
		} catch (UnsupportedHardwareException e) {
			printLine("Unable to initiate scan: " + e.getMessage());
		}
    }    

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopScanning();
    }

	public void scanComplete(ArrayList<String> arduinoDevicesFound) {
		setTitle("Finished scanning for devices...");		
	}

	public void arduinoDeviceFound(ArduinoBluetoothConnection arduinoDevice) {
		printLine("Connected to an Arduino!");
		
		protocol = new Protocol(arduinoDevice);
		
		for( int i = 0; i < 10000; i++)
			protocol.print(new String(new char[256]));
		
		arduinoDevice.close();
	}
    
}

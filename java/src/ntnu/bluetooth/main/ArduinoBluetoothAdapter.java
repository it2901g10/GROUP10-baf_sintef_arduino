package ntnu.bluetooth.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;


public class ArduinoBluetoothAdapter {
    private final BluetoothAdapter wBluetooth;
    private final ArduinoBluetoothAdapterListener callbackListener;
    private final Activity app;
    
    private ArrayList<String> finishedDevices = new ArrayList<String>();  //list of non-arduino devices found
	private ArrayList<String> arduinoDevices = new ArrayList<String>();  //list of confirmed arduino devices found
	private boolean filterRegistered;
    
	public class UnsupportedHardwareException extends Exception {
		private static final long serialVersionUID = 7361286372494041006L;
		
		public UnsupportedHardwareException(String message) {
			super(message);
		}
    }

	public ArduinoBluetoothAdapter(Activity app, ArduinoBluetoothAdapterListener listener) throws UnsupportedHardwareException {
		
		//Initialize bluetooth adapter
		wBluetooth = BluetoothAdapter.getDefaultAdapter();
    	if( wBluetooth == null ){
        	throw new UnsupportedHardwareException("Bluetooth hardware not found!");
    	}
		
		finishedDevices = new ArrayList<String>();  //list of non-arduino devices found
		arduinoDevices = new ArrayList<String>();  //list of confirmed arduino devices found
		this.app = app;
		this.callbackListener = listener;
	}
		
    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //When discovery is enabled
            if( BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action) ) {
            	Log.e("BluetoothTest", "START SCANNING");             	
            }
            
            // When discovery finds a device
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                ArduinoBluetoothConnection connection;
            	                
            	//Is this a device we already have tried connecting to?
            	if (finishedDevices.contains(device.getAddress())) {
                	Log.e("BluetoothTest", "Already tried this device: " + device.getAddress());             	
            		return;
            	}

        		//Don't discover this one again
        		finishedDevices.add( device.getAddress() );            	
            	
                //Stop discovery mode while connecting
            	Log.e("BluetoothTest", "Found new device: " + device.getName() + " (" + device.getAddress() + ")");             	

            	//Try to establish connection
            	try {
                	wBluetooth.cancelDiscovery();
        			connection = new ArduinoBluetoothConnection(device);
            		Log.e("BluetoothTest", "Found an Arduino Device!");
        			arduinoDevices.add( device.getAddress() );
        			stopScanning();
        			callbackListener.arduinoDeviceFound(connection);
        			return;
        		} 
            	
            	catch (Exception e2) {
                	Log.e("BluetoothTest", "Unable to open connection: " + e2.getMessage());
        		}
            	
    		//Restart discovery mode
    		wBluetooth.startDiscovery();
    		        		            	                
            // When discovery is finished, tell the listener
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	Log.e("BluetoothTest", "STOP SCANNING");
            	callbackListener.scanComplete(arduinoDevices);
            }
        }
    };    

    public void scanArduinoDevices() throws UnsupportedHardwareException {

    	//Reset old search sessions
    	finishedDevices.clear();
    	arduinoDevices.clear();
    	
        //Enable bluetooth if needed
        if( !wBluetooth.isEnabled() ) {
        	//printLine("Bluetooth is disabled - starting Bluetooth.");
	        if( !wBluetooth.enable() ) {
	        	throw new UnsupportedHardwareException("Bluetooth could not be enabled!");
	        }
        }
        
        //Abort any active discovery process
        if( wBluetooth.isDiscovering() ){
        	wBluetooth.cancelDiscovery();
        }
        
        //Start new scanning session
        wBluetooth.startDiscovery();    	
        
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        app.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        app.registerReceiver(mReceiver, filter);       
        
        // Register for broadcasts when discovery has started
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        app.registerReceiver(mReceiver, filter);
        
        //Tell the listener we have begun scanning
        filterRegistered = true;
    }   
	
	/**
	 * Creates and establishes a new connection to an Arduino device.
	 * @param macAddress the address of the remote device
	 * @throws IOException Throws an exception if we could not get a connection with the device or if
	 * 		               the device is not an Arduino.
	 * @throws TimeoutException If remote device uses too long time to respond
	 * @throws IllegalArgumentException is thrown if the specified BluetoothDevice is not an Arduino device
	 */    
    public ArduinoBluetoothConnection openConnection(String macAddress) throws IllegalArgumentException, IOException, TimeoutException {
    	BluetoothDevice device;
    	
    	//check if the address is valid
    	if( !BluetoothAdapter.checkBluetoothAddress(macAddress) ){
    		throw new IllegalArgumentException("Invalid mac address");
    	}
    	
    	//Get the device.. this always works even if the device is not nearby
    	device = wBluetooth.getRemoteDevice(macAddress);
    	
    	//establish actual connection - this can throw an IOException on any errors
		return new ArduinoBluetoothConnection(device);
    }
    
    public void stopScanning() {    	
    	//Disable discover mode
        if( wBluetooth.isDiscovering() ){
        	wBluetooth.cancelDiscovery();
        }
                
        // Unregister broadcast listeners
        if( filterRegistered ) {
	        app.unregisterReceiver(mReceiver);
	        filterRegistered = false;
        }
        
        //Tell the listener our scan is complete
        callbackListener.scanComplete(arduinoDevices);
    }
}

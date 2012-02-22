package no.ntnu.osnap.com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class BluetoothConnection {
	private Context context;
	
	protected BufferedInputStream input;
	protected BufferedOutputStream output;
	
	protected BluetoothDevice device;
	protected BluetoothSocket socket;
	protected BluetoothAdapter bluetooth;	
	
	private ConnectionState connectionState;
	
	public enum ConnectionState {
		STATE_DISCONNECTED,
		STATE_CONNECTING,
		STATE_CONNECTED
	}

	
	public BluetoothConnection(BluetoothDevice device, Context context) throws UnsupportedHardwareException, IllegalArgumentException, IOException{
		this(device.getAddress(), context);
	}
	
	
	public BluetoothConnection(String address, Context context) throws UnsupportedHardwareException, IllegalArgumentException, IOException {
		
		//Validate the address
		if( !BluetoothAdapter.checkBluetoothAddress(address) ){
			throw new IllegalArgumentException("The specified bluetooth address is not valid");
		}
		
		//Make sure this device has bluetooth
		bluetooth = BluetoothAdapter.getDefaultAdapter();
		if( bluetooth == null ){
			throw new UnsupportedHardwareException("No bluetooth hardware found");
		}		
		
		this.context = context;
		connectionState = ConnectionState.STATE_DISCONNECTED;
		device = bluetooth.getRemoteDevice(address);
		
		//Register broadcast receivers
//		context.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_REQUEST_ENABLE));
		context.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
	}	
	
	synchronized void setConnectionState(ConnectionState setState) {
		connectionState = setState;
	}
		
    private synchronized final void establishConnection() {    	
		//Start an asynchronous connection and return immediately so we do not interrupt program flow
		ConnectionThread thread = new ConnectionThread(this);
		thread.start();    	
    }
    
	public synchronized void connect() {
		
		//Don't try to connect more than once
		if( connectionState != ConnectionState.STATE_DISCONNECTED ) {
			Log.w("BluetoothConnection", "Trying to connecto to the same device twice!");
			return;
		}
		
		//Start connecting
    	setConnectionState(ConnectionState.STATE_CONNECTING);
		
		//Make sure bluetooth is enabled
		if( !bluetooth.isEnabled() ) {
			//wait until Bluetooth is enabled by the OS
			context.sendBroadcast(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BluetoothAdapter.ACTION_REQUEST_ENABLE);
			return;
		}
		
		//Stop discovery when connecting
		if( bluetooth.isDiscovering() ){
			//TODO: implement intent for this
			return;
		}
		
		//All is good!
		establishConnection();
	}

	
	public String getAddress() {
		return device.getAddress();
	}

	public String toString() {
		return device.getName();
	}
	
	public void sendBytes(byte[] data) throws IOException {
		output.write(data);
	}
	
	public synchronized ConnectionState getConnectionState() {
		return connectionState;
	}

	public synchronized void disconnect() throws IOException {
		
		//Close socket only if we are connected
		if(getConnectionState() == ConnectionState.STATE_CONNECTED)
		{
			input.close();
			output.close();
			socket.close();
		}
		
		//We are now officially disconnected
		setConnectionState(ConnectionState.STATE_DISCONNECTED);
	}
	
/*
  	private class InputListenerThread extends Thread {
		private BufferedInputStream inputStream;
		
		public InputListenerThread(BufferedInputStream inputStream) {
			super.setDaemon(true);
			this.inputStream = inputStream;
		}
		
		@Override
		public void run() {
			while(true){
				try {
					int readByte = inputStream.read();
					listener.byteReceived((byte)readByte);
				} catch (IOException e) {
					Log.e("BluetoothConnection", "Read error: " + e.getMessage());
					try {
						inputStream.available();
					} catch (IOException e1) {
						//If this happens there is an error with the connection
						break;
					}
				}
			}
		}
	}
*/
	
	 // Create a BroadcastReceiver for enabling bluetooth
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
                                    
            //Device is turning on or off
            if( action.equals(BluetoothAdapter.ACTION_STATE_CHANGED) ) {
            	
            	switch(bluetooth.getState())
            	{
            		case BluetoothAdapter.STATE_TURNING_ON:
            			//Don't care
            	    break;
            	    
            		case BluetoothAdapter.STATE_TURNING_OFF:
            		case BluetoothAdapter.STATE_OFF:
						try { disconnect(); } catch (IOException e) {}
            		break;
            		
            		case BluetoothAdapter.STATE_ON:
            			if( getConnectionState() == ConnectionState.STATE_CONNECTING ) {
            				establishConnection();
            			}
            		break;         		
            	}
            	            		
            }
        }
        
    };	
		
}

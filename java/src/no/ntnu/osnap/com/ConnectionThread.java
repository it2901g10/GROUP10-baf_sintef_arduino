package no.ntnu.osnap.com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import no.ntnu.osnap.com.BluetoothConnection.ConnectionState;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * A package private class used by BluetoothConnection. Only one instance of this class should exist per
 * BluetoothConnection after connect() has been executed. The thread will try to connect to the
 * remote device through Bluetooth and set the BluetoothConnection state to STATE_CONNECTED and exit when
 * the connection is established successfully. A disconnect() on the BluetoothConnection will kill any
 * running ConnectionThread.
 */
class ConnectionThread extends Thread {	
	private final BluetoothConnection connection;
	
	/**
	 * Create new thread to connect with the remote device
	 * @param connection Which BluetoothConnection created this thread
	 * @throws IllegalArgumentException If the specified BluetoothConnection is already connected.
	 */
	public ConnectionThread(BluetoothConnection connection) throws IllegalArgumentException {
		this.connection = connection;
		
		if( connection.getConnectionState() == ConnectionState.STATE_CONNECTED ) {
			throw new IllegalArgumentException("The specified BluetoothConnection is already connected!");
		}
				
		setDaemon(true);
		setName("Connection Thread: " + connection.device.getName() + " (" + connection.device.getAddress() + ")");		
	}
	
	@Override
	public void run() {
		Log.e("", "STARTING CONNECTION");
		
		//Wait until bluetooth is finished discovering
		while( connection.bluetooth.isDiscovering() ) {
			try {
				wait(250);
			} catch (InterruptedException e) {}
		}
		
		//Create a socket through a hidden method (normal method does not work on all devices like Samsung Galaxy SII)
		try {
			Method m  = connection.device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
			connection.socket = (BluetoothSocket) m.invoke(connection.device, Integer.valueOf(1));
		}
		catch (Exception ex){
			Log.e("ConnectionThread", "Unable to create socket: " + ex.getMessage());
			connection.setConnectionState(ConnectionState.STATE_DISCONNECTED);
			return;
		}		
		
		//Connect to the remote device
		try {
			connection.socket.connect();
		} catch (IOException ex) {
			Log.e("ConnectionThread", "Unable to open socket: " + ex.getMessage());
			connection.setConnectionState(ConnectionState.STATE_DISCONNECTED);
			return;
		}
				
		//Get input and output streams
		try {
	    	connection.output = new BufferedOutputStream(connection.socket.getOutputStream());
	    	connection.input = new BufferedInputStream(connection.socket.getInputStream());	
		} catch (IOException ex) {
			Log.e("ConnectionThread", "Unable to get input/output stream: " + ex.getMessage());
			connection.setConnectionState(ConnectionState.STATE_DISCONNECTED);
			return;
		}
		
		//Start a background thread listening for input
//		inputThread = new InputListenerThread(input);
//		inputThread.start();
		
		//We are now connected!
		connection.setConnectionState(ConnectionState.STATE_CONNECTED);
	}
}
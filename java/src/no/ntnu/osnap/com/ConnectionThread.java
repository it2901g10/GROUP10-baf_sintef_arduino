/*
* Copyright 2012 Anders Eie, Henrik Goldsack, Johan Jansen, Asbjørn 
* Lucassen, Emanuele Di Santo, Jonas Svarvaa, Bjørnar Håkenstad Wold
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/
package no.ntnu.osnap.com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

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
	private volatile boolean connectionSuccessful;
	
	/**
	 * Create new thread to connect with the remote device
	 * @param connection Which BluetoothConnection created this thread
	 * @throws IllegalArgumentException If the specified BluetoothConnection is already connected.
	 */
	public ConnectionThread(BluetoothConnection connection) throws IllegalArgumentException {
		this.connection = connection;
		connectionSuccessful = false;
		
		if( connection.getConnectionState() == ConnectionState.STATE_CONNECTED ) {
			throw new IllegalArgumentException("The specified BluetoothConnection is already connected!");
		}
				
		setDaemon(true);
		setName("Connection Thread: " + connection.device.getName() + " (" + connection.device.getAddress() + ")");
	}
	
	/**
	 * This thread monitors and polls for new data recieved from the remote device
	 */
	private class PollingThread extends Thread {
		public void run() {			
			
			//Keep listening bytes from the stream
			while( connection.isConnected() ){
				try {
					int readByte = connection.input.read();
					if( readByte != -1 ) {
				    	connection.byteReceived( (byte)readByte );
					}
					else {
						try { Thread.sleep(10); } catch (InterruptedException ex) {}
					}
				} catch (IOException e) {
					Log.e("ConnectionThread", "Read error: " + e.getMessage());
					connection.disconnect();
				}			
			}
			
			Log.i("BluetoothConnection", "Stopped polling for new data.");
		}
	}
	
	@Override
	public void run() {
		
		//Wait until bluetooth is finished discovering
		while( connection.bluetooth.isDiscovering() && connection.getConnectionState() != ConnectionState.STATE_DISCONNECTED ) {
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
		
		//Open socket in new thread because socket.connect() is blocking
		Thread socketThread = new Thread(){
			@Override
			public void run() {
				try {
					connection.socket.connect();
					connectionSuccessful = true;
				} catch (IOException ex) {
					Log.e("ConnectionThread", "Unable to open socket: " + ex);
					connection.disconnect();
				}
			}
		};
		
		//Wait until connection is successful or TIMEOUT milliseconds has passed
		socketThread.start();		
		long timeout = System.currentTimeMillis() + Protocol.TIMEOUT;
		while(!connectionSuccessful) {
			if(System.currentTimeMillis() > timeout) {
				connection.disconnect();
				return;
			}			
			try {Thread.sleep(10); } catch (InterruptedException e) {}
		}
				
		//Get input and output streams
		try {
	    	connection.output = new BufferedOutputStream(connection.socket.getOutputStream());
	    	connection.input = new BufferedInputStream(connection.socket.getInputStream());	
		} catch (IOException ex) {
			Log.e("ConnectionThread", "Unable to get input/output stream: " + ex.getMessage());
			connection.disconnect();
			return;
		}

		//Start the super protocol thread loop
		connection.setConnectionState(ConnectionState.STATE_FINALIZE_CONNECTION);
		new Thread(connection).start();
		new PollingThread().start();
				
		//Check if we are connected properly by sending a metadata request
		try {
			connection.handshakeConnection();
		} catch (TimeoutException e) {
			Log.e("ConnectionThread", "Failed to setup connection: Could not retrieve ConnectionMetadata (" + e + ")");
			connection.disconnect();
			return;
		}

		//We are now connected!
		connection.setConnectionState(ConnectionState.STATE_CONNECTED);						
	}

}

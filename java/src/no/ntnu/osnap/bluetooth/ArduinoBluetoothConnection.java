package no.ntnu.osnap.bluetooth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import no.ntnu.osnap.arduinointerface.ComLayerListener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ArduinoBluetoothConnection {
	private BluetoothSocket socket;
	private BufferedInputStream input;
	private BufferedOutputStream output;
	private ArrayList<ComLayerListener> listenerList;
	
	static final byte[] PING_COMMAND = new byte[] {(byte)0xFF, (byte)0x04, (byte)0x00, (byte)0xFF, (byte)0x00};   
		
	/**
	 * Creates and establishes a new connection to an Arduino device.
	 * @param device Which device we will try to establish an connection to
	 * @throws IOException Throws an exception if we could not get a connection with the device or if
	 * 		               the device is not an Arduino.
	 * @throws TimeoutException If remote device uses too long time to respond
	 * @throws IllegalArgumentException is thrown if the specified BluetoothDevice is not an Arduino device
	 */
	ArduinoBluetoothConnection(BluetoothDevice device) throws IOException, IllegalArgumentException, TimeoutException {		
		
		//Create a socket
		try {
			Method m  = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
			socket = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));
		}
		catch (Exception ex){
			throw new IOException("Unable to create socket: " + ex.getMessage());
		}
	
		Log.e("BluetoothTest", "STARTING CONNECT");
		socket.connect();
		Log.e("BluetoothTest", "CONNECT SUCCESS");
		
		//Get input and output streams
    	output = new BufferedOutputStream(socket.getOutputStream());
		input = new BufferedInputStream(socket.getInputStream());

		Log.e("BluetoothTest", "STREAMS ACQUIRED");
		
		//Say hello
		Log.e("BluetoothTest", "SENDING PING");
		sendData(PING_COMMAND);
		
		//Wait for ack
		Log.e("BluetoothTest", "WAITING FOR ACK");
		byte highByte = (byte)input.read();
		byte lowByte = (byte)input.read();
		
		//Check if respond ID matches
		if( highByte != (byte)0x00 || lowByte != (byte)0xFF ) {
			close();
			throw new IllegalArgumentException("The specified device is not an Arduino device!");
		}
		
		//Construct a listener list
		listenerList = new ArrayList<ComLayerListener>();
		
		//Add an event listener to the input stream
		new InputListenerThread(input).start();
	}
	
	private class InputListenerThread extends Thread {
		private BufferedInputStream inputStream;
		
		public InputListenerThread(BufferedInputStream inputStream) {
			this.inputStream = inputStream;
		}
		
		@Override
		public void run() {
			while(true){
				try {
					int readByte = inputStream.read();
					for(ComLayerListener listener : listenerList){
						listener.byteReceived((byte)readByte);
					}
				} catch (IOException e) {
					break;
				}
			}
		}
	}
	
	public void addComLayerListener(ComLayerListener listener) {
		listenerList.add(listener);
	}
	
	public void sendData(byte[] data) throws IOException{
		output.write(data);
		output.flush();
	}
		
	/**
	 * Gracefully close the connection with the peer
	 * @return true if the connection was successfully broken
	 */
	public boolean close(){
		
		//Try to close connection
		try {
			output.close();
			input.close();
			socket.close();
		} catch (IOException e) {
			Log.e("BluetoothTest", "Unable to close connection: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
}

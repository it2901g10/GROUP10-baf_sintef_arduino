package ntnu.bluetooth.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ArduinoBluetoothConnection {
	private BluetoothSocket socket;
	private BufferedInputStream input;
	private BufferedOutputStream output;
	
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
		
		int timeouts = 0;
		while(true){
			
			//Say hello
			Log.e("BluetoothTest", "SENDING PING");
			sendData(PING_COMMAND);
			
			//Wait for ack
			try {
				Log.e("BluetoothTest", "WAITING FOR ACK");
				byte[] ack = recieveData(2);
				
				//Check if respond ID matches
				if( ack[0] != (byte)0x00 || ack[1] != (byte)0xFF ) {
					close();
					Log.e("BluetoothTest", "NOT AN ARDUINO!");
					throw new IllegalArgumentException("The specified device is not an Arduino device! " + Byte.toString(ack[0]) + ", " + Byte.toString(ack[1]));
				}
					
				//success!
				Log.e("BluetoothTest", "ARDUINO FOUND");
				break;
			}
			catch( TimeoutException ex){
				Log.e("BluetoothTest", "TIMEOUT");
				timeouts++;
			}
			
			//give up after three tries
			if( timeouts >= 3 ){
				close();
				throw new TimeoutException("Remote device used too long time to respond.");				
			}			
		}
		
	}

	
	public void sendData(byte[] data) throws IOException{
		output.write(data);
		output.flush();
	}
	
	public byte[] recieveData(int length) throws IOException, TimeoutException {
		int totalBytesRead = 0;
		byte[] data = new byte[length];
		
		long timeStart = System.currentTimeMillis() + 2500;
		
		//Read the entire packet
		do {
			int byteRead;
			
			//Timeout?
			if( System.currentTimeMillis() > timeStart ) throw new TimeoutException("Communication timeout.");
			
			//Read one byte
			byteRead = input.read();
			Log.e("READ_BYTE", "Read one byte: " + byteRead);
			
			//Throw an exception if we reached end of the stream
			if(byteRead == -1) throw new IOException("Reached end of stream prematurely.");
			
			//Store the byte in the array
			data[totalBytesRead++] = (byte) byteRead;
		}
		while(totalBytesRead < length);
			
		return data;
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

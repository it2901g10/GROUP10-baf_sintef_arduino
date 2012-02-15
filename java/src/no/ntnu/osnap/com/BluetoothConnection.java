package no.ntnu.osnap.com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothConnection implements ComLayerInterface {
	private ComLayerListener listener;
	
	protected BufferedInputStream input;
	protected BufferedOutputStream output;
	
	private BluetoothDevice device;
	private BluetoothSocket socket;
	private BluetoothAdapter bluetooth;	
	private InputListenerThread inputThread;
	private boolean isConnected = false;
	
	public BluetoothConnection(BluetoothDevice device) throws UnsupportedHardwareException, IllegalArgumentException, IOException{
		this(device.getAddress());
	}
	
	public BluetoothConnection(String address) throws UnsupportedHardwareException, IllegalArgumentException, IOException {
		
		//Validate the address
		if( !BluetoothAdapter.checkBluetoothAddress(address) ){
			throw new IllegalArgumentException("The specified bluetooth address is not valid");
		}
		
		//Make sure this device has bluetooth
		bluetooth = BluetoothAdapter.getDefaultAdapter();
		if( bluetooth == null ){
			throw new UnsupportedHardwareException("No bluetooth hardware found");
		}
		
		//Make sure it is enabled
		if( !bluetooth.isEnabled() ){
			bluetooth.enable();
		}
		
		//Stop scanning when connecting
		if( bluetooth.isDiscovering() ) {
			bluetooth.cancelDiscovery();
		}
		
		//Get the remote device
		device = bluetooth.getRemoteDevice(address);
		
		//Create a socket through a hidden method (normal method does not work on all devices like Samsung Galaxy SII)
		try {
			Method m  = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
			socket = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));
		}
		catch (Exception ex){
			throw new IOException("Unable to create socket: " + ex.getMessage());
		}
		
		//Connect to the remote device
		//TODO: new thread?
		socket.connect();
				
		//Get input and output streams
    	output = new BufferedOutputStream(socket.getOutputStream());
		input = new BufferedInputStream(socket.getInputStream());	
		
		//Start a background thread listening for input
		inputThread = new InputListenerThread(input);
		inputThread.start();
		
		//We are now connected!
		isConnected = true;
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
	
	public boolean isConnected() {
		return isConnected;
	}


/*	public void onDestroy(){
		super.onDestroy();
		
		//Close socket
		try {
			socket.close();
			input.close();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}*/
	
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


	public void setListener(ComLayerListener listener) {
		this.listener = listener;
	}
	
}

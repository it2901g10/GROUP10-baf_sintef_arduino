package no.ntnu.osnap.com;

public interface ConnectionListener {
	/**
	 * Event that is fired whenever a connection was successfully established
	 * @param connection The connection this event was fired from
	 */
	void onConnect(BluetoothConnection connection);
	
	/**
	 * Event that is fired whenever a new connection is being established
	 * @param connection The connection this event was fired from
	 */
	void onConnecting(BluetoothConnection connection);
	
	/**
	 * Event that is fired whenever a connection was closed
	 * @param connection The connection this event was fired from
	 */
	void onDisconnect(BluetoothConnection connection);
}

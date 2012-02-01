package ntnu.bluetooth.main;

import java.util.ArrayList;

public interface ArduinoBluetoothAdapterListener {
	public void arduinoDeviceFound(ArduinoBluetoothConnection arduinoDevice);
	public void scanComplete(ArrayList<String> arduinoDevicesFound);
}

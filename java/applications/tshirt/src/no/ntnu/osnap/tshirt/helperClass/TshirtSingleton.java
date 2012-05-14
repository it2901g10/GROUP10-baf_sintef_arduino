/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.ntnu.osnap.tshirt.helperClass;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;
import no.ntnu.osnap.com.BluetoothConnection;
import no.ntnu.osnap.com.ConnectionListener;
import no.ntnu.osnap.com.UnsupportedHardwareException;
import no.ntnu.osnap.tshirt.R;

import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.TimeoutException;

public class TshirtSingleton {

    private static TshirtSingleton instance = null;

    //Database
    //BT Connection
    private Context context;
    public RulesDB database;
    private BluetoothConnection con;

    /**
     * What service we are working on (Example facebook) (If we have multiple)
     */
    public String serviceName;

    /**
     * Boolean value if we want our service to run in the background
     */
    public boolean serviceActivated;

    public TshirtSingleton(Context applicationContext) {
        context = applicationContext;
        database = new RulesDB(context);
        database.open();

    }

    /**
     * Requires activity to create BT connection
     */
    public void initBTConnection(Activity activity) {
        try {
            con = new BluetoothConnection("00:10:06:29:00:48", activity, getConnectionListener());
            con.connect();
        } catch (UnsupportedHardwareException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        this.con = con;


    }

    public static TshirtSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new TshirtSingleton(context.getApplicationContext());
        }
        return instance;
    }
    //Connection


    public String getServiceName() {
        L.i("getName" + serviceName);
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        L.i("SetName" + serviceName);

        this.serviceName = serviceName;
    }

    public void toggleArduinoConnection() {

        Toast.makeText(context, "toggleArduinoConnection() is not yet implemented", Toast.LENGTH_SHORT).show();
    }

    public void sendToArduino(String output, String device) {

        L.i("Sending data " + output + " to " + device + " on Arduino");

        if (device.equals(context.getString(R.string.outputDISPLAY))) {
            sendToLCDArduino(output);
        } else if (device.equals(context.getString(R.string.outputLED))) {
            sendToLEDArduino(output);
        } else if (device.equals(context.getString(R.string.outputVIBRATOR))) {
            sendToVibratorArduino(output);
        } else if (device.equals(context.getString(R.string.outputSPEAKER))) {
            sendToSpeakerArduino(output);
        } else {
            L.e("Err, Unknown output" + device + context.getString(R.string.outputVIBRATOR));
        }
    }

    private void sendToLEDArduino(String text) {
        try {
            con.write(5, true, false);
            Thread.sleep(1000);
            con.write(5, false, false);
        } catch (TimeoutException e) {
            L.e(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Log.i("ARDUINO###############", " ## # LED" + text);

    }

    private void sendToLCDArduino(String text) {
        Log.i("ARDUINO###############", " ## # " + text);
    }

    private void sendToVibratorArduino(String text) {
        Log.i("ARDUINO###############", " ## # " + text);
    }

    private void sendToSpeakerArduino(String text) {
        try {
            con.data(new byte[]{100, 75, 52, 15}, false);
        } catch (TimeoutException e) {
            L.e(e.getMessage());
        }
        Log.i("ARDUINO###############", " ## # " + text);
    }


    public ConnectionListener getConnectionListener() {
        return new ConnectionListener() {
            public void onConnect(BluetoothConnection bluetoothConnection) {
                L.i("Connected! (" + con.toString() + ")");
            }

            public void onConnecting(BluetoothConnection bluetoothConnection) {
                L.i("Connecting");
            }

            public void onDisconnect(BluetoothConnection bluetoothConnection) {
                L.i("Disconnected");
            }
        };
    }

    public void disconnect() {
        if(con != null || !con.isConnected()){
            con.disconnect();
        }
    }


    public void connect() {
        if(con != null && con.isConnected()){
            con.connect();
        }
    }
    
    public boolean isConnected(){
        if(con != null){
            return con.isConnected();
        }
        return false;
    }
}

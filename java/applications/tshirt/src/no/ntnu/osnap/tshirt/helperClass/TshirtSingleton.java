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
import android.widget.Toast;
import no.ntnu.osnap.com.BluetoothConnection;
import no.ntnu.osnap.com.ComLibException;
import no.ntnu.osnap.com.ConnectionListener;
import no.ntnu.osnap.social.Prototype;
import no.ntnu.osnap.tshirt.R;

import java.util.concurrent.TimeoutException;

public class TshirtSingleton {

    private static TshirtSingleton instance = null;

    //Database
    //BT Connection
    private Context context;
    public RulesDB database;
    private BluetoothConnection con;
    public Prototype prototype;

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
            con = new BluetoothConnection("00:10:06:29:00:48", activity, getConnectionListener(activity));
            con.connect();
        } catch (ComLibException e) {
            L.e(e.getMessage());
        }
    }

    public static TshirtSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new TshirtSingleton(context.getApplicationContext());
        }
        return instance;
    }


    /** Set social service to retrieve data from */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /** Get social service name to retrieve data from */
    public String getServiceName() {
        L.d("getName |" + serviceName + "|");
        return serviceName;
    }

    public void toggleArduinoConnection() {

        Toast.makeText(context, "toggleArduinoConnection() is not yet implemented", Toast.LENGTH_SHORT).show();
    }

    public void sendToArduino(String output, String device) {

        L.d("Sending data " + output + " to " + device + " on Arduino");

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

    }

    private void sendToLCDArduino(String text) {

        try {
            con.print(text);
        } catch (TimeoutException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void sendToVibratorArduino(String text) {
        try {
            con.write(4, true, false);
            Thread.sleep(1000);
            con.write(4, false, false);
        } catch (TimeoutException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void sendToSpeakerArduino(String text) {
        try {
            con.data(new byte[]{100, 75, 52, 15}, false);
        } catch (TimeoutException e) {
            L.e(e.getMessage());
        }
    }


    public ConnectionListener getConnectionListener(final Activity activity) {
        return new ConnectionListener() {
            public void onConnect(BluetoothConnection bluetoothConnection) {
                quickToast("Connected");
                L.d("Connected! (" + con.toString() + ")");
            }

            public void onConnecting(BluetoothConnection bluetoothConnection) {
                quickToast("Connecting");
                L.d("Connecting");
            }

            public void onDisconnect(BluetoothConnection bluetoothConnection) {
                quickToast("Disconnected");
                L.d("Disconnected");
            }
            private void quickToast(final String message){
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
    }

    public void disconnect() {
        if(con != null && con.isConnected()){
           con.disconnect();
        }
        else{
            L.d("Unable to disconnect");
        }
    }


    public void connect() {
        if(con != null && con.isConnected()){
            con.connect();
            try {
                con.print("Connected to App");
            } catch (TimeoutException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
    
    public boolean isConnected(){
        if(con != null){
            return con.isConnected();
        }
        return false;
    }
}

package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import no.ntnu.osnap.com.BluetoothConnection;
import no.ntnu.osnap.com.ConnectionListener;
import no.ntnu.osnap.com.UnsupportedHardwareException;

import java.util.concurrent.TimeoutException;

public class TestTshirt extends Activity {


    BluetoothConnection con;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ConnectionListener listener = getConnectionListener();
        try {
            L.i("creating new connection");
//            con = new BluetoothConnection("00:06:66:42:9B:C1", this, listener);
            con = new BluetoothConnection("00:10:06:29:00:48", this, listener);
        } catch (UnsupportedHardwareException e) {
            e.printStackTrace();
        }
        setOnClickListners();


    }

    private void setOnClickListners() {
        Button connect = (Button) findViewById(R.id.buttonConnect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    L.i("InitConnect To bluetooth");
                    con.connect();
                } catch (Exception e) {
                    Toast.makeText(TestTshirt.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        Button lcdDisplay = (Button)findViewById(R.id.buttonLCDDisplay);
        lcdDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                L.i( "Sending string");
                if(isPingConnected()){
                    L.i("Is Connected");
                    try {
                        con.print("ab", true);
                        L.i("called con.print()");
                    } catch (TimeoutException e) {
                        L.i(e.getMessage());
                    }
                    catch (Exception e){
                        L.i( e.getMessage());
                    }

                }

            }
            
        });

    }
    private boolean isPingConnected(){
        try {
            con.ping();
            L.i("Is ping connected");
            return true;
        } catch (TimeoutException e) {
            L.i( "Timeout, is not ping connected");
            return false;
        }
    }

    private ConnectionListener getConnectionListener() {
        return new ConnectionListener() {
            @Override
            public void onConnect(BluetoothConnection bluetoothConnection) {
                L.i("Connected to bluetooth");
                quickToastMessage("Connected!");
            }

            @Override
            public void onConnecting(BluetoothConnection bluetoothConnection) {
                L.i( "Trying to connect to bluetooth");
                quickToastMessage("Connecting");
            }

            @Override
            public void onDisconnect(BluetoothConnection bluetoothConnection) {
                L.i( "Disconnected from bluetooth");
                quickToastMessage("Disconnected");
            }
        };

    }

    private void quickToastMessage(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TestTshirt.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    //private void quickToastMessage(String s)
}

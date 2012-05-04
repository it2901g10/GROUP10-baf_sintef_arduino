package com.example;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import no.ntnu.osnap.com.BluetoothConnection;
import no.ntnu.osnap.com.ConnectionListener;

public class TestTshirt extends Activity {

    BluetoothConnection con;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
/*        ConnectionListener listener = getConnectionListener();
        try {
            L.i("creating new connection");
            con = new BluetoothConnection("00:06:66:42:9B:C1", this, listener);		//the red bluetooth thingy
            //con = new BluetoothConnection("00:10:06:29:00:48", this, listener);
            //con = new BluetoothConnection("00:12:02:10:44:77", this, listener);			//anders sin module
            //con = new BluetoothConnection("00:12:02:09:03:72", this, listener);			//anders sin module nr 2
        } catch (UnsupportedHardwareException e) {
            e.printStackTrace();
        }*/
        
        setOnClickListners();
    }
    
    private Timer timer = new Timer();
    
    private Button connectionButton;
    private Button loadMacAddressButton;

    private void setOnClickListners() {
        loadMacAddressButton = (Button) findViewById(R.id.buttonMacScan);
        loadMacAddressButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	try{
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            	}
            	catch(ActivityNotFoundException ex) {
            		quickToastMessage("You need to install zxing Barcode scanner!");
            	}
            }
        });
    	
    	
        connectionButton = (Button) findViewById(R.id.buttonConnect);
        setButtonVisible(connectionButton, false);
        connectionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                	if(con.isConnected())
                	{
                    	con.disconnect();
                	}
                	else
                	{
                    	con.connect();
                	}
                	
                } catch (Exception e) {
                    quickToastMessage(e.getMessage());
                }
            }
        });
        
        Button lcdDisplay = (Button)findViewById(R.id.buttonLCDDisplay);
        lcdDisplay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                L.i( "Sending string");
                if(con.isConnected()){
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

        Button soundModule = (Button)findViewById(R.id.buttonSpeaker);
        soundModule.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(con.isConnected()){
    	    		try {
						con.print("qwertyui", false);
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }

            }
            
        });

        Button vibrationModule = (Button)findViewById(R.id.buttonVibrator);
        vibrationModule.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(con.isConnected()){            
                	
                	timer.schedule(new TimerTask(){
                		public void run(){
                        	try {
								con.write(3, true, false);
	                        	Thread.sleep(2000);
	                        	con.write(3, false, false);
							} 
                        	catch (Exception e) {
                                quickToastMessage(e.getMessage());
							}
                		}
                	}, 0);

                }

            }
            
        });

    }

    private ConnectionListener getConnectionListener() {
        return new ConnectionListener() {
            public void onConnect(BluetoothConnection bluetoothConnection) {
                L.i("Connected to bluetooth");
                quickToastMessage("Connected!");
                changeButtonText(connectionButton, "Disconnect");
                setTitle("Connected to " + con.toString());
                setButtonVisible(connectionButton, true);
            }

            public void onConnecting(BluetoothConnection bluetoothConnection) {
                L.i( "Trying to connect to bluetooth");
                quickToastMessage("Connecting");
                changeButtonText(connectionButton, "Connecting....");
                setButtonVisible(connectionButton, false);
            }

            public void onDisconnect(BluetoothConnection bluetoothConnection) {
                L.i( "Disconnected from bluetooth");
                quickToastMessage("Disconnected");
                changeButtonText(connectionButton, "Connect");
                setTitle("Not connected");
                setButtonVisible(connectionButton, true);
            }
        };

    }
    
    private void setTitle(final String title) {
        this.runOnUiThread(new Runnable() {
            public void run() {
            	TestTshirt.super.setTitle(title);
            	}
            });
    }
    
    private void setButtonVisible(final Button button, final boolean visible) {
        this.runOnUiThread(new Runnable() {
            public void run() {
            	if(visible)	    button.setVisibility(Button.VISIBLE);
            	else			button.setVisibility(Button.INVISIBLE);
            }
        });    	
    }
    
    private void changeButtonText(final Button button, final String text) {
        this.runOnUiThread(new Runnable() {
            public void run() {
            	button.setText(text);
            }
        });
    }

    private void quickToastMessage(final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(TestTshirt.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    //private void quickToastMessage(String s)
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	try {
			if(con != null) con.disconnect();
		} catch (IOException e) {
			L.i("Failed to disconnect: " + e);
		}
    }
    
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            if (requestCode == 0) {
            	
            	//Successful scan
                if (resultCode == RESULT_OK) {
                    String macAddress = intent.getStringExtra("SCAN_RESULT");
                    setTitle("MAC Address: " + macAddress);
                    
                    // Handle successful scan
                    try {
						con = new BluetoothConnection(macAddress, this, getConnectionListener());
	                    setButtonVisible(connectionButton, true);
					} catch (Exception e) {
						quickToastMessage(e.getMessage());
						con = null;
	                    setButtonVisible(connectionButton, false);
					}
                } 
                
                //Failed scan
                else if (resultCode == RESULT_CANCELED) {
                    // Handle cancel
                	con = null;
                    setButtonVisible(connectionButton, false);
                    quickToastMessage("Failed to scan!");
                }
            }
        }
}

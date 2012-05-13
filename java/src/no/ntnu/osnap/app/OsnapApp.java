package no.ntnu.osnap.app;

/*con = new BluetoothConnection("00:06:66:42:9B:C1", this, listener);		//the red bluetooth thingy
//con = new BluetoothConnection("00:10:06:29:00:48", this, listener);
//con = new BluetoothConnection("00:12:02:10:44:77", this, listener);			//anders sin module
//con = new BluetoothConnection("00:12:02:09:03:72", this, listener);			//anders sin module nr 2
*/


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Layout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import no.ntnu.osnap.com.BluetoothConnection;
import no.ntnu.osnap.com.ConnectionListener;
import no.ntnu.osnap.com.ConnectionMetadata;
import no.ntnu.osnap.com.ConnectionMetadata.DefaultServices;

public class OsnapApp extends Activity {

    private static final int CUSTOM_REQUEST_QR_SCANNER = 0;
    
    private Timer timer;
    private BluetoothConnection con;

    private TableLayout layout;
    Button disconnectButton;
    
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        layout = new TableLayout(this);
        layout.setLayoutParams( new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT) );

        //Initialize scan button
        addButton("Scan QR Tag", new View.OnClickListener() {
            public void onClick(View view) {
            	try{
	                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
	                startActivityForResult(intent, CUSTOM_REQUEST_QR_SCANNER);
            	}
            	catch(ActivityNotFoundException ex) {
            		quickToastMessage("You need to install zxing Barcode scanner!");
            	}
            }
        });
        
        //Initialize disconnect/reconnect button
        disconnectButton = new Button(this);
        disconnectButton.setText("Disconnect");
        disconnectButton.setOnClickListener( new OnClickListener(){

			public void onClick(View v) {
				removeButton(disconnectButton);
				if(con != null) con.disconnect();				
			}
        	
        });
        
        timer = new Timer();
        
        super.setContentView(layout);         
    }
    
    private void addButton(final Button button) {
       runOnUiThread(new Runnable() {
            public void run() {
            	layout.addView(button);
            }
       });
    }
    
    private void removeButton(final Button button) {
        runOnUiThread(new Runnable() {
             public void run() {
             	layout.removeView(button);
             }
        });
    }
    
    private void addButton(final String text, final View.OnClickListener listener) {
        runOnUiThread(new Runnable() {
            public void run() {
		    	Button button = new Button(OsnapApp.this);
		    	
		    	button.setText(text);
		    	button.setOnClickListener(listener);
		    	
		    	layout.addView(button);
        }
   });
    }
        
    /*private void setOnClickListners() {
    	
        connectionButton = (Button) findViewById(R.id.buttonConnect);
        setButtonVisible(connectionButton, false);
        connectionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	if(con == null) return;
            	if(con.isConnected())	con.disconnect();
            	else					con.connect();
            }
        });
        
        Button lcdDisplay = (Button)findViewById(R.id.buttonLCDDisplay);
        lcdDisplay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(con.isConnected()){
                    try {
                        con.print("Update: " + System.currentTimeMillis(), false);
                    }
                    catch (Exception e){
                        L.i( e.getMessage());
                    }

                }

            }
            
        });

        Button ledButton = (Button)findViewById(R.id.buttonLED);
        ledButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
        		try {
        			ledIsOn = !ledIsOn;
					con.write(2, ledIsOn, false);
				} catch (TimeoutException e) {
                    L.i( e.getMessage());
				}
            }
            
        });


        Button soundModule = (Button)findViewById(R.id.buttonSpeaker);
        soundModule.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(con.isConnected()){
    	    		try {
						con.data(new byte[]{100, 75, 52, 15}, false);
					} catch (TimeoutException e) {
						L.i(e.getMessage());
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
                        		
                        		//Vibrate mobile
                        		Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        		vib.vibrate(2000);
                        		
                        		//Vibrate remote module
								con.write(4, true, false);
	                        	Thread.sleep(2000);
	                        	con.write(4, false, false);
	                        	
							} 
                        	catch (Exception e) {
                                quickToastMessage(e.getMessage());
							}
                		}
                	}, 0);

                }

            }
            
        });

    }*/ 
    
    /**
     * SERVICE_LED
     */
    private class LedButton extends Button implements View.OnClickListener{
    	boolean ledIsToggled;
    	int pin;
    	
		public LedButton(Context context, int pin) {
			super(context);
			ledIsToggled = false;
			setOnClickListener(this);
			setText("Toggle LED (" + pin + ")");
			this.pin = pin;
		}

		public void onClick(View v) {
			ledIsToggled = !ledIsToggled;
			try {
				con.write(pin, ledIsToggled, false);
			} catch (TimeoutException e) {}
		}
    	
    }
    
    /**
     * SERVICE_SPEAKER
     */
    private class SpeakerButton extends Button implements View.OnClickListener {
    	
		public SpeakerButton(Context context, int pin) {
			super(context);
			setOnClickListener(this);
			setText("Play Sound (" + pin + ")");
		}

		public void onClick(View v) {
			try {
				con.data(new byte[]{100, 75, 52, 15}, false);
			} catch (TimeoutException e) {}
		}
    	
    }
    
    /**
     * SERVICE_VIBRATION
     */
    private class VibrationButton extends Button implements View.OnClickListener {
    	Timer timer = new Timer();
    	int pin;
    	
		public VibrationButton(Context context, int pin) {
			super(context);
			setOnClickListener(this);
			setText("Vibration (" + pin + ")");
			this.pin = pin;
		}

		public void onClick(View v) {
        	timer.schedule(new TimerTask(){
        		public void run(){
                	try {
                		
                		//Vibrate mobile
                		Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                		vib.vibrate(2000);
                		
                		//Vibrate remote module
						con.write(pin, true, false);
                    	Thread.sleep(2000);
                    	con.write(pin, false, false);
                    	
					} 
                	catch (Exception e) {
                        quickToastMessage(e.getMessage());
					}
        		}
        	}, 0);
		}
    }

    private ConnectionListener getConnectionListener() {
        return new ConnectionListener() {
            public void onConnect(BluetoothConnection bluetoothConnection) {
                quickToastMessage("Connected! (" + con.toString() + ")");
                addButton(disconnectButton);
                
                //Add a button for every service found
                ConnectionMetadata meta = con.getConnectionData();
				for(String service : meta.getServicesSupported()) {
					Integer pins[] = meta.getServicePins(service);
					
					if(pins.length > 0) {
						if(service.equals(DefaultServices.SERVICE_LED_LAMP.name()))  for(int pin : pins) addButton(new LedButton(OsnapApp.this, pin));
						if(service.equals(DefaultServices.SERVICE_VIBRATION.name()))  for(int pin : pins) addButton(new VibrationButton(OsnapApp.this, pin));
						if(service.equals(DefaultServices.SERVICE_SPEAKER.name())) addButton(new SpeakerButton(OsnapApp.this, pins[0]));
					}
					
					//Unknown button
					else {
						Button button = new Button(OsnapApp.this);
						button.setText("Unknown " + "(" + service + ")");
						addButton(button);
					}
					
				}
                
            }

            public void onConnecting(BluetoothConnection bluetoothConnection) {
                quickToastMessage("Connecting");
            }

            public void onDisconnect(BluetoothConnection bluetoothConnection) {
                quickToastMessage("Disconnected");
                setTitle("Not connected");
            }
        };

    }
    
    private void setTitle(final String title) {
        runOnUiThread(new Runnable() {
            public void run() {
            	OsnapApp.super.setTitle("OSNAP - " + title);
            	}
            });
    }
    
    private void setButtonVisible(final Button button, final boolean visible) {
        runOnUiThread(new Runnable() {
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
                Toast.makeText(OsnapApp.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	if(con != null) con.disconnect();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CUSTOM_REQUEST_QR_SCANNER) {
        	
        	//Successful scan
            if (resultCode == RESULT_OK) {
                String macAddress = intent.getStringExtra("SCAN_RESULT");
                
                // Handle successful scan
                try {
					con = new BluetoothConnection(macAddress, this, getConnectionListener());
					con.connect();					
				} catch (Exception e) {
					quickToastMessage(e.getMessage());
					con = null;
				}
            } 
            
            //Failed scan
            else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            	con = null;
            	removeButton(disconnectButton);
                quickToastMessage("Failed to scan!");
            }
        }
    }
}

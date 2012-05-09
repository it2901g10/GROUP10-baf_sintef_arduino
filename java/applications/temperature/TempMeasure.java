package no.ntnu.osnap.temp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.ntnu.osnap.com.BluetoothConnection;
import no.ntnu.osnap.social.Prototype;
import no.ntnu.osnap.social.Request;
import no.ntnu.osnap.social.Response;

public class TempMeasure extends Activity {

    private final String DEF_VALUE = "def";
    private Toast toast;
    private long lastBackPressTime = 0;
    SharedPreferences preferences;
    private BluetoothConnection blueTooth = null;
    private int analog0;
    private int analog1;
    private double tempMeasured;
    private double tempMax;
    private double tempMin;
    private boolean macSet;
    private String macAddress;
    private TextView viewTemperature;
    private TextView viewMin;
    private TextView viewMax;
    private TextView lastReset;
    private TextView lastUpdate;
    private Prototype proto;
    private String facebook;
    private no.ntnu.osnap.com.ConnectionListener listener;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //set the Font for the text
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/DSDIGI.TTF");
        viewTemperature = (TextView) findViewById(R.id.temperatureShow);
        viewMax = (TextView) findViewById(R.id.temperatureMax);
        viewMin = (TextView) findViewById(R.id.temperatureMin);
        lastReset = (TextView) findViewById(R.id.lastreset);
        lastUpdate = (TextView) findViewById(R.id.lastupdate);

        //connectionlistener
        listener = new no.ntnu.osnap.com.ConnectionListener() {

            public void onConnect(BluetoothConnection bc) {
            }

            public void onConnecting(BluetoothConnection bc) {
            }

            public void onDisconnect(BluetoothConnection bc) {
            }
        };

        viewTemperature.setTypeface(tf);
        viewMax.setTypeface(tf);
        viewMin.setTypeface(tf);

        //Load preferences        
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //get mac address from preferences
        macAddress = preferences.getString(getString(R.string.pref_mac), DEF_VALUE);
        macSet = preferences.getBoolean(getString(R.string.mac_set), false);

        //fetch previous values for temp, max, min
        tempMeasured = Double.parseDouble(preferences.getString(getString(R.string.text_temp), "0"));
        tempMax = Double.parseDouble(preferences.getString(getString(R.string.text_max), "0"));
        tempMin = Double.parseDouble(preferences.getString(getString(R.string.text_min), "0"));

        //set previous values for temp, max, min
        viewTemperature.setText("+" + tempMeasured);
        if (tempMax == -99.99) {
            viewMax.setVisibility(TextView.INVISIBLE);
        } else {
            setMaxTemp(tempMax);
        }
        if (tempMin == +99.99) {
            viewMin.setVisibility(TextView.INVISIBLE);
        } else {
            setMinTemp(tempMin);
        }

        //get/set update/reset
        lastReset.setText(preferences.getString("lastReset", "Last reset: "));
        lastUpdate.setText(preferences.getString("lastUpdate", "Last update: "));

        //check 
        if (DEF_VALUE.equals(macAddress)) {
            alertBuilder().show();
        } else if (!DEF_VALUE.equals(macAddress)) {
            preferences.edit().putBoolean(getString(R.string.mac_set), true).commit();
            macSet = preferences.getBoolean(getString(R.string.mac_set), false);
            connectBluetooth();
        }

        //social stuff
        proto = new Prototype(this, new ServiceListener());
        proto.discoverServices();
    }

    private void connectBluetooth() {
        //Set up the bluetooth connection
        try {
            if (blueTooth == null && macSet) {
                Log.d("lol", "macSet connect bluetooootooththoth");
                blueTooth = new BluetoothConnection(macAddress, this, listener);
                blueTooth.connect();
            }
        } catch (Exception e) {
            Log.d("lol", "Failed connection: " + e.getMessage() + " " + e.getClass());
        }
    }

    //Alert dialog for missing mac Address, starts preference activity
    private AlertDialog alertBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        builder.setMessage("Missing Arduino Unit, go to Settings?").
                setCancelable(false).setPositiveButton(
                "Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                //call qr scan intent
                Intent prefScreen = new Intent(TempMeasure.this, Preferences.class);
                startActivityForResult(prefScreen, 0);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        return alert;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (blueTooth != null && macSet) {
                blueTooth.disconnect();
            }
        } catch (IOException ex) {
            Log.d("lol", "didn't destroy bluetooth");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent prefScreen = new Intent(TempMeasure.this, Preferences.class);
                startActivityForResult(prefScreen, 0);
                break;

            case R.id.share:
                Bundle param = new Bundle();
                param.putString("message", "" + tempMeasured);
                proto.sendRequest(facebook,
                        Request.obtain(Request.RequestCode.POST_MESSAGE, param),
                        new DumbListener());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            macSet = preferences.getBoolean(getString(R.string.mac_set), false);
            macAddress = preferences.getString(getString(R.string.pref_mac),
                    DEF_VALUE);
            if (macSet && blueTooth == null) {
                connectBluetooth();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 2000) {
            toast = Toast.makeText(this, "Press back again to exit", 2000);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            toast.cancel();
            super.onBackPressed();
        }
    }

    public void onClick(View v) throws FileNotFoundException, IOException {
        if (preferences.getBoolean("preftap", true) && blueTooth != null) {
            tempMeasured = getTemp();
            prefStringEdit(getString(R.string.text_temp), ""+tempMeasured);
            setMaxTemp(tempMeasured);
            setMinTemp(tempMeasured);
            lastUpdate.setText("Last updated: " + getDate());
            prefStringEdit("lastUpdate", ""+lastUpdate.getText());
            if (tempMeasured >= 0) {
                viewTemperature.setText("+" + tempMeasured);
            } else {
                viewTemperature.setText("" + tempMeasured);
            }
        }
    }
    
    private void prefStringEdit(String key, String value) {
        preferences.edit().putString(key, value).commit();
    }
    
    public String getDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("EEE d'.' MMM 'at' HH:mm");
        return format.format(date);
    }

    public void onReset(View v) throws FileNotFoundException, IOException {
        prefStringEdit(getString(R.string.text_max), "-99.99");
        prefStringEdit(getString(R.string.text_min), "+99.99");
        viewMax.setVisibility(TextView.INVISIBLE);
        viewMin.setVisibility(TextView.INVISIBLE);
        lastReset.setText("Last reset: " + getDate());
        prefStringEdit("lastReset", ""+lastReset.getText());
    }
    
    //fetches the temperature data from Arduino unit
    private double getTemp() {
        try {
            analog0 = blueTooth.sensor(0);
            analog1 = blueTooth.sensor(1);
            return ((analog0 - analog1) * 5 * 100) / 1024.0;
        } catch (TimeoutException ex) {
            Logger.getLogger(TempMeasure.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ((analog0 - analog1) * 5 * 100) / 1024.0;
    }

    private void setMaxTemp(double newTemp) {
        double currentMax = Double.parseDouble(viewMax.getText().toString());
        viewMax.setVisibility(TextView.VISIBLE);
        if (newTemp >= 0 && newTemp > currentMax) {
            viewMax.setText("+" + newTemp);
        } else if (newTemp < 0 && newTemp > currentMax) {
            viewMax.setText("" + newTemp);
        }
        prefStringEdit(getString(R.string.text_max), ""+viewMax.getText());
    }

    private void setMinTemp(double newTemp) {
        double currentMin = Double.parseDouble(viewMin.getText().toString());
        viewMin.setVisibility(TextView.VISIBLE);
        if (newTemp >= 0 && newTemp < currentMin) {
            viewMin.setText("+" + newTemp);
        } else if (newTemp < 0 && newTemp < currentMin) {
            viewMin.setText("" + newTemp);
        }
        prefStringEdit(getString(R.string.text_min), ""+viewMin.getText());
    }

    //social stuff
    private class ServiceListener implements no.ntnu.osnap.social.listeners.ConnectionListener {

        public void onConnected(String service) {
            facebook = service;
        }
    }

    private class DumbListener implements no.ntnu.osnap.social.listeners.ResponseListener {

        public void onComplete(Response response) {
        }
    }
}

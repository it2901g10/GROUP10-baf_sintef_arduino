package no.ntnu.osnap.temp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.IOException;
import no.ntnu.osnap.com.BluetoothConnection;
import no.ntnu.osnap.social.Prototype;
import no.ntnu.osnap.social.Request;
import no.ntnu.osnap.social.Response;
import no.ntnu.osnap.social.listeners.*;

public class TempMeasure extends Activity {

    private final String DEF_VALUE = "def";
    private Toast toast;
    private long lastBackPressTime = 0;
    private ShareActionProvider mShareActionProvider;
    SharedPreferences preferences;
    private BluetoothConnection blueTooth = null;
    private int analog0;
    private int analog1;
    private double tempMeasured;
    private double tempMinimum;
    private double tempMaximum;
    private boolean macSet = false;
    private String macAddres;
    private TextView tempShow;
    private TextView tempMin;
    private TextView tempMax;
    private Prototype proto;
    private String facebook;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //set the Font for the text
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/DSDIGI.TTF");
        tempShow = (TextView) findViewById(R.id.temperatureShow);
        tempMax = (TextView) findViewById(R.id.temperatureMax);
        tempMin = (TextView) findViewById(R.id.temperatureMin);
        
        
        tempShow.setTypeface(tf);
        tempMax.setTypeface(tf);
        tempMin.setTypeface(tf);
        
        //Load preferences        
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        macAddres = getPreferences(MODE_PRIVATE).getString("addres", DEF_VALUE);
        
        if (DEF_VALUE.equals(macAddres)) {
            alertBuilder().show();
        } else macSet = false;

        //social stuff
        proto = new Prototype(this, new ServiceListener());
        proto.discoverServices();

        //Set up the bluetooth connection
//        try {
//            if (blueTooth == null && macSet) {
//                blueTooth = new BluetoothConnection(macAddres, this);
//                blueTooth.connect();
//                Log.d("lol", "Trying to connect");
//            }
//        } catch (Exception e) {
//            Log.d("lol", "Failed connectoin" + e.getMessage());
//        }
    }

    //scanning in macAddress
    private AlertDialog alertBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Missing Arduino Unit, scan QR now?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                //call qr scan intent
                scanSomething();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        return alert;
    }

    public void scanSomething() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                getPreferences(MODE_PRIVATE).edit().putString("addres", contents).commit();
                macAddres = getPreferences(MODE_PRIVATE).getString("addres", DEF_VALUE);
                macSet = true;
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                macSet = false;
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tempShow.setText(savedInstanceState.getCharSequence("tempValue"));
        tempMax.setText(savedInstanceState.getCharSequence("tempMaxValue"));
        tempMin.setText(savedInstanceState.getCharSequence("tempMinValue"));
        Log.d("instance", "load");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("tempValue", tempShow.getText());
        outState.putCharSequence("tempMaxValue", tempMax.getText());
        outState.putCharSequence("tempMinValue", tempMin.getText());
        Log.d("instance", "save");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        try {
//            blueTooth.disconnect();
//        } catch (IOException ex) {
//            Log.d("lol", "didn't destroy bluetooth");
//        }
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
                startActivity(new Intent(TempMeasure.this, Preferences.class));
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
        if (preferences.getBoolean("preftap", true)) { //&& blueTooth.isConnected()) {
            tempMeasured = 40.1;
            maxTemp(tempMeasured);
            minTemp(tempMeasured);
            tempShow.setText("+" + tempMeasured);
        }
    }
    
    public void onReset(View v) throws FileNotFoundException, IOException {
        tempMax.setText(R.string.text_max);
        tempMin.setText(R.string.text_min);
    }
    
    private double getTemp() {
        analog0 = blueTooth.sensor(0);
        analog1 = blueTooth.sensor(1);
        return ((analog0 - analog1) * 5 * 100) / 1024.0;
    }
    
    private void maxTemp(double temp) {
        tempMaximum = Double.parseDouble(tempMax.getText().toString());
        if (temp > tempMaximum) tempMax.setText("+"+temp);
    }
    
    private void minTemp(double temp) {
        tempMinimum = Double.parseDouble(tempMin.getText().toString());
        if (temp < tempMinimum) tempMin.setText("+"+temp);
    }
    
    //social stuff
    private class ServiceListener implements no.ntnu.osnap.social.listeners.ConnectionListener {

        public void onConnected(String name) {
            facebook = name;
            Log.d("lol", "found " + facebook);
        }
          
    }
    
    private class DumbListener implements no.ntnu.osnap.social.listeners.ResponseListener {

        public void onComplete(Response response) {
            
        }
        
    }
}

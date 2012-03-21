package no.ntnu.osnap.prototypes.temp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ShareActionProvider;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.IOException;
import no.ntnu.osnap.com.BluetoothConnection;
import no.ntnu.osnap.com.ConnectionListener;
import no.ntnu.osnap.temp.R;

public class TempMeasure extends Activity implements ConnectionListener {

    private final String DEF_VALUE = "def";
    private Toast toast;
    private long lastBackPressTime = 0;
    private ShareActionProvider mShareActionProvider;
    SharedPreferences preferences;
    private Intent temperature;
    private BluetoothConnection blueTooth = null;
    private int analog0;
    private int analog1;
    private double tempMeasured;
    private TempFragment tempFrag;
    private StatsFragment statFrag;
    private boolean macScanned = false;
    private String macAddres;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Load preferences        
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
   
        //Setting up the actionBar tabs
        ActionBar actionbar = getActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionbar.setDisplayShowTitleEnabled(false);
        //Tab fragments        
        tempFrag = new TempFragment();
        statFrag = new StatsFragment();
        //Setting the tabs, adding listeners
        ActionBar.Tab ShowTemp = actionbar.newTab().setText("Temp").setTabListener(new TabListener(tempFrag));
        ActionBar.Tab ShowStat = actionbar.newTab().setText("Stat").setTabListener(new TabListener(statFrag));
        //adding the tabs to the actionbar
        actionbar.addTab(ShowTemp);
        actionbar.addTab(ShowStat);

        //keeping selected tab on orientation change
        if (savedInstanceState != null) {
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt("tab"));
        }
        macAddres = getPreferences(MODE_PRIVATE).getString("addres", DEF_VALUE);
        
        if (DEF_VALUE.equals(macAddres)) {
            alertBuilder().show();
        } else macScanned = false;

        //share intent
        temperature = new Intent(Intent.ACTION_SEND);
        temperature.setType("text/plain");
        temperature.putExtra(Intent.EXTRA_TEXT, "");


        //Set up the bluetooth connection
        try {
            if (blueTooth == null && macScanned) {
                blueTooth = new BluetoothConnection(macAddres, this, this);
                blueTooth.connect(null);
                Log.d("lol", "Trying to connect");
            }
        } catch (Exception e) {
            Log.d("lol", "Failed connectoin" + e.getMessage());
        }
    }

    private AlertDialog alertBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Missing Arduino Unit, scan QR now?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

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
                macScanned = true;
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                macScanned = false;
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tempFrag.getTextView().setText(savedInstanceState.getCharSequence("tempValue"));
        Log.d("instance", "load");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("tempValue", tempFrag.getTextView().getText());
        outState.putInt("tab", getActionBar().getSelectedTab().getPosition());
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
        //adding Share Action Provider
        mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.share).getActionProvider();

        mShareActionProvider.setShareHistoryFileName(
                ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);

        mShareActionProvider.setShareIntent(temperature);

        //the intent to be shared
        temperature.putExtra(Intent.EXTRA_SUBJECT, "Current temperature");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(TempMeasure.this, Preferences.class));

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
            tempMeasured = 40;
            temperature.putExtra(Intent.EXTRA_TEXT, "Temperature: " + tempMeasured);
            tempFrag.getTextView().setText(getString(R.string.text_temp)
                    + macAddres);
        }
    }

    public double getTemp() {
        analog0 = blueTooth.sensor(0);
        analog1 = blueTooth.sensor(1);
        return ((analog0 - analog1) * 5 * 100) / 1024.0;
    }

    public void onConnect(BluetoothConnection bc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onConnecting(BluetoothConnection bc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onDisconnect(BluetoothConnection bc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

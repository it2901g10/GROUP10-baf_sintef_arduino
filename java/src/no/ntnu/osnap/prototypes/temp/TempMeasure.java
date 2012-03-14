package no.ntnu.osnap.prototypes.temp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.Toast;
import android.app.Fragment;
import android.util.Log;
import android.view.View;

public class TempMeasure extends Activity
{
	private Toast toast;
	private long lastBackPressTime = 0;
	private ShareActionProvider mShareActionProvider;
	SharedPreferences preferences;
	private Intent temperature;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Load preferences        
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        
        //Setting up the actionBar tabs
        ActionBar actionbar = getActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionbar.setDisplayShowTitleEnabled(false);
        //Tab fragments        
        Fragment tempFrag = new TempFragment();
        Fragment statFrag = new StatsFragment();
        //Setting the tabs, adding listeners
        ActionBar.Tab ShowTemp = actionbar.newTab().setText("Temp")
                .setTabListener(new TabListener(tempFrag));
        ActionBar.Tab ShowStat = actionbar.newTab().setText("Stat")
                .setTabListener(new TabListener(statFrag));
        //adding the tabs to the actionbar
        actionbar.addTab(ShowTemp);
        actionbar.addTab(ShowStat);
        
        //intents
        temperature = new Intent(android.content.Intent.ACTION_SEND);
        temperature.setType("text/plain");
        temperature.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
        String testMes = "Test string";
        temperature.putExtra(android.content.Intent.EXTRA_TEXT, testMes);
        
   }
    
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        //adding Share Action Provider
        mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.share)
                .getActionProvider();
        mShareActionProvider.setShareHistoryFileName(ShareActionProvider
                .DEFAULT_SHARE_HISTORY_FILE_NAME);
        mShareActionProvider.setShareIntent(temperature);
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
    
    public void onClick(View v) {
        if (preferences.getBoolean("preftap", true)) {
            Log.d("lol", "handle pulling from arduino");
        }
     }
}

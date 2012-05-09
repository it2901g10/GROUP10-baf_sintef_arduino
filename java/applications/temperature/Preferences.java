package no.ntnu.osnap.temp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.util.Log;

public class Preferences extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    
    private EditTextPreference macAddress;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        macAddress = (EditTextPreference) findPreference(
                getString(R.string.pref_mac));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String mac = getString(R.string.pref_mac);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        findPreference(mac).setSummary(sp.getString(mac, "Not set"));
        sp.registerOnSharedPreferenceChangeListener(this);
        
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().
                unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        Log.d("sp", "key change: " + key);
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            Preference etp = (EditTextPreference) pref;
            etp.setSummary(sp.getString(key, "Not set"));
        }
    }
    
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == getPreferenceScreen().findPreference(getString(
                R.string.scan_mac))) {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
                String macSet = getString(R.string.mac_set);
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                macAddress.setText(contents);
                findPreference(macSet).getEditor().putBoolean(macSet, true).commit();
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                if (macAddress.equals("")) {
                    macAddress.setText("def");
                    findPreference(macSet).getEditor().putBoolean(macSet, false).commit();
                }
            }
        }
    }
}

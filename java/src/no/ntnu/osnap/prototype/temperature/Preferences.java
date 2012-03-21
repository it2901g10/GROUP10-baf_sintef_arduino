package no.ntnu.osnap.prototype.temperature;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import no.ntnu.osnap.prototype.temperature.R;

public class Preferences extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();
    }

    public class PreferencesFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}

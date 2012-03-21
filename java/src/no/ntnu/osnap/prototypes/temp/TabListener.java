package no.ntnu.osnap.prototypes.temp;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import no.ntnu.osnap.temp.R;

/**
 * Listener for handling tab changes
 * @author B
 */
public class TabListener implements ActionBar.TabListener {

    private Fragment fragment;

    public TabListener(Fragment frag) {
        this.fragment = frag;
    }

    public void onTabSelected(Tab tab, FragmentTransaction frag) {
        frag.replace(R.id.fragment_container, fragment);
    }

    public void onTabUnselected(Tab tab, FragmentTransaction frag) {
        frag.remove(fragment);
    }

    public void onTabReselected(Tab tab, FragmentTransaction fragt) {
        //do nothing
    }
}

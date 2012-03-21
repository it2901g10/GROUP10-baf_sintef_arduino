package no.ntnu.osnap.prototypes.temp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import no.ntnu.osnap.temp.R;

/**
 *
 * @author B
 */
public class StatsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stats_fragment, container, false);
    }
}

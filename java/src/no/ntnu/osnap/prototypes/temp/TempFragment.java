package no.ntnu.osnap.temp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 *
 * @author B
 */
public class TempFragment extends Fragment {
	TextView showTapRefresh;
	@Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, 
    Bundle savedInstanceState) {
       return inflater.inflate(R.layout.temp_fragment, container, false);
   }
}

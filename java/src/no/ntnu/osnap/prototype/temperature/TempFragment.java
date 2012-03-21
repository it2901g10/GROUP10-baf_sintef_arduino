package no.ntnu.osnap.prototype.temperature;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import no.ntnu.osnap.prototype.temperature.R;

/**
 *
 * @author B
 */
public class TempFragment extends Fragment {

    private TextView textView;

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        setRetainInstance(true);
        View v = inflater.inflate(R.layout.temp_fragment, container, false);
        textView = ((TextView) v.findViewById(R.id.temperatureShow));
        return v;
    }
}

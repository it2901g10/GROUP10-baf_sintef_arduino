package no.ntnu.osnap.tshirt.filterMode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import no.ntnu.osnap.tshirt.L;
import no.ntnu.osnap.tshirt.R;

/**
 * End of navigation of filters, the user is prompted to write in what he wants to compare the filter to.
 */
public class FilterCompare extends Activity implements View.OnClickListener {
    
    private String currentFilter;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_compare);

        currentFilter = getIntent().getStringExtra(ChangeMode.CURRENT_FILTER);
        ((TextView)findViewById(R.id.fc_labelCurrentFilter)).setText(currentFilter);
        ((Button)findViewById(R.id.fc_buttonSave)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fc_buttonSave:
                finaliseFilter();
                break;
        }
    }

    private void finaliseFilter() {
        RadioGroup group = (RadioGroup)findViewById(R.id.fc_radioGroupOperator);
        int operatorID = group.getCheckedRadioButtonId();
        if(operatorID < 0){
            Toast.makeText(this, "Please select one radio button", Toast.LENGTH_SHORT).show();
            return;
        }
        EditText editText = (EditText)findViewById(R.id.fc_editComparison);
        if(editText.getText().toString().length() == 0){
            Toast.makeText(this, "Field to compare needs to be longer then 0", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i = new Intent();
        String finalFilter = currentFilter + ":";
        finalFilter += (operatorID == R.id.fc_radioEqual)?"=":"!";
        finalFilter += ":" + editText.getText().toString();
        i.putExtra(ChangeMode.FINAL_FILTER, finalFilter);
        setResult(RESULT_OK, i);
        L.i("Return from FilterCompare with filter " + finalFilter);
        finish();
    }

}

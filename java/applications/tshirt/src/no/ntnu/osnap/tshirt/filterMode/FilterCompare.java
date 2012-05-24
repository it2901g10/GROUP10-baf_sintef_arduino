/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.ntnu.osnap.tshirt.filterMode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import no.ntnu.osnap.tshirt.helperClass.L;
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
        switch(operatorID){
            case R.id.fc_radioEqual: finalFilter+="="; break;
            case R.id.fc_radioNotEqual: finalFilter+="!"; break;
            case R.id.fc_radioContains: finalFilter+="contains"; break;


        }
        finalFilter += ":" + editText.getText().toString();
        i.putExtra(ChangeMode.FINAL_FILTER, finalFilter);
        setResult(RESULT_OK, i);
        L.d("Return from FilterCompare with filter " + finalFilter);
        finish();
    }

    public String getCurrentFilter() {
        return currentFilter;
    }
}

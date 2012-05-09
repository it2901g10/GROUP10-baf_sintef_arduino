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
package no.ntnu.osnap.tshirt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import no.ntnu.osnap.tshirt.filterMode.ChangeMode;
import no.ntnu.osnap.tshirt.filterMode.FilterMessage;

/**
 * Created by IntelliJ IDEA.
 * User: goldsack
 * Date: 19.04.12
 * Time: 22:31
 * To change this template use File | Settings | File Templates.
 */
public class ActivityOutput extends Activity implements View.OnClickListener{
    /** Called when the activity is first created. */

    public final static String FILTER = "filter";
    public final static String OUTPUT = "output";

    private String currentFilter;
   
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.output_select_window);
        
        setOnClickListener();


    }

    private void setOnClickListener() {
        Button saveOutputButton = (Button) findViewById(R.id.os_buttonSaveOutput);
        saveOutputButton.setOnClickListener(this);

        Button saveOutputFilter = (Button) findViewById(R.id.os_buttonSetFilter);
        saveOutputFilter.setOnClickListener(this);
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);    
        if(resultCode == RESULT_OK){
            currentFilter = data.getStringExtra(ChangeMode.FINAL_FILTER);
            TextView tv = (TextView)findViewById(R.id.os_labelCurrentFilter);
            tv.setText(currentFilter);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.os_buttonSetFilter:
                L.i("Send intent to start FilterSelection");
                Intent i = new Intent(this, FilterMessage.class);
                i.putExtra(ChangeMode.CURRENT_FILTER, getString(R.string.getLatestPost));
                startActivityForResult(i, 0);
                break;
           
            case R.id.os_buttonSaveOutput:
                returnOutput();
                break;
            
        }

    }

    /** Return set information to parent activity**/
    private void returnOutput(){
        RadioGroup groupOutput = (RadioGroup) findViewById(R.id.os_radioGroupOutput);
        int go = groupOutput.getCheckedRadioButtonId();
        if(go < 0){
            Toast.makeText(ActivityOutput.this, "Please select one radio button in each group", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent();
        RadioButton rButtonOutput = (RadioButton)findViewById(go);
        i.putExtra(FILTER, currentFilter);
        i.putExtra(OUTPUT, rButtonOutput.getText());
        setResult(RESULT_OK, i);
        L.i("Returned from ActivityOutput with " + currentFilter + " and " + rButtonOutput.getText());
        finish();
    }
}
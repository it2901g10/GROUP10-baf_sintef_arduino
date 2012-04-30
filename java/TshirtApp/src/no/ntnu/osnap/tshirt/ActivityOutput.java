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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: goldsack
 * Date: 19.04.12
 * Time: 22:31
 * To change this template use File | Settings | File Templates.
 */
public class ActivityOutput extends Activity {
    /** Called when the activity is first created. */

    public final static String FILTER = "filter";
    public final static String OUTPUT = "output";    
   
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.output_select_window);
        
        setOnClickListener();


    }

    private void setOnClickListener() {
        Button saveOutPutButton = (Button) findViewById(R.id.os_buttonSaveOutput);
        saveOutPutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioGroup groupSignal = (RadioGroup) findViewById(R.id.os_radioGroupFilter);
                int gf = groupSignal.getCheckedRadioButtonId();

                RadioGroup groupOutput = (RadioGroup) findViewById(R.id.os_radioGroupOutput);
                int go = groupOutput.getCheckedRadioButtonId();

                if(gf < 0 || go < 0){
                    Toast.makeText(ActivityOutput.this, "Please select one radio button in each group", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i = new Intent();
                
                RadioButton rButtonFilter = (RadioButton)findViewById(gf);
                RadioButton rButtonOutput = (RadioButton)findViewById(go);
                rButtonFilter.getText();
                
                i.putExtra(FILTER, rButtonFilter.getText());
                i.putExtra(OUTPUT, rButtonOutput.getText());
                setResult(RESULT_OK, i);
                L.i("Returned from ActivityOutput with " + rButtonFilter.getText() + " and " + rButtonOutput.getText());
                finish();

            }
        });

        
        
        
    }

}
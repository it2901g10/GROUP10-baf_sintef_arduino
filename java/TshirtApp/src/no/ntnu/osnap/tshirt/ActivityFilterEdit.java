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

/**
 * Created by IntelliJ IDEA.
 * User: goldsack
 * Date: 19.04.12
 * Time: 22:30
 * To change this template use File | Settings | File Templates.
 */
public class ActivityFilterEdit extends Activity{

    public static final String FILTER = "filter";
    public static final String COMPARE_STRING = "compare";
    public static final String OPERATOR = "operator";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_edit_window);

        Button saveFilter = (Button)findViewById(R.id.fe_buttonSaveFilter);
        saveFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                RadioGroup groupFilter= (RadioGroup) findViewById(R.id.fe_radioGroupFilter);
                int filterID = groupFilter.getCheckedRadioButtonId();

                RadioGroup groupOperator = (RadioGroup) findViewById(R.id.fe_radioGroupOperator);
                int operatorID = groupOperator.getCheckedRadioButtonId();

                if(filterID < 0 || operatorID < 0){
                    Toast.makeText(ActivityFilterEdit.this, "Please select one radio button in each group", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i = new Intent();

                RadioButton rButtonFilter = (RadioButton)findViewById(filterID);
                RadioButton rButtonOperator = (RadioButton)findViewById(operatorID);
                EditText editText = (EditText)findViewById(R.id.fe_editCompareString);

                i.putExtra(FILTER, rButtonFilter.getText());
                i.putExtra(COMPARE_STRING, editText.getText().toString());
                i.putExtra(OPERATOR, operatorID == R.id.fe_radioEqual ? "==": "!=" );
                setResult(RESULT_OK, i);
                L.i("Returned from ActivityFilter with " + rButtonFilter.getText() + ", " + rButtonOperator.getText() + " and " + editText.getText());
                finish();

            }
        });
    }
    

}

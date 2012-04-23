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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.R;

import java.util.ArrayList;


public class ActivityRulesEdit extends ListActivity{
    private static final int ACTIVITY_OUTPUT = 1;
    private static final int ACTIVITY_FILTER = 2;

    private String outputFilter = null;
    private String outputDevice = null;
    private int ruleID = -1;

    public static final String RULE = "rule";

    ArrayList<Filter> list = new ArrayList<Filter>();
    ArrayAdapter<String> listAdapter;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules_edit_window);
        setOnClickListener();
    }

    private void setOnClickListener() {
        Button setOutput = (Button)findViewById(R.id.re_buttonSetOutput);
        setOutput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                L.i("Send intent to start ActivityOutput");
                Intent i = new Intent(ActivityRulesEdit.this, ActivityOutput.class);
                startActivityForResult(i, ACTIVITY_OUTPUT);
            }
        });
        
        Button addFilter = (Button)findViewById((R.id.re_buttonAddFilter));
        addFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                L.i("Send intent to start ActivityOutput");
                Intent i = new Intent(ActivityRulesEdit.this, ActivityFilterEdit.class);
                startActivityForResult(i, ACTIVITY_FILTER);
            }
        });


        Button saveRule = (Button)findViewById(R.id.re_buttonSaveRule);
        saveRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                
                if(outputDevice == null || outputFilter == null){
                    Toast.makeText(ActivityRulesEdit.this, "Output has not been set", Toast.LENGTH_SHORT).show();
                    return;    
                }
                
                if(list.size() == 0){
                    Toast.makeText(ActivityRulesEdit.this, "No filters added", Toast.LENGTH_SHORT).show();
                    return;    
                }

                EditText editText = (EditText)findViewById(R.id.re_editRuleName);
                String ruleName = editText.getText().toString().trim();
                
                if(ruleName.length() == 0){
                    Toast.makeText(ActivityRulesEdit.this, "Please add rulename", Toast.LENGTH_SHORT).show();
                    return;
                }

                Filter[] filterArray = new Filter[list.size()];

                for (int i = 0; i < filterArray.length; i++) {
                    filterArray[i] = list.get(i);
                }

                Intent i = new Intent();
                i.putExtra(RULE, new Rule(ruleName, outputFilter, outputDevice, filterArray, ruleID));
                setResult(RESULT_OK, i);
                L.i("Returned from ActivtyRulesEdit with " + outputFilter + " to " + outputDevice + " and with " + list.size() + " filter(s)");
                finish();
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK){
            L.i("ActivityFilterEdit was returned " + resultCode + " and not the int for RESULT_OK");
            return;
        }

        if(requestCode == ACTIVITY_OUTPUT){
            setOutput(data.getStringExtra(ActivityOutput.FILTER), data.getStringExtra(ActivityOutput.OUTPUT));
        }
        if(requestCode == ACTIVITY_FILTER){
            addFilter(data.getStringExtra(ActivityFilterEdit.FILTER), data.getStringExtra(ActivityFilterEdit.COMPARE_STRING), data.getStringExtra(ActivityFilterEdit.OPERATOR));
        }

    }

    private void addFilter(String filter, String compare, String operator) {
        list.add(new Filter(filter, compare, operator));
        
        String[] array = new String[list.size()];

        for (int i = 0; i < array.length; i++) {
            Filter f = list.get(i);
            array[i] = f.filter + " is " + f.operator + " to " +f.compare;
        }

        listAdapter = new ArrayAdapter<String>(this, R.layout.small_list_row,array);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ActivityRulesEdit.this.setListAdapter(listAdapter);
            }
        });

    }

    private void setOutput(String filter, String output) {
        TextView tv = (TextView)findViewById(R.id.re_labelSetOutput);
        tv.setText(filter + " will be sent to " + output);

        outputFilter = filter;
        outputDevice = output;
        
    }

}
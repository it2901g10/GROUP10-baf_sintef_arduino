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
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.example.R;

/**
 * Created by IntelliJ IDEA.
 * User: goldsack
 * Date: 19.04.12
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
public class ActivityRulesList extends ListActivity{
    /** Called when the activity is first created. */

    private TshirtSingleton singleton;
    public static final int ACTIVITY_NEW_RULE= 1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules_list_window);
        setOnClickListeners();

        singleton = TshirtSingleton.getInstance(this);

        singleton.database.open();
        //singleton.database.addNewRule(new Rule("name", "tew", "Ts", null, 3));
        layoutRulesList();
    }

    private void layoutRulesList(){
        setListAdapter(new RuleListAdapter(singleton.database.getRules(), this));
    }

    private void setOnClickListeners() {
        Button addRule = (Button)findViewById(R.id.rl_buttonAddRule);

        addRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                L.i("Send intent to start ActivityOutput");
                Intent i = new Intent(ActivityRulesList.this, ActivityRulesEdit.class);
                startActivityForResult(i, ACTIVITY_NEW_RULE);


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(resultCode != RESULT_OK){
            L.i("ActivityRulesList was returned " + resultCode + " and not the int for RESULT_OK");
            return;    
        }
        if(requestCode == ACTIVITY_NEW_RULE){
            Rule rule = (Rule)data.getParcelableExtra(ActivityRulesEdit.RULE);
            singleton.database.addNewRule(rule);
            layoutRulesList();
            L.i("Received new rule: " + rule);
        }

    }
}

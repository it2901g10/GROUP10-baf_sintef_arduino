/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.osnap.app.tshirt;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

/**
 *  Example app to be used to connect to tshirt and social intents
 */

public class TshirtApp extends ListActivity {

    public static final int ACTIVITY_CREATE = 0;
    public static final int ACTIVITY_EDIT = 1;
    private RulesDbAdapter mDbHelper;
    private Cursor rulesCursor;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules_list);
        mDbHelper = new RulesDbAdapter(this);
        mDbHelper.open();

        Button button = (Button) findViewById(R.id.newRule);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRule();
            }
        });
        
        fillData();
    }


    /**
     * Fetch rules from database and fills the list
     */
    private void fillData() {

        //Get rules
        rulesCursor = mDbHelper.fetchAllRules();

        //Select rules table column names
        String[] from = new String[]{ RulesDbAdapter.KEY_ROWID, RulesDbAdapter.KEY_RULE, RulesDbAdapter.KEY_OUTPUT};
        //to go into TextView as definend in rules_row.xml
        int[] to = new int[] {R.id.rowIDrule, R.id.ruleName, R.id.outputName};

        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.rules_row, rulesCursor, from, to);
        setListAdapter(notes);
    }



    //Starts activity RuleEdit to create new rule
    private void createRule(){
        Intent i = new Intent(this, RuleEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    //Update rule when its clicked
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //Find the row and rowID user clicked on
        Cursor c = rulesCursor;
        c.moveToPosition(position);
        long rowID = c.getLong(c.getColumnIndexOrThrow(RulesDbAdapter.KEY_ROWID));

        //Prepear intent to send to RuleEdit activity
        Intent i = new Intent(this, RuleEdit.class);
        i.putExtra(RulesDbAdapter.KEY_ROWID, rowID );
        i.putExtra(RulesDbAdapter.KEY_RULE, c.getString(c.getColumnIndexOrThrow(RulesDbAdapter.KEY_RULE)));
        i.putExtra(RulesDbAdapter.KEY_OUTPUT, c.getString(c.getColumnIndexOrThrow(RulesDbAdapter.KEY_OUTPUT)));

        //Read all filters connected to the rule and add them to the intent
        Cursor filters = mDbHelper.fetchAllFiltersFromRule(rowID);
        int cIndexKeyFilter = filters.getColumnIndexOrThrow(RulesDbAdapter.KEY_FILTER);
        ArrayList<String> filterList = new ArrayList<String>();
        if(filters.moveToFirst()){
            filterList.add(filters.getString(cIndexKeyFilter));
            while (filters.moveToNext()){
                filterList.add(filters.getString(cIndexKeyFilter));
            }
        }
        String filterArray[] = filterList.toArray(new String[filterList.size()]);
        i.putExtra(RulesDbAdapter.KEY_FILTER, filterArray);


        startActivityForResult(i, ACTIVITY_EDIT);
    }

    //When new rule is created or edited, it returns here to update database
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK){
            return;
        }

        Bundle extras = data.getExtras();

        switch (requestCode){
            case ACTIVITY_CREATE:
                //Creates a new rule in database
                String rule = extras.getString(RulesDbAdapter.KEY_RULE);
                String output = extras.getString(RulesDbAdapter.KEY_OUTPUT);
                String[] filters = extras.getStringArray(RulesDbAdapter.KEY_FILTER);
                mDbHelper.createRule(rule, output, filters);
                break;

            case ACTIVITY_EDIT:
                //Updates an existing rule in database
                Long row = extras.getLong(RulesDbAdapter.KEY_ROWID);
                if(row != null){
                    rule = extras.getString(RulesDbAdapter.KEY_RULE);
                    output = extras.getString(RulesDbAdapter.KEY_OUTPUT);
                    filters = extras.getStringArray(RulesDbAdapter.KEY_FILTER);
                    mDbHelper.updateRule(row.longValue(), rule, output, filters);

                }
                break;
        }
        fillData();

    }
    

}

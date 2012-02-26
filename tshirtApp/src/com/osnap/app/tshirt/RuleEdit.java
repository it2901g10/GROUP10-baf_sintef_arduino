package com.osnap.app.tshirt;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Fields to update/create rule
 */
public class RuleEdit extends ListActivity{

    private int ACTIVITY_NEWFILTER = 0;

    private EditText noteNameText;
    private EditText noteOutputText;

    private Button saveRule;
    private Button newFilter;

    private Long mRowId;
    ArrayList<String> filterList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules_edit);
        setTitle(R.string.note_edit);

        initComp();
        fetchDataFromIntent();
        fillData();
        addListeners();



    }

    /**
     * If RuleEdit receives a rule on Activity start, fill out relevant fields
     */
    private void fetchDataFromIntent() {
        mRowId = null;
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            String name = extras.getString(RulesDbAdapter.KEY_RULE);
            String output = extras.getString(RulesDbAdapter.KEY_OUTPUT);
            String[] filterList = extras.getStringArray(RulesDbAdapter.KEY_FILTER);
            mRowId = extras.getLong(RulesDbAdapter.KEY_ROWID);

            if(name != null){
                noteNameText.setText(name);

            }
            if(output != null){
                noteOutputText.setText(output);
            }

            if (filterList != null) {
                for (int i = 0; i < filterList.length; i++) {
                    this.filterList.add(filterList[i]);

                }
            }


        }
    }

    /**
     * Grab fields and buttons from rules_edit.xml
     */
    private void initComp() {
        filterList = new ArrayList<String>();
        noteNameText = (EditText) findViewById(R.id.ruleNameBox);
        noteOutputText = (EditText) findViewById(R.id.ruleOutputBox);

        saveRule = (Button) findViewById(R.id.buttonSaveRule);
        newFilter = (Button) findViewById(R.id.buttonNewFilter);
    }

    /**
     * Add listeners to buttons
     */
    private void addListeners() {
        saveRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();

                bundle.putString(RulesDbAdapter.KEY_RULE, noteNameText.getText().toString());
                bundle.putString(RulesDbAdapter.KEY_OUTPUT, noteOutputText.getText().toString());

                if (mRowId != null) {
                    bundle.putLong(RulesDbAdapter.KEY_ROWID, mRowId);
                }
                bundle.putStringArray(RulesDbAdapter.KEY_FILTER, filterList.toArray(new String[filterList.size()]));


                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();


            }
        });
        newFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RuleEdit.this, FilterAdder.class);
                startActivityForResult(i, ACTIVITY_NEWFILTER);
            }
        });
    }

    /**
     * Simply fill the ListActivity list with the given ArrayList &lt;String&gt; array
     */
    private void fillData() {

        setListAdapter(new ArrayAdapter(this, R.layout.filters_row,R.id.filter_row, filterList.toArray(new String[filterList.size()])));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }
        if(requestCode == ACTIVITY_NEWFILTER) {
            filterList.add(data.getExtras().getString("filter"));
            fillData();
        }
    }
}

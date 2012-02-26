package com.osnap.app.tshirt;

/**
 * Created by IntelliJ IDEA.
 * User: goldsack
 * Date: 24.02.12
 * Time: 07:46
 * To change this template use File | Settings | File Templates.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class FilterAdder extends Activity {

    LinearLayout layout;
    TextView filter;
    TextView options;
    Button save;

    Button person;
    Button personName;
    Button personAge;

    Button group;
    Button groupName;

    Button message;
    Button messageSender;
    Button messageText;

    Button equal;
    Button different;
    Button biggerThen;
    Button smallerThen;

    EditText editFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComp();
        addListeners();

        setStartLayout();


    }

    private void addListeners() {

        person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter.append("Person:");
                setPersonLayout();
            }
        });

        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter.append("Group:");
                setGroupLayout();

            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter.append("Message:");
                setMessageLayout();
            }
        });


        personAge.setOnClickListener(new CompareClick("personAge:"));
        personName.setOnClickListener(new CompareClick("personName:"));
        messageText.setOnClickListener(new CompareClick("messageText:"));
        groupName.setOnClickListener(new CompareClick("groupName:"));
        messageSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter.append("messageSender:");
                setPersonLayout();

            }
        });

        biggerThen.setOnClickListener(new FinalFilterClick(">:"));
        smallerThen.setOnClickListener(new FinalFilterClick("<:"));
        equal.setOnClickListener(new FinalFilterClick("==:"));
        different.setOnClickListener(new FinalFilterClick("!=:"));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter.append(editFilter.getText());
                Intent intent = new Intent();
                intent.putExtra("filter", filter.getText().toString());
                setResult(RESULT_OK, intent);
                finish();

            }
        });

    }

    /**
     * Set layout to compareLayout (equal, notequal, biggerThen, smallerThen)
     */
    class CompareClick implements View.OnClickListener {
        String tag;

        CompareClick(String tag) {
            this.tag = tag;
        }

        @Override
        public void onClick(View view) {
            filter.append(tag);
            setCompareLayout();
        }
    }

    /**
     * set layout to an EditText to write in final comparison
     */
    class FinalFilterClick implements View.OnClickListener {
        String tag;

        FinalFilterClick(String tag) {
            this.tag = tag;
        }

        @Override
        public void onClick(View view) {
            filter.append(tag);
            setFinalFilter();
        }
    }


    private void setStartLayout() {

        layout.addView(filter);
        layout.addView(options);
        layout.addView(person);
        layout.addView(group);
        layout.addView(message);

        setContentView(layout);
    }

    private void setMessageLayout() {
        layout.removeAllViews();
        layout.addView(filter);
        layout.addView(options);

        layout.addView(messageSender);
        layout.addView(messageText);
    }

    private void setGroupLayout() {
        layout.removeAllViews();
        layout.addView(filter);
        layout.addView(options);

        layout.addView(groupName);

    }

    private void setPersonLayout() {
        layout.removeAllViews();
        layout.addView(filter);
        layout.addView(options);

        layout.addView(personAge);
        layout.addView(personName);


    }

    private void setFinalFilter() {
        layout.removeAllViews();
        layout.addView(filter);
        layout.addView(options);
        options.setText("Press save after writing filter to compare");
        layout.addView(editFilter);
        layout.addView(save);
    }

    private void setCompareLayout() {
        layout.removeAllViews();
        layout.addView(filter);
        layout.addView(options);

        layout.addView(equal);
        layout.addView(different);
        layout.addView(biggerThen);
        layout.addView(smallerThen);

    }


    private void initComp() {
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, 1));

        filter = new TextView(this);
        filter.setText("");

        save = new Button(this);
        save.setText("Save");

        options = new TextView(this);
        options.setText("Options");

        person = new Button(this);
        person.setText("Person");

        personName = new Button(this);
        personName.setText("PersonName");

        personAge = new Button(this);
        personAge.setText("PersonAge");

        group = new Button(this);
        group.setText("Group");

        groupName = new Button(this);
        groupName.setText("GroupName");


        message = new Button(this);
        message.setText("Message");

        messageSender = new Button(this);
        messageSender.setText("MessageSender");

        messageText = new Button(this);
        messageText.setText("MessageText");

        equal = new Button(this);
        equal.setText("Equals");

        different = new Button(this);
        different.setText("NotEqual");

        biggerThen = new Button(this);
        biggerThen.setText("BiggerThen");

        smallerThen = new Button(this);
        smallerThen.setText("SmallerThen");

        editFilter = new EditText(this);


    }


}

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
import no.ntnu.osnap.social.Prototype;
import no.ntnu.osnap.social.listeners.ConnectionListener;
import no.ntnu.osnap.tshirt.helperClass.L;
import no.ntnu.osnap.tshirt.helperClass.TshirtSingleton;

import java.util.ArrayList;

public class ActivityStartWindow extends Activity implements View.OnClickListener{

    TshirtSingleton singleton;
    ArrayList<String> socialServiceList;
    Prototype prototype;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_window);
        singleton = TshirtSingleton.getInstance(this);
        socialServiceList = new ArrayList<String>();
        initComp();
    }

    private void initComp() {
        setOnClickListeners();
        ToggleButton toggleButton = (ToggleButton)findViewById(R.id.sw_toggleButtonActiveService);
        toggleButton.setChecked(singleton.serviceActivated);
        
        if(singleton.getServiceName() != null){
            updateSelectedServiceView(singleton.getServiceName());
        }
    }

    private void setOnClickListeners() {
        Button setRulesButton = (Button)findViewById(R.id.sw_buttonSetRules);
        setRulesButton.setOnClickListener(this);
        
        Button arduinoConnectionButton = (Button)findViewById(R.id.sw_buttonConnection);
        arduinoConnectionButton.setOnClickListener(this);

        Button searchSSButton = (Button)findViewById(R.id.sw_buttonSearchSocialServices);
        searchSSButton.setOnClickListener(this);

        ToggleButton toggleButton = (ToggleButton)findViewById(R.id.sw_toggleButtonActiveService);
        toggleButton.setOnClickListener(this);
    }
    /** Displays radioButtons for found social services */
    private void updateServiceListView() {
        final TextView selectedService = (TextView)findViewById(R.id.sw_labelServiceSelect);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(socialServiceList.size() == 0){
                    selectedService.setText("No service found");
                    return;
                }
                if(singleton.getServiceName() != null){
                    updateSelectedServiceView(singleton.getServiceName());
                }
                else{
                    selectedService.setText("No selected Service");
                }

                RadioGroup group = (RadioGroup)findViewById(R.id.sw_radioGroupService);
                group.clearCheck();
                group.removeAllViews();
                for (int i = 0; i < socialServiceList.size(); i++) {
                    RadioButton button = new RadioButton(ActivityStartWindow.this);
                    button.setText(socialServiceList.get(i));
                    button.setOnClickListener(getRadioOnClickListener(socialServiceList.get(i)));
                    LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.WRAP_CONTENT,
                            RadioGroup.LayoutParams.WRAP_CONTENT);
                    group.addView(button, i, layoutParams);
                }
            }
        });
    }

    /** Updates TextView */
    private void updateSelectedServiceView(final String serviceName){
        ActivityStartWindow.this.runOnUiThread(new Runnable() {
            public void run() {
                TextView selectedService = (TextView)findViewById(R.id.sw_labelServiceSelect);
                selectedService.setText("App will get data from " + serviceName);
            }
        });
    };

    /** Listener for RadioButtons */
    private View.OnClickListener getRadioOnClickListener(final String serviceName) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleton.setServiceName(serviceName);
                updateSelectedServiceView(serviceName);

            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.sw_buttonSetRules:
                Intent i = new Intent(ActivityStartWindow.this, ActivityRulesList.class);
                startActivity(i);
                break;
            case R.id.sw_buttonConnection:
                singleton.setServiceName(socialServiceList.get(0));
                singleton.initBTConnection(this);
                break;
            case R.id.sw_toggleButtonActiveService:

                if(singleton.serviceActivated){
                    stopService(new Intent(ActivityStartWindow.this, ServiceDataFetcher.class));
                    L.i("Disabled Service");
                    singleton.serviceActivated = false;
                }
                else{
                    startService(new Intent(ActivityStartWindow.this, ServiceDataFetcher.class));
                    L.i("Activated Service");
                    singleton.serviceActivated = true;
                }

                break;

            case R.id.sw_buttonSearchSocialServices:
                socialServiceList.clear();
                updateServiceListView();
                ConnectionListener listener = new ConnectionListener() {
                    @Override
                    public void onConnected(String name) {
                        L.i("Activity Start window got " + name);
                        socialServiceList.add(name);
                        updateServiceListView();
                    }
                };
                prototype = new Prototype(ActivityStartWindow.this, listener);
                prototype.discoverServices();
                break;
        }
    }

    private void quickToastMessage(final String message) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ActivityStartWindow.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

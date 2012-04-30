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
import android.widget.TextView;
import no.ntnu.osnap.social.Prototype;
import no.ntnu.osnap.social.listeners.ConnectionListener;

import java.util.ArrayList;

public class ActivityStartWindow extends Activity
{

    TshirtSingleton singleton;
    ArrayList<String> socialServiceList;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_window);
        singleton = TshirtSingleton.getInstance(this);
        setOnClickListeners();
        socialServiceList = new ArrayList<String>();
        startService(new Intent(ActivityStartWindow.this, ServiceDataFetcher.class));
    }

    private void setOnClickListeners() {
        Button setRulesButton = (Button)findViewById(R.id.sw_buttonSetRules);
        setRulesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityStartWindow.this, ActivityRulesList.class);
                startActivity(i);
            }
        });
        
        Button arduinoConnectionButton = (Button)findViewById(R.id.sw_buttonConnection);
        arduinoConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleton.toggleArduinoConnection();
            }
        });

        Button searchSSButton = (Button)findViewById(R.id.sw_buttonSearchSocialServices);
        searchSSButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
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
                Prototype prototype = new Prototype(ActivityStartWindow.this, listener);
                prototype.discoverServices();
            }

        });

    }

    private void updateServiceListView() {
        final TextView view = (TextView)findViewById(R.id.sw_labelFoundServicesList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(socialServiceList.size() == 0){
                    view.setText("No service found");
                    return;
                }

                String services = "";
                for (int i = 0; i < socialServiceList.size(); i++) {
                    services += socialServiceList.get(i) + ((i < socialServiceList.size()-1)?"\n":"");

                }
                view.setText(services);

            }
        });
    }

}

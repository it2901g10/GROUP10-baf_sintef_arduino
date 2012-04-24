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
import com.example.R;

public class ActivityStartWindow extends Activity
{

    TshirtSingleton singleton;
    SocialFinderHandler socialFinder;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_window);
        singleton = TshirtSingleton.getInstance(this);
        setOnClickListeners();
        socialFinder = new SocialFinderHandler(this, (TextView)findViewById(R.id.sw_labelFoundServicesList));
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
                socialFinder.searchSocialServices();
            }
        });
        
    }

}

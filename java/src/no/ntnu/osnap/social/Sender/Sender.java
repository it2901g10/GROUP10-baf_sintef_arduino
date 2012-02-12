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

package no.ntnu.osnap.social.Sender;

import no.ntnu.osnap.social.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opensocial.models.Model;
import org.opensocial.models.Person;

/**
 * @author Emanuele 'Lemrey' Di Santo
 */
public class Sender extends Activity
{
	private final String TAG = "SOCIALS";
	private final String TEXT_MESSAGE = "osnap.social.intent.action.TEXT_MESSAGE";
	
	private Button mSendButton;	
    
	/** Called when the activity is first created. */	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		mSendButton = (Button) findViewById(R.id.send);
		
		mSendButton.setOnClickListener(
			new View.OnClickListener() {
				public void onClick(View v) {					
					Util util = new Util();
					Intent i = new Intent(TEXT_MESSAGE);
					
					try {						
						Person p = new Person();
						p.setField("givenName", "Emanuele");
						p.setField("currentLocation", "Trondheim");
						p.setField("age", Integer.valueOf(24));
						
						util.bundleModel(i, p);
						startActivity(i);
					} catch (Exception e) {
						Log.d(TAG, e.toString());
					}
				}
			}
		);
	}	
}

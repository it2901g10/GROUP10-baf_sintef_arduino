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

package no.ntnu.osnap.social.tshirt;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.ComponentName;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;


public class TshirtActivity extends Activity implements View.OnClickListener {

	private final String TAG = "Tshirt-Activity";
	private Button mButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//there's a handy button but it is invisible now
		mButton = (Button) findViewById(R.id.start);
		mButton.setVisibility(View.INVISIBLE);
		mButton.setOnClickListener(this);	

		/* create an intent and bundle the classname of the Tshirt service, the
		 * one that will receive the reply from the broadcast receiver via
		 * onStartCommand()
		 */
		Intent intent = new Intent("android.intent.action.SOCIAL");
		ComponentName c = new ComponentName(this, TshirtService.class);

		//the key for the classname is 'reply'
		intent.putExtra("reply", c);
		Log.d(TAG, "Sending broadcast.");

		sendBroadcast(intent);
	}

	//here we handle clicks
	public void onClick(View v) {
		Log.d(TAG, "onClick()");
		switch (v.getId()) {
			case (R.id.start): {
			}
		}
	}

	/* maybe we'd like to have a function that updates the list of
	 * social services installed as their names are returned
	 * to the tshirt service during 'handshake'.
	 * atm it's not possible to call this method from the tshirt service.
	 */
	public void addToList(String name) {

		ListView listView = (ListView) findViewById(R.id.servicelist);
		String[] values = new String[] {name};

		// First paramenter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the View to which the data is written
		// Forth - the Array of data
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);

		// Assign adapter to ListView
		listView.setAdapter(adapter);
	}
}

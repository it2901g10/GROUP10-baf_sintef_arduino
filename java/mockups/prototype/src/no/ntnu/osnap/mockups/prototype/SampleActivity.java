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
package no.ntnu.osnap.mockups.prototype;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import no.ntnu.osnap.social.*;
import no.ntnu.osnap.social.listeners.*;
import no.ntnu.osnap.social.models.*;


/**
 * A mockup application to test the Prototype class.
 * @author Emanuele 'Lemrey' Di Santo
 */
public class SampleActivity extends Activity implements View.OnClickListener {

	private final String TAG = "Sample-Activity";
	private TextView mText;
	private Button mButtonRead;
	private Button mButtonDiscovery;
	private Prototype mPrototype;
	private ArrayList<String> mServices;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mServices = new ArrayList<String>();

		mText = (TextView) findViewById(R.id.txt);

		mButtonRead = (Button) findViewById(R.id.read);
		mButtonDiscovery = (Button) findViewById(R.id.discovery);

		mButtonRead.setOnClickListener(this);
		mButtonDiscovery.setOnClickListener(this);

		// Init the prototype
		mPrototype = new Prototype(this, "SamplePrototype");

	}

	private int counter;
	// Handle clicks on activity's components
	public void onClick(View button) {

		switch (button.getId()) {

			case R.id.read: {
				Request r = new Request(Request.RequestCode.SELF);
				mPrototype.sendRequest("Facebook", r, new SelfListener());
				
				Bundle param = new Bundle();
				param.putString("message", "hi there! this test " + counter++);
				r = new Request(Request.RequestCode.POST_MESSAGE, null, param);
				
				mPrototype.sendRequest("Facebook", r, null);
				
				//r = new Request(Request.RequestCode.MESSAGE_STREAM);
				//mPrototype.sendRequest("Facebook", r, new MessageListener());
			} break;

			case R.id.discovery: {
				// Send a discovery message
				LinearLayout linearLayout =
							(LinearLayout)findViewById(R.id.services);
				linearLayout.removeAllViews();
				mPrototype.discoverServices(new ServiceListener());
			} break;
		}
	}

	// The discovery callback
	private class ServiceListener implements ConnectionListener {

		// the service replies with its name
		public void onConnected(final String name) {
			
			// add the service to our service list
			mServices.add(name);
			
			SampleActivity.this.runOnUiThread(new Runnable() {

				public void run() {
					LinearLayout linearLayout =
							(LinearLayout)findViewById(R.id.services);
					
					TextView serviceFound = new TextView(SampleActivity.this);
					serviceFound.setText(name);
					
					linearLayout.addView(serviceFound);
					mButtonRead.setVisibility(View.VISIBLE);
				}
			});
		}

		// no service replied within 3 seconds
		public void onConnectionFailed() {
			SampleActivity.this.runOnUiThread(new Runnable() {

				public void run() {
					LinearLayout linearLayout =
							(LinearLayout)findViewById(R.id.services);
					
					TextView serviceFound = new TextView(SampleActivity.this);
					serviceFound.setText("No service found");
					
					linearLayout.addView(serviceFound);
					mButtonRead.setVisibility(View.INVISIBLE);
				}
			});
		}
	}

	// Response listeners
	
	private class SelfListener implements ResponseListener {

		public void onComplete(Response response) {
			if (response.getStatus() == Response.Status.COMPLETED) {
				final Person self = (Person) response.getModel();
				SampleActivity.this.runOnUiThread(new Runnable() {

					public void run() {
						mText.setText("Hi " + self.getName());
					}
				});
			}
		}
	}

	private class MessageListener implements ResponseListener {

		public void onComplete(Response response) {
			if (response.getStatus() == Response.Status.COMPLETED) {
				ArrayList<Message> list = response.getModelArrayList();
				for (int i = 0; i < list.size(); i++) {
					Log.d(TAG, list.get(i).getText());
				}
			}
		}
	}

	private class FriendsListener implements ResponseListener {

		public void onComplete(Response response) {
			if (response.getStatus() == Response.Status.COMPLETED) {
				ArrayList<Person> list = response.getModelArrayList();
				for (int i = 0; i < list.size(); i++) {
					Log.d(TAG, list.get(i).getName());
				}
			}
		}
	}
}

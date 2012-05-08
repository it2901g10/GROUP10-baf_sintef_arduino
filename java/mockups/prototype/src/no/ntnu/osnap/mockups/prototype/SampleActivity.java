package no.ntnu.osnap.mockups.prototype;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import no.ntnu.osnap.social.*;
import no.ntnu.osnap.social.listeners.*;
import no.ntnu.osnap.social.models.*;

public class SampleActivity extends Activity implements View.OnClickListener {

	private final String TAG = "Sample-Activity";
	private TextView mText;
	private Button mButton;
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
		mButton = (Button) findViewById(R.id.read);
		mButton.setOnClickListener(this);

		// Init the prototype
		mPrototype = new Prototype(this, new ServiceListener());
		mPrototype.setPrototypeName("SamplePrototype");

		// Send a discovery message
		mPrototype.discoverServices();
	}

	// Handle clicks on activity's components
	public void onClick(View arg0) {

		Request r = new Request(Request.RequestCode.FRIENDS);
		mPrototype.sendRequest("Facebook", r, new FriendsListener());
		r = new Request(Request.RequestCode.MESSAGE_STREAM);
		mPrototype.sendRequest("Facebook", r, new MessageListener());
		r = new Request(Request.RequestCode.MESSAGES);
		mPrototype.sendRequest("Facebook", r, new MessageListener());
	}

	// Here we handle a discovery reply
	// The service replies with it's name
	private class ServiceListener implements ConnectionListener {

		public void onConnected(final String name) {
			mServices.add(name);
			SampleActivity.this.runOnUiThread(new Runnable() {

				public void run() {
					mText.setText("Found: " + name);
					mButton.setVisibility(View.VISIBLE);
				}
			});
		}
	}

	// Here we listen for the response
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

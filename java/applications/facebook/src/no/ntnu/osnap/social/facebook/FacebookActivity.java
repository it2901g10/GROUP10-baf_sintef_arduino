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

package no.ntnu.osnap.social.facebook;

import android.app.Activity;
import android.util.Log;
import android.os.Bundle;
import android.content.Intent;

import android.widget.TextView;

import no.ntnu.osnap.social.facebook.SessionEvents.AuthListener;
import no.ntnu.osnap.social.facebook.SessionEvents.LogoutListener;

/**
 * Handles Facebook authentication and starts/stops the Facebook
 * social service implementation.
 * @author Emanuele 'lemrey' Di Santo
 */

public class FacebookActivity extends Activity {

	private final String TAG = "Facebook-Activity";
	
	private TextView mText;
	private LoginButton mLoginButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mText = (TextView) findViewById(R.id.txt);
		mLoginButton = (LoginButton) findViewById(R.id.login);

		// try to restore previous sessions
		if (SessionStore.restore(FB.getInstance(), this)) {
			mText.setText("You are logged in.");
			startService(new Intent(getBaseContext(), FacebookService.class));
		}
		
		// setup callbacks for login/logout events
		SessionEvents.addAuthListener(new SampleAuthListener());
		SessionEvents.addLogoutListener(new SampleLogoutListener());

		// init the facebook button
		mLoginButton.init(this, FB.getInstance(),
				new String[] {"manage_notifications","publish_stream",
							  "read_stream", "user_groups"});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		FB.getInstance().authorizeCallback(requestCode, resultCode, data);
	}

	// here we implement callback functions for authentication events
	private class SampleAuthListener implements AuthListener {

		public void onAuthSucceed() {
			mText.setText("User logged in!");
			startService(new Intent(getBaseContext(), FacebookService.class));
		}

		public void onAuthFail(String error) {
			mText.setText("Login Failed: " + error);
		}
	}

	// here we implement callback functions for logout events
	private class SampleLogoutListener implements LogoutListener {

		public void onLogoutBegin() {
			mText.setText("Logging out...");
		}

		public void onLogoutFinish() {
			mText.setText("You have logged out!");
		}
	}
}
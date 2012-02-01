/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Lemrey
package org.snaa;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TextView;

import com.facebook.android.*;

import org.snaa.SessionEvents.AuthListener;
import org.snaa.SessionEvents.LogoutListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

	private TextView mText;
	private Button mReadButton;
	private LoginButton mLoginButton;
	
	private Facebook mFacebook;	
	private AsyncFacebookRunner mAsyncRunner;
	
	private final String TAG = "Facebook";
	private final String APP_ID = "322276144483780";

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mText = (TextView) findViewById(R.id.txt);
		mReadButton = (Button) findViewById(R.id.read);		
		mLoginButton = (LoginButton) findViewById(R.id.login);

		// init Facebook object
		mFacebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		
		SessionEvents.addAuthListener(new SampleAuthListener());
		SessionEvents.addLogoutListener(new SampleLogoutListener());

		if (SessionStore.restore(mFacebook, this))
		{
			Log.d("Facebook", "SESSION TOKEN:"+mFacebook.getAccessToken());
			//mText.setText("Restored previous FB session");
			mReadButton.setVisibility(View.VISIBLE);
		}
		
		mLoginButton.init(this, mFacebook, new String[] {"read_stream"} );
		
		mReadButton.setOnClickListener(
			new OnClickListener()
			{
				public void onClick(View v) {
					mAsyncRunner.request("me/feed", new SampleRequestListener());
				}
			}
		);

	}

	@Override
	protected void onActivityResult(int requestCode, int
	  resultCode, Intent data) {
		mFacebook.authorizeCallback(requestCode,resultCode, data);
    }
	 
	public class SampleAuthListener implements AuthListener {

		public void onAuthSucceed() {
			mText.setText("User logged in!");
			mReadButton.setVisibility(View.VISIBLE);
		}

		public void onAuthFail(String error) {
			mText.setText("Login Failed: " + error);
			mReadButton.setVisibility(View.INVISIBLE);
		}
	}

	public class SampleLogoutListener implements LogoutListener {

		public void onLogoutBegin() {
			mText.setText("Logging out...");
		}

		public void onLogoutFinish() {
			mText.setText("You have logged out!");
			mReadButton.setVisibility(View.INVISIBLE);
		}
	}

	public class SampleRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			
			JSONObject json = null;
			final String name;
			
			try {
				// process the response here: executed in background thread
				//Log.d("Facebook-Example", "Response: " + response.toString());
				
				json = Util.parseJson(response);
				name = json.getJSONArray("data").getJSONObject(0).getString("story");
				
				// then post the processed result back to the UI thread
				// if we do not do this, an runtime exception will be generated

				MainActivity.this.runOnUiThread(
					new Runnable()
					{
						public void run() {
							mText.setText("Hello there, " + name + "!");
						}
					}
				);
				
			} catch (JSONException e) {
				Log.d("Facebook", "JSON Error in response");
			} catch (FacebookError e) {
				Log.d("Facebook", "Facebook Error: " + e.getMessage());
			}
		}
	}
}

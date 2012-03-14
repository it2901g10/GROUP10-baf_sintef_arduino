package no.ntnu.osnap.social.facebook;

import android.app.Activity;
import android.util.Log;
import android.os.Bundle;
import android.content.Intent;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TextView;

import no.ntnu.osnap.social.*;
import no.ntnu.osnap.social.facebook.SessionEvents.AuthListener;
import no.ntnu.osnap.social.facebook.SessionEvents.LogoutListener;


public class FacebookActivity extends Activity {

	private TextView mText;
	//private Button mReadButton;
	//private Button mShareButton;
	private LoginButton mLoginButton;
	//protected Intent intent;
	//protected Post post;
	//protected AsyncFacebookRunner mAsyncRunner;
	private final String TAG = "Facebook-Activity";

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mText = (TextView) findViewById(R.id.txt);
		//mReadButton = (Button) findViewById(R.id.read);
		//mShareButton = (Button) findViewById(R.id.share);
		mLoginButton = (LoginButton) findViewById(R.id.login);

		SessionEvents.addAuthListener(new SampleAuthListener());
		SessionEvents.addLogoutListener(new SampleLogoutListener());

		mLoginButton.init(this, FB.getIstance(),
				new String[]{"read_stream", "user_groups"});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		FB.getIstance().authorizeCallback(requestCode, resultCode, data);
	}

	private class SampleAuthListener implements AuthListener {

		public void onAuthSucceed() {
			mText.setText("User logged in!");
			//mReadButton.setVisibility(View.VISIBLE);
			startService(new Intent(getBaseContext(),FacebookService.class));
		}

		public void onAuthFail(String error) {
			mText.setText("Login Failed: " + error);
			//mReadButton.setVisibility(View.INVISIBLE);
		}
	}

	private class SampleLogoutListener implements LogoutListener {

		public void onLogoutBegin() {
			mText.setText("Logging out...");
		}

		public void onLogoutFinish() {
			mText.setText("You have logged out!");
			//mReadButton.setVisibility(View.INVISIBLE);
			//mShareButton.setVisibility(View.INVISIBLE);
		}
	}
}
package no.ntnu.osnap.social.facebook;

import android.app.Activity;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import java.util.ArrayList;
import java.util.Iterator;

import no.ntnu.osnap.social.*;
import no.ntnu.osnap.social.facebook.SessionEvents.AuthListener;
import no.ntnu.osnap.social.facebook.SessionEvents.LogoutListener;

import org.json.JSONException;
import org.json.JSONObject;


public class FacebookApp extends Activity
{
	
	private TextView mText;
	private Button mReadButton;
	private Button mShareButton;
	private LoginButton mLoginButton;
	
	//private ArduinoBluetoothAdapter adapter;
	//private Protocol protocol;
	
	protected Intent intent;
	//protected Group group;
	protected Post post;
	
	protected AsyncFacebookRunner mAsyncRunner;
	
	private Facebook mFacebook;	
	
	private final String APP_TAG = "Facebook";
	private final String APP_ID = "322276144483780";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		mText = (TextView) findViewById(R.id.txt);
		mReadButton = (Button) findViewById(R.id.read);
		mShareButton = (Button) findViewById(R.id.share);
		mLoginButton = (LoginButton) findViewById(R.id.login);

		// init Facebook object
		mFacebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		
		SessionEvents.addAuthListener(new SampleAuthListener());
		SessionEvents.addLogoutListener(new SampleLogoutListener());
		
		mLoginButton.init(this, mFacebook,
				new String[] {"read_stream", "user_groups"} );
		
		mReadButton.setOnClickListener(
			new OnClickListener()
			{
				public void onClick(View v) {
					//mAsyncRunner.request("me", new SampleRequestListener());
					//mAsyncRunner.request("me/groups", new GroupRequestListener());
					mAsyncRunner.request("1485062417_180461462061390", new PostRequestListener());
				}
			}
		);
		
		mShareButton.setOnClickListener(
			new OnClickListener()
			{
				public void onClick(View v) {
					startActivity(intent);
				}
			}
		);
    }
	
	@Override
	protected void onActivityResult(int requestCode, int
	  resultCode, Intent data) {
		mFacebook.authorizeCallback(requestCode,resultCode, data);
    }
	 
	private class SampleAuthListener implements AuthListener {

		public void onAuthSucceed() {
			mText.setText("User logged in!");
			mReadButton.setVisibility(View.VISIBLE);
		}

		public void onAuthFail(String error) {
			mText.setText("Login Failed: " + error);
			mReadButton.setVisibility(View.INVISIBLE);
		}
	}
	
	private class SampleLogoutListener implements LogoutListener {

		public void onLogoutBegin() {
			mText.setText("Logging out...");
		}

		public void onLogoutFinish() {
			mText.setText("You have logged out!");
			mReadButton.setVisibility(View.INVISIBLE);
			mShareButton.setVisibility(View.INVISIBLE);
		}
	}
		
	private class PostRequestListener extends BaseRequestListener {
		public void onComplete(final String response, final Object state) {
			
			JSONObject json;
			//Log.d(APP_TAG, "parsing response: "+response.toString());
			
			try {
				json = Util.parseJson(response);
				post = new Post(json);
				
				/*Iterator<String> iter = post.keys();
				while (iter.hasNext()) {
					String s = iter.next();
					Log.d(APP_TAG, s + " : " + post.get(s).toString());
				}*/
				
				Log.d(APP_TAG, "post message: " + post.getMessage());
				
				ArrayList<Person> p = post.getLikesAsList();
				
				for (int i = 0; i < p.size(); i++)
					Log.d(APP_TAG, p.get(i).getName() + " liked it");
				
				Comment comment;
				ArrayList<Comment> c = post.getCommentsAsList();
				
				for (int i = 0; i < c.size(); i++) {
					comment = c.get(i);
					Log.d(APP_TAG, "post comment by: " +
							comment.getSenderAsPerson().getName());
					Log.d(APP_TAG, "post comment: " +
							comment.getMessage());
				}
				
				intent = new Intent(IntentUtil.ACTION_TEXT_MSG);
				IntentUtil util = new IntentUtil();
				util.bundleObject(intent, (JSONObject)post);
				
				FacebookApp.this.runOnUiThread(
					new Runnable()
					{
						public void run() {
							//mText.setText(text);
							mShareButton.setVisibility(View.VISIBLE);
						}
					}
				);
				
			} catch (FacebookError ex) {
				Log.d(APP_TAG, ex.toString());
			} catch (JSONException ex) {
				Log.d(APP_TAG, ex.toString());
			}
		}
	}
	
	/*private class GroupRequestListener extends BaseRequestListener {
		public void onComplete(final String response, final Object state) {
			
			JSONObject json;
			//Log.d(APP_TAG, "parsing response: "+response.toString());
			
			try {
				json = Util.parseJson(response);
				json = json.getJSONArray("data").getJSONObject(0);
				//Log.d(APP_TAG,"group is (JSON): "+json.toString());
				//Log.d(APP_TAG,"name is " + json.getString("name"));
				group = new Group(json);
				
				Log.d(APP_TAG,"group is: "+group.toString());

				if (group.getString("name") != null) {
					String id = group.getString("id");
					mAsyncRunner.request(id+"/members", new GroupMembersListener());
				}				
							
			} catch (FacebookError ex) {
				Log.d(APP_TAG, ex.toString());
			} catch (JSONException ex) {
				Log.d(APP_TAG, ex.toString());
			}
		}
	}
	
	private class GroupMembersListener extends BaseRequestListener {
		public void onComplete(final String response, final Object state) {
			
			//GroupGroup g;
			JSONObject json;
			//Log.d(APP_TAG, "parsing response: "+response.toString());
			
			try {
				json = Util.parseJson(response);
				//g = new Group();
				
				group.remove("members");
				group.put("members", json.getJSONArray("data"));
				
				//Log.d(APP_TAG, "members are: " + group.getMembers().toString());
				
				intent = new Intent(IntentUtil.ACTION_TEXT_MSG);
				IntentUtil util = new IntentUtil();
				util.bundleObject(intent, (JSONObject)group);
				
				FacebookApp.this.runOnUiThread(
					new Runnable()
					{
						public void run() {
							//mText.setText(text);
							mShareButton.setVisibility(View.VISIBLE);
						}
					}
				);
				
				//group = g;
				
			} catch (FacebookError ex) {
				Log.d(APP_TAG, ex.toString());
			} catch (JSONException ex) {
				Log.d(APP_TAG, ex.toString());
			}
		}
	}
	*/	
}

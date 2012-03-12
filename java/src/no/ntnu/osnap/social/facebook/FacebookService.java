/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.osnap.social.facebook;

import android.app.Service;


import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author lemrey
 */
public class FacebookService extends Service {

	private final String TAG = "Facebook-Service";
	
	@Override
	public void onCreate() {
	}

	@Override
	public int onStartCommand(Intent intent, int flag, int cmd) {
		Log.d(TAG, "onStartCommand");
		/*Intent i = new Intent();
		i.setClassName("no.ntnu.osnap.social.testing", "SampleService");
		startService(i);*/
		//FB.getAsyncInstance().request("", null);
		/*
		 * Timer t = new Timer("timer!"); t.schedule(new TimerTask() {
		 *
		 * public void run() { Log.d(TAG, "RUNNING!"); } }, 0, 5000);
		 */
		//FB.getIstance().re

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private class PostRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {

			JSONObject json;
			//Log.d(TAG, "parsing response: "+response.toString());

			try {
				json = Util.parseJson(response);
				//post = new Post(json);

				/*
				 * Iterator<String> iter = post.keys(); while (iter.hasNext()) {
				 * String s = iter.next(); Log.d(TAG, s + " : " +
				 * post.get(s).toString()); }
				 */

				//Log.d(TAG, "post message: " + post.getMessage());

				/*
				 * ArrayList<Person> p = post.getLikesAsList();
				 *
				 * for (int i = 0; i < p.size(); i++) { Log.d(TAG,
				 * p.get(i).getName() + " liked it"); }
				 *
				 * Comment comment; ArrayList<Comment> c =
				 * post.getCommentsAsList();
				 *
				 * for (int i = 0; i < c.size(); i++) { comment = c.get(i);
				 * Log.d(TAG, "post comment by: " +
				 * comment.getSenderAsPerson().getName()); Log.d(TAG, "post
				 * comment: " + comment.getMessage()); }
				 */

				/*
				 * FacebookActivity.this.runOnUiThread( new Runnable() {
				 *
				 * public void run() { //mText.setText(text);
				 * mShareButton.setVisibility(View.VISIBLE); } });
				 */

			} catch (FacebookError ex) {
				Log.d(TAG, ex.toString());
			} catch (JSONException ex) {
				Log.d(TAG, ex.toString());
			}
		}
	}
}

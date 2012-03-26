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

import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.content.Context;
import android.content.ComponentName;
import android.content.BroadcastReceiver;

/**
 * Handles the incoming connection broadcasts from Prototype applications.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class BroadcastRcv extends BroadcastReceiver {

	private final String TAG = "Broadcast-Rcv";

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.d(TAG, "Broadcast received: " + intent.getAction());

		try {
			SessionStore.restore(FB.getInstance(), context);
			Log.d(TAG, "this a test: " + FB.getInstance().request("me"));
		} catch (Exception ex) {
			Log.d(TAG, ex.toString());
		}
		
		if (intent.getAction().equals("android.intent.action.SOCIAL")
				&& FB.getInstance().isSessionValid()) {
			
			Log.d(TAG, "Replying");
			

			Bundle bundle = intent.getExtras();

			// create a new intent with the give action
			Intent resp = new Intent("android.intent.action.SOCIAL");

			// the intent is to be sent to the class
			// contained in the 'reply' field
			resp.setComponent((ComponentName) bundle.get("replyTo"));

			// the social service is return in a field
			// called 'socialService'
			ComponentName cn = new ComponentName(context, FacebookService.class);
			resp.putExtra("socialService", cn);

			// answer the broadcast
			context.startService(resp);
		}
		
		// otherwise start the activity for result?
	}
}

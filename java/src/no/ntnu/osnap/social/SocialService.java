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
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.ntnu.osnap.social;

import android.app.Service;
import android.content.*;
import android.os.*;

import android.util.Log;


/**
 * Implements
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class SocialService extends Service {

	/**
	 * Use for logging purposes.
	 */
	private static final String TAG = "Social-Service";
	
	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	private final Messenger mMessenger = new Messenger(new IncomingHandler());
	
	/**
	 * When binding to the service, we return an interface to our messenger for
	 * sending messages to the service.
	 */
	@Override
	public void onCreate() {
		Log.d(TAG, "Started");

		registerReceiver(new BroadcastRcv(),
				new IntentFilter("android.intent.action.SOCIAL"));
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	private class IncomingHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			Log.d(TAG, "handleMessage()");

			Message reply;
			Response response = null;

			switch (msg.what) {

				case 1: { // Request (GET)
					Log.d(TAG, "Replying a request(GET)");
					Request req = Request.fromMessage(msg);
					response = handleRequest(req);
				}
				break;

				case 2: { // Request (POST)
					Log.d(TAG, "Replying a request(POST)");
					Request req = Request.fromMessage(msg);
					response = handlePostRequest(req);
				}
				break;

				default:
					super.handleMessage(msg);
			}

			// send back the response
			if (response != null) {
				reply = Message.obtain(null, 1, response.getBundle());
				reply.arg1 = msg.arg1; //copy the id
				try {
					msg.replyTo.send(reply);
				} catch (RemoteException ex) {
					Log.e(TAG, ex.toString());
				}
			}
		}
	}

	protected Response handleRequest(Request req) {
		return null;
	}
	
	protected Response handlePostRequest(Request req) {
		return null;
	}

	private class BroadcastRcv extends BroadcastReceiver {

		private final String TAG = "Broadcast-Rcv";

		@Override
		public void onReceive(Context context, Intent intent) {

			//Log.d(TAG, "Broadcast received");

			if (intent.getAction().equals("android.intent.action.SOCIAL")
					/*&& FB.getInstance().isSessionValid()*/ ) {

				Log.d(TAG, "Discovered");
				Bundle bundle;
				Bundle extras = intent.getExtras();

				Messenger messenger = extras.getParcelable("replyTo");

				try {

					bundle = new Bundle();
			
					bundle.putString("name",
							SocialService.this.getClass().getSimpleName());
					
					Message msg = Message.obtain(null, 0, bundle);
					msg.replyTo = SocialService.this.mMessenger;
					messenger.send(msg);

				} catch (RemoteException ex) {
					Log.d(TAG, ex.toString());
				}
			}
		}
	}
}

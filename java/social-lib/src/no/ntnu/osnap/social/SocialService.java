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
 * oSNAP class representing a Social service, which is an Android service that
 * handles prototypes requests. To use it, extend it and implement
 * {@link #handleRequest(Request )} and {@link #handlePostRequest(Request )}.
 * Requests are processed one at a time.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class SocialService extends Service {

	/**
	 * Used for logging purposes.
	 */
	private String TAG;
	/**
	 * The SocialService name, as published to prototypes.
	 */
	protected String mName = "Social-Service";
	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	private final Messenger mMessenger = new Messenger(new IncomingHandler());
	/**
	 * BroadcastReceiver for discovery intents.
	 */
	private BroadcastReceiver broadcastRcv;
	/**
	 * Constants for Android messages
	 */
	private final int DISCOVERY_REPLY = 0;
	private final int RESPONSE = 1;

	/**
	 * Called by the system when the service is first started. Subclasses must
	 * call the superclass implementation if overriding this method.
	 */
	@Override
	public void onCreate() {
		TAG = this.getClass().getSimpleName();
		Log.d(TAG, "Started");

		broadcastRcv = new BroadcastRcv();
		registerReceiver(broadcastRcv,
				new IntentFilter("android.intent.action.SOCIAL"));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastRcv);
	}

	/**
	 * When binding to the service, we return an interface to our messenger for
	 * sending messages to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	/**
	 * Sets the Social service name, which is used by prototypes to identify the
	 * service and send requests to it.
	 *
	 * @param name the name to be published to prototypes
	 */
	public void setServiceName(String name) {
		mName = name;
	}

	/**
	 * Handles incoming messages from prototypes.
	 */
	private class IncomingHandler extends Handler {

		private final int GET_REQUEST = 1;
		private final int POST_REQUEST = 2;

		@Override
		public void handleMessage(Message msg) {

			Message reply;
			Response response = null;
			Request req = Request.fromMessage(msg);

			Log.d(TAG, "Replying a request(" + msg.arg1 + ")" + " "
					+ req.getRequestCode().name());

			switch (msg.what) {

				case GET_REQUEST: { // Request (GET)
					response = handleRequest(req);
				}
				break;

				case POST_REQUEST: { // Request (POST)
					response = handlePostRequest(req);
				}
				break;

				default:
					super.handleMessage(msg);
			}

			// send back the response
			if (response != null) {
				reply = Message.obtain(null, RESPONSE, response.getBundle());
				reply.arg1 = msg.arg1; //copy the id
				try {
					msg.replyTo.send(reply);
				} catch (RemoteException ex) {
					Log.e(TAG, ex.toString());
				}
			}
		}
	}

	/**
	 * This method is invoked by the SocialService to handle incoming requests
	 * to fetch data from the social network. This method should return an
	 * appropriate {@link Response} object to be returned to the {@link Prototype}
	 * who made the request. Only non-{@code null} {@link Response} objects are
	 * sent back to the Prototype. If a request is not supported by the
	 * SocialService, this function should return a response with status
	 * NOT_SUPPORTED. Only one request is processed at a time.
	 *
	 * @param req the {@link Request} to be carried out
	 * @return a {@link Response} containing the data requested
	 */
	protected Response handleRequest(Request req) {
		Response ret = new Response();
		ret.setStatus(Response.Status.NOT_SUPPORTED);
		return ret;
	}

	/**
	 * Called by the SocialService implementation to handle incoming requests to
	 * send data to the social network.
	 *
	 * @param req the {@link Request} to be carried out
	 * @return a {@link Response}
	 */
	protected Response handlePostRequest(Request req) {
		Response ret = new Response();
		ret.setStatus(Response.Status.NOT_SUPPORTED);
		return ret;
	}

	/**
	 * Receives and replies discovery broadcasts sent by prototypes.
	 */
	private class BroadcastRcv extends BroadcastReceiver {

		// used for logging purposes
		private final String TAG = SocialService.this.TAG;

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals("android.intent.action.SOCIAL")) {

				Log.d(TAG, "Discovered");
				Bundle bundle = new Bundle();
				Bundle extras = intent.getExtras();

				// get the messenger to reply to
				Messenger messenger = extras.getParcelable("replyTo");

				//send a Message object containg our Messenger and service
				try {
					bundle.putString("name", mName);
					Message msg = Message.obtain(null, DISCOVERY_REPLY, bundle);
					msg.replyTo = SocialService.this.mMessenger;
					messenger.send(msg);
				} catch (RemoteException ex) {
					Log.d(TAG, ex.toString());
				}
			}
		}
	}
}

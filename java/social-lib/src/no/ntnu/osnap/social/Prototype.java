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
package no.ntnu.osnap.social;

import no.ntnu.osnap.social.listeners.*;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import android.util.Log;

import java.util.HashMap;

/**
 * oSNAP class for a Prototype Android application/service. This class is
 * intended to communicate with
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class Prototype {

	/**
	 * Used for logging purposes.
	 */
	//private String TAG = "Prototype";
	/**
	 * Name of the prototype. Used for logging purpose mainly.
	 */
	protected String mName = "Prototype";
	/**
	 * We'll use this context to send broadcasts and restore the class loaders
	 * of marshaled models.
	 */
	private final Context mContext;
	/**
	 * Used to receive messages.
	 */
	private final Messenger mMessenger;
	/**
	 * A mapping of Social services and their Messengers.
	 */
	private HashMap<String, Messenger> mServices;
	/**
	 * The connection callback.
	 */
	private ConnectionListener mConnection;
	/**
	 * A mapping of requests and their callback functions.
	 */
	private HashMap<Integer, ResponseListener> mListeners;
	/**
	 * Used to identify requests and fire the appropriate callback.
	 */
	private static int requestID = 0;

	/**
	 * Creates a prototype
	 *
	 * @param contex
	 * @param listener the listener
	 */
	public Prototype(Context contex, ConnectionListener listener) {
		mContext = contex;
		mConnection = listener;
		mServices = new HashMap<String, Messenger>();
		mListeners = new HashMap<Integer, ResponseListener>();
		mMessenger = new Messenger(new Prototype.IncomingHandler());
	}
	
	/**
	 * Sets the name of this prototype, used for logging purposes.
	 * @param name the name to be set
	 */
	public void setPrototypeName(String name) {
		mName = name;
	}

	/**
	 * Holds the logic to handle incoming {@link Message} from SocialServices.
	 */
	private class IncomingHandler extends Handler {

		private final int SERVICE_DISCOVERED = 0;
		private final int RESPONSE = 1;

		/**
		 * Handles incoming {@link Message} from Social services.
		 *
		 * @param msg the received {@link Message}
		 */
		@Override
		public void handleMessage(Message msg) {

			//Log.d(mName, "Message received");

			switch (msg.what) {

				case SERVICE_DISCOVERED: {
					if (msg.replyTo != null) {
						String name = ((Bundle) msg.obj).getString("name");
						Log.d(mName, "Discovered " + name);
						/*
						 * save service's name and messenger and fire the
						 * connection callback
						 */
						mServices.put(name, msg.replyTo);
						mConnection.onConnected(name);
					} else {
						Log.e(mName, "Null messenger in discovery");
					}
				}
				break;

				case RESPONSE: {
					Log.d(mName, "Response received(" + msg.arg1 + ")");
					Integer i = new Integer(msg.arg1);
					Response resp = Response.fromMessage(msg);
					
					// restore the class loader of marshaled classes
					resp.getBundle().setClassLoader(mContext.getClassLoader());

					// fire the callback if any and remove it from the list
					if (mListeners.containsKey(i)) {
						if (mListeners.get(i) != null) {
							mListeners.get(i).onComplete(resp);
						}
						mListeners.remove(i);
					}
				}
				break;

				default:
					super.handleMessage(msg);
			}
		}
	}

	/**
	 * Broadcasts a service discovery message.
	 */
	public void discoverServices() {

		Intent intent = new Intent("android.intent.action.SOCIAL");

		// bundle our Messenger class that will receive the reply
		intent.putExtra("replyTo", mMessenger);

		Log.d(mName, "Sending broadcast");
		mContext.sendBroadcast(intent);
	}

	/**
	 * Sends a request to the specified Social service.
	 *
	 * @param serviceName the name of the Social service to send the
	 * {@link Request} to
	 * @param req the {@link Request} to be sent
	 * @param listener the {@link ResponseListener} listener that will receive
	 * the {@link Response}. Can be {@code null} if you're not interested in
	 * the {@link Response}
	 * 
	 */
	public void sendRequest(String serviceName, Request req,
			ResponseListener listener) {

		int what = 1;
		Message msg;
		
		synchronized(this) {
			Log.d(mName, "Sending request(" + requestID + ") "
				+ req.getRequestCode().name() + " " + serviceName);
				
		}

		if (req.getRequestCode() == Request.RequestCode.POST_MESSAGE) {
			what = 2;
		}

		if (mServices.containsKey(serviceName)) {

			msg = Message.obtain(null, what, req.getBundle());
			msg.replyTo = mMessenger;

			// 
			synchronized (this) {
				msg.arg1 = requestID;
				mListeners.put(new Integer(requestID), listener);
				requestID++;
			};

			try {
				mServices.get(serviceName).send(msg);
				//Log.d(mName, "Message sent: " + requestID);
			} catch (Exception ex) {
				Log.e(mName, ex.toString());
			}
		}
	}
}
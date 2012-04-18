/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.osnap.social;

import no.ntnu.osnap.social.listeners.ResponseListener;
import no.ntnu.osnap.social.listeners.ConnectionListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import java.util.HashMap;

/**
 *
 * @author lemrey
 */
public class Prototype {

	private final String TAG = "Prototype";

	private final Context mContext;

	//private Timer mTimer;

	// Used to receive messages
	private final Messenger mMessenger;	
	private HashMap<String, Messenger> mServices;
	
	private ConnectionListener mConnection;
	private HashMap<Integer, ResponseListener> mListeners;

	private static int requestID = 0;

	public Prototype(Context contex, ConnectionListener listener) {
		mContext = contex;
		mConnection = listener;
		mServices = new HashMap<String, Messenger>();
		mListeners = new HashMap<Integer, ResponseListener>();
		mMessenger = new Messenger(new Prototype.IncomingHandler());
	}

	private class IncomingHandler extends Handler {
		
		private final int SERVICE_DISCOVERED = 0;
		private final int RESPONSE = 1;

		@Override
		public void handleMessage(Message msg) {
			
			Log.d(TAG, "Message received");
			
			String name = null;
			
			switch (msg.what) {
				
				case SERVICE_DISCOVERED: {
					Log.d(TAG, "Service found");
					if (msg.replyTo != null) {
						name = ((Bundle)msg.obj).getString("name");
						Log.d(TAG, "Discovered " + name);
						// save the service name and messenger
						// and fire the callback
						mServices.put(name, msg.replyTo);
						mConnection.onConnected(name);		
					} else {
						Log.e(TAG, "Null messenger in discovery");
					}
										
				} break;
					
				case RESPONSE: {					
					Integer i = new Integer(msg.arg1);
					Response resp = Response.fromMessage(msg);
					
					resp.getBundle().setClassLoader(mContext.getClassLoader());

					// fire the response callback
						// and remove it from the list
					if (mListeners.containsKey(i)) {						
						mListeners.get(i).onComplete(resp);
						mListeners.remove(i);
					}
				} break;

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

		// the reply shall be sent to this service
		intent.putExtra("replyTo", mMessenger);

		Log.d(TAG, "Sending broadcast");
		mContext.sendBroadcast(intent);
	}

	public void sendRequest(String serviceName, Request req,
			ResponseListener listener) {
		
		Log.d(TAG, "sendRequest() " + serviceName);
		  
		int what = 1;
		Message msg;
		
		if (req.getRequestCode().compareTo(Request.RequestCode.POST_MESSAGE) == 0)
			what = 2;
		
		if (mServices.containsKey(serviceName)) {
					
			msg = Message.obtain(null, what, req.getBundle());
			msg.replyTo = mMessenger;
			
			//
			synchronized(this) {
				msg.arg1 = requestID;
				mListeners.put(new Integer(requestID), listener);
				requestID++;
			};
			
			try {
				mServices.get(serviceName).send(msg);
				Log.d(TAG, "Message sent: " + requestID);
			} catch (Exception ex) {
				Log.e(TAG, ex.toString());
			}
		}	
	}
}
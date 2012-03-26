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
package no.ntnu.osnap.social.tshirt;

import no.ntnu.osnap.social.Person;
import no.ntnu.osnap.social.Message;
import no.ntnu.osnap.social.ISocialService;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Binder;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import android.util.Log;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import no.ntnu.osnap.com.Protocol;
import no.ntnu.osnap.com.BluetoothConnection;
import no.ntnu.osnap.com.UnsupportedHardwareException;

//import no.ntnu.osnap.social.Model;

//import org.json.JSONException;

/**
 *
 * @author lemrey
 */
public class TshirtService extends Service {

	// used for loggin
	private final String TAG = "Tshirt-Service";
	
	// a timer for scheduling requests to be
	// executed one at time in a separate thread (the timer's)
	private Timer mTimer;
	
	// our bt connection
	private BluetoothConnection mBtConn = null;
	
	// this is holds the interface for this service
	// (the Tshirt service) it is used by the
	// Tshirt activity to control the Tshirt service
	private IBinder mBinder = new TshirtBinder();
	
	// for tshirt app callbacks
	private EventListener mEventListener = null;

	// used to expose the service functionalities
	// to the activity
	public class TshirtBinder extends Binder {
		TshirtService getService() {
			return TshirtService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// upon binding request by the Tshirt activity
		// we return the tshirt service interface
		return mBinder;
	}

	@Override
	public void onCreate() {
		
		Log.d(TAG, "onCreate()");
		
		// BT related code. probably outdated.
		try {
			mBtConn = new BluetoothConnection("00:10:06:29:00:48",
				Tshirt.getActivity());
			
		} catch (UnsupportedHardwareException ex) {
			Log.d(TAG, ex.toString());
		} catch (IllegalArgumentException ex) {
			Log.d(TAG, ex.toString());
		}

		if (mBtConn != null) {
			mBtConn.connect();
			Log.d(TAG, "Trying to connect: " + mBtConn.getAddress());
			while (!mBtConn.isConnected()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException ex) {;}
			}
			mBtConn.start();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
		if (mTimer != null)
			mTimer.cancel();
	}
	
	/**
	 * This method can be used by the Tshirt activity to be notified
	 * when the Tshirt service establishes a connection with a social service.
	 * Note: Callbacks MUST be unregistered by the activity when it's being
	 * destroyed.
	 * 
	 * @param listener
	 */
	public void registerCallbacks (EventListener listener) {
		mEventListener = listener;
	}
	
	/**
	 * Unregisters callbacks. Must be called by the activity
	 * to unregister any registered callback before being destroyed.
	 */
	public void unregisterCallbacks() {
		mEventListener = null;
	}

	@Override
	public int onStartCommand(Intent intent, int flag, int startId) {

		// here we receive the responses from the social services
		// that expose our interface
		Log.d(TAG, "onStartCommand() " + intent.getAction());

		if (intent.getAction().equals("android.intent.action.SOCIAL")) {
			
			mTimer = new Timer();

			Log.d(TAG, "Binding..");
			Bundle extra = intent.getExtras();

			// create a new intent (needed to bind) using
			// the social service class name
			Intent i = new Intent("android.intent.action.SOCIAL");
			i.setComponent((ComponentName) extra.get("socialService"));

			// bind to the remote service
			bindService(i, mConnection, Context.BIND_AUTO_CREATE);
		}

		// remain running!
		return START_STICKY;
	}
	
	public boolean isConnected() {
		boolean ret = false;
		if (Tshirt.getServiceNames().size() > 0)
			ret = true;
		
		return ret;
	}

	public void disconnectSocialServices() {
		
		// cancel scheduled requets
		mTimer.cancel();
		
		// unbind from the remote service
		unbindService(mConnection);
		
		// remove it from the lists
		Tshirt.getServiceList().remove(0);
		Tshirt.getServiceNames().remove(0);
	}

	/*public void scheduleRequest(final String json,
			final int model_type, final int code) {

		Timer timer = new Timer();

		TimerTask task = new TimerTask() {

			public void run() {
				try {
					String buf[] = Tshirt.getServiceList().get(0)
							.request(json, model_type, code);
					
					Log.d(TAG, buf[0]);

				} catch (RemoteException ex) {
					Log.d(TAG, ex.toString());
				}
			}
		};

		timer.scheduleAtFixedRate(task, 0, 8000);
	}*/
	
	public void scheduleRequest(TimerTask task) {
		mTimer.scheduleAtFixedRate(task, 0, 8000);
	}
	
	public void printToArduino(String text) {
		Log.d(TAG, "printing to Arduino: " + text);
		if (mBtConn.isConnected())
			mBtConn.print(text);
	}
	
	// here we implement the callback methods to handle
	// service connection events
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className,
				IBinder service) {

			Log.d(TAG, "onServiceConnected()");

			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.

			//Person p = null;
			//String[] buf = null;

			// add the service to our list of connected services
			Tshirt.getServiceList().add(ISocialService.Stub.asInterface(service));

			// save its name
			Tshirt.getServiceNames().add(className.getClassName());

			
			//Tshirt.getEventListener().onConnected(className.getClassName());
			
			// invoke the callback method so that the activity can be notified.
			if (mEventListener != null) {
				mEventListener.serviceConnected(className.getClassName());
			}

			/*
			 * try { buf = mService.get(0).request("", 1,
			 * Person.REQUEST.FULL_PROFILE.ordinal());
			 *
			 * Log.d(TAG, buf[0]); p = new Person(buf[0]);
			 *
			 * } catch (JSONException ex) { Log.d(TAG, ex.toString()); } catch
			 * (RemoteException e) { }
			 *
			 * // As part of the sample, tell the user what happened.
			 * Toast.makeText(TshirtService.this, "Remote service connected: " +
			 * p.getName(), Toast.LENGTH_SHORT).show();
			 *
			 * try { buf = mService.get(0).request("", 1,
			 * Person.REQUEST.GET_FRIENDS.ordinal());
			 *
			 * for (int i = 0; i < buf.length; i++) { Person pp = new
			 * Person(buf[i]); Log.d(TAG, "Friend: " + pp.getName()); }
			 *
			 * //Log.d(TAG, "Posts by: " + new Person(buf[0]).getName());
			 * Log.d(TAG, "Posts by: me");
			 *
			 * buf = mService.get(0).request("", 1,
			 * Person.REQUEST.GET_MESSAGES.ordinal());
			 *
			 * for (int i = 0; i < buf.length; i++) { Message mm = new
			 * Message(buf[i], Message.Facebook); Log.d(TAG, mm.getText()); }
			 *
			 * mService.get(0).request("", 1, 10);
			 *
			 * } catch (JSONException ex) { Log.d(TAG, ex.toString()); } catch
			 * (RemoteException e) { }
			 */


			/*
			 * try { btconn = new BluetoothConnection("00:10:06:29:00:48",
			 * Tshirt.getActivity()); } catch (UnsupportedHardwareException ex)
			 * { Log.d(TAG, ex.toString()); } catch (IllegalArgumentException
			 * ex) { Log.d(TAG, ex.toString()); }
			 *
			 * if (btconn != null) { btconn.connect(); Log.d(TAG, "Trying to
			 * connect: " + btconn.getAddress()); while (!btconn.isConnected())
			 * { try { Thread.sleep(500); } catch (InterruptedException ex) {;}
			 * } btconn.start(); btconn.print(p.getName()); }
			 */
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.

			Log.d(TAG, "onServiceDisconnected()");
			String name = className.getClassName();
			ArrayList<String> serviceNames = Tshirt.getServiceNames();

			for (int i = 0; i < serviceNames.size(); i++) {
				if (name.equals(serviceNames.get(i))) {
					Tshirt.getServiceList().remove(i);
					Tshirt.getServiceNames().remove(i);
					//Tshirt.getEventListener().onDisconnected(name);
					if (mEventListener != null) {
						mEventListener.serviceDisconnected(name);
					}
					
					
				}
			}
		}
	};
}

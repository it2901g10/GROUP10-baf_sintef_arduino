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

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.ntnu.osnap.com.Protocol;
import no.ntnu.osnap.com.BluetoothConnection;
import no.ntnu.osnap.com.UnsupportedHardwareException;

import no.ntnu.osnap.social.ISocialService;
import no.ntnu.osnap.social.Person;
import no.ntnu.osnap.social.Message;
import org.json.JSONException;

/**
 *
 * @author lemrey
 */
public class TshirtService extends Service {

	// used for loggin
	private final String TAG = "Tshirt-Service";
	protected BluetoothConnection btconn = null;
	// we may want to connect to more than one social service
	ArrayList<ISocialService> mService = new ArrayList<ISocialService>();

	@Override
	public IBinder onBind(Intent arg0) {
		// nobody binds to the tshirt service
		return null;
	}

	@Override
	public void onCreate() {
		//oddly enough it's not called..
		Log.d(TAG, "onCreate()");
	}

	@Override
	public int onStartCommand(Intent intent, int flag, int startId) {

		/*
		 * here we receive the responses from the social services
		 */

		Log.d(TAG, "onStartCommand()");
		Bundle extra = intent.getExtras();

		/*
		 * create a new intent (needed to bind) using the social service class
		 * name
		 */
		Intent i = new Intent("android.intent.action.SOCIAL");
		i.setComponent((ComponentName) extra.get("socialService"));

		//bind to the service
		bindService(i, mConnection, Context.BIND_AUTO_CREATE);

		//remain running!
		return START_STICKY;
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className,
				IBinder service) {

			Log.d(TAG, "onServiceConnected()");

			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  We are communicating with our
			// service through an IDL interface, so get a client-side
			// representation of that from the raw service object

			Person p = null;
			//Message m = null;
			String[] buf = null;
			
			mService.add(ISocialService.Stub.asInterface(service));

			// We want to monitor the service for as long as we are
			// connected to it.
			try {
				buf = mService.get(0).request("", 1,
						Person.REQUEST.FULL_PROFILE.ordinal());

				Log.d(TAG, buf[0]);
				p = new Person(buf[0]);

			} catch (JSONException ex) {
				Log.d(TAG, ex.toString());
			} catch (RemoteException e) {	
			}

			// As part of the sample, tell the user what happened.
			Toast.makeText(TshirtService.this, "Remote service connected: "
					+ p.getName(), Toast.LENGTH_SHORT).show();
			
			try {
				buf = mService.get(0).request("", 1,
						Person.REQUEST.GET_FRIENDS.ordinal());
				
				for (int i = 0; i < buf.length; i++) {
					Person pp = new Person(buf[i]);
					Log.d(TAG, "Friend: " + pp.getName());
				}

				//Log.d(TAG, "Posts by: " + new Person(buf[0]).getName());
				Log.d(TAG, "Posts by: me");
				
				buf = mService.get(0).request("", 1,
						Person.REQUEST.GET_MESSAGES.ordinal());
				
				for (int i = 0; i < buf.length; i++) {
					Message mm = new Message(buf[i], Message.Facebook);
					Log.d(TAG, mm.getText());
				}

			} catch (JSONException ex) {
				Log.d(TAG, ex.toString());
			} catch (RemoteException e) {
			}
			
			
			
			/*try {
				btconn = new BluetoothConnection("00:10:06:29:00:48",
					Tshirt.getActivity());
			} catch (UnsupportedHardwareException ex) {
				Log.d(TAG, ex.toString());
			} catch (IllegalArgumentException ex) {
				Log.d(TAG, ex.toString());
			}

			if (btconn != null) {
				btconn.connect();
				Log.d(TAG, "Trying to connect: " + btconn.getAddress());
				while (!btconn.isConnected()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException ex) {;}
				}
				btconn.start();
				btconn.print(p.getName());
			}*/
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			for (ISocialService i : mService) {
				Log.d(TAG, i.getClass().getCanonicalName());
				Log.d(TAG, i.getClass().getName());
				Log.d(TAG, className.getClassName());
				Log.d(TAG, className.getPackageName());
				if (i.getClass().getName().equals(
						className.getPackageName() + className.getClassName())) {
					mService.remove(i);
				}
			}

			// As part of the sample, tell the user what happened.
			Toast.makeText(TshirtService.this, "Disconnected from service "
					+ className.getClassName(), Toast.LENGTH_SHORT).show();
		}
	};
}

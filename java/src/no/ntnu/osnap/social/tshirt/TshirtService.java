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

import no.ntnu.osnap.social.ISocialService;

/**
 *
 * @author lemrey
 */
public class TshirtService extends Service {

	// used for loggin
	private final String TAG = "Tshirt-Service";
	
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
		
		/* here we receive the responses from the
		 * social services 
		 */
		
		Log.d(TAG, "onStartCommand()");
		Bundle extra = intent.getExtras();
		
		/* create a new intent (needed to bind) using
		 * the social service class name
		 */
		Intent i = new Intent("android.intent.action.SOCIAL");
		i.setComponent((ComponentName)extra.get("socialService"));
		
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
			String testString = null;
			mService.add(ISocialService.Stub.asInterface(service));

			// We want to monitor the service for as long as we are
			// connected to it.
			try {
				testString = mService.get(0).hello();
			} catch (RemoteException e) {
				// In this case the service has crashed before we could even
				// do anything with it; we can count on soon being
				// disconnected (and then reconnected if it can be restarted)
				// so there is no need to do anything here.
			}

			// As part of the sample, tell the user what happened.
			Toast.makeText(TshirtService.this, "Remote service connected: "
					+ testString, Toast.LENGTH_SHORT).show();
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			for (ISocialService i : mService) {
				if (i.getClass().getName().equals(className.getClassName())) {
					mService.remove(i);
				}
			}

			// As part of the sample, tell the user what happened.
			Toast.makeText(TshirtService.this, "Disconnected from service "
					+ className.getClassName(), Toast.LENGTH_SHORT).show();
		}
	};
}

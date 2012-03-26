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
package no.ntnu.osnap.social.tshirt;

import no.ntnu.osnap.social.tshirt.TshirtService.TshirtBinder;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.TimerTask;
import no.ntnu.osnap.social.ISocialService;
import no.ntnu.osnap.social.Message;

public class TshirtActivity extends Activity implements View.OnClickListener {

	private final String TAG = "Tshirt-Activity";
	
	private Button mStartButton;
	private Button mStopButton;
	private Button mForwardButton;

	private TshirtService mService;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// init our buttons
		mStartButton = (Button) findViewById(R.id.start);
		mStopButton = (Button) findViewById(R.id.stop);
		mForwardButton = (Button) findViewById(R.id.forward);

		mStartButton.setOnClickListener(this);
		mStopButton.setOnClickListener(this);
		mForwardButton.setOnClickListener(this);

		// we store our Activity instance in our singleton class
		// as it must be available in order to init a BT connection
		Tshirt.setActivity(this);
		
		// bind to the Tshirt service (start it if it's not)
		bindService(new Intent(this, TshirtService.class),
			mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onDestroy() {

		Log.d(TAG, "onDestroy()");
		
		// unregister callbacks or bad things will happen
		mService.unregisterCallbacks();

		// unbind from local service
		unbindService(mConnection);

		super.onDestroy();
	}

	//here we handle clicks
	public void onClick(View v) {

		//Log.d(TAG, "onClick()");

		switch (v.getId()) {

			case (R.id.start): {

				Intent intent = new Intent("android.intent.action.SOCIAL");
				ComponentName cn = new ComponentName(this, TshirtService.class);

				// the key for the classname is 'replyTo'
				intent.putExtra("replyTo", cn);

				Log.d(TAG, "Sending broadcast");
				sendBroadcast(intent);

			} break;

			case (R.id.stop): {
				Toast.makeText(this,
						"Disconnecting from: " +
						Tshirt.getServiceNames().get(0), 5000).show();
				
				// unbind from the remote services
				mService.disconnectSocialServices();
				
				mStartButton.setVisibility(View.VISIBLE);
				mStopButton.setVisibility(View.INVISIBLE);
				mForwardButton.setVisibility(View.INVISIBLE);
				
			} break;

			case (R.id.forward): {

				TimerTask t = new TimerTask() {

					ISocialService service = Tshirt.getServiceList().get(0);

					public void run() {
						try {
							Message msg;
							String[] buf = service.request("", 1, 2);
							//for (int i = 0; i < buf.length; i++) {
								msg = new Message(buf[0], Message.Facebook);
								mService.printToArduino(msg.getText());
							//}
						} catch (Exception ex) {
							Log.d(TAG, ex.toString());
						}
					}
				};
				
				mService.scheduleRequest(t);
			} break;
		}
	}

	// here we implement the callbacks for tshirt-service connection events
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName cn, IBinder service) {
			
			mService = ((TshirtBinder) service).getService();
			
			Toast.makeText(TshirtActivity.this,
					"Connected to Tshirt service", 5000).show();
			
			mService.registerCallbacks(new ConnectionListener());
			
			if (mService.isConnected()) {
				TshirtActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						mStartButton.setVisibility(View.INVISIBLE);
						mStopButton.setVisibility(View.VISIBLE);
						mForwardButton.setVisibility(View.VISIBLE);
					}
				});
			}
		}

		// never called
		public void onServiceDisconnected(ComponentName cn) {;}
	};

	// here we implement the callbacks for social-connection events
	private class ConnectionListener implements EventListener {

		public void serviceConnected(final String className) {
			Log.d(TAG, "onConnected()");

			Toast.makeText(Tshirt.getActivity(),
					"Connected to: " + className, 5000).show();

			TshirtActivity.this.runOnUiThread(new Runnable() {

				public void run() {
					mStartButton.setVisibility(View.INVISIBLE);
					mStopButton.setVisibility(View.VISIBLE);
					mForwardButton.setVisibility(View.VISIBLE);
				}
			});
		}

		public void serviceDisconnected(final String className) {
			Log.d(TAG, "onDisconnected()");

			Toast.makeText(Tshirt.getActivity(),
					"Disconnected from: " + className, 5000).show();

			TshirtActivity.this.runOnUiThread(new Runnable() {

				public void run() {
					mStartButton.setVisibility(View.VISIBLE);
					mStopButton.setVisibility(View.INVISIBLE);
					mForwardButton.setVisibility(View.INVISIBLE);
				}
			});
		}
	}
}

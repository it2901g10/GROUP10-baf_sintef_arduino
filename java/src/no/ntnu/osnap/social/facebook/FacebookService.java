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

package no.ntnu.osnap.social.facebook;

import android.app.Notification;
import android.app.NotificationManager;
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

import no.ntnu.osnap.social.ISocialService;

/**
 *
 * @author lemrey
 */
public class FacebookService extends Service {

	private final String TAG = "Facebook-Service";
	private NotificationManager mNM;
	/**
	 * The IRemoteInterface is defined through IDL
	 */
	private final ISocialService.Stub mBinder = new ISocialService.Stub() {

		public void noop() {;
		}

		public String hello() {
			return new String("Hello from FBService!");
		}
	};

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		//Notification notification = new Notification()

	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind received.");
		return mBinder;
	}
}

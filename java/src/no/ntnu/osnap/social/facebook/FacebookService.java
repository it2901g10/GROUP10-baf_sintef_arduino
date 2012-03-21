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
import android.os.RemoteException;
import android.util.Log;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import no.ntnu.osnap.social.ISocialService;
import no.ntnu.osnap.social.Person;
import no.ntnu.osnap.social.Message;
import no.ntnu.osnap.social.Model;

/**
 *
 * @author lemrey
 */
public class FacebookService extends Service {

	private final String TAG = "Facebook-Service";
	private NotificationManager mNM;
	
	private final ISocialService.Stub mBinder = new ISocialService.Stub() {

		public String[] request(String json, int model_type, int code)
				throws RemoteException {

			String[] ret = null;
			Log.d(TAG, "request() received");

			switch (model_type) {
				case 0:
					break;
				case 1: {
					ret = handlePersonRequest(json, code);
				}
				break;
				case 2: {
					ret = handleMessageRequest(json, code);
				}
				break;
				case 3: {
				}
				break;
			}

			return ret;
		}
	};

	public String[] handlePersonRequest(String json, int code) {

		Person person;
		String buf, id;
		JSONObject jsonObj;
		JSONArray jsonArray;

		String[] ret = null;
		
		Log.d(TAG, "handlePerson(): " + json); 

		try {

			if (!json.equals("")) {
				Log.d(TAG, "person has ID");
				person = new Person(json);
				id = person.getID();
			} else {
				Log.d(TAG, "person is self");
				id = "me";
			}

			switch (code) {
				case 0: {
					buf = FB.getIstance().request(id);
					ret = new String[1];
					ret[0] = buf;
				}
				break;
				case 1: {
					Log.d(TAG, "requesting: " + id + "/friends"); 
					buf = FB.getIstance().request(id + "/friends");
					
					/*ArrayList<Person> list = (ArrayList<Person>)
							Model.makeArrayList(buf, null);
					
					for (int i = 0; i < list.size(); i++) {
						Log.d(TAG, "arraylist<person> :" + list.get(i).getName());
					}*/
					
					Log.d(TAG, "fetched: " + buf); 
					jsonArray = new JSONObject(buf).getJSONArray("data");
					ret = new String[jsonArray.length()];
					for (int i = 0; i < jsonArray.length(); i++) {
						jsonObj = jsonArray.getJSONObject(i);
						ret[i] = jsonObj.toString();
					}
				}
				break;
				case 2: {
					Log.d(TAG, "requesting: " + id + "/posts"); 
					buf = FB.getIstance().request(id + "/posts");
					Log.d(TAG, "fetched: " + buf); 
					jsonArray = new JSONObject(buf).getJSONArray("data");
					ret = new String[jsonArray.length()];
					for (int i = 0; i < jsonArray.length(); i++) {
						jsonObj = jsonArray.getJSONObject(i);
						ret[i] = jsonObj.toString();
					}
				}
				break;
			}
		} catch (Exception ex) {
			Log.d(TAG, ex.toString());
		}
		return ret;
	}

	public String[] handleMessageRequest(String json, int code) {

		Message msg;
		String buf, id;
		JSONObject jsonObj;
		JSONArray jsonArray;
		String[] ret = null;
		
		Log.d(TAG, "handleMessage(): " + json); 

		try {
			msg = new Message(json);
			id = msg.getID();

			switch (code) {
				case 0:
					break;
				case 1: {
					buf = FB.getIstance().request(id);
					Log.d(TAG, "fetched: " + buf); 
					ret = new String[1];
					ret[0] = buf;
				}
				break;
			}
		} catch (Exception ex) {
			Log.d(TAG, ex.toString());
		}

		return ret;
	}

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

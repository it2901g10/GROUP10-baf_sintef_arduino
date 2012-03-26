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

import no.ntnu.osnap.social.Model;
import no.ntnu.osnap.social.Person;
import no.ntnu.osnap.social.Message;
import no.ntnu.osnap.social.ISocialService;

import android.app.Service;
import android.util.Log;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.content.Intent;

import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONArray;


/**
 * Implements the Social interface methods.
 * Contains the FB specific code needed to answer Social requests.
 * 
 * @author Emanuele 'lemrey' Di Santo
 */
public class FacebookService extends Service {

	private final String TAG = "Facebook-Service";
	
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
		
		Log.d(TAG, "handlePerson():");// + json); 
		
		if (json == null) {
			json = "me";
		}

		try {
			if (!json.equals("")) {
				Log.d(TAG, "person has ID");
				person = new Person(json);
				id = person.getID();
			} else {
				Log.d(TAG, "person is 'me'");
				id = "me";
			}

			switch (code) {
				case 0: {
					buf = FB.getInstance().request(id);
					ret = new String[1];
					ret[0] = buf;
				}
				break;
				case 1: {
					Log.d(TAG, "requesting: " + id + "/friends"); 
					buf = FB.getInstance().request(id + "/friends");
					//Log.d(TAG, "fetched: " + buf); 
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
					buf = FB.getInstance().request(id + "/posts");
					//Log.d(TAG, "fetched: " + buf); 
					jsonArray = new JSONObject(buf).getJSONArray("data");
					ret = new String[jsonArray.length()];
					for (int i = 0; i < jsonArray.length(); i++) {
						jsonObj = jsonArray.getJSONObject(i);
						ret[i] = jsonObj.toString();
					}
				} break;
				case 10: {
					Log.d(TAG, "requesting: make a post");
					Bundle param = new Bundle();
					param.putString("message", "aah");
					FB.getInstance().request("me/feed", param, "POST");
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
					buf = FB.getInstance().request(id);
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
		//mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public IBinder onBind(Intent intent) {
		
		Log.d(TAG, "onBind()");
		
		// we return our remote interface implementaton
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind()");
		return true;
	}
}

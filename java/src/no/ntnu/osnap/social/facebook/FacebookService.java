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

import android.app.Service;
import android.content.*;
import android.os.*;

import android.util.Log;
import java.io.IOException;
import java.net.MalformedURLException;

import no.ntnu.osnap.social.Request;
import no.ntnu.osnap.social.Response;
import no.ntnu.osnap.social.SocialService;
import no.ntnu.osnap.social.models.*;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;


/**
 * Implements
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class FacebookService extends SocialService {

	private static final String TAG = "Facebook-Service";

	@Override
	protected Response handleRequest(Request req) {

		String buf;
		Response response = new Response();

		switch (req.getRequestCode()) {

			case SELF: {

				Log.d(TAG, "Answering SELF");
				Person person;

				try {
					buf = FB.getInstance().request("me");
					person = new Person(buf, Person.Facebook);
					response.bundle(person);
				} catch (Exception ex) {
					Log.d(TAG, ex.toString());
				}

			}
			break; // Request.SELF

			case PERSON_DATA: {

				Log.d(TAG, "Answering PERSON_DATA");
				Model model;
				Person person;

				if (req.getModel() != null) {

					model = req.getModel();

					try {
						buf = FB.getInstance().request(model.getID());
						person = new Person(buf, Person.Facebook);
						response.bundle(person);
					} catch (MalformedURLException ex) {
						Log.e(TAG, ex.toString());
					} catch (IOException ex) {
						Log.e(TAG, ex.toString());
					} catch (JSONException ex) {
						Log.e(TAG, ex.toString());
					}
				}
			}
			break;

			case FRIENDS: {

				Log.d(TAG, "Answering FRIENDS");
				Person person;

				try {
					buf = FB.getInstance().request("me/friends");

					JSONObject json = new JSONObject(buf);
					JSONArray array = json.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						person = new Person(array.getJSONObject(i),
								Person.Facebook);
						response.bundle(person);
					}
				} catch (Exception ex) {
					Log.d(TAG, ex.toString());
				}
			}
			break; // Request.FRIENDS

			case GROUPS: {

				Log.d(TAG, "Answering GROUPS");
				Group group;

				try {
					
					buf = FB.getInstance().request("me/groups");

					JSONObject json = new JSONObject(buf);
					JSONArray array = json.getJSONArray("data");

					for (int i = 0; i < array.length(); i++) {
						group = new Group(array.getJSONObject(i), Group.Facebook);
						response.bundle(group);
					}
				} catch (Exception ex) {
					Log.d(TAG, ex.toString());
				}

			}
			break;


			case GROUP_DATA: {
			}
			break;

			case GROUP_MEMBERS: {
			}
			break;

			case MESSAGES: {
				Log.d(TAG, "Answering MESSAGES");
				Post post;

				try {
					buf = FB.getInstance().request("me/feed");

					JSONObject json = new JSONObject(buf);
					JSONArray array = json.getJSONArray("data");

					for (int i = 0; i < array.length(); i++) {
						post = new Post(array.getJSONObject(i), Post.Facebook);
						response.bundle(post);
					}
				} catch (Exception ex) {
					Log.d(TAG, ex.toString());
				}
			}
			break;

			case NOTIFICATIONS: {
				Log.d(TAG, "Answering NOTIFICATIONS");
			}
			break;

		}

		return response;

	}
}

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

import android.os.Bundle;
import no.ntnu.osnap.social.Request;
import no.ntnu.osnap.social.Response;
import no.ntnu.osnap.social.SocialService;
import no.ntnu.osnap.social.models.*;

import android.util.Log;

import org.json.JSONObject;
import org.json.JSONArray;


/**
 * Facebook social service implementation.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class FacebookService extends SocialService {

	private static final String TAG = "Facebook-Service";

	@Override
	public void onCreate() {
		super.onCreate();
		
		// Set the name to be published to prototypes
		setServiceName("Facebook");
	}

	/**
	 * Here we handle incoming requests.
	 * A proper Response object must be returned. In case the Request is not
	 * supported, return a Response with status NOT_SUPPORTED.
	 */
	@Override
	protected Response handleRequest(Request request) {
        Log.d(TAG,"RESPONSE" + request.toString());
		String buf;
		
		// our response object
		Response response = new Response();

		switch (request.getRequestCode()) {

			case SELF: {
				Log.d(TAG, "Answering SELF");
				
				try {
					buf = FB.getInstance().request("me");
					Person person = new Person(buf, Person.Facebook);
					response.bundle(person);
				} catch (Exception ex) {
					Log.e(TAG, ex.toString());
				}
			} break; // Request.SELF

			case PERSON_DATA: {
				Log.d(TAG, "Answering PERSON_DATA");
				
				Model model = request.getModel();
				if (model != null) {
					try {
						buf = FB.getInstance().request(model.getID());
						Person person = new Person(buf, Person.Facebook);
						response.bundle(person);
					} catch (Exception ex) {
						Log.e(TAG, ex.toString());
					}
				} else {
					response.setStatus(Response.Status.MISSING_PARAMETERS);
				}
			} break; // Request.PERSON_DATA

			case FRIENDS: {
				Log.d(TAG, "Answering FRIENDS");
				
				Model model = request.getModel();				
				try {
					if (model == null) {
						buf = FB.getInstance().request("me/friends");
					} else {
						buf = FB.getInstance().request(model.getID()+"/friends");
					}
					JSONArray array = new JSONObject(buf).getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						buf = array.getJSONObject(i).toString();
						Person person = new Person(buf, Person.Facebook);
						response.bundle(person);
					}
				} catch (Exception ex) {
					Log.e(TAG, ex.toString());
				}
			} break; // Request.FRIENDS

			case GROUPS: {
				Log.d(TAG, "Answering GROUPS");
				
				Model model = request.getModel();
				try {
					if (model == null) {
						buf = FB.getInstance().request("me/groups");
					} else {
						buf = FB.getInstance().request(model.getID()+"/groups");
					}
					JSONArray array = new JSONObject(buf).getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						buf = array.getJSONObject(i).toString();
						Group group = new Group(buf, Group.Facebook);
						response.bundle(group);
					}
				} catch (Exception ex) {
					Log.e(TAG, ex.toString());
				}
			} break; // Request.GROUPS

			case GROUP_DATA: {
				Log.d(TAG, "Answering GROUP_DATA");
				
				Model model = request.getModel();
				if (model != null) {
					try {
						buf = FB.getInstance().request(model.getID());
						Group group = new Group(buf, Group.Facebook);
						response.bundle(group);
					} catch (Exception ex) {
						Log.e(TAG, ex.toString());
					}
				} else {
					response.setStatus(Response.Status.MISSING_PARAMETERS);
				}
			} break;

			case GROUP_MEMBERS: {
				Log.d(TAG, "Answering GROUP_MEMBERS");
				
				Model model = request.getModel();
				if (model != null) {
					try {
						buf = FB.getInstance().request(model.getID()+"/members");
						JSONArray array = new JSONObject(buf).getJSONArray("data");
						for (int i = 0; i < array.length(); i++) {
							buf = array.getJSONObject(i).toString();
							Person person = new Person(buf, Person.Facebook);
							response.bundle(person);
						}
					} catch (Exception ex) {
						Log.e(TAG, ex.toString());
					}
				} else {
					response.setStatus(Response.Status.MISSING_PARAMETERS);
				}
			} break; // Request.GROUP_MEMBERS
				
			case GROUP_FEED: {
				Log.d(TAG, "Answering GROUP_FEED");
				
				Model model = request.getModel();
				if (model != null) {
					try {
						buf = FB.getInstance().request(model.getID()+"/feed");
						JSONArray array = new JSONObject(buf).getJSONArray("data");
						for (int i = 0; i < array.length(); i++) {
							buf = array.getJSONObject(i).toString();
							Message msg = new Message(buf, Message.Facebook);
							response.bundle(msg);
						}
					} catch (Exception ex) {
						Log.e(TAG, ex.toString());
					}
				} else {
					response.setStatus(Response.Status.MISSING_PARAMETERS);
				}
			} break; // Request.GROUP_FEED
				
			case MESSAGE_STREAM: {
				Log.d(TAG, "Answering MESSAGE_STREAM");
				
				Model model = request.getModel();
				try {
					if (model == null) {
						buf = FB.getInstance().request("me/feed");
					} else {
						buf = FB.getInstance().request(model.getID()+"/feed");
					}
					JSONArray array = new JSONObject(buf).getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						buf = array.getJSONObject(i).toString();
						Message msg = new Message(buf, Message.Facebook);
						response.bundle(msg);
					}
				} catch (Exception ex) {
					Log.e(TAG, ex.toString());
				}
			} break; // Request.MESSAGE_STREAM

			case MESSAGES: {
				Log.d(TAG, "Answering MESSAGES");
				
				Model model = request.getModel();
				try {
					if (model == null) {
						buf = FB.getInstance().request("me/posts");
					} else {
						buf = FB.getInstance().request(model.getID()+"/posts");
					}
					JSONArray array = new JSONObject(buf).getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						buf = array.getJSONObject(i).toString();
						Message msg = new Message(buf, Message.Facebook);
						response.bundle(msg);
					}
				} catch (Exception ex) {
					Log.e(TAG, ex.toString());
				}
			} break; // Request.MESSAGES
			
			// Not implemented
			//case MESSAGE_DATA: {;} break;

			case NOTIFICATIONS: {
				Log.d(TAG, "Answering NOTIFICATIONS");
				
				try {
					buf = FB.getInstance().request("me/notifications");
					JSONArray array = new JSONObject(buf).getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						buf = array.getJSONObject(i).toString();
						Notification n = new Notification(buf, Person.Facebook);
						response.bundle(n);
					}
				} catch (Exception ex) {
					Log.e(TAG, ex.toString());
				}
			} break; // Request.NOTIFICATION
			
			// Not implemented
			//case NOTIFICATION_DATA: {;} break;
				
			default: {
				/**
				 * If we don't support the request, we set the response
				 * status to NOT_SUPPORTED.
				 */
				response.setStatus(Response.Status.NOT_SUPPORTED);
			}
		}

		return response;
	}

	@Override
	protected Response handlePostRequest(Request request) {

		Response response = new Response();

		switch (request.getRequestCode()) {

			case POST_MESSAGE: {
				Log.d(TAG, "Answering POST_MESSAGE");

				Model model = request.getModel();		
				try {
					if (model == null) {
						FB.getInstance().request("me/feed",
							request.getParams(), "POST");
					} else {
						FB.getInstance().request(model.getID()+"/feed",
							request.getParams(), "POST");
					}
				} catch (Exception ex) {
					Log.e(TAG, ex.toString());
				}
			} break; // Request.POST_MESSAGE
				
			case POST_GROUP_MESSAGE: {
				Log.d(TAG, "Answering POST_GROUP_MESSAGE");
				
				Model model = request.getModel();
				if (model != null) {
					try {
						FB.getInstance().request(model.getID()+"/feed",
							request.getParams(), "POST");
					} catch (Exception ex) {
						Log.e(TAG, ex.toString());
					}
				} else {
					response.setStatus(Response.Status.MISSING_PARAMETERS);
				}
			} break;

			default: {
				response.setStatus(Response.Status.NOT_SUPPORTED);
			} break;
		}

		return response;
	}
}

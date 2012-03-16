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

package no.ntnu.osnap.social;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents a notification.
 * @author Emanuele 'lemrey' Di Santo
 */
public class Notification extends Model {
	
	/** Constructs an empty Notification.
	 * An empty Notification has no fields.
	 */
	public Notification() {;}
	
	/** Construct a Notification from a source JSON text string.
	 * 
	 * @param json a JSON string, starting with { and ending with }.
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */
	public Notification(String json) throws JSONException {
		super(json);
	}
	
	/** Constructs a Notification from a {@code JSONObject} instance.
	 * 
	 * @param object a JSONObject
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */	
	public Notification (JSONObject object) throws JSONException {
		super(object);
	}
	
	/** Gets the sender of this notification.
	 * 
	 * @return the value of the key 'from' as a {@code JSONObject} or
	 * {@code null} if the key doesn't exists.
	 */
	public JSONObject getSender() {
		JSONObject ret;
		ret = optJSONObject("from");
		if (ret == null) {
			Log.d(APP_TAG, "key 'from' doesn't exist.");
		}
		return ret;
	}
	
	/** Gets the recipient of this notification.
	 * 
	 * @return the value of the key 'to' as a {@code JSONObject} or
	 * {@code null} if the key doesn't exists.
	 */
	public JSONObject getRecipient() {
		JSONObject ret;
		ret = optJSONObject("to");
		if (ret == null) {
			Log.d(APP_TAG, "key 'to' doesn't exist.");
		}
		return ret;
	}
	
	/** Gets the sender of this notification as person.
	 * 
	 * @return the value of the key 'from' as a {@link Person} or
	 * {@code null} if the key doesn't exists.
	 * @see Person
	 */
	public Person getSenderAsPerson() {
		Person p = null;
		try {
			p = new Person(getSender());
		} catch (JSONException ex) {
			Log.d(APP_TAG, ex.toString());
		}
		return p;
	}
	
	/** Gets the recipient of this notification as person.
	 * 
	 * @return the value of the key 'to' as a [@link Person} or
	 * {@code null} if the key doesn't exists.
	 * @see Person
	 */
	public Person getRecipientAsPerson() {
		Person p = null;
		try {
			p = new Person(getRecipient());
		} catch (JSONException ex) {
			Log.d(APP_TAG, ex.toString());
		}
		return p;
	}
	
	/** Gets the title of this notification as a string.
	 * 
	 * @return the value of the key 'title' as a string
	 * or an empty string if the key doesn't exist.
	 */
	public String getTitle() {
		String ret;
		ret = optString("title");
		if (ret.equals("")) {
			Log.d(APP_TAG, "key 'title' doesn't exist.");
		}
		return ret;
	}
	
	/** Gets the message of this notification as a string.
	 * 
	 * @return the value of the key 'message' as a string
	 * or an empty string if the key doesn't exist.
	 */
	public String getMessage() {
		String ret;
		ret = optString("message");
		if (ret.equals("")) {
			Log.d(APP_TAG, "key 'message' doesn't exist.");
		}
		return ret;
	}
	
	/** Tells whether this notification is unread or not.
	 * 
	 * @return {@code true} if the key 'unread' exists
	 * and its value is 1, returns {@code false} otherwise.
	 */
	public boolean isUnread() {
		return optBoolean("unread", false);
	}
	
	/** Gets the link associated with the notification.
	 * 
	 * @return the value of the key 'link' or {@code null}
	 * if the key doesn't exists.
	 * (Or maybe it will fail?)
	 */
	public URL getLink() {
		URL link = null;
		try {
			link = new URL(getString("link"));
		} catch (JSONException ex) {
			Log.d(APP_TAG, ex.toString());
		} catch (MalformedURLException ex) {
			Log.d(APP_TAG, ex.toString());
		}
		return link;
	}
		
}

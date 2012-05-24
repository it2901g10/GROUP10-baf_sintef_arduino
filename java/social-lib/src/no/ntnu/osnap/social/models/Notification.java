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
package no.ntnu.osnap.social.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * oSNAP class representing a notification sent to a user.
 * 
 * @author Emanuele 'lemrey' Di Santo
 */
public class Notification extends Model implements Parcelable {
	
	/**
	 * Holds the translation table used to create this Notification.
	 */
	private HashMap<String, String> mTransl;
	
	/**
	 * Constructs an empty Notification.
	 * An empty Notification has no fields.
	 */
	public Notification() {;}
	
	/**
	 * Construct a Notification from a source JSON text string.
	 * 
	 * @param json a JSON string, starting with { and ending with }
	 * @throws JSONException if there's a syntax error or duplicated key
	 */
	public Notification(String json) throws JSONException {
		super(json);
	}
	
	public Notification(String json, HashMap<String, String> transl)
			throws JSONException {
		super(json, transl);
		mTransl = transl;
	}
	
	/**
	 * Gets the sender of this notification as person.
	 * 
	 * @return the value of the field 'from' as a {@link Person} or
	 * {@code null} if no value is associated with such field
	 */
	public Person getSenderAsPerson() {
		
		Person p = null;
		String buf = ((JSONObject)getField("from")).toString();
		try {
			if (mTransl != null) {
				p = new Person(buf, mTransl);
			} else {
				p  = new Person(buf, Person.Facebook);
			}
		} catch (JSONException ex) {
			Log.d(TAG, ex.toString());
		}
		return p;
	}
	
	/**
	 * Gets the recipient of this notification as a {@link Person}.
	 * 
	 * @return the value of the field 'to' as a [@link Person} or
	 * {@code null} if no value is associated with such field
	 */
	public Person getRecipientAsPerson() {
		Person p = null;
		String buf = ((JSONObject)getField("to")).toString();
		try {
			if (mTransl != null) {
				p = new Person(buf, mTransl);
			} else {
				p  = new Person(buf, Person.Facebook);
			}
		} catch (JSONException ex) {
			Log.d(TAG, ex.toString());
		}
		return p;
	}
	
	/**
	 * Gets the title of this notification as a {@code String}.
	 * 
	 * @return the value of the field 'title' as a {@code String}
	 * or an empty {@code String} if no value is associated with such field
	 */
	public String getTitle() {
		String ret;
		ret = getStringField("title");
		if (ret.equals("")) {
			Log.d(TAG, "field 'title' doesn't exist.");
		}
		return ret;
	}
	
	/**
	 * Gets the message of this notification as a {@code String}.
	 * 
	 * @return the value of the field 'message' as a {@code String}
	 * or an empty {@code String} if no value is associated with such field
	 */
	public String getMessage() {
		String ret;
		ret = getStringField("message");
		if (ret.equals("")) {
			Log.d(TAG, "field 'message' doesn't exist.");
		}
		return ret;
	}
	
	/**
	 * Returns whether this Notification is unread or not.
	 * 
	 * @return {@code true} if the field 'unread' exists
	 * and its value is 1, returns {@code false} otherwise.
	 */
	public boolean isUnread() {
		return getBooleanField("unread");
	}
	
	/**
	 * UNTESTED: Gets the link associated with this notification.
	 * 
	 * @return the value of the field 'link' or {@code null}
	 * if the no value is associated with such field.
	 * (Or maybe it will fail?)
	 */
	public URL getLink() {
		URL link = null;
		try {
			link = new URL(getStringField("link"));
		} catch (MalformedURLException ex) {
			Log.d(TAG, ex.toString());
		}
		return link;
	}
	
	// Parcelable interface-related code follows
	
	public static final Parcelable.Creator<Notification> CREATOR =
			new Parcelable.Creator<Notification>() {

				public Notification createFromParcel(Parcel in) {
					Notification n = null;
					try {
						n = new Notification(in);
					} catch (Exception ex) {
						Log.e(TAG, ex.toString());
					}
					return n;
				}

				public Notification[] newArray(int size) {
					return new Notification[size];
				}
			};

	public Notification(Parcel in) throws JSONException {
		super(in.readString());
		mTransl = in.readHashMap(null);
	}

	@Override
	public void writeToParcel(Parcel out, int flag) {
		super.writeToParcel(out, flag);
		out.writeMap(mTransl);
	}
}

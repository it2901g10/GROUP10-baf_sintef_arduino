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
import no.ntnu.osnap.social.models.Model;
import android.util.Log;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a person.
 * @author Emanuele 'lemrey' Di Santo
 */
public class Person extends Model implements Parcelable {
	
	
	public static final Parcelable.Creator<Person> CREATOR =
			new Parcelable.Creator<Person>() {

				public Person createFromParcel(Parcel in) {
					return new Person(in);
				}

				public Person[] newArray(int size) {
					return new Person[size];
				}
			};

	public Person(Parcel in) {
		try {
			jsonModel = new JSONObject(in.readString());
		} catch (Exception ex) {
			Log.d(TAG, ex.toString());
		}
	}

	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(jsonModel.toString());
	}

	public int describeContents() {
		//throw new UnsupportedOperationException("Not supported yet.");
		return 0;
	}
	
	
	public static final HashMap<String, String> Facebook = 
	new HashMap<String, String> () {{
		put("name", "displayName");
		put("bio", "aboutMe");
	}};
	
	public static enum REQUEST {
		
		/**
		 * Retrieves the full user profile.
		 */
		FULL_PROFILE,
		/**
		 * Retrieves the user's friend list.
		 */
		GET_FRIENDS,
		/**
		 * Retrieves the messages posted by the user.
		 */
		GET_MESSAGES,
		/**
		 * Retrieves the list of groups the user belongs to.
		 */
		GET_GROUPS,
		/**
		 * Retrieves the user home page.
		 * This is actually the Facebook wall.
		 */
		GET_HOME,
		/**
		 * Retrieves the notifications received by the user.
		 */
		GET_NOTIFICATIONS,
		
		
		SEND_STATUS,
		SEND_MESSAGE,
		SEND_PHOTO,
	};
	
	public Person() {}
	
	/** Constructs a Person from a source JSON text string.
	 * 
	 * @param json a JSON string, starting with { and ending with }.
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */
	public Person(String json) throws JSONException {
		super(json);
		Log.d(TAG, "new Person()");
	}
	
	/** Constructs a Person from a {@code JSONObject} instance.
	 * 
	 * @param object a JSONObject
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */	
	public Person (JSONObject object) throws JSONException {
		super(object);
	}
	
	/**
	 * Constructs a Person from a source JSON text string,
	 * performing a translation.
	 * 
	 * @param json a JSON string, starting with { and ending with }.
	 * @param transl a translation table for key names.
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */
	public Person(String json, HashMap<String, String> transl) 
			throws JSONException {
		
		super(json, transl);
	}
	
	/**
	 * Constructs a Person from a JSONObject,
	 * performing a translation.
	 * 
	 * @param object a JSONObject
	 * @param transl a translation table for key names.
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */
	public Person(JSONObject object, HashMap<String, String> transl) 
			throws JSONException {
		
		super(object, transl);
	}

	/** Returns the name of this person.
	 * 
	 * @return the value of the key 'displayName' as a string
	 * or an empty string if the key doesn't exist.
	 */
	public String getName() {
		String ret;
		ret = jsonModel.optString("displayName");
		if (ret.equals("")) {
			Log.d(TAG, "key 'displayName' doesn't exist.");
		}
		return ret;
	}
}

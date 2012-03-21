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
import java.util.HashMap;
//import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a person.
 * @author Emanuele 'lemrey' Di Santo
 */
public class Person extends Model {
	
	private JSONObject jsonModel;
	
	/*
	 * OpenSocial	|	FB
	 * displayName		name
	 * aboutMe			bio
	 */
	public static final HashMap<String, String> OpenSocial = 
	new HashMap<String, String> () {{
		put("displayName", "name");
		put("aboutMe", "bio");
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
		 * Retrieves the public messages sent by the user.
		 */
		GET_MESSAGES,
		/**
		 * Retrieves the list of groups the user belongs to.
		 */
		GET_GROUPS,
		/**
		 * Retrieves the user home page.
		 */
		//GET_HOME,
		/**
		 * Retrieves the notifications received by the user.
		 */
		GET_NOTIFICATIONS,
		
		SEND_STATUS,
		SEND_MESSAGE,
		//SEND_COMMENT,
		SEND_PHOTO,
	};
	
	public Person() {
		jsonModel = new JSONObject();
	}
	
	/** Constructs a Person from a source JSON text string.
	 * 
	 * @param json a JSON string, starting with { and ending with }.
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */
	public Person(String json) throws JSONException {
		try {
			jsonModel = new JSONObject(json);
		} catch (JSONException ex) {
			Log.d(TAG, ex.toString());
			throw(ex);
		}
	}
	
	/** Constructs a Person from a {@code JSONObject} instance.
	 * 
	 * @param object a JSONObject
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */	
	public Person (JSONObject object) throws JSONException {
		try {
			jsonModel = new JSONObject(object.toString());
		} catch (JSONException ex) {
			Log.d(TAG, ex.toString());
			throw(ex);
		}
	}
	
	/**
	 * 
	 * @param json
	 * @param transl 
	 */
	public Person(String json, HashMap<String, String> transl) 
			throws JSONException {
		
		this(json);
		translate(transl);
	}
	
	/**
	 * 
	 * @param json
	 * @param transl
	 * @throws JSONException 
	 */
	public Person(JSONObject object, HashMap<String, String> transl) 
			throws JSONException {
		
		this(object);
		translate(transl);
	}

	/** Returns the name of this person.
	 * 
	 * @return the value of the key 'name' as a string
	 * or an empty string if the key doesn't exist.
	 */
	public String getName() {
		String ret;
		ret = jsonModel.optString("name");
		if (ret.equals("")) {
			Log.d(TAG, "key 'name' doesn't exist.");
		}
		return ret;
	}
	
	/*public Request obtainRequest(Person person, int req) {
		return Request.obtain(person, req);
	}*/
}

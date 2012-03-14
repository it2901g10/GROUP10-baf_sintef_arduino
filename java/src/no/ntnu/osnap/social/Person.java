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
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a person.
 * @author Emanuele 'lemrey' Di Santo
 */
public class Person extends Model {
	
	/*
	 * OpenSocial	|	FB
	 * displayName		name
	 * aboutMe			bio
	 * birthday			birthday
	 * gender			gender
	 */
	public static final Map<String, String> FBMap = 
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
		 * Retrieves the user's Facebook wall.
		 * Facebook specific.
		 */
		GET_FBHOME,
		/**
		 * Retrieves the posts made by the user.
		 * Facebook specific. For OpenSocial
		 * containers use GET_MESSAGES.
		 */
		GET_FBPOSTS,
		/**
		 * Retrieves user's statuses.
		 */
		GET_STATUSES,
		/**
		 * Retrieves the user's friend list.
		 */
		GET_FRIENDS,
		/**
		 * Retrieves the list of groups the user belongs to.
		 */
		GET_GROUPS,
		/**
		 * Retrieves the list of events the user is attending.
		 */
		GET_EVENTS,
		/**
		 * Retrieves the notifications received by the user.
		 */
		GET_NOTIFICATIONS,
		/**
		 * Retrieves the posts made by the user.
		 */
		GET_ALBUMS,
		
		SEND_STATUS,
		SEND_POST,
		SEND_COMMENT,
		SEND_MESSAGE,
		SEND_PHOTO,
	};
	
	public Person() {;}
	
	/** Constructs a Person from a source JSON text string.
	 * 
	 * @param json a JSON string, starting with { and ending with }.
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */
	public Person(String json) throws JSONException {
		super(json);
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
	 * 
	 * @param json
	 * @param transl 
	 */
	public Person(String json, Map<String, String> transl) 
			throws JSONException {
		
		super(json, transl);
	}
	
	/**
	 * 
	 * @param json
	 * @param transl
	 * @throws JSONException 
	 */
	public Person(JSONObject object, Map<String, String> transl) 
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
		ret = optString("displayName");
		if (ret.equals("")) {
			Log.d(APP_TAG, "key 'displayName' doesn't exist.");
		}
		return ret;
	}
	
	public Request obtainRequest(Person person, int req) {
		return Request.obtain(person, req);
	}
}

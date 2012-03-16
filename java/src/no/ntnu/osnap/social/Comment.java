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

/**
 * Represents a comment.
 * @author Emanuele 'lemrey' Di Santo
 */
public class Comment extends Model {
	
	/** Constructs an empty Comment.
	 * An empty comment has no fields.
	 */
	public Comment() {;}
	
	/** Constructs a Comment from a source JSON text string
	 * 
	 * @param json a JSON string, starting with { and ending with }.
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */
	public Comment(String json) throws JSONException {
		super(json);
	}
	
	/** Constructs a Group from a {@code JSONObject} instance
	 * 
	 * @param object a JSONObject
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */	
	public Comment (JSONObject object) throws JSONException {
		super(object);
	}
	
	/** Gets the message of this comment as a string.
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
	
	/** Gets the sender of this comment.
	 * 
	 * @return the value of the key 'from' as a {@code JSONObject}
	 * or {@code null} if the key doesn't exist.
	 */
	public JSONObject getSender() {
		JSONObject ret;
		ret = optJSONObject("from");
		if (ret == null) {
			Log.d(APP_TAG, "key 'from' doesn't exist.");
		}
		return ret;
	}
	
	/** Gets the sender of this comment as a person.
	 * 
	 * @return the value of the key 'from' as a {@link Person}
	 * or {@code null} if the key doesn't exist.
	 * (FALSE, it will probably fail)
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
}

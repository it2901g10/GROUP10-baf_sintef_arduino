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

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/** Represents a group.
 * @author Emanuele 'lemrey' Di Santo
 */
public class Group extends Model {
	
	/** Constructs an empty Group.
	 * An empty group has no field except 'members'
	 * whose value is an empty JSONArray.
	 */
	public Group () {
		try {
			put("members", new JSONArray());
		} catch (JSONException ex) {
			Log.d(APP_TAG, ex.toString());
		}
	}
	
	/** Constructs a Group from a source JSON text string.
	 * 
	 * @param json a JSON string, starting with { and ending with }.
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */
	public Group (String json) throws JSONException {
		super(json);
	}
	
	/** Constructs a Group from a {@code JSONObject} instance.
	 * 
	 * @param object a JSONObject
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */	
	public Group (JSONObject object) throws JSONException {
		super(object);
	}
	
	/** Returns the name of this group.
	 * 
	 * @return the value of the key 'name' as a string
	 * or an empty string if the key doesn't exist.
	 */
	public String getName() {
		String ret;
		ret = optString("name");
		if (ret.equals("")) {
			Log.d(APP_TAG, "key 'name' doesn't exist.");
		}
		return ret;
	}
	
	/** Returns the members of this group.
	 * 
	 * @return the value of the key 'members' as a {@code JSONArray} or
	 * {@code null} if the key doesn't exist.
	 */
	public JSONArray getMembers() {
		JSONArray ret;
		ret = optJSONArray("members");
		if (ret == null) {
			Log.d(APP_TAG, "key 'members' doesn't exist.");
		}
		return ret;
	}
	
	/** Returns a list of the members of this group.
	 * 
	 * @return an {@code ArrayList} of {@link Person} representing
	 * the members of the group
	 * @see Person
	 */
	public ArrayList<Person> getMembersAsList () {
		Person p;
		JSONArray members = getMembers();
		ArrayList<Person> ret = new ArrayList<Person>();
		try {
			for (int i =  0; i < members.length(); i++) {
				p = new Person(members.getJSONObject(i));
				ret.add(p);
			}			
		} catch (JSONException ex) {
			Log.d(APP_TAG, ex.toString());
		}
		return ret;
	}
	
	/** Adds a member to this group.
	 * 
	 * @param person the person to be added.
	 * A copy of the object will be added.
	 */
	public void addMember(Person person) {
		Person p = null;
		try {
			p = new Person(person);
		} catch (JSONException ex) {
			Log.d(APP_TAG, ex.toString());
		}
		getMembers().put(p);
	}
	
}

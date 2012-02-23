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
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Represents a person.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class Person extends Model {
	
	public Person() {
		try {
			put("groups", new JSONArray());
		} catch (JSONException ex) {
			Log.d(APP_TAG, ex.toString());
		}
	}
	
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

	/** Returns the name of this person.
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

	/** Returns the family name of this person.
	 * 
	 * @return the value of the key 'familyName' as a string
	 * or an empty string if the key doesn't exist.
	 */
	public String getFamilyName() {
		String ret;
		ret = optString("familyName");
		if (ret.equals("")) {
			Log.d(APP_TAG, "key 'familyName' doesn't exist.");
		}
		return ret;
	}
	
	/** Returns the friends of this person.
	 * 
	 * @return the value of the key 'friends' as a {@code JSONArray}
	 * or {@code null} if the key doesn't exist.
	 */
	public JSONArray getFriends() {
		JSONArray ret;
		ret = optJSONArray("friends");
		if (ret == null) {
			Log.d(APP_TAG, "key 'friends' doesn't exist.");
		}
		return ret;
	}
	
	/** Returns the groups this person belongs to.
	 * 
	 * @return the value of the key 'groups' as a {@code JSONArray}
	 * or {@code null} if the key doesn't exist.
	 */
	public JSONArray getGroups() {
		JSONArray ret;
		ret = optJSONArray("groups");
		if (ret == null) {
			Log.d(APP_TAG, "key 'groups' doesn't exist.");
		}
		return ret;
	}

	/** Returns a list of friends of this person.
	 * 
	 * @return the value of the key 'friends' as an {@code ArrayList}
	 * of {@link Person} or {@code null} if the key doesn't exist.
	 * @see Person
	 */
	public ArrayList<Person> getFriendsAsList() {
		Person p;
		JSONArray friends = getFriends();
		ArrayList<Person> ret = new ArrayList<Person>();
		try {
			for (int i =  0; i < friends.length(); i++) {
				p = new Person(friends.getJSONObject(i));
				ret.add(p);
			}
		} catch (JSONException ex) {
				Log.d(APP_TAG, ex.toString());
		}
		return ret;
	}
	
	/** Returns a list of the groups this person belongs.
	 * 
	 * @return the value of the key 'friends' as an {@code ArrayList} of
	 * {@link Group} or {@code null} if the key doesn't exist.
	 * @see Group
	 */	
	public ArrayList<Group> getGroupsAsList() {
		Group g;
		JSONArray groups = getGroups();
		ArrayList<Group> ret = new ArrayList<Group>();
		try {
			for (int i =  0; i < groups.length(); i++) {
				g = new Group(groups.getJSONObject(i));
				ret.add(g);
			}
		} catch (JSONException ex) {
				Log.d(APP_TAG, ex.toString());
		}
		return ret;
	}
	
	/** Adds this person in a group.
	 * @param group the group which the person will belong to.
	 */
	public void addGroup (Group group) {
		Group g = null;
		try {
			g = new Group(group);
		} catch (JSONException ex) {
			Log.d(APP_TAG, ex.toString());
		}
		getGroups().put(g);
	}
}

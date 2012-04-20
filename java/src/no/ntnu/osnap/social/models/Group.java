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

import java.util.HashMap;

import org.json.JSONException;

/**
 * oSNAP class representing a group.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class Group extends Model implements Parcelable {

	/**
	 * A translation table for Facebook name fields.
	 */
	public static final HashMap<String, String> Facebook =
			new HashMap<String, String>() {

				{
					put("name", "title");
				}
			};

	/**
	 * Constructs an empty Group.
	 */
	public Group() {;}

	/**
	 * Constructs a Group from a source JSON text string.
	 *
	 * @param json a JSON string, starting with { and ending with }.
	 * @throws JSONException if there's a syntax error or duplicated field.
	 */
	public Group(String json) throws JSONException {
		super(json);
	}

	/**
	 *
	 * @param json
	 * @param transl
	 * @throws JSONException
	 */
	public Group(String json, HashMap<String, String> transl)
			throws JSONException {
		this(json);
		translate(transl);
	}

	/**
	 * Returns the title of this group.
	 *
	 * @return the value of the field 'title' as a {@link String} or an empty
	 * {@link String} if no value is associated with such field.
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
	 * Returns the description of this group.
	 *
	 * @return the value of the field 'description' as a {@link String} or an
	 * empty {@link String} if no value is associated with such field.
	 */
	public String getDescription() {
		String ret;
		ret = getStringField("description");
		if (ret.equals("")) {
			Log.d(TAG, "field 'description' doesn't exist.");
		}
		return ret;
	}
	
	// Parcelable interface-related code follows
	
	public static final Parcelable.Creator<Group> CREATOR =
			new Parcelable.Creator<Group>() {

				public Group createFromParcel(Parcel in) {
					Group g = null;
					try {
						g = new Group(in);
					} catch (Exception ex) {
						Log.e(TAG, ex.toString());
					}
					return g;
				}

				public Group[] newArray(int size) {
					return new Group[size];
				}
			};

	public Group(Parcel in) throws JSONException {
		super(in.readString());
	}
	
}

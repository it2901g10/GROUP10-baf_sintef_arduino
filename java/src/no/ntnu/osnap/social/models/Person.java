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
 * oSNAP class representing a person/user.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class Person extends Model implements Parcelable {

	/**
	 * A translation table for Facebook field names.
	 */
	public static final HashMap<String, String> Facebook =
			new HashMap<String, String>() {

				{
					put("name", "displayName");
					put("bio", "aboutMe");
				}
			};

	/**
	 * Constructs an empty Person model.
	 */
	public Person() {;
	}

	/**
	 * Constructs a Person from a source JSON text string.
	 *
	 * @param json a JSON string, starting with { and ending with }.
	 * @throws JSONException if there's a syntax error or duplicated field.
	 */
	public Person(String json) throws JSONException {
		super(json);
	}

	/**
	 * Constructs a Person from a source JSON text string, performing a field
	 * name translation.
	 *
	 * @param json a JSON string, starting with { and ending with }.
	 * @param transl a translation table for field names.
	 * @throws JSONException if there's a syntax error or duplicated field.
	 */
	public Person(String json, HashMap<String, String> transl)
			throws JSONException {

		super(json, transl);
	}

	/**
	 * Returns the name of this person.
	 *
	 * @return the value of the field 'displayName' as a {@link String} or an
	 * empty {@link String} if no value is associated with such field.
	 */
	public String getName() {
		String ret;
		ret = getStringField("displayName");
		if (ret.equals("")) {
			Log.d(TAG, "field 'displayName' doesn't exist.");
		}
		return ret;
	}
	
	// Parcelable interface-related code follows
	
	public static final Parcelable.Creator<Person> CREATOR =
			new Parcelable.Creator<Person>() {

				public Person createFromParcel(Parcel in) {
					Person p = null;
					try {
						p = new Person(in);
					} catch (Exception ex) {
						Log.e(TAG, ex.toString());
					}
					return p;
				}

				public Person[] newArray(int size) {
					return new Person[size];
				}
			};

	public Person(Parcel in) throws JSONException {
		super(in.readString());
	}
}

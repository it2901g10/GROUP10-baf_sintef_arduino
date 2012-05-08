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
import org.json.JSONObject;

/**
 * oSNAP class representing a message, which is some text sent by a user to
 * another.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class Message extends Model implements Parcelable {
	
	
	/**
	 * Holds the translation table used to create this Message.
	 */
	private HashMap<String, String> mTransl = null;

	/**
	 * A translation table for Facebook field names.
	 */
	public static final HashMap<String, String> Facebook =
			new HashMap<String, String>() {

				{
					put("story", "message");
				}
			};

	/**
	 * A translation table for Twitter field names.
	 */
	public static final HashMap<String, String> Twitter =
			new HashMap<String, String>() {

				{
					put("text", "message");
				}
			};

	/**
	 * Constructs an empty Message.
	 */
	public Message() {;
	}

	/**
	 * Constructs a Message from a source JSON text string.
	 *
	 * @param json a JSON string, starting with { and ending with }
	 * @throws JSONException if there's a syntax error or duplicated field
	 */
	public Message(String json) throws JSONException {
		super(json);
	}

	/**
	 * Constructs a Message from a source JSON text string, performing a field
	 * name translation.
	 *
	 * @param json a JSON string, starting with { and ending with }
	 * @param transl a translation table for field names
	 * @throws JSONException if there's a syntax error or duplicated field
	 */
	public Message(String json, HashMap<String, String> transl)
			throws JSONException {
		super(json, transl);
		mTransl = transl;
	}

	/**
	 * Gets the sender of this message as a Person.
	 *
	 * @return the value of the field 'from' as a {@link Person} or {@code null}
	 * if no value is associated with such field
	 * @see Person
	 */
	public Person getSenderAsPerson() {
		Person p = null;
		String buf = ((JSONObject) getField("from")).toString();
		try {
			if (mTransl != null) {
				p = new Person(buf, mTransl);
			} else {
				p  = new Person(buf);
			}
		} catch (JSONException ex) {
			Log.d(TAG, ex.toString());
		}
		return p;
	}

	/**
	 * Gets the text of this message as a string.
	 *
	 * @return the value of the field 'message' as a {@link String} or an empty
	 * {@link String} if no value is associated with such field
	 */
	public String getText() {
		String ret;
		ret = getStringField("message");
		if (ret.equals("")) {
			Log.d(TAG, "field 'message' doesn't exist.");
		}
		return ret;
	}
	
	// Parcelable interface-related code follows
	
	public static final Parcelable.Creator<Message> CREATOR =
			new Parcelable.Creator<Message>() {

				public Message createFromParcel(Parcel in) {
					Message m;
					try {
						m = new Message(in);
					} catch (JSONException ex) {
						m = new Message();
						Log.e(TAG, ex.toString());
					}
					return m;
				}

				public Message[] newArray(int size) {
					return new Message[size];
				}
			};

	public Message(Parcel in) throws JSONException {
		super(in.readString());
		mTransl = in.readHashMap(null);
	}
	
	@Override
	public void writeToParcel(Parcel out, int flag) {
		super.writeToParcel(out, flag);
		out.writeMap(mTransl);
	}
}

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

import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import org.json.JSONObject;
import org.json.JSONException;

/**
 * oSNAP generic class for all social objects.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class Model implements Iterable, Parcelable {

	/**
	 * Used for logging purposes.
	 */
	protected static final String TAG = "Social-Lib";
	
	/**
	 * Holds the JSON representation of the model.
	 */
	private JSONObject jsonModel;

	/**
	 * Constructs an empty Model. An empty Model has no fields.
	 */
	public Model() {
		jsonModel = new JSONObject();
	}

	/**
	 * Constructs a Model from a source JSON text string.
	 *
	 * @param json a JSON string, starting with { and ending with }
	 * @throws JSONException if there's a syntax error or duplicated field
	 */
	public Model(String json) throws JSONException {
		jsonModel = new JSONObject(json);
	}

	/**
	 * Constructs a Model from a source JSON text string performing a field name
	 * translation.
	 *
	 * @param json a JSON string, starting with { and ending with }
	 * @param transl a translation table for field names
	 * @throws JSONException if there's a syntax error or duplicated field
	 */
	public Model(String json, Map<String, String> transl)
			throws JSONException {

		this(json);
		translate(transl);
	}

	/**
	 * Returns an iterator over field names.
	 */
	public Iterator iterator() {
		return jsonModel.keys();
	}

	/**
	 * Returns {@code true} if a value is associated with the specified field
	 * name, {@code false} otherwise.
	 *
	 * @param fieldName name of field to look up
	 */
	public boolean hasField(String fieldName) {
		return jsonModel.has(fieldName);
	}

	/**
	 * Sets the value of the specified field to the passed Object.
	 *
	 * @param fieldName name of field to set
	 * @param value object to associate with passed field name
	 */
	public void setField(String fieldName, Object value) {
		try {
			jsonModel.put(fieldName, value);
		} catch (JSONException ex) {
			Log.d(TAG, ex.toString());
		}
	}

	/**
	 * Returns the value of the specified field as an {@link Object}.
	 *
	 * @param fieldName name of field whose value is to be returned
	 * @return the value of the specified field as an {@link Object} or
	 * {@code null} if no value is associated with such field
	 */
	public Object getField(String fieldName) {
		return jsonModel.opt(fieldName);
	}

	/**
	 * Returns the value of the specified field as a {@link String}.
	 *
	 * @param fieldName name of field whose value is to be returned
	 * @return the value of the specified field as a {@link String} or an
	 * empty {@link String} if no value is associated with such field
	 */
	public String getStringField(String fieldName) {
		return jsonModel.optString(fieldName);
	}
	
	/**
	 * Returns the value of the specified field as {@code boolean}.
	 * 
	 * @param fieldName name of the field whose value is to be returned
	 * @return the value of the specified field as a {@code boolean} or
	 * {@code false} if no value is associated with such field
	 */
	public boolean getBooleanField(String fieldName) {
		return jsonModel.optBoolean(fieldName);
	}
	
	/**
	 * Performs a field names translation.
	 *
	 * @param transl table of ("key","value") pairs to be used for the
	 * translation. Field names matching a "key" will be renamed to the
	 * corresponding "value"
	 */
	public void translate(Map<String, String> transl) {

		String key, val;
		Set<Map.Entry<String, String>> fields = transl.entrySet();

		try {
			for (Map.Entry<String, String> field : fields) {
				key = field.getKey();
				val = field.getValue();
				if (jsonModel.has(key)) {
					jsonModel.put(val, jsonModel.get(key));
					jsonModel.remove(key);
				}
			}
		} catch (JSONException ex) {
			Log.d(TAG, ex.toString());
		}
	}

	/**
	 * Returns the string representation of this Model in JSON format.
	 */
	@Override
	public String toString() {
		return jsonModel.toString();
	}

	/**
	 * Returns the JSON representation of the Model.
	 */
	public JSONObject toJSONObject() {
		return jsonModel;
	}

	/**
	 * Gets a unique identifier for this model in the social network.
	 * It can be a numerical ID or a screen name (string).
	 *
	 * @return the value of the field 'id' as a {@link String} or an empty
	 * {@link String} if no value is associated with such field
	 */
	public String getID() {
		String ret;
		ret = jsonModel.optString("id");
		if (ret.equals("")) {
			Log.d(TAG, "field 'id' doesn't exist.");
		}
		return ret;
	}

	/**
	 * Gets the name of the network this object belongs to.
	 *
	 * @return the value of the field 'osnap:network' as a {@link String} or an
	 * empty {@link String} if no value is associated with such field
	 */
	public String getNetwork() {
		String ret;
		ret = jsonModel.optString("osnap:network");
		if (ret.equals("")) {
			Log.d(TAG, "field 'osnap:network' doesn't exist.");
		}
		return ret;
	}
	
	// Parcelable interface-related code follows
	
	public static final Parcelable.Creator<Model> CREATOR =
			new Parcelable.Creator<Model>() {

				public Model createFromParcel(Parcel in) {
					return new Model(in);
				}

				public Model[] newArray(int size) {
					return new Model[size];
				}
			};

	public Model(Parcel in) {
		try {
			jsonModel = new JSONObject(in.readString());
		} catch (Exception ex) {
			Log.d(TAG, ex.toString());
		}
	}

	public void writeToParcel(Parcel parcel, int flag) {
		parcel.writeString(jsonModel.toString());
	}

	public int describeContents() {
		return 0;
	}
}
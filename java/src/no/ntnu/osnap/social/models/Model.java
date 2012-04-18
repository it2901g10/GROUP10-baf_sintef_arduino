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
 * A generic class for all social objects.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class Model implements Iterable, Parcelable {

	/**
	 * Used for logging purposes
	 */
	protected static final String TAG = "SocialLib";
	/**
	 * Holds the JSON representation of the model
	 */
	protected JSONObject jsonModel;

	/**
	 * Constructs an empty Model. An empty Model has no fields.
	 */
	public Model() {
		jsonModel = new JSONObject();
	}

	/**
	 * Constructs a Model from a source JSON text string.
	 *
	 * @param json a JSON string, starting with { and ending with }.
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */
	public Model(String json) throws JSONException {
		jsonModel = new JSONObject(json);
	}

	/**
	 * Constructs a Model from a JSONObject.
	 *
	 * @param object a JSONObject instance
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */
	public Model(JSONObject object) throws JSONException {
		jsonModel = new JSONObject(object.toString());
	}

	public Model(Model m) throws JSONException {
		this(m.toJSONObject());
	}

	/**
	 * Constructs a Model from a source JSON text string performing a
	 * translation.
	 *
	 * @param json a JSON string, starting with { and ending with }.
	 * @param transl a translation table for key names.
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */
	public Model(String json, Map<String, String> transl)
			throws JSONException {

		this(json);
		translate(transl);
	}

	/**
	 * Constructs a Model from a JSONObject performing a translation.
	 *
	 * @param object a JSONObject instance
	 * @param transl a translation table for key names.
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */
	public Model(JSONObject object, Map<String, String> transl)
			throws JSONException {

		this(object.toString());
		translate(transl);
	}

	/**
	 * Gets the ID of this object.
	 *
	 * @return the value of the key 'id' as a string or an empty string if the
	 * key doesn't exist.
	 */
	public String getID() {
		String ret;
		ret = jsonModel.optString("id");
		if (ret.equals("")) {
			Log.d(TAG, "key 'id' doesn't exist.");
		}
		return ret;
	}

	/**
	 * Gets the network this object belongs to.
	 *
	 * @return the value of the key 'osnap:network' as a string or an empty
	 * string if the key doesn't exist.
	 */
	public String getNetwork() {
		String ret;
		ret = jsonModel.optString("osnap:network");
		if (ret.equals("")) {
			Log.d(TAG, "key 'osnap:network' doesn't exist.");
		}
		return ret;
	}

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

	/*
	 * public static <T extends Model> ArrayList<T> makeArrayList(String
	 * response, HashMap<String, String> transl) throws JSONException {
	 *
	 * T entry; ArrayList<T> list = new ArrayList(); JSONObject json = new
	 * JSONObject(response); JSONArray jsonArray = json.getJSONArray("data");
	 * for (int i = 0; i < jsonArray.length(); i++) { //entry = new
	 * //(jsonArray.getString(i), transl); //list.add(entry); } return list;
	}
	 */
	public boolean hasKey(String key) {
		return jsonModel.has(key);
	}

	public Iterator iterator() {
		return jsonModel.keys();
	}

	public void setField(String key, Object value) {
		try {
			jsonModel.put(key, value);
		} catch (JSONException ex) {
			Log.d(TAG, ex.toString());
		}
	}

	public Object getField(String key) {
		return jsonModel.opt(key);
	}

	/**
	 * Returns the string representation of the Model in JSON format.
	 */
	@Override
	public String toString() {
		//Log.d(TAG, "Model toString() " + jsonModel.toString());
		return jsonModel.toString();
	}

	/**
	 * Returns the JSON representation of the Model.
	 */
	public JSONObject toJSONObject() {
		return jsonModel;
	}
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

	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(jsonModel.toString());
	}

	public int describeContents() {
		//throw new UnsupportedOperationException("Not supported yet.");
		return 0;
	}
}
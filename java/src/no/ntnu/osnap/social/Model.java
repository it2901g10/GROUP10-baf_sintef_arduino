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
import java.util.*;
import org.json.JSONArray;

import org.json.JSONObject;
import org.json.JSONException;

/**
 * A generic class for all social objects.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class Model implements Iterable {
	
	/**
	 * Used for logging purposes
	 */
	protected final String TAG = "SocialLib";
	
	/**
	 * Holds the JSON representation of the model
	 */
	private JSONObject jsonModel;

	/**
	 * Constructs an empty Model.
	 * An empty Model has no fields.
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
		try {
			jsonModel = new JSONObject(json);
		} catch (JSONException ex) {
			Log.d(TAG, ex.toString());
			throw(ex);
		}
	}
	
	/**
	 * Constructs a Model from a JSONObject.
	 *
	 * @param object a JSONObject instance
	 * @throws JSONException if there's a syntax error or duplicated key.
	 */
	public Model(JSONObject object) throws JSONException {
		try {
			jsonModel = new JSONObject(object.toString());
		} catch (JSONException ex) {
			Log.d(TAG, ex.toString());
			throw(ex);
		}
	}

	/**
	 * Constructs a Model from a source JSON text string
	 * performing a translation.
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
	 * Constructs a Model from a JSONObject
	 * performing a translation.
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
	 * @return the value of the key 'id' as a string or
	 * an empty string if the key doesn't exist.
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
	
	protected void translate(Map<String, String> transl) {

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

	public Iterator iterator() {
		return jsonModel.keys();
	}
	
	protected static ArrayList<? extends Model> makeArrayList(String response,
			HashMap<String, String> transl) throws JSONException {
		
		ArrayList<? extends Model> list = new ArrayList();
		JSONObject json = new JSONObject(response);
		JSONArray jsonArray = json.getJSONArray("data");
		for (int i = 0; i < jsonArray.length(); i++) {
			Model m = new Model(jsonArray.getString(i));
			//list.add((?)m);
		}
		return list;
	}
	
	//public void setField(String key, JSONObject value) {;}
			
	public String getStringField(String key) {
		return jsonModel.optString(key);
	}
	
	@Override
	public String toString() {
		return jsonModel.toString();
	}
}
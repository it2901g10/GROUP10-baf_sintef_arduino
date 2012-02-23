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
 * A generic class for all social objects.
 * @author Emanuele 'lemrey' Di Santo
 */
public class Model extends JSONObject {
	
	/** Used for logging purposes
	 * 
	 */
	protected final String APP_TAG = "SocialLib";
	
	/** Constructs an empty Model.
	 * An empty Model has no fields.
	 */
	public Model() {;}
	
	/** Constructs a Model from a source JSON text string.
	 * 
	 * @param json a JSON string, starting with { and ending with }.
	 * @throws JSONException if there's a syntax error or duplicated key.
	*/
	public Model(String json) throws JSONException {
			super(json);
	}
	
	/** Constructs a Model from a JSONObject.
	 * 
	 * @param object a JSONObject instance
	 * @throws JSONException 
	 */
	public Model(JSONObject object) throws JSONException {
		super(object.toString());
	}
	
	/** Gets the network this object belongs to.
	 * 
	 * @return the value of the key 'network' as a string
	 * or an empty string if the key doesn't exist.
	 */
	public String getNetwork () {
		String ret;
		ret = optString("network");
		if (ret.equals("")) {
			Log.d(APP_TAG, "key 'network' doesn't exist.");
		}
		return ret;
	}
	
	/** Gets the ID of this object.
	 * 
	 * @return the value of the key 'id' as a string
	 * or an empty string if the key doesn't exist.
	 */
	public String getID() {
		String ret;
		ret = optString("id");
		if (ret.equals("")) {
			Log.d(APP_TAG, "key 'id' doesn't exist.");
		}
		return ret;
	}
}

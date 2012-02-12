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

import org.opensocial.models.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;

import org.json.simple.parser.*;
import org.json.simple.JSONObject;


/**
 * @author lemrey
 */

/** Provides an easy {@link Model} bundling-extraction
 * mechanism for {@link Intent}
 */
public class Util {
	
	private final String SOCIAL_TAG = "SOCIAL";
	private final String SOCIAL_KEY;
	
	public Util() {
		SOCIAL_KEY = "social";
	}
	
	public Util(String key) {
		SOCIAL_KEY = key;
	}
	
	/** Bundles a {@link Model} into an {@link Intent}.
	 * 
	 * @param intent the intent to be bundled with the model
	 * @param model the model object that is to be bundled
	 */
	public void bundleModel(Intent intent, Model model) {
		intent.putExtra(SOCIAL_KEY, model.toJSONString());
	}
	
	/** Returns the {@link Model} bundled with the {@link Intent}
	 * or {@code null} if an error occurs.
	 * 
	 * @param intent the intent instance to extract the model from
	 * 
	 * @return the model bundled with the intent.
	 */
	public Model extractModel(Intent i) {
		
		Model m = null;
		Bundle b = null;
		
		JSONObject json = null;
		JSONParser parser;
		
		Map.Entry field;
		Iterator<Map.Entry> iter;
		
		if (i.hasExtra(SOCIAL_KEY)) {	
			b = i.getExtras();
			try {
				parser = new JSONParser();
				//Log.d(SOCIAL_TAG, "PARSING..");
				json = (JSONObject) parser.parse(b.getString(SOCIAL_KEY));
				//Log.d(SOCIAL_TAG, "OK");
				iter = json.entrySet().iterator();
				m = new Model();
				
				// iterate over the fields in the JSON object
				// to build a Model object
				while (iter.hasNext()) {
					field = iter.next();
					//Log.d(SOCIAL_TAG, "adding: " + field.getKey());
					m.put(field.getKey(), field.getValue());
				}
				
			} catch (Exception e) {
				Log.d(SOCIAL_TAG, e.toString());
			}
		}
		return m;
	}
}

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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Provides utility methods to bundle and extract
 * social classes in and from intents.
 * @author lemrey
 */
public class IntentUtil {
	
	/** used for logging purposes */
	private final String SOCIAL_TAG = "SocialLib";
	
	/** the Intent action */
	public static final String ACTION_TEXT_MSG = "osnap.social.intent.action.TEXT_MESSAGE";
	public static final String SHARE_NOTIFICATION = null;
	public static final String SHARE_POST = null;
	public static final String SHARE_COMMENT = null;
	public static final String SHARE_PERSON = null;

	/** */
	private final String SOCIAL_KEY;
	
	public IntentUtil() {
		SOCIAL_KEY = "social";
	}
	
	public IntentUtil(String key) {
		SOCIAL_KEY = key;
	}
	
	public void bundleObject(Intent intent, JSONObject object) {
		intent.putExtra(SOCIAL_KEY, object.toString());
	}
	
	public JSONObject extractObject (Intent intent) {

		Bundle b = null;
		JSONObject json = null;

		if (intent.hasExtra(SOCIAL_KEY)) {	
			b = intent.getExtras();
			try {
				Log.d(SOCIAL_TAG, "parsing intent extras..");
				json = new JSONObject(b.getString(SOCIAL_KEY));
				Log.d(SOCIAL_TAG, "build json object");
			} catch (JSONException e) {
				Log.d(SOCIAL_TAG, e.toString());
			}
		}
		return json;
	}
}
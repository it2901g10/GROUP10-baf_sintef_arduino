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
package no.ntnu.osnap.social.twitter;

import no.ntnu.osnap.social.*;
import no.ntnu.osnap.social.models.Message;

import android.util.Log;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.IOException;

import org.apache.http.*;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.ClientProtocolException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A minimal SocialService implementation for Twitter.
 * It only reads the last tweet on NTNU's public twitter page.
 * @author Emanuele 'lemrey' Di Santo
 */
public class TwitterService extends SocialService {

	private final String TAG = "Twitter-Service";
	
	@Override
	public void onCreate() {
		super.onCreate();
		setServiceName("Twitter");
	}

	@Override
	protected Response handleRequest(Request request) {

		String buf;
		Message post;
		Response response = new Response();

		switch (request.getRequestCode()) {
			case MESSAGE_STREAM: {
				// get the last tweed
				buf = readTwitterFeed();
				try {
					// parse the JSON string and build a Message from it
					JSONArray array = new JSONArray(buf);
					JSONObject json = array.getJSONObject(0);
					post = new Message();
					post.setField("message", json.opt("text"));
					response.bundle(post);
				} catch (JSONException ex) {
					Log.e(TAG, ex.toString());
					response.setStatus(Response.Status.UNKNOWN_ERROR);
				}
			}
			break;

			default: {
				response.setStatus(Response.Status.NOT_SUPPORTED);
			} break;
		}

		return response;
	}

	/**
	 * Read the latest tweet on NTNU's public profile.
	 */
	public String readTwitterFeed() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(
				"http://twitter.com/statuses/user_timeline.json?"
				+ "&trim_user=true&include_entities=false&include_rts=false"
				+ "&exclude_replies=true&screen_name=ntnu&count=1");
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(TAG, "Failed to download file");
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return builder.toString();
	}
}

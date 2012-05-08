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
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.ntnu.osnap.social;

import android.os.*;
import android.util.Log;
import android.os.Message;

import no.ntnu.osnap.social.models.Model;

import org.json.JSONException;

/**
 * oSNAP class representing a request made by a {@link Prototype} to a
 * {@link SocialService} to fetch/send data from/to social networks.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class Request {

	/**
	 * Used for logging purposes.
	 */
	private final static String TAG = "Social-Lib";
	private Bundle mBundle;

	/**
	 * Possible request codes.
	 */
	public static enum RequestCode {

		/**
		 * Request to obtain the logged-in user. Social services shall bundle a
		 * Person model with their response.
		 */
		SELF,
		/**
		 * Request to obtain a user's data. A model representing the Person
		 * whose data is to be retrieved must be bundled with the request.
		 * Social services shall bundle a Person model in their response.
		 */
		PERSON_DATA,
		/**
		 * Request to obtain the list of fiends of a user. If no model is
		 * supplied, then the list of friends of the logged-in user is returned.
		 * Social services shall bundle one or more Person models with their
		 * response.
		 */
		FRIENDS,
		/**
		 * Request to obtain the list of groups a user belongs to. If no model
		 * is supplied, then the list of groups the logged-in user belongs to is
		 * returned. Social services shall bundle one or more Group models with
		 * their response.
		 */
		GROUPS,
		/**
		 * Request to obtain a group's data. The group whose data is to be
		 * retrieved must be bundled with the request. Social service shall
		 * bundle one Group model.
		 */
		GROUP_DATA,
		/**
		 * Request to obtain the list of members of a group. The group whose
		 * members are to be retrieved must be bundled with the request. Social
		 * services shall bundle one or more Person models with their response.
		 */
		GROUP_MEMBERS,
		/**
		 * Request to obtain the list of messages posted in a group. The group
		 * whose feed is to be retrieved must be bundled with the request.
		 * Social services shall bundle one or more Message in their response.
		 */
		GROUP_FEED,
		/**
		 * Request to obtain the list of messages posted by a user. If no model
		 * is supplied then the list of messages posted by the logged-in user is
		 * returned. Social services shall bundle one or more Message models
		 * with their response.
		 */
		MESSAGES,
		/**
		 * Request to obtain a user's message stream (the timeline in Facebook).
		 * If no model is supplied then the message stream of the logged-in user
		 * is returned. Social services shall bundle one or more Message models
		 * in their response.
		 */
		MESSAGE_STREAM,
		MESSAGE_DATA,
		/**
		 * Request to obtain the list of notifications for the logged-in user.
		 * Social services shall bundle one or more Notification models with
		 * their response.
		 */
		NOTIFICATIONS,
		/**
		 * Request to obtain a notification's data. The notification whose data
		 * is to be retrieved must be bundled with the request. Social services
		 * shall bundle one Notification model with their response.
		 */
		NOTIFICATION_DATA,
		
		// POST requests

		/**
		 * Request to post a message on a user home page (timeline in Facebook).
		 * If no model is supplied with the request, then the message is posted
		 * on the logged-in user home page. Parameters as required by the
		 * network.
		 */
		POST_MESSAGE,
		/**
		 * Request to post a message on a Group page. The group on whose page
		 * post a message must be bundled with the request. Parameters as
		 * required by the network.
		 */
		POST_GROUP_MESSAGE
	}

	private Request() {
		mBundle = new Bundle();
	}

	/**
	 * Constructs a {@link Request} from an incoming {@link Message}.
	 *
	 * @param msg the message from which to build the {@link Request}
	 */
	public static Request fromMessage(Message msg) {

		Bundle bundle;
		Request ret = new Request();

		bundle = (Bundle) msg.obj;

		if (bundle != null) {
			ret.mBundle = bundle;
		} else {
			Log.e(TAG, "fromMessage(): null request bundle");
		}

		return ret;
	}

	/**
	 * Constructs a request with a given {@link RequestCode}.
	 *
	 * @param reqCode the code of the request
	 */
	public Request(RequestCode reqCode) {

		mBundle = new Bundle();
		mBundle.putString("request-code", reqCode.name());
	}

	/**
	 * Constructs a request and bundles a model with it.
	 *
	 * @param reqCode the code of the request
	 * @param model the model to be bundled
	 */
	public Request(RequestCode reqCode, Model model) {

		mBundle = new Bundle();

		mBundle.putString("request-code", reqCode.name());

		if (model != null) {
			mBundle.putString("model", model.toString());
		}
	}

	/**
	 * Constructs a request and bundles a model and parameters with it.
	 *
	 * @param reqCode the request code
	 * @param model model to be bundled with the request
	 * @param params parameters for the request
	 */
	public Request(RequestCode reqCode, Model model, Bundle params) {

		mBundle = new Bundle();
		mBundle.putString("request-code", reqCode.name());

		if (model != null) {
			mBundle.putString("model", model.toString());
		}

		if (params != null) {
			mBundle.putBundle("params", params);
		}
	}

	/**
	 * Returns the {@link RequestCode} of this request.
	 */
	public RequestCode getRequestCode() {
		String buf = mBundle.getString("request-code");
		return RequestCode.valueOf(buf);
	}

	/**
	 * Returns the {@link Model} bundled in this request or {@code null} if none
	 * exist.
	 */
	public Model getModel() {
		Model ret = null;
		try {
			if (mBundle.containsKey("model")) {
				ret = new Model((mBundle.getString("model")));
			}
		} catch (JSONException ex) {
			Log.d(TAG, ex.toString());
		}
		return ret;
	}

	/**
	 * Returns the parameters associated with this request as a {@link Bundle} a
	 * or an {@code EMPTY} {@link Bundle} if none exist.
	 */
	public Bundle getParams() {
		Bundle ret = new Bundle(Bundle.EMPTY);
		if (mBundle.containsKey("params")) {
			ret = mBundle.getBundle("params");
		}
		return ret;
	}

	/**
	 * Returns the representation of this request as a {@link Bundle}.
	 */
	public Bundle getBundle() {
		return mBundle;
	}
}

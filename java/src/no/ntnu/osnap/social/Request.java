/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.osnap.social;

import no.ntnu.osnap.social.models.Model;
import android.os.*;
import android.util.Log;
import android.os.Message;

import org.json.JSONException;

/**
 * oSNAP class representing a request to fetch or send data to a social
 * network. Requests are sent by prototypes to social services.
 * 
 * @author Emanuele 'lemrey' Di Santo
 */
public class Request {

	/**
	 * Used for logging purposes.
	 */
	private final static String TAG = "Social-Lib";
	
	public static enum RequestCode {
		/**
		 * Request to obtain the logged in user.
		 * No additional parameters required.
		 * SocialServices handling this request should bundle a {@link Person}
		 * model in their response.
		 */
		SELF,
		/**
		 * Request to obtain a Person's data.
		 * Prototypes shall include the ID of the Person whose
		 * data is to be retrieved.
		 * SocialServices handling this request should bundle a {@link Person}
		 * model in their response.
		 */
		PERSON_DATA,
		/**
		 * Request to obtain a friend list of the logged in user.
		 * No additional parameters required.
		 * SocialServices handling this request should bundle one or more
		 * {@link Person} models in their response.
		 */
		FRIENDS,
		/**
		 * Request to obtain a list of the groups the logged in user belongs to.
		 * No additional parameters required.
		 * SocialServices handling this request should bundle one or more
		 * {@link Group} models in their response.
		 */
		GROUPS,
		/**
		 * Request to obtain a Group's data.
		 * The SocialService response to this request should bundle one Group
		 * model.
		 * 
		 */
		GROUP_DATA,
		GROUP_MEMBERS,
		GROUP_FEED,
		/**
		 * Request to obtain a list of messages sent by the logged in user.
		 * No additional parameters required.
		 * SocialServices handling this request should bundle one or more
		 * {@link Message} models in their response.
		 */
		MESSAGES,
		MESSAGE_DATA,
		NOTIFICATIONS,
		/**
		 * Request to obtain a list of notifications for the logged in user.
		 * No additional parameters required.
		 * SocialServices handling this request should bundle one or more
		 * {@link Notification} models in their response.
		 * As of now, it is still not implemented.
		 */
		NOTIFICATION_DATA,
		
		// POST requests
		
		/**
		 * Request to post a public message in the social network.
		 */
		POST_MESSAGE
	}
	
	private Bundle mBundle;
	
	private Request() {
		mBundle = new Bundle();
	}
	
	/**
	 * Constructs a {@link Request} from an incoming {@link Message}.
	 * @param msg the message from which to build the {@link Request}
	 */
	public static Request fromMessage(Message msg) {
		
		Bundle bundle;
		Request ret = new Request();
			
		bundle = (Bundle)msg.obj;
		
		if (bundle != null) {			
			ret.mBundle = bundle;
		} else {
			Log.e(TAG, "fromMessage(): null bundle");
		}
		
		return ret;
	}
	
	/**
	 * Constructs a request
	 * @param reqCode the code of the request
	 */
	public static Request obtain(RequestCode reqCode) {
		return obtain(reqCode, (Model)null);
	}
	
	public static Request obtain(RequestCode reqCode, Bundle param) {
		return obtain(reqCode, null, param);
	}

	/**
	 * Constructs a request and bundles a model
	 * @param reqCode
	 * @param model
	 * @return 
	 */
	public static Request obtain(RequestCode reqCode, Model model) {

		Bundle bundle = new Bundle();
		Request request = new Request();

		bundle.putString("request-code", reqCode.name());
		
		if (model != null)
			bundle.putString("model", model.toString());

		request.mBundle = bundle;

		return request;
	}
	
	/**
	 * Constructs a request.
	 * 
	 * @param reqCode the request code
	 * @param model
	 * @param params parameters of the request to be sent to the social service
	 */
	public static Request obtain(RequestCode reqCode, Model model, Bundle params) {
		
		Bundle bundle = new Bundle();
		Request request = new Request();

		bundle.putString("request-code", reqCode.name());
		
		if (model != null)
			bundle.putString("model", model.toString());
		
		bundle.putBundle("params", params);

		request.mBundle = bundle;

		return request;
	}

	/**
	 * Returns the {@link RequestCode} of this request.
	 * @return 
	 */
	public RequestCode getRequestCode() {
		String buf = mBundle.getString("request-code");
		return RequestCode.valueOf(buf);
	}

	/**
	 * 
	 */
	public String getNetwork() {
		return mBundle.getString("network");
	}

	/**
	 * Returns the {@link Model} bundled in this request.
	 */
	public Model getModel() {
		Model ret = null;
		try {
			ret = new Model((mBundle.getString("model")));
		} catch (JSONException ex) {
			Log.d(TAG, ex.toString());
		}
		return ret;
	}

	/**
	 * Returns the parameters associated with this request as a {@link Bundle}.
	 */
	public Bundle getParams() {
		return mBundle.getBundle("params");
	}
	
	/**
	 * Returns the representation of this request as a {@link Bundle}.
	 */
	public final Bundle getBundle() {
		return mBundle;
	}
}

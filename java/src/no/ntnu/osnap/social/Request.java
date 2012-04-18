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
 *
 * @author lemrey
 */
public class Request {

	private final static String TAG = "Social-Lib";
	
	public static enum RequestCode {
		SELF,
		PERSON_DATA,
		FRIENDS,
		GROUPS,
		GROUP_DATA,
		GROUP_MEMBERS,
		GROUP_FEED,
		MESSAGES,
		MESSAGE_DATA,
		POST_MESSAGE,
		NOTIFICATIONS,
		NOTIFICATION_DATA
	}
	
	private Bundle mBundle;
	
	private Request() {
		mBundle = new Bundle();
	}
	
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
	
	public static Request obtain(RequestCode reqCode) {
		return obtain(reqCode, (Model)null);
	}
	
	public static Request obtain(RequestCode reqCode, Bundle param) {
		return obtain(reqCode, null, param);
	}

	public static Request obtain(RequestCode reqCode, Model model) {

		Bundle bundle = new Bundle();
		Request request = new Request();

		bundle.putString("request-code", reqCode.name());
		
		if (model != null)
			bundle.putString("model", model.toString());

		request.mBundle = bundle;

		return request;
	}
	
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

	public RequestCode getRequestCode() {
		String buf = mBundle.getString("request-code");
		return RequestCode.valueOf(buf);
	}

	public String getNetwork() {
		return mBundle.getString("network");
	}

	public Model getModel() {
		Model ret = null;
		try {
			ret = new Model((mBundle.getString("model")));
		} catch (JSONException ex) {
			Log.d(TAG, ex.toString());
		}
		return ret;
	}

	
	public Bundle getParams() {
		return mBundle.getBundle("params");
	}
	public final Bundle getBundle() {
		return mBundle;
	}
}

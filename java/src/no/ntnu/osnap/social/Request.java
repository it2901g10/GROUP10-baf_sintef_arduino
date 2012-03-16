/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.osnap.social;

import android.os.Bundle;
import android.util.Log;
import org.json.JSONException;

/**
 *
 * @author lemrey
 */
public class Request {
	
	private final String APP_TAG = "SocialLib";
	
	private Bundle bundle;
	
	public static Request obtain(Model model, int code) {
		
		Bundle b = new Bundle();
		Request request = new Request();
		
		b.putInt("req-id", 0);
		b.putInt("request-code", code);
		b.putString("network", model.getNetwork());
		b.putString("model-class", "");
		b.putString("model", model.toString());
		
		request.bundle = b;
		
		return request;
	}
	
	public int getReqID() {
		return bundle.getInt("req-id");
	}
	
	public int getRequestCode() {
		return bundle.getInt("request-code");
	}
	
	public String getNetwork() {
		return bundle.getString("network");
	}
	
	public Model getModel() {
		Model m = null;
		try {
			m = new Model (bundle.getString("model"));
		} catch (JSONException ex) {
			Log.d(APP_TAG, ex.toString());
		}
		return m;
	}
	
	public final Bundle getBundle() {
		return bundle;
	}
}

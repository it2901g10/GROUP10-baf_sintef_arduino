/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.osnap.social;

import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import android.os.Message;
import android.os.Parcelable;
import no.ntnu.osnap.social.models.Model;

/**
 *
 * @author lemrey
 */
public class Response<T extends Model> {

	private final static String TAG = "Social-Lib";
	
	private Bundle mBundle;
	private ArrayList<String> mModels;
	private ArrayList<T> mTModels;

	public static Response fromMessage(Message msg) {
		Response ret = new Response();
		ret.mBundle = (Bundle) msg.obj;
		return ret;
	}

	public Response() {
		mBundle = new Bundle();
		mModels = new ArrayList<String>();
		mBundle.putStringArrayList("models", mModels);
		mTModels = new ArrayList<T>();
		mBundle.putParcelableArrayList("tmodels", mTModels);
	}

	public void bundle(String model) {
		mBundle.getStringArrayList("models").add(model);
	}
	
	public void bundle(T model) {
		mBundle.getParcelableArrayList("tmodels").add(model);
	}

	public void bundle(ArrayList<String> modelList) {
		mBundle.putStringArrayList("models", modelList);
	}

	public String getModel() {
		String ret;
		ret = mBundle.getStringArrayList("models").get(0);
		return ret;
	}
	
	public T getTModel() {
		return (T) mBundle.getParcelableArrayList("tmodels").get(0);
	}
	
	public ArrayList<T> getTModelArrayList() {
		return mBundle.getParcelableArrayList("tmodels");
	}

	public ArrayList<String> getModelArrayList() {
		ArrayList<String> ret;
		ret = mBundle.getStringArrayList("models");
		return ret;
	}

	public Bundle getBundle() {
		return mBundle;
	}
}

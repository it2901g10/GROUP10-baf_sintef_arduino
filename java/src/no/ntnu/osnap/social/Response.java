/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.osnap.social;

import android.os.Bundle;
import java.util.ArrayList;
import android.os.Message;
import android.os.Parcelable;
import no.ntnu.osnap.social.models.Model;

/**
 * oSNAP class representing a SocialService response.
 * 
 * @author Emanuele 'lemrey' Di Santo
 */
public class Response {

	private final static String TAG = "Social-Lib";
	
	public enum Status {
		COMPLETED,
		NOT_SUPPORTED,
		ERROR
	};
	
	private Bundle mBundle;
	private ArrayList<? extends Model> mModels;

	public static Response fromMessage(Message msg) {
		Response ret = new Response();
		ret.mBundle = (Bundle) msg.obj;
		return ret;
	}

	/**
	 * Constructs a response.
	 * The status of the response is set to {@code Status.COMPLETED}.
	 */
	public <T extends Model> Response() {
		mBundle = new Bundle();
		mModels = new ArrayList<T>();
		mBundle.putParcelableArrayList("models", mModels);
		mBundle.putString("status", Status.COMPLETED.name());
	}
	
	public <T extends Model> void bundle(T model) {
		mBundle.getParcelableArrayList("models").add(model);
	}
	
	public Status getStatus() {
		return Status.valueOf(mBundle.getString("status"));
	}
	
	/**
	 * Sets the status of this response.
	 * If not set otherwise, the status of any response is
	 * {@code Status.COMPLETED}. Other status should be specified when
	 * appropriate.
	 * @param status the status to be set
	 */
	public void setStatus(Status status) {
		mBundle.putString("status", status.name());
	}
	
	/**
	 * Extracts the Model bundled with the response.
	 * @return 
	 */
	public <T extends Model> T getModel() {
		return (T) mBundle.getParcelableArrayList("models").get(0);
	}
	
	/**
	 * Extracts the Models bundled with the response.
	 * @return an list of all the models bundled.
	 */
	public <T extends Model> ArrayList<T> getModelArrayList() {
		return mBundle.getParcelableArrayList("models");
	}

	public Bundle getBundle() {
		return mBundle;
	}
}

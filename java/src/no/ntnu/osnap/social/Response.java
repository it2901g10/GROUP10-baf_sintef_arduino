/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.osnap.social;

import android.os.Bundle;
import java.util.ArrayList;
import android.os.Message;
import no.ntnu.osnap.social.models.Model;

/**
 * oSNAP class representing a SocialService response.
 * 
 * @author Emanuele 'lemrey' Di Santo
 */
public class Response {

	private final static String TAG = "Social-Lib";
	
	public enum Status {
		/**
		 * Status indicating that the corresponding {@link Request} was
		 * correctly processed.
		 */
		COMPLETED,
		/**
		 * Status indicating that the corresponding {@link Request} is
		 * not supported by the SocialService.
		 */
		NOT_SUPPORTED,
		/**
		 * Status indicating an error in the processing of the corresponding
		 * {@link Request}.
		 */
		ERROR
	};
	
	private Bundle mBundle;
	private ArrayList<? extends Model> mModels;

	/**
	 * Constructs a Response from a {@link Message}.
	 * @param msg
	 */
	public static Response fromMessage(Message msg) {
		Response ret = new Response();
		ret.mBundle = (Bundle) msg.obj;
		return ret;
	}

	/**
	 * Constructs a response. When created, the status of a response is set
	 * to {@code Status.COMPLETED}.
	 */
	public <T extends Model> Response() {
		mBundle = new Bundle();
		mModels = new ArrayList<T>();
		mBundle.putParcelableArrayList("models", mModels);
		mBundle.putString("status", Status.COMPLETED.name());
	}
	
	/**
	 * Bundles a {@link Model} subclass in the response.
	 * @param <T> a subclass of {@link Model}
	 * @param model the model to be bundled
	 */
	public <T extends Model> void bundle(T model) {
		mBundle.getParcelableArrayList("models").add(model);
	}
	
	/**
	 * Returns the status of this response, which indicates if the corresponding
	 * request was carried out properly or not.
	 */
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
	
	public void setNetwork(String network) {
		mBundle.putString("network", network);
	}
	
	/**
	 * Returns the network.
	 * @return 
	 */
	public String getNetwork() {
		String ret = "";
		if (mBundle.containsKey("network")) {
			ret = mBundle.getString("network");
		}
		return ret;
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

	/**
	 * 
	 * @return 
	 */
	public Bundle getBundle() {
		return mBundle;
	}
}

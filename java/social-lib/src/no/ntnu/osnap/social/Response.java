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

import android.os.Bundle;
import java.util.ArrayList;
import android.os.Message;
import no.ntnu.osnap.social.models.Model;

/**
 * oSNAP class representing a social service data response; encapsulates all
 * information returned by a {@link SocialService} after processing the {@link Prototype}
 * request.
 *
 * @author Emanuele 'lemrey' Di Santo
 */
public class Response {

	/**
	 * Used for logging purposes.
	 */
	private final static String TAG = "Social-Lib";

	private Bundle mBundle;
	private ArrayList<? extends Model> mModels;
	
	/**
	 * Various response statuses.
	 */
	public enum Status {

		/**
		 * The {@link Request} was correctly processed.
		 */
		COMPLETED,
		/**
		 * The {@link Request} was missing some mandatory parameters.
		 */
		MISSING_PARAMETERS,
		/**
		 * The {@link Request} is not supported by the social service.
		 */
		NOT_SUPPORTED,
		/**
		 * An unknown error has occurred while processing the {@link Request}.
		 */
		UNKNOWN_ERROR
	};

	/**
	 * Helper function.
	 * Constructs a Response from a {@link Message}.
	 *
	 * @param msg the source message
	 */
	public static Response fromMessage(Message msg) {
		Response ret = new Response();
		ret.mBundle = (Bundle) msg.obj;
		return ret;
	}

	/**
	 * Constructs a response.
	 * When created, the status of a response is set to {@code Status.COMPLETED}.
	 */
	public <T extends Model> Response() {
		mBundle = new Bundle();
		mModels = new ArrayList<T>();
		mBundle.putParcelableArrayList("models", mModels);
		mBundle.putString("status", Status.COMPLETED.name());
	}

	/**
	 * Bundles a {@link Model} subclass with the response.
	 *
	 * @param <T> a subclass of {@link Model}
	 * @param model the model to be bundled
	 */
	public <T extends Model> void bundle(T model) {
		mBundle.getParcelableArrayList("models").add(model);
	}

	/**
	 * Returns the status of this response.
	 */
	public Status getStatus() {
		return Status.valueOf(mBundle.getString("status"));
	}

	/**
	 * Sets the status of this response. If not set otherwise, the status of any
	 * response is {@code Status.COMPLETED}. Other status should be specified when
	 * appropriate.
	 *
	 * @param status the status to be set
	 */
	public void setStatus(Status status) {
		mBundle.putString("status", status.name());
	}

	
	public void setNetwork(String network) {
		mBundle.putString("network", network);
	}

	/**
	 * Returns the social network that create this response,
	 * or an empty {@code String} if not set.
	 */
	public String getNetwork() {
		String ret = "";
		if (mBundle.containsKey("network")) {
			ret = mBundle.getString("network");
		}
		return ret;
	}

	/**
	 * Returns a single Model bundled with the response.
	 */
	public <T extends Model> T getModel() {
		return (T) mBundle.getParcelableArrayList("models").get(0);
	}

	/**
	 * Returns an ArrayList of Models bundled with the response.
	 *
	 * @return an ArrayList of all the models bundled.
	 */
	public <T extends Model> ArrayList<T> getModelArrayList() {
		return mBundle.getParcelableArrayList("models");
	}

	/**
	 * Returns a representation of this response as a {@link Bundle}.
	 */
	public Bundle getBundle() {
		return mBundle;
	}
}

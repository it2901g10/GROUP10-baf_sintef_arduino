/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.osnap.social;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

/**
 *
 * @author lemrey
 */
public class Response implements Parcelable {
	
	private Bundle bundle;
	private String TAG;
	
	public Response() {
		bundle = new Bundle();
	}
	
	public <T extends Model> void bundle(T model) {
		bundle.putString("model", model.toString());
	}
	
	public <T extends Model> void bundle(ArrayList<T> modelList) {
		//bundle.putStringArray("model", );
	}
	
	public <T extends Model> void bundle(T[] modelArray) {
		//bundle.putStringArray("model", );
	}
	
	
	/*public void bundleArray(String[] data) {
		bundle.putStringArray("model", data)
	}*/
	
	public <T extends Model> T getModel() {
		T ret = null;
		try {
			 ret = (T) new Model(bundle.getString("model"));
		} catch (JSONException ex) {
			Log.d(TAG, ex.toString());
		}
		return ret;
	}
	
	/*public <T extends Model> T[] getModelArray() {
		T[] ret = null;
		try {
			ret = (T[]) new Model[2];
		} catch (JSONException ex) {
			Log.d(TAG, ex.toString());
		}
	}*/

	public int describeContents() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void writeToParcel(Parcel arg0, int arg1) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}

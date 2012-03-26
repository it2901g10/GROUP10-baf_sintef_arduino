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
package no.ntnu.osnap.social.tshirt;

import no.ntnu.osnap.social.ISocialService;

import android.app.Activity;
import java.util.ArrayList;


/**
 * A singleton class used to realize a communication
 * between the Tshirt service and application.
 * 
 * @author Emanuele 'lemrey' Di Santo
 */
public class Tshirt {
	
	// this is required by the ComLib to init a BT connection
	private static Activity mActivity;
	
	private static EventListener mListener;
	
	// here we store the service names
	private static ArrayList<String> mServiceNames = new
			ArrayList<String>();
	
	// here we store our interfaces to the remote services
	// we may want to connect to more than one social service	
	private static ArrayList<ISocialService> mServiceList = new
			ArrayList<ISocialService>();
	
	public static void setActivity(Activity activity) {
		mActivity = activity;
	}
	
	public static Activity getActivity() {
		return mActivity;
	}
	
	/**
	 * Returns a list of remote interfaces.
	 */
	public static ArrayList<ISocialService> getServiceList() {
		return mServiceList;
	}
	
	/**
	 * Returns a list of services we're connected to.
	 */
	public static ArrayList<String> getServiceNames() {
		return mServiceNames;
	}
	
	/*public static interface EventListener {
		public void onConnected(final String className);
		public void onDisconnected(final String className);
	}*/
	
	/*public static void setEventListener(EventListener listener) {
		mListener = listener;
	}
	
	public  static EventListener getEventListener() {
		return mListener;
	}*/
}

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
package no.ntnu.osnap.social.facebook;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

/**
 * A singleton class used to realize the communication
 * between the Facebook service and application.
 * 
 * @author Emanuele 'lemrey' Di Santo
 */
public class FB {
	
	private static final String APP_ID = "322276144483780";
		
	private static final Facebook mFacebook = new Facebook(APP_ID);
	private static final AsyncFacebookRunner mAsyncRunner = new
			AsyncFacebookRunner(mFacebook);
	
	public static Facebook getInstance() {
		return mFacebook;
	}
	
	public static AsyncFacebookRunner getAsyncInstance() {
		return mAsyncRunner;
	}
	
}

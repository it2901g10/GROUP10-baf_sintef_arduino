/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.osnap.social.facebook;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

/**
 *
 * @author lemrey
 */
public class FB {
	
	private static final String APP_ID = "322276144483780";
		
	private static final Facebook mFacebook = new Facebook(APP_ID);
	private static final AsyncFacebookRunner mAsyncRunner = new
			AsyncFacebookRunner(mFacebook);
	
	public static Facebook getIstance() {
		return mFacebook;
	}
	
	public static AsyncFacebookRunner getAsyncInstance() {
		return mAsyncRunner;
	}
	
}

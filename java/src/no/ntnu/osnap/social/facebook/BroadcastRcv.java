/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.osnap.social.facebook;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 *
 * @author lemrey
 */
public class BroadcastRcv extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Log.d("Broadcast", "RECEIVED!!");
		Intent resp = new Intent("android.intent.action.SOCIAL");
		Bundle b = arg1.getExtras();
		resp.setComponent((ComponentName)b.get("component"));
		//resp.setClassName(b.getString("pkg"),b.getString("class"));
		resp.putExtra("text", "FUCK YEAH");
		arg0.startService(resp);
		//startService(resp);
	}
	
}

package no.ntnu.osnap.social.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;

import no.ntnu.osnap.social.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//

public class TestActivity extends Activity
{
	private final String APP_TAG = "TestApp";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		Intent intent = getIntent();
		
		if (intent != null) {
			
			Post p = null;
			IntentUtil util = new IntentUtil();

			try {
				p = new Post(util.extractObject(intent));
			} catch (JSONException ex) {
				Log.d(APP_TAG, ex.toString());
			}
			
			Log.d(APP_TAG, "Post Message: " + p.getMessage());
			
			ArrayList<Person> likes  = p.getLikesAsList();
			Log.d(APP_TAG, p.getLikesCount() + " people liked it.");
		
			for (int i = 0; i < likes.size(); i++)
				Log.d(APP_TAG, likes.get(i).getName() + " liked it.");
			
			ArrayList<Comment> comments = p.getCommentsAsList();
			
			for (int i = 0; i < comments.size(); i++) {
				Comment comm = comments.get(i);
				Person sender = comm.getSenderAsPerson();
				Log.d(APP_TAG, sender.getName() + " said:");
				Log.d(APP_TAG, comm.getMessage());
			}
		}
    }
}

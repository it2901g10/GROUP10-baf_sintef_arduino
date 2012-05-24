package no.ntnu.osnap.social.twitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TwitterActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		startService(new Intent(getBaseContext(), TwitterService.class));
    }
}

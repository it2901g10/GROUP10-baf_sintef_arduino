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

package no.ntnu.osnap.social.receiver;

import no.ntnu.osnap.social.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.opensocial.models.Model;

/**
 * @author Emanuele 'Lemrey' Di Santo
 */
public class Receiver extends Activity
{
	private final String TAG = "SOCIALR";
	
	private TextView mTextView;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		mTextView = (TextView) findViewById(R.id.text);
		
		Intent i = getIntent();
		if (i != null) {
			Util util = new Util();
			//Log.d("SOCIALTEST", "EXTRACT MODEL BEGIN!");
			Model m = util.extractModel(i);

			String msg = (String) m.getField("givenName");
			//Log.d("SOCIALTEST", msg);

			mTextView.setText("Hei " + msg + "!");
		}
    }
}

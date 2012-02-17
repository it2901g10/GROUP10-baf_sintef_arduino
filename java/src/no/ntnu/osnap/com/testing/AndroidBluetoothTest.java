/*
* Copyright 2012 NTNU
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/
package no.ntnu.osnap.test;

import no.ntnu.osnap.com.BluetoothConnection;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class AndroidBluetoothTest extends Activity {
    private TextView GUI;
    	    
    private void print(String line) {
		GUI.append(line);
    	Log.v("BluetoothTest", line);
    }
    
	private void printLine(String line) {
		print(line + "\n");
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	//Initialize GUI, do first
        super.onCreate(savedInstanceState);
        GUI = new TextView(this);
        setContentView(GUI);
    }
    
    @Override
    public void onStart() {
    	super.onStart();

		setTitle("program is running");
		
		try {
			BluetoothConnection con = new BluetoothConnection("THIS_IS_THE_MAC_ADDRESS");
			printLine("Connection established! " + con.getAddress());
		} catch (Exception e) {
			printLine("Could not establish connection: " + e.getMessage());
		}
    }    

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    
}

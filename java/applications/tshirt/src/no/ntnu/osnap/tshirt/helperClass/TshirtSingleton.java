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
package no.ntnu.osnap.tshirt.helperClass;

import android.content.Context;
import android.widget.Toast;
import no.ntnu.osnap.tshirt.R;

public class TshirtSingleton{

    private static TshirtSingleton instance = null;

    //Database
    //BT Connection
    private Context context;
    public RulesDB database;



    public TshirtSingleton(Context applicationContext) {
        context = applicationContext;
        database = new RulesDB(context);
        database.open();

    }

    public static TshirtSingleton getInstance(Context context){
        if(instance == null){
            instance = new TshirtSingleton(context.getApplicationContext());
        }
        return instance;
    }
    //Connection

    public void toggleArduinoConnection() {
        Toast.makeText(context, "toggleArduinoConnection() is not yet implemented", Toast.LENGTH_SHORT).show();
    }
    
    public void sendToArduino(String output, String device){
        
        L.i("Sending data " + output + " to " + device + " on Arduino");
        
        if(device.equals(context.getString(R.string.outputDISPLAY))){
           sendToLCDArduino(output); 
        }
        else if(device.equals(context.getString(R.string.outputLED))){
            sendToLEDArduino(output);   
        }
        else if(device.equals(context.getString(R.string.outputVIBRATOR))){
            sendToVibratorArduino(output);
        }
        else if(device.equals(context.getString(R.string.outputSPEAKER))){
            sendToSpeakerArduino(output);   
        }
        else{
            L.e("Err, Unknown output");
        }

    }
    
    private void sendToLEDArduino(String string){
        
    }
    private void sendToLCDArduino(String text){
        
    }
    private void sendToVibratorArduino(String text){

    }
    private void sendToSpeakerArduino(String text){

    }
}

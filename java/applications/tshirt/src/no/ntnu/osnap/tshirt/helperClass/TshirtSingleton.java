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
        connectBT();

    }

    private void connectBT() {
//        ConnectionListener listener = new ConnectionListener() {
//            @Override
//            public void onConnect(BluetoothConnection bluetoothConnection) {
//            }
//            @Override
//            public void onConnecting(BluetoothConnection bluetoothConnection) {
//            }
//            @Override
//            public void onDisconnect(BluetoothConnection bluetoothConnection) {
//            }
//        };
//        con = new BluetoothConnection("00:10:06:29:00:48", context ,listener);

    }

    public void sendToLEDArduino(String string){
        
    }
    public void sendToLCDDiplayArduino(String text){
        
    }
    public void sendToVibratorArduino(String text){

    }
    public void sendToSpeakerArduino(String text){

    }
}

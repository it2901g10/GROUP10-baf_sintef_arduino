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
package testing;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.text.DateFormatter;
import source.ComLayer;
import source.Protocol;

public class ProtocolTest {
    public static void main(String[] args) {
        Protocol arduino = new Protocol(new ComLayer());
        
        for (int i = 0; i < 100000000; ++i){
            //arduino.print(new String(new char[250]));
            arduino.print(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            //arduino.print(i + "s");
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                
            }*/
        }
        
        //arduino.print("ZooPark");
        //arduino.print("D");
    }
    
}

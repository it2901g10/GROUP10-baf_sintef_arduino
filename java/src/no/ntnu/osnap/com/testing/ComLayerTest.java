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

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import source.ComLayer;

/**
 *
 * @author anders
 */
public class ComLayerTest {
    public static void main(String[] args) {
        ComLayer com = new ComLayer();
        //com.sendMsg(com.text);
        Scanner in = new Scanner(System.in);
        
        while (true){
            while (in.hasNextLine()){
                try {
                    com.sendBytes(in.nextLine().getBytes());
                } catch (IOException ex) {
                    System.out.println("Send error");
                }
            }
        }
    }
}

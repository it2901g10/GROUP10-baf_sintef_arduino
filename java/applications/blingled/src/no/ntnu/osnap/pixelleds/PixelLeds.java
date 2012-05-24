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
package no.ntnu.osnap.pixelleds;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.ntnu.osnap.com.BluetoothConnection;
import no.ntnu.osnap.com.UnsupportedHardwareException;

public class PixelLeds extends Activity {
        BluetoothConnection bt;
        Bitmap image;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bt = null;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String action = intent.getAction();
        image = null;
        // if this is from the share menu
        if (Intent.ACTION_SEND.equals(action)) {
            if (extras.containsKey(Intent.EXTRA_STREAM)) {
                try {
                    Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
                    ContentResolver cr = getContentResolver();
                    InputStream is = cr.openInputStream(uri);
                    
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inSampleSize = 4;
                    image = BitmapFactory.decodeStream(is, null, opt);

                } catch (Exception e) {
                    Log.e(this.getClass().getName(), e.toString());
                }
            } 
        }
        else{
            
        }
        image = Pixelater.pixelate(image);
        setContentView(createContent());
        
        
        
    }
    
    public void btConnect(){
         try {
           bt = new BluetoothConnection("213", this);
        } catch (UnsupportedHardwareException ex) {
            Logger.getLogger(PixelLeds.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(PixelLeds.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public LinearLayout createContent(){
        LinearLayout view = new LinearLayout(this);
        view.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        ImageView iv = new ImageView(this);
        ViewGroup.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        iv.setImageBitmap(image);
        Button button = new Button(this);
        button.setText("Move to Arduino");
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                btConnect();
                bt.data(Pixelater.byteMap(image));
            }
        });
        
        view.addView(iv, params2);
        view.addView(button, params2);
        return view;
        
    }
    

}

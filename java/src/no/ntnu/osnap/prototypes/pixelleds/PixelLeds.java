package no.ntnu.osnap.prototypes.pixelleds;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import java.io.InputStream;

public class PixelLeds extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView iv = new ImageView(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String action = intent.getAction();
        Bitmap originalImage = null;
        // if this is from the share menu
        if (Intent.ACTION_SEND.equals(action)) {
            if (extras.containsKey(Intent.EXTRA_STREAM)) {
                try {
                    Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
                    ContentResolver cr = getContentResolver();
                    InputStream is = cr.openInputStream(uri);
                    
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inSampleSize = 4;
                    originalImage = BitmapFactory.decodeStream(is, null, opt);

                } catch (Exception e) {
                    Log.e(this.getClass().getName(), e.toString());
                }
            } 
        }
        iv.setImageBitmap(Pixelater.pixelate(originalImage));
        setContentView(iv);
    }
}

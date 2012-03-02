package no.ntnu.osnap.prototypes.pixelleds;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 *
 * @author rabba
 */
public class Pixelater {

    public static Bitmap resizeImage(Bitmap img, int newx, int newy) {
        return Bitmap.createScaledBitmap(img, newx, newy, false);

    }

    public static Bitmap pixelate(Bitmap img) {
        int newsize = img.getWidth() > img.getHeight() ? img.getWidth() : img.getHeight();
        newsize = newsize - (newsize % 5);
        Bitmap newimg = resizeImage(img, newsize, newsize);
        img.recycle();
        Bitmap dest = Bitmap.createBitmap(newsize, newsize, Bitmap.Config.ARGB_8888);
        int xsize = newsize / 5;
        int ysize = newsize / 5;
        
        for (int x = 0; x < newimg.getWidth(); x += xsize) {
            for (int y = 0; y < newimg.getHeight(); y += ysize) {

                int px = 0;

                for (int xi = 0; xi < xsize; xi++) {
                    for (int yi = 0; yi < ysize; yi++) {
                        px += newimg.getPixel(x, y);
                        px = px / 2;
                    }
                }

                for (int xi = 0; xi < xsize; xi++) {
                    for (int yi = 0; yi < ysize; yi++) {
                        dest.setPixel(x + xi, y + yi, px);
                    }
                }
            }
        }

        return dest;
    }
}

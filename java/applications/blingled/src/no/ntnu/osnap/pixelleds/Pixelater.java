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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

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
        newsize = newsize - (newsize % 3);
        Bitmap newimg = resizeImage(img, newsize, newsize);
        img.recycle();
        Bitmap dest = Bitmap.createBitmap(newsize, newsize, Bitmap.Config.ARGB_8888);
        int xsize = newsize / 3;
        int ysize = newsize / 3;
        
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
            public static byte[] byteMap(Bitmap im){
        /*making the byte array.
         * the array is shaped like this:
         * 3x3 pixels
         * ABC
         * DEF
         * GHI
         * the array is then { red A, green A, blue A, 
         *                    rB,gB,bB, rC,gC,bC,
         *                      rD.etc}
         * 
         */
        im = Pixelater.resizeImage(im, 3, 3);
        byte[] pixelData = new byte[9*3];
        int x = 0;
        for(int i=0; i<3;i++){
            for (int j = 0; j < 3; j++) {
                int pre = im.getPixel(i, j);
                
                byte r = (byte)Color.red(pre);
                byte g = (byte)Color.green(pre);
                byte b = (byte)Color.blue(pre);
                pixelData[x] = r;
                x++;
                pixelData[x] = g;
                x++;
                pixelData[x] = b;
                x++;
            }
        }
        return pixelData;
    }
}

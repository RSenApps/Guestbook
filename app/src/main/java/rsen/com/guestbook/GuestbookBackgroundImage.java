package rsen.com.guestbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import java.io.File;
import java.util.Random;

/**
 * Created by Ryan on 12/24/2014.
 */
public class GuestbookBackgroundImage {
    int x;
    int y;
    Bitmap bitmap;
    File file;
    int alpha = 15;
    int w;
    int h;
    Paint paint;
    int maxX;
    int maxY;
    View foreground;
    int rotate;
    Matrix matrix;
    Random random;
    static final int borderSize = 10;
    int alphaChange = 15; //increasing more opaque, decreasing more transparent
    public GuestbookBackgroundImage(File file, int maxX, int maxY, View foreground, Random random)
    {
        this.random = random;
        w = 300 + random.nextInt(200);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        this.file = file;
        this.maxX = maxX;
        this.maxY = maxY;
        this.foreground = foreground;
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, w);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        Bitmap bmpWithBorder = Bitmap.createBitmap(bitmap.getWidth() + borderSize * 2, bitmap.getHeight() + borderSize * 2, bitmap.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(bitmap, borderSize, borderSize, null);
        bitmap = bmpWithBorder;
        w = bitmap.getWidth();
        h = bitmap.getHeight();
        paint = new Paint(Paint.DITHER_FLAG);
        newPosition();
    }

    public void newPosition()
    {
        while (true) {
            rotate = random.nextInt(90) - 45;
            x = random.nextInt(maxX);
            y = random.nextInt(maxY);
            Rect proposed = new Rect(x, y, x+w, y+h);
            if (!isViewContains(foreground, proposed))
            {
                matrix = new Matrix();
                matrix.setRotate(rotate, bitmap.getWidth()/2, bitmap.getHeight()/2);
                matrix.postTranslate(x, y);
                break;
            }
        }
    }
    private boolean isViewContains(View view, Rect proposed) {
        int[] l = new int[2];
        view.getLocationOnScreen(l);
        int x = l[0];
        int y = l[1];
        int w = view.getWidth();
        int h = view.getHeight();

        Rect check = new Rect(x+250, y+150, x+w-150, y+h-150);

        return check.intersect(proposed);
    }
    public void updateAlpha()
    {
        if (alpha <= 0)
        {
            alphaChange = 15;
            newPosition();
            alpha = 0;
        }
        if (alpha >= 255)
        {
            alphaChange = 0;
            alpha = 255;
        }
        alpha += alphaChange;
    }
    public void redraw()
    {

        alphaChange = -15;
        alpha += alphaChange;
    }
    public void draw(Canvas c)
    {

        if (file.exists()) {
            if (alpha > 0) {
                paint.setAlpha(alpha);
                c.drawBitmap(bitmap, matrix, paint);
            }
        }
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth) {
        // Raw height and width of image
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (width > reqWidth) {

            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}

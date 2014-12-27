package rsen.com.guestbook;

import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Ryan on 12/24/2014.
 */
public class BackgroundView extends View {

    MainActivity context;
    List<GuestbookBackgroundImage> images = new ArrayList<>();
    File dir;
    int width;
    int height;
    Random random = new Random();
    int countdownToDrawNew = 5;
    Handler updateViewHandler = new Handler();
    Runnable updateViewRunnable = new Runnable() {
        @Override
        public void run() {
            countdownToDrawNew--;
            if(countdownToDrawNew<=0)
            {
                countdownToDrawNew = 40;
                drawNew();
            }
            for (GuestbookBackgroundImage image : images)
            {
                image.updateAlpha();
            }
            invalidate();

            updateViewHandler.postDelayed(updateViewRunnable, 100);
        }
    };
    private void drawNew()
    {
        File[] files = dir.listFiles();
        if (files.length > images.size())
        {
            for (final File file : files)
            {
                boolean matchFound = false;
                for (GuestbookBackgroundImage image : images)
                {
                    if (file.equals(image.file))
                    {
                        matchFound = true;
                        break;
                    }
                }
                if (!matchFound)
                {
                    if (width > 0 && height > 0) {
                        countdownToDrawNew = 5;
                        LoadImageTask task = new LoadImageTask();
                        task.execute(file);
                        break;
                    }
                }
            }
        }
        else if (images.size() > files.length) {
            Iterator<GuestbookBackgroundImage> iterator = images.iterator();
            while (iterator.hasNext())
            {
                GuestbookBackgroundImage image = iterator.next();
                if (!image.file.exists())
                {
                    iterator.remove();
                }
            }
        }
        else {

            try {
                GuestbookBackgroundImage image = images.get(0);
                images.remove(0);
                image.redraw();
                images.add(image);
            }
            catch (Exception e) {
            }
        }
    }
    private class LoadImageTask extends AsyncTask<File, Void, Object> {

        @Override
        protected Object doInBackground(File... file) {
            images.add(new GuestbookBackgroundImage(file[0], width, height, context.getCardView(), random));
            return null;
        }
    }
    public BackgroundView (MainActivity c, File dir) {
        super(c);
        context=c;
        this.dir = dir;
        updateViewRunnable.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        updateViewHandler.removeCallbacks(updateViewRunnable);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        images = new ArrayList<>();

    }
    @Override
    protected void onDraw(Canvas canvas) {
        List<GuestbookBackgroundImage> copiedList = new ArrayList<GuestbookBackgroundImage>(images);
        width = canvas.getWidth();
        height = canvas.getHeight();
        for (GuestbookBackgroundImage image : copiedList)
        {
            image.draw(canvas);
        }

        super.onDraw(canvas);
    }

}

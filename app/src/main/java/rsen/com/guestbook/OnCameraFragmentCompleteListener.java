package rsen.com.guestbook;

import android.app.Activity;

import java.io.File;

/**
 * Created by Ryan on 12/23/2014.
 */
public interface OnCameraFragmentCompleteListener {
       public void onComplete(Activity activity, int width, int height, File file);
}

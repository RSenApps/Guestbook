package rsen.com.guestbook;


import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import it.sephiroth.android.library.exif2.ExifInterface;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {


    public MessageFragment() {
        // Required empty public constructor
    }
    File file;
    EditText message;
    public static MessageFragment newInstance(File file) {

        MessageFragment fragment = new MessageFragment();
        fragment.file = file;
        fragment.setRetainInstance(true);
        return fragment;
    }
        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View rootView = inflater.inflate(R.layout.fragment_message, container, false);
            rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
            {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                           int oldRight, int oldBottom)
                {
                    v.removeOnLayoutChangeListener(this);
                    int cx = (right-left)/2 + left;
                    int cy = (bottom-top)/2 + top;

                    // get the hypothenuse so the radius is from one corner to the other
                    int radius = (int)Math.hypot(right, bottom);

                    Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
                    reveal.setInterpolator(new DecelerateInterpolator(2f));
                    reveal.setDuration(1000);
                    reveal.start();
                }
            });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        imageView.setImageBitmap(myBitmap);
        message = (EditText) view.findViewById(R.id.message);

        message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    done();
                }
                return false;
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    public void done()
    {

        if (message.getText().toString().trim().length() > 0) {
            try {
                ExifInterface exif = new ExifInterface();
                exif.readExif(file.getAbsolutePath(), ExifInterface.Options.OPTION_ALL);
                exif.setTag(exif.buildTag(ExifInterface.TAG_IMAGE_DESCRIPTION, message.getText().toString()));
                exif.writeExif(file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

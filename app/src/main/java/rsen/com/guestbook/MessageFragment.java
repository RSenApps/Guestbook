package rsen.com.guestbook;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

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
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        imageView.setImageBitmap(myBitmap);
        message = (EditText) view.findViewById(R.id.message);
        super.onViewCreated(view, savedInstanceState);
    }
    public void done()
    {

        try {
            ExifInterface exif = new ExifInterface();
            exif.readExif( file.getAbsolutePath(), ExifInterface.Options.OPTION_ALL );
            exif.setTag(exif.buildTag(ExifInterface.TAG_IMAGE_DESCRIPTION, message.getText().toString()));
            exif.writeExif(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

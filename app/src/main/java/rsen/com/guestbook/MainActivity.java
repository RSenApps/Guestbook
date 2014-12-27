package rsen.com.guestbook;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.File;


public class MainActivity extends Activity implements OnCameraFragmentCompleteListener {
    FragmentManager fragmentManager;
    FloatingActionButton done;
    FloatingActionButton cancel;
    ImageView stepImage;
    TextView title;
    TextView detail;
    Camera2BasicFragment cameraFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();
        cameraFragment = Camera2BasicFragment.newInstance(this);
        fragmentManager.beginTransaction().add(R.id.card_layout, cameraFragment).commit();
        done = (FloatingActionButton) findViewById(R.id.done);
        cancel = (FloatingActionButton) findViewById(R.id.cancel);
        stepImage = (ImageView) findViewById(R.id.stepImage);
        title = (TextView) findViewById(R.id.title);
        detail = (TextView) findViewById(R.id.detail);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "GuestBook");
        directory.mkdirs();
        new TestTask().execute();
        layout.addView(new BackgroundView(this, directory), 0);
    }
    public View getCardView()
    {
        return findViewById(R.id.card_view);
    }
    //initialize static members
    private class TestTask extends AsyncTask<Object, Void, Object> {

        @Override
        protected Object doInBackground(Object... object) {
            return null;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
    @Override
    public void onComplete(final Activity activity, final int width, final int height, final File file) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final SignFragment signFragment = SignFragment.newInstance(width, height, file, done);
                fragmentManager.beginTransaction().replace(R.id.card_layout, signFragment).commit();
                cancel.setVisibility(View.VISIBLE);
                stepImage.setImageResource(R.drawable.step2);
                cancel.setIcon(R.drawable.ic_retake);
                title.setText("Sign Your Picture");
                detail.setText("Use the stylus to sign your name");
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signFragment.done();
                        stepImage.setImageResource(R.drawable.step3);
                        title.setText("Write a Message");
                        detail.setText("Will be printed in the Guestbook");
                        cancel.setIcon(R.drawable.ic_fab_cancel);

                        final MessageFragment messageFragment = MessageFragment.newInstance(file);
                        fragmentManager.beginTransaction().replace(R.id.card_layout, messageFragment).commit();
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onComplete(activity, width, height, file);
                            }
                        });
                        done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                messageFragment.done();
                                Toast.makeText(view.getContext(), "Your message has been saved to the guestbook!", Toast.LENGTH_LONG).show();
                                fragmentManager.beginTransaction().replace(R.id.card_layout, cameraFragment).commit();
                                stepImage.setImageResource(R.drawable.step1);
                                title.setText("Take a Picture");
                                detail.setText("Press the button to begin!");
                                done.setVisibility(View.GONE);
                                cancel.setVisibility(View.GONE);

                            }
                        });
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        file.delete();
                        fragmentManager.beginTransaction().replace(R.id.card_layout, cameraFragment).commit();
                        done.setVisibility(View.GONE);
                        cancel.setVisibility(View.GONE);
                        stepImage.setImageResource(R.drawable.step1);
                        title.setText("Take a Picture");
                        detail.setText("Press the button to begin!");
                    }
                });
            }
        });

    }
}

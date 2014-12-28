package rsen.com.guestbook;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.File;
import java.util.prefs.PreferenceChangeEvent;


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
        setupUI(findViewById(R.id.layout));
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        ActionBar ab = getActionBar();
        ab.setTitle(PreferenceManager.getDefaultSharedPreferences(this).getString("guestbookname", "Test & Test's") + " Guestbook");
        ab.setSubtitle("Provided by GuestTech LLC");

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

    @Override
    public void onBackPressed() {

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("loggedin", false))
        {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("loggedin", false).commit();
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
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

    }
    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
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

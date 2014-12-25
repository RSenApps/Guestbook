package rsen.com.guestbook;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.File;


public class MainActivity extends Activity implements OnCameraFragmentCompleteListener {
    FragmentManager fragmentManager;
    FloatingActionButton done;
    FloatingActionButton cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();
        Camera2BasicFragment cameraFragment = Camera2BasicFragment.newInstance(this);
        fragmentManager.beginTransaction().add(R.id.card_view, cameraFragment).commit();
        done = (FloatingActionButton) findViewById(R.id.done);
        cancel = (FloatingActionButton) findViewById(R.id.cancel);
    }

    @Override
    public void onComplete(final Activity activity, final int width, final int height, final File file) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final SignFragment signFragment = SignFragment.newInstance(width, height, file);
                fragmentManager.beginTransaction().replace(R.id.card_view, signFragment).commit();
                done.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signFragment.done();
                        final MessageFragment messageFragment = MessageFragment.newInstance(file);
                        fragmentManager.beginTransaction().replace(R.id.card_view, messageFragment).commit();
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
                                Camera2BasicFragment cameraFragment = Camera2BasicFragment.newInstance(MainActivity.this);
                                fragmentManager.beginTransaction().replace(R.id.card_view, cameraFragment).commit();
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
                        Camera2BasicFragment cameraFragment = Camera2BasicFragment.newInstance(MainActivity.this);
                        fragmentManager.beginTransaction().replace(R.id.card_view, cameraFragment).commit();
                        done.setVisibility(View.GONE);
                        cancel.setVisibility(View.GONE);
                    }
                });
            }
        });

    }
}

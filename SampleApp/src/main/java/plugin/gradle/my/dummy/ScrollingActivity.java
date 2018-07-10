package plugin.gradle.my.dummy;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import plugin.gradle.my.R;
import plugin.gradle.my.test.ItemFragment;

public class ScrollingActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {

    private static final String TAG = "ScrollingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        ItemFragment itemFragment = new ItemFragment();
        tx.add(R.id.scrolling_content_layout, itemFragment, "ONE");
        tx.commit();

        Button b = (Button) findViewById(R.id.test_fragment_click);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(TAG, "on test click clicked");
            }
        });
        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i(TAG, "on long click!!!!!");
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DummyContent d = new DummyContent();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

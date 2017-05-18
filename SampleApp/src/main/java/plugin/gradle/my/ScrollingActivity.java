package plugin.gradle.my;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import plugin.gradle.my.test.ItemFragment;
import plugin.gradle.my.test.dummy.DummyContent;

public class ScrollingActivity extends Activity implements ItemFragment.OnListFragmentInteractionListener {

    private static final String TAG = "ScrollingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        FragmentManager fm = getFragmentManager();
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
}

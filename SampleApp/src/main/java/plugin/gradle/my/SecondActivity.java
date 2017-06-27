package plugin.gradle.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.*;

import plugin.gradle.my.dummy.DummyContent;

/**
 * Created by ZhouKeWen on 17/3/31.
 */
public class SecondActivity extends AppCompatActivity implements OnClickListener, OnLongClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        BlankFragment blankFragment = new BlankFragment();
        tx.add(R.id.id_content, blankFragment, "ONE");
        tx.commit();

        blankFragment.onLowMemory();

        Button b = (Button) findViewById(R.id.test_click);
        b.setOnClickListener(this);
        b.setOnLongClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DummyContent d = new DummyContent();
        d.sleep();
    }

    @Override
    public void onClick(View v) {
        onA();
        onB();
    }

    private void onA() {
    }

    private void onB() {
        onC();
    }

    private void onC() {
    }

    @Override
    public boolean onLongClick(View v) {
        Log.i("zkw", "on long click!!!!!");
        return true;
    }
}

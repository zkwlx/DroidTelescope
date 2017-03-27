package plugin.gradle.my;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            g2();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        gogo(100);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onSlowClick(View view) {
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            g2();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        gogo(87878787);
    }

    public int gogo(int c) {
        int a = Log.i("zkw", "hello");
        if (a > 0) {
            Log.i("zkw", ">>0");
        } else {
            Log.i("zkw", "<<0");
        }
        isT();
        return a;
    }

    public Object g2() throws IllegalAccessException {
        int a = new Random().nextInt(100);
        if (isT()) {
            Log.i("g2", "isT() true");
        } else if (a > 5) {
            Log.i("g2", "isT() false>>> " + a);
            throw new IllegalAccessException();
        }
        if (a < 5) {
            Log.i("g2", "a>>> " + a);
            return "aaa";
        } else {
            Log.i("g2", "......." + a);
        }
        try {
            throw new NullPointerException();
        } catch (NullPointerException e) {
            Log.i("g2", "NullPointerException!!!!");
        } finally {
            Log.i("g2", "finally");
        }
        Log.i("g2", ">>>>>" + a);
        return null;
    }

    private boolean isT() {
        return new Random().nextInt(10) > 5;
    }

}

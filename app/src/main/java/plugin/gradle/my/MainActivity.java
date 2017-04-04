package plugin.gradle.my;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.shit.testlibrary.TestLibraryClass;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TestLibraryClass test;

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

        Fragment f = new Fragment();
        android.app.Fragment ff = new android.app.Fragment();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            Thread.sleep(400);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i("zkw", "----------------_>>>on trim memory:"+level);
    }

    public void onSlowClick(View view) {
        Intent i = new Intent(this, SecondActivity.class);
        startActivity(i);
        //
        //        try {
        //            g2();
        //        } catch (IllegalAccessException e) {
        //            e.printStackTrace();
        //        }
        //        gogo(87878787);
        //
        //        new Thread(new Runnable() {
        //            @Override
        //            public void run() {
        //                isT("");
        //                try {
        //                    g2();
        //                } catch (IllegalAccessException e) {
        //                    e.printStackTrace();
        //                }
        //                try {
        //                    Thread.sleep(200);
        //                } catch (InterruptedException e) {
        //                    e.printStackTrace();
        //                }
        //            }
        //        }).start();
        //
        //        if (test == null) {
        //            test = new TestLibraryClass();
        //        }
        //        test.startTestt();
        //
        //        new TestLibraryClass().startTestt();
        //
        //        try {
        //            Thread.sleep(600);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }
    }

    public int gogo(int c) {
        int a = Log.i("zkw", "hello");
        if (a > 0) {
            Log.i("zkw", ">>0");
        } else {
            Log.i("zkw", "<<0");
        }
        isT(new Paint());
        isT(this);
        return a;
    }

    public Object g2() throws IllegalAccessException {
        int a = new Random().nextInt(100);
        if (isT("")) {
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

    private boolean isT(Object o) {
        Log.i("", "=======" + o);
        return new Random().nextInt(10) > 5;
    }

}

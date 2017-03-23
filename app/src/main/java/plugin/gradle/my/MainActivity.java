package plugin.gradle.my;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.IllegalFormatException;
import java.util.Objects;
import java.util.Random;

import andr.perf.monitor.CpuMonitor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        try {
//            g2();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
        gogo(100);

        new SomeTest().someM();
        new SomeTest().run();
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
//        return new Random(16).nextInt();
    }

//    public Object g2() throws IllegalAccessException {
//        int a = new Random().nextInt(100);
//        if (isT()) {
//            Log.i("zkw", "isT() true");
//        } else if (a > 5) {
//            Log.i("zkw", "isT() false>>> " + a);
//            throw  new IllegalAccessException();
//        }
//        if (a < 5) {
//            Log.i("zkw", "a>>> " + a);
//            return "aaa";
//        } else {
//            Log.i("", "......." + a);
//        }
//        try {
//            throw new NullPointerException();
//        } catch (NullPointerException e) {
//            Log.i("", "NullPointerException!!!!");
//        } finally {
//            Log.i("zkw", "finally");
//        }
//        Log.i("", ">>>>>" + a);
//        return null;
//    }

    private boolean isT() {
        return new Random().nextInt(10) > 5;
    }

}

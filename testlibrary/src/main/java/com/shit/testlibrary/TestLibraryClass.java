package com.shit.testlibrary;

import android.util.Log;

/**
 * Created by ZhouKeWen on 17/3/29.
 */
public class TestLibraryClass {

    public synchronized void startTestt() {
        Log.i("zkw", ">>>>>>i am test!!!");
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void toding() {
        Log.i("zkw", ">>>>>>> tututu");
    }

//    public void toding2() {
//        Log.i("zkw", "tututu");
//    }
}

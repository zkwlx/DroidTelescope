package andr.perf.monitor.memory;

import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZhouKeWen on 17/4/1.
 */
public class ObjectReferenceSampler {

    private List<WeakReference> suspectObjects = new LinkedList<>();

    public ObjectReferenceSampler() {

    }

    public void onKeyObjectCreate(Object object) {
        WeakReference reference = new WeakReference<>(object);
        suspectObjects.add(reference);
        Log.i("zkw", "on create>>>>>---------" + object);
    }

    public void onKeyObjectDestroy(Object object) {
        //TODO 在这里添加记录调用栈的代码
        //TODO 增加触发异步GC代码
        Log.i("zkw", "on destroy>>>>>---------" + object);
        MessageQueue.IdleHandler i = new GCHandler();
        Looper.getMainLooper().getQueue().addIdleHandler(i);
        Handler h = new Handler(Looper.getMainLooper());
        h.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 1000);

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("zkw", "find garbage!!!!!");
                if (suspectObjects.isEmpty()) {
                    Log.i("zkw", "===============all clean!!!");
                    return;
                }
                Iterator<WeakReference> iterator = suspectObjects.iterator();
                while (iterator.hasNext()) {
                    WeakReference reference = iterator.next();
                    Object o = reference.get();
                    Log.i("zkw", "reference>>>>>>>>>>>>>>>>" + reference);
                    if (o == null) {
                        iterator.remove();
                        Log.i("zkw", "===============success clean!!!>>");
                    } else {
                        Log.i("zkw", "...............garbage!!!!!!!>> " + o.toString());
                    }
                }
            }
        }, 10000);
    }

    static class GCHandler implements MessageQueue.IdleHandler {

        @Override
        public boolean queueIdle() {
            Runtime.getRuntime().gc();
            System.runFinalization();
            return false;
        }
    }

}

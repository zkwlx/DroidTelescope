package andr.perf.monitor.memory;

import android.content.ComponentCallbacks2;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import andr.perf.monitor.AndroidMonitor;
import andr.perf.monitor.JobManager;
import andr.perf.monitor.memory.models.LeakInfo;

/**
 * Created by ZhouKeWen on 17/4/1.
 */
public class ObjectReferenceSampler {

    private static final String TAG = "ObjectReferenceSampler";

    /**
     * 在同一个WorkThread里操作嫌疑对象列表，所以无需加锁
     */
    private List<SuspectWeakReference> suspectObjects = new LinkedList<>();
    /**
     * 在同一个WorkThread里操作对象创建堆栈，所以无需加锁
     */
    private Deque<String> objectCreateStack = new LinkedList<>();

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            //TODO 是否限制执行次数
            Log.i(TAG, "[---]start find garbage!!!!!");
            if (suspectObjects == null || suspectObjects.isEmpty()) {
                Log.i(TAG, "[---]===============all clean!!!");
                return;
            }
            Iterator<SuspectWeakReference> iterator = suspectObjects.iterator();
            LeakInfo leakInfo = new LeakInfo();
            while (iterator.hasNext()) {
                SuspectWeakReference reference = iterator.next();
                Object obj = reference.get();
                Log.i(TAG, "[---]reference: " + reference + " obj: " + obj);
                if (obj == null) {
                    iterator.remove();
                    Log.i(TAG, "[---]===============success clean!!!>>");
                } else {
                    //增加对象的计数，当达到阈值时视为泄漏
                    reference.increaseLookUpTimes();
                    if (reference.tooManyTimes()) {
                        //发生泄漏，记录泄漏Object
                        Log.i(TAG, "[---]...............garbage!!!!!!!>> " + obj.toString());
                        iterator.remove();
                        leakInfo.addGarbageReference(reference);
                    }
                }
            }
            AndroidMonitor.LeakListener listener = AndroidMonitor.getLeakListener();
            if (listener != null && !leakInfo.getReferenceList().isEmpty()) {
                listener.onLeak(leakInfo);
            }
        }
    };

    private MessageQueue.IdleHandler gcHandler = new MessageQueue.IdleHandler() {

        @Override
        public boolean queueIdle() {
            Runtime.getRuntime().gc();
            System.runFinalization();
            //TODO 当IdleHandler执行时，向HandlerThread发送异步任务(delay算法思考一下)，用于判断是否发生泄漏
            JobManager.getInstance().postDelayWorkerThread(task, 15000);
            return false;
        }
    };

    public ObjectReferenceSampler() {

    }

    public void onKeyObjectCreate(final Object object) {
        Log.i(TAG, "[---]on create>>>>>---------" + object);
        JobManager.getInstance().postWorkerThread(new Runnable() {
            @Override
            public void run() {
                objectCreateStack.push(object.toString());
            }
        });
    }

    public void onKeyObjectDestroy(final Object object) {
        Log.i(TAG, "[---]on destroy>>>>>---------" + object);
        JobManager.getInstance().postWorkerThread(new Runnable() {
            @Override
            public void run() {
                //记录object的create栈，并存入reference对象，一个Reference对应一个调用栈
                SuspectWeakReference reference = new SuspectWeakReference(object);
                String[] objectIdStack = objectCreateStack.toArray(new String[objectCreateStack
                        .size()]);
                reference.setCreateStack(objectIdStack);
                suspectObjects.add(reference);
                //防止堆栈混乱，索性直接用remove
                objectCreateStack.remove(object.toString());
            }
        });

        triggerGC();
    }

    public void onLowMemory(Object object) {
        onTrimMemory(object, ComponentCallbacks2.TRIM_MEMORY_COMPLETE);
    }

    public void onTrimMemory(Object object, int level) {
        triggerGC();
    }

    private void triggerGC() {
        // 向MainLooper中添加IdleHandler用于触发gc
        Looper.getMainLooper().getQueue().addIdleHandler(gcHandler);
    }

}

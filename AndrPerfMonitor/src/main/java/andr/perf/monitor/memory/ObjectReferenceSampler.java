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
     * 在同一个UI线程里操作嫌疑对象列表，所以无需加锁
     */
    private List<SuspectWeakReference> suspectObjects = new LinkedList<>();
    /**
     * 在UI线程里操作对象创建堆栈，所以无需加锁
     */
    private Deque<String> objectCreateStack = new LinkedList<>();

    private Runnable gcTask = new Runnable() {
        @Override
        public void run() {
            //TODO 可以考虑判断时间间隔是否过小
            // 触发gc会消耗CPU20ms左右，所以异步触发
            Runtime.getRuntime().gc();
            Runtime.getRuntime().runFinalization();
            //将扫描引用任务放到主线程的idle里
            Looper.getMainLooper().getQueue().addIdleHandler(scanReferenceTask);
        }
    };

    //一次post任务到Looper要消耗1ms左右，所以当任务执行时间小于1ms，则不适用异步
    //==============实验证明，由于任务耗时很短，异步执行反而耗时变长，所以改成同步调用=========

    private MessageQueue.IdleHandler scanReferenceTask = new MessageQueue.IdleHandler() {

        @Override
        public boolean queueIdle() {
            //TODO 是否限制执行次数
            Log.i("zkw", "[---]start find garbage!!!!!");
            if (suspectObjects == null || suspectObjects.isEmpty()) {
                Log.i(TAG, "[---]===============all clean!!!");
                return false;
            }
            Iterator<SuspectWeakReference> iterator = suspectObjects.iterator();
            LeakInfo leakInfo = new LeakInfo();
            while (iterator.hasNext()) {
                SuspectWeakReference reference = iterator.next();
                Object obj = reference.get();
                if (obj == null) {
                    iterator.remove();
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
            //在UI线程回调
            AndroidMonitor.LeakListener listener = AndroidMonitor.getLeakListener();
            if (listener != null && !leakInfo.getReferenceList().isEmpty()) {
                listener.onLeak(leakInfo);
            }
            return false;
        }
    };

    public ObjectReferenceSampler() {
    }

    public void onKeyObjectCreate(final Object object) {
        //维护关键对象create栈，用于记录对象的create链
        objectCreateStack.push(object.toString());
    }

    public void onKeyObjectDestroy(final Object object) {
        //记录object的create栈，并存入SuspectWeakReference对象，一个Reference对应一个调用栈
        SuspectWeakReference reference = new SuspectWeakReference(object);
        String[] objectIdStack = objectCreateStack.toArray(new String[objectCreateStack.size()]);
        reference.setCreateStack(objectIdStack);
        suspectObjects.add(reference);
        //防止堆栈混乱，索性直接用remove
        objectCreateStack.remove(object.toString());
        triggerGC();
    }

    public void onLowMemory(Object object) {
        onTrimMemory(object, ComponentCallbacks2.TRIM_MEMORY_COMPLETE);
    }

    public void onTrimMemory(Object object, int level) {
        triggerGC();
    }

    private void triggerGC() {
        //由于触发gc要消耗20ms左右，所以异步触发
        JobManager.getInstance().postWorkerThread(gcTask);
    }

}

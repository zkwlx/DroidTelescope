package andr.perf.monitor.memory;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * Created by ZhouKeWen on 2017/4/5.
 */
public class SuspectWeakReference extends WeakReference {

    /**
     * 索引0是stack的first
     * Activity和Fragment的创建顺序
     */
    private String[] objectCreateStack;

    /**
     * 用户交互事件的发生顺序，索引0代表最近发生的事件
     */
//    private IEvent[] viewEventArray;

    //因为是在统一Message队列里运行，所以无需AtomicInteger
    private int markTimes = 0;

    public SuspectWeakReference(Object referent) {
        super(referent);
    }

    public void setCreateStack(String[] stack) {
        objectCreateStack = stack;
    }

    public String[] getCreateStack() {
        return objectCreateStack;
    }

    public SuspectWeakReference(Object referent, ReferenceQueue q) {
        super(referent, q);
    }

    public void increaseLookUpTimes() {
        markTimes++;
    }

    public int getMarkeTimes() {
        return markTimes;
    }

//    public IEvent[] getViewEventArray() {
//        return viewEventArray;
//    }

//    public void setViewEventArray(IEvent[] viewEventArray) {
//        this.viewEventArray = viewEventArray;
//    }
}

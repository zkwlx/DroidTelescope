package andr.perf.monitor.memory;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * Created by ZhouKeWen on 2017/4/5.
 */
public class SuspectWeakReference extends WeakReference {

    /**
     * 索引0是stack的first
     */
    private String[] objectCreateStack;

    /**
     * 最大标记次数，超过则视为垃圾
     */
    private static final int MAX_MARK_TIMES = 3;

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

    public boolean tooManyTimes() {
        return markTimes > MAX_MARK_TIMES;
    }

}

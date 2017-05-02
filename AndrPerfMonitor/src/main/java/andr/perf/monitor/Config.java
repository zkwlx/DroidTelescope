package andr.perf.monitor;

/**
 * Created by ZhouKeWen on 17/3/28.
 */

public class Config {

    /**
     * judge whether the loop is blocked, you can override this to decide
     * whether it is blocked by your logic
     * <p>
     * Note: running in none ui thread
     *
     * @param useMsTime       in millisecond
     * @param useThreadMsTime in millisecond
     * @return true if blocked, else false
     */
    public boolean isBlock(long useMsTime, long useThreadMsTime) {
        return useMsTime > 100 && useThreadMsTime > 0;//8
    }

    /**
     * 判断一个方法是否应该记录。如果方法执行时间很短，则不记录，节省内存
     * @param useNanoTime
     * @param useThreadMsTime
     * @return
     */
    public boolean shouldRecordMethod(long useNanoTime, long useThreadMsTime) {
        return useNanoTime > 1000000 || useThreadMsTime > 1;
    }

}

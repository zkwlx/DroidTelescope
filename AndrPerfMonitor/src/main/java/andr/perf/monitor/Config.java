package andr.perf.monitor;

/**
 * Created by ZhouKeWen on 17/3/28.
 */

public class Config {

    /**
     * judge whether the loop is blocked, you can override this to decide
     * whether it is blocked by your logic
     *
     * Note: running in none ui thread
     *
     * @param useMsTime in millisecond
     * @param useThreadTime in millisecond
     * @return true if blocked, else false
     */
    public boolean isBlock(long useMsTime, long useThreadTime) {
        return useMsTime > 100 && useThreadTime > 8;
    }

}

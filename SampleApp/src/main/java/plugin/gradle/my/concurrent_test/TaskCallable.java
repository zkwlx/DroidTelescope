package plugin.gradle.my.concurrent_test;

import java.util.concurrent.Callable;

/**
 * Created by ZhouKeWen on 2017/7/13.
 */
public class TaskCallable implements Callable {

    private int index = -1;

    public TaskCallable(int index) {
        this.index = index;
    }

    @Override
    public Object call() throws Exception {

        Thread.sleep(30000);

        return index;
    }
}

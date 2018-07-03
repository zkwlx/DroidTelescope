package plugin.gradle.my.concurrent_test;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import andr.perf.monitor.utils.Logger;

/**
 * Created by ZhouKeWen on 2017/7/13.
 */
public class ExecutorManager {

    ExecutorService executorService = null;

    public ExecutorManager() {
        executorService = Executors.newFixedThreadPool(2);
    }

    public void execut() {
        try {
            SparseArray<FutureTask<?>> tasks = createFutureTasks();
            submitTasks(executorService, tasks);
            waitAllTaskComplete(tasks);
        } catch (InterruptedException | ExecutionException e) {
            Logger.i("ex!!!!!>>>>>>>>" + e);
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    private void submitTasks(ExecutorService executorService, SparseArray<FutureTask<?>> futureTaskMap) {
        for (int i = 0; i < futureTaskMap.size(); i++) {
            FutureTask<?> task = futureTaskMap.valueAt(i);
            executorService.submit(task);
        }
    }

    private SparseArray<FutureTask<?>> createFutureTasks() {
        SparseArray<FutureTask<?>> futureTaskMap = new SparseArray<>();
        for (int i = 0; i < 10; i++) {
            futureTaskMap.put(i, newFutureTask(i));
        }
        return futureTaskMap;
    }

    private FutureTask<?> newFutureTask(int index) {
        TaskCallable callable = new TaskCallable(index);
        FutureTask<?> futureTask = new FutureTask<>(callable);
        return futureTask;
    }

    private void waitAllTaskComplete(SparseArray<FutureTask<?>> uploadTaskMap) throws ExecutionException,
            InterruptedException {
        for (int i = 0; i < uploadTaskMap.size(); i++) {
            FutureTask<?> uploadTask = uploadTaskMap.valueAt(i);
            try {
                // just need block here
                Integer result = (Integer) uploadTask.get();
                Logger.i("get index:" + i + " result>>>>>>" + result);
            } catch (InterruptedException | ExecutionException e) { // executorService shut down
                throw e;
            }
        }
    }

}

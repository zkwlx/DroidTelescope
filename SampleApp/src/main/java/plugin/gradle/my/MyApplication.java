package plugin.gradle.my;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import andr.perf.monitor.Config;
import andr.perf.monitor.DroidTelescope;
import andr.perf.monitor.cpu.models.BlockInfo;
import andr.perf.monitor.injected.TimeConsumingSample;
import andr.perf.monitor.memory.models.LeakInfo;
import andr.perf.monitor.persist.ConvertUtils;
import andr.perf.monitor.utils.Logger;

/**
 * Created by ZhouKeWen on 17/3/24.
 */

public class MyApplication extends Application {

    private Config config = new AndrPerfMonitorConfig();

    private DroidTelescope.BlockListener blockListener = new MyBlockListener();
    private DroidTelescope.LeakListener leakListener = new MyLeakListener();


//    {
//        DroidTelescope.setBlockListener(blockListener);
//        DroidTelescope.setLeakListener(leakListener);
//        DroidTelescope.install(config);
//        DroidTelescope.startMethodTracing();
//    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        DroidTelescope.setBlockListener(blockListener);
//        DroidTelescope.setLeakListener(leakListener);

//        TimeConsumingSample.methodEnter("plugin.gradle.my.MyApplication", "attachBaseContext", "android.content.Context");

        fuck();
        b();
    }

    private void fuck() {
        Logger.d("----");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DroidTelescope.install(config);
        DroidTelescope.startMethodTracing();
        a();
    }

    private void a() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void b() {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private static class AndrPerfMonitorConfig extends Config {
        @Override
        public boolean isBlock(long wallClockTimeMs, long cpuTimeMs) {
            return true;
        }

        @Override
        public boolean shouldRecordMethod(long wallClockTimeNs, long cpuTimeMs) {
            return true;
        }

        @Override
        public boolean useSysTrace() {
            return false;
        }

        @Override
        public boolean justRecordUIThread() {
            return false;
        }

        @Override
        public boolean debugLog() {
            return true;
        }
    }

    private static class MyBlockListener implements DroidTelescope.BlockListener {
        @Override
        public void onBlock(BlockInfo blockInfo) {
            JSONObject blockInfoJson = null;
            //使用框架提供的转换工具，将BlockInfo对象转换成Json格式
            try {
                blockInfoJson = ConvertUtils.convertBlockInfoToJson(blockInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //可以将json数据上传服务器，或者保存到本地
            if (blockInfoJson != null) {
                FileUtils fileUtils = new FileUtils();
                String s = blockInfoJson.toString();
                Random r = new Random();
                String fileName = "apm_block" + r.nextInt(100);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //                fileUtils.write2SDFromInput("", fileName, s);
                Log.i("zkw", "[-----on block, save to file:" + fileName + "]");
            }

        }
    }

    private static class MyLeakListener implements DroidTelescope.LeakListener {
        @Override
        public void onLeak(LeakInfo leakInfo) {
            JSONObject leakInfoJson = null;
            //使用框架提供的转换工具，将BlockInfo对象转换成Json格式
            try {
                leakInfoJson = ConvertUtils.convertLeakInfoToJson(leakInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //可以将json数据上传服务器，或者保存到本地
            if (leakInfoJson != null) {
                FileUtils fileUtils = new FileUtils();
                String s = leakInfoJson.toString();
                Random r = new Random();
                String fileName = "apm_leak" + r.nextInt(100);
                fileUtils.write2SDFromInput("", fileName, s);
                Log.i("zkw", "[-----on leak, save to file:" + fileName + "]");
            }

        }
    }

}

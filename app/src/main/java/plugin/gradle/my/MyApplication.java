package plugin.gradle.my;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import andr.perf.monitor.AndroidMonitor;
import andr.perf.monitor.Config;
import andr.perf.monitor.cpu.models.BlockInfo;
import andr.perf.monitor.persist.ConvertUtils;

/**
 * Created by ZhouKeWen on 17/3/24.
 */

public class MyApplication extends Application {

    private Config config = new AndrPerfMonitorConfig();

    private AndroidMonitor.BlockListener blockListener = new MyBlockListener();

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidMonitor.install(config);
        AndroidMonitor.setBlockListener(blockListener);
    }

    private static class AndrPerfMonitorConfig extends Config {

    }

    private static class MyBlockListener implements AndroidMonitor.BlockListener {
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
                Log.i("MyApplication", blockInfoJson.toString());
            }

        }
    }

}

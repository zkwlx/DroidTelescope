package plugin.gradle.my;

import android.content.Context;

/**
 * @author zhoukewen
 * @since 2018/7/3
 */
public class DroidTelescopeProxy {

//    private static Config config = new AndrPerfMonitorConfig();

//    private DroidTelescope.BlockListener blockListener = new MyBlockListener();
//    private DroidTelescope.LeakListener leakListener = new MyLeakListener();

    public static void init() {
//        DroidTelescope.install(config);
    }

    public static void startMethodTracing() {
//        DroidTelescope.startMethodTracing();
    }

    public static String stopMethodTracing(Context context) {
//       return DroidTelescope.stopMethodTracing(context);
        return "";
    }

//    private static class AndrPerfMonitorConfig extends Config {
//        @Override
//        public boolean isBlock(long wallClockTimeMs, long cpuTimeMs) {
//            return true;
//        }
//
//        @Override
//        public boolean shouldRecordMethod(long wallClockTimeNs, long cpuTimeMs) {
//            return true;
//        }
//
//        @Override
//        public boolean useSysTrace() {
//            return false;
//        }
//
//        @Override
//        public boolean justRecordUIThread() {
//            return true;
//        }
//
//        @Override
//        public boolean debugLog() {
//            return true;
//        }
//    }
//
//
//    private static class MyBlockListener implements DroidTelescope.BlockListener {
//        @Override
//        public void onBlock(BlockInfo blockInfo) {
//            JSONObject blockInfoJson = null;
//            //使用框架提供的转换工具，将BlockInfo对象转换成Json格式
//            try {
//                blockInfoJson = ConvertUtils.convertBlockInfoToJson(blockInfo);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            //可以将json数据上传服务器，或者保存到本地
//            if (blockInfoJson != null) {
//                FileUtils fileUtils = new FileUtils();
//                String s = blockInfoJson.toString();
//                Random r = new Random();
//                String fileName = "apm_block" + r.nextInt(100);
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                //                fileUtils.write2SDFromInput("", fileName, s);
//                Log.i("zkw", "[-----on block, save to file:" + fileName + "]");
//            }
//
//        }
//    }
//
//    private static class MyLeakListener implements DroidTelescope.LeakListener {
//        @Override
//        public void onLeak(LeakInfo leakInfo) {
//            JSONObject leakInfoJson = null;
//            //使用框架提供的转换工具，将BlockInfo对象转换成Json格式
//            try {
//                leakInfoJson = ConvertUtils.convertLeakInfoToJson(leakInfo);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            //可以将json数据上传服务器，或者保存到本地
//            if (leakInfoJson != null) {
//                FileUtils fileUtils = new FileUtils();
//                String s = leakInfoJson.toString();
//                Random r = new Random();
//                String fileName = "apm_leak" + r.nextInt(100);
//                fileUtils.write2SDFromInput("", fileName, s);
//                Log.i("zkw", "[-----on leak, save to file:" + fileName + "]");
//            }
//
//        }
//    }
}

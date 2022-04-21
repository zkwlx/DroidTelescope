<img src="https://github.com/zkwlx/DroidTelescope/blob/master/wiki/logo.png" width="50px" /> DroidTelescope（DT）
=======
这是一套Android端线上应用性能监控框架，目前支持卡顿监控、内存泄露监控；后续还会增加更多监控对象。此项目参考自开源项目[BlockCanaryEx](https://github.com/seiginonakama/BlockCanaryEx)。

## 框架简介
* 支持方法耗时追踪，提供 startXX 和 endXX 接口，并支持框架自研和 Android SysTrace 两种实现
* 框架支持卡顿监控，当发生卡顿时会记录所有方法的调用耗时和调用栈
* 框架支持内存泄露监控，当发生内存泄露时会记录用户的交互行为和页面的创建关系（为了提高性能，内存泄露并不会dump内存）
* 框架支持用户交互行为的监控，为其他监控提供支持，比如内存泄露（交互监控还未开发完全）
* 框架会在编译时进行代码注入，所以对apk的性能会有一点影响，具体影响范围会在下面介绍。

## 架构图
<br>![](https://github.com/zkwlx/DroidTelescope/blob/master/wiki/DroidTelescope%E6%9E%B6%E6%9E%84%E5%9B%BE.png "整体架构")

## 使用效果
### 方法调用追踪（新功能）
可以通过接口 DroidTelescope.startMethodTracing 和 DroidTelescope.stopMethodTracing 追踪方法的调用栈的耗时，
例如想要监控 App 的启动耗时方法，可以在 App 中加入如下代码：
```java
public class MyApplication extends Application {
    ...
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        DroidTelescope.install();
        DroidTelescope.startMethodTracing();
    }
    ...
}
 
public class MainActivity extends Activity {
    ...
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        String path = DroidTelescope.stopMethodTracing(this.getApplicationContext());
        // 打印日志路径。使用 SysTrace 时框架不会主动生成日志文件，由 Android 提供的 systrace.py 工具生成
        if (!TextUtils.isEmpty(path)) {
            Log.i("zkw", "加载完成:::>" + path);
        } else {
            Log.i("zkw", "Path is null, maybe use Systrace.");
        }
    }
    ...
}
```
### 使用框架自研模块追踪效果
之后按如下几部打开报告文件：
* 生成的日志文件 trace_html_report.zip 从设备中取出并解压
* 用浏览器打开 trace_html_report/index.html 文件

报告的效果和分析方法如下：

<br>![](https://github.com/zkwlx/DroidTelescope/blob/master/wiki/TracesDemo.png)
简单说明下两个时长的意义：
* 时钟时长，表示方法执行所消耗的时钟时间，即使方法没有占用 cpu，仅等待另一线程的完成，时长也会被记录。
* Cpu 时长，表示方法执行所消耗的 cpu 时间，当方法没有占用 cpu 时，时间不会被记录。

相比 Google 官方提供的检测方法，DroidTelescope 框架有如下优点：
* 报告规模、方法数量可控，不会被大量系统方法干扰；
* 性能损耗较小，可针对某些需求应用到线上；
* 高度可定制，可以根据 App 特点定制框架（例如想监控 Fragment 的创建耗时）；

### 使用 SysTrace 追踪效果
详细使用方式请参考[官方文档](https://developer.android.com/studio/command-line/systrace)，框架中使用 SysTrace 方式如下：
```java
    public void init() {
        DroidTelescope.install(new MyConfig());
    }
    ...
    class MyConfig extends Config {
        @Override
        public boolean useSysTrace() {
            return true;
        }
    }
    ...
```
使用 SysTrace 后，DT 框架会在每个方法的开始和结束调用 Trace.beginSection(方法签名) 和 Trace.endSection()，并维护调用栈关系。
在开始追踪之前，运行命令：
```shell
systrace.py -o output.html --app=app进程名 app
```
结束后用浏览器打开 output.html，效果如下图所示。
<br>![](https://github.com/zkwlx/DroidTelescope/blob/master/wiki/systrace_demo.png)

### 卡顿监控
当发生卡顿时，框架会记录相关方法的调用时间和调用栈，并生成BlockInfo对象，使用框架提供的ConvertUtils工具将BlockInfo对象转换成JSON格式的日志，如下例子，每个字段的意义请看注释：
```js
{
    "loop_wall_clock_time":319,//表示一次loop所消耗的时钟时长，单位是ms毫秒
    "loop_cpu_time":47,//表示一次loop所消耗的cpu时长，单位是ms毫秒
    "invoke_trace_array":[//表示一次loop记录的耗时方法调用关系
        {
            "method_signature":"plugin.gradle.my.SecondActivity.onCreate(android.os.Bundle)",
            "thread_id":1,
            "wall_clock_time":31.06,
            "cpu_time":20
        },
        {
            "method_signature":"plugin.gradle.my.BlankFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",
            "thread_id":1,
            "wall_clock_time":5.74,
            "cpu_time":5
        },
        {
            "method_signature":"plugin.gradle.my.SecondActivity.onResume()",//方法的签名
            "thread_id":1,//方法调用时所在线程id
            "wall_clock_time":257.77,//方法调用消耗的时钟时长，单位是ms毫秒
            "cpu_time":8,//方法调用消耗的cpu时长，单位是ms毫秒
            "invoke_trace":[//记录在当前方法中调用的其他子方法
                {
                    "method_signature":"plugin.gradle.my.dummy.DummyContent.<clinit>()",
                    "thread_id":1,
                    "wall_clock_time":6.62,
                    "cpu_time":6
                },
                {
                    "method_signature":"plugin.gradle.my.dummy.DummyContent.sleep()",
                    "thread_id":1,
                    "wall_clock_time":250.18,
                    "cpu_time":0
                }
            ]
        }
    ]
}
```
框架不会记录所有方法，只有当方法耗时超过阈值时（可以配置）记录，日志中所有的time单位都是ms毫秒。
触发这次卡顿的源码结构是这样的：
```java
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        BlankFragment blankFragment = new BlankFragment();
        tx.add(R.id.id_content, blankFragment, "ONE");
        tx.commit();

        blankFragment.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DummyContent d = new DummyContent();
        d.sleep();
    }
    
}
```
```java
public class DummyContent {
    public void sleep() {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
### 内存泄露监控
下面模拟一个内存泄露的环境：MainActivity启动SecondActivity，SecondActivity添加一个BlankFragment，BlankFragment会导致泄露，泄露代码如下：
```java
public class BlankFragment extends Fragment {

    public static List<Activity> ins = new ArrayList<>();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ins.add(activity);
    }
}
```
当发生内存泄露时（目前只支持监控Activity和Fragment的引用泄露），会创建LeakInfo对象，使用框架提供的ConvertUtils工具将LeakInfo对象转换成Json格式，如下例子，每个字段的意义请看注释：
```js
{
    "garbage_reference_list":[//怀疑是泄露对象的列表
        {
            //泄露对象的id，通过Object.toString()生成
            "objectId":"BlankFragment{1324e62}",
            //泄露对象的调用链，只记录Activity、Fragment之间的调用关系
            "object_create_chain":"plugin.gradle.my.MainActivity@2695ae7->plugin.gradle.my.SecondActivity@6cce780->BlankFragment{1324e62 #0 id=0x7f0b006f ONE}"
        },
        {
            "objectId":"plugin.gradle.my.SecondActivity@6cce780",
            "object_create_chain":"plugin.gradle.my.MainActivity@2695ae7->plugin.gradle.my.SecondActivity@6cce780"
        }
    ]
}
```
## 使用方法
（不知为何bintray一直不通过我的包，所以jcenter上还没有插件包，可以先使用本地编译，见谅）
<br>框架会在编译期间注入代码，首先配置代码注入的插件，将repo目录复制到你自己项目的根目录，在项目app的build.gradle文件中加入如下代码：
```groovy
buildscript {
    repositories {
        maven {
            url uri('../repo')
        }
        jcenter()
    }
    dependencies {
        classpath 'andr.perf.monitor:TelescopeInjector:0.9.0'
    }
}
apply plugin: 'telescope.injector'
```
然后项目添加对DroidTelescope库的依赖，可以直接使用项目目录下的DroidTelescope_v0.8.0_xxxxxx.jar包，
添加完后，大致是这个样子：
<br>![](https://raw.githubusercontent.com/zkwlx/DroidTelescope/master/wiki/demo.png "项目配置图例")

然后再代码中配置监控框架，建议在自定义的Application.onCreate中配置，示例如下：
```java
public class MyApplication extends Application {

    // 需要自定义配置项时，重写Config的相应方法
    private Config config = new AndrPerfMonitorConfig();

    // 设置监听器，当发生卡顿或者内存泄露时回调
    private DroidTelescope.BlockListener blockListener = new MyBlockListener();
    private DroidTelescope.LeakListener leakListener = new MyLeakListener();

    @Override
    public void onCreate() {
        super.onCreate();
        // 设置自定义配置
        DroidTelescope.install(config);
        // 设置监听器，当发生卡顿或者内存泄露时回调
        DroidTelescope.setBlockListener(blockListener);
        DroidTelescope.setLeakListener(leakListener);
    }

    //自定义配置类，本例没有自定义配置，所以没有重写任何方法
    private static class AndrPerfMonitorConfig extends Config {
        
    }

    //卡顿监听器，当发生卡顿时，使用框架提供的转换工具类将BlockInfo转换成Json，并保存到文件
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
                String blockJson = blockInfoJson.toString();
                fileUtils.write2SDFromInput("", "block.txt", blockJson);
            }
        }
    }

    //泄露监听器，当发生内存泄露时，使用框架提供的转换工具类将LeakInfo转换成Json，并保存到文件
    private static class MyLeakListener implements DroidTelescope.LeakListener {
        @Override
        public void onLeak(LeakInfo leakInfo) {
            JSONObject leakInfoJson = null;
            //使用框架提供的转换工具，将LeakInfo对象转换成Json格式
            try {
                leakInfoJson = ConvertUtils.convertLeakInfoToJson(leakInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //可以将json数据上传服务器，或者保存到本地
            if (leakInfoJson != null) {
                FileUtils fileUtils = new FileUtils();
                String leakJson = leakInfoJson.toString();
                fileUtils.write2SDFromInput("", "leak.txt", leakJson);
            }
        }
    }
    
}
```
## 对应用的性能影响测试
* __包大小：__ 由于编译时会注入代码，所以会增加class文件的大小，如果按默认配置（只注入所有模块src下的代码）编出的dex平均会增加20%~40%。
后期会增加编译开关来控制注入哪些模块。
* __方法耗时：__ 测试方法是对一个方法循环调用2万次，对比注入前和注入后耗时。当只注入卡顿监控模块时会慢120ms；
当只注入泄露模块时会慢170ms；当只注入用户交互模块时会慢1000ms。
<br>Looper监控的测试比较特殊，测试时每次loop生成10个耗时方法，然后触发200次loop监控，耗时180ms左右。
* __内存消耗：__ 同样是循环2万次调用，卡顿模块消耗2.38MB，内存模块消耗510.89KB，交互模块消耗400KB（该测试和实际差别较大，仅供参考）。

* * *

欢迎关注我的公众号 `二叉树根子`，在这里可以看到不曾见过的 Android 底层技术。

<img width="258" height="258" alt="公众号" src="wiki/binary tree root.jpg">

## License
DroidTelescope使用的GPL3.0协议，详细请参考[License](https://raw.githubusercontent.com/zkwlx/DroidTelescope/master/LICENSE)







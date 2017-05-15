DroidTelescope
=======
这是一套Android端线上应用性能监控框架，目前支持卡顿监控、内存泄露监控；后续还会增加更多监控对象。此项目部分源码参考自开源项目[BlockCanaryEx](https://github.com/seiginonakama/BlockCanaryEx)。

## 使用效果
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








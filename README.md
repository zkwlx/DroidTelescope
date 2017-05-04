# AndrPerfMonitor
这是一套Android端线上应用性能监控框架，目前支持卡顿监控、内存泄露监控；后续还会增加更多监控对象。此项目部分功能参考自[BlockCanaryEx](https://github.com/seiginonakama/BlockCanaryEx)。

## 使用效果
当发生卡顿时，框架会记录相关方法的调用时间和调用栈，并生成JSON格式的日志，如下例子：
```js
{
    "loop_wall_clock_time":319,
    "loop_cpu_time":47,
    "invoke_trace_array":[
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
            "method_signature":"plugin.gradle.my.SecondActivity.onResume()",
            "thread_id":1,
            "wall_clock_time":257.77,
            "cpu_time":8,
            "invoke_trace":[
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
日志中的所有time单位都是ms毫秒，触发这次卡顿的源码结构是这样的：
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
当发生内存泄露时（目前只支持Activity和Fragment的引用泄露），生成的json日志格式如下：
```json

```
当发生内存泄露时（目前只支持Activity和Fragment的）
当发生内存泄露时（目前只支持Activity和Fragment的）

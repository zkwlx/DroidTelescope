package plugin.gradle.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ZhouKeWen on 17/3/31.
 */
public class SecondActivity extends AppCompatActivity {

    private byte[] a = new byte[8024000];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onLowMemory();
        onTrimMemory(TRIM_MEMORY_RUNNING_MODERATE);
    }
}

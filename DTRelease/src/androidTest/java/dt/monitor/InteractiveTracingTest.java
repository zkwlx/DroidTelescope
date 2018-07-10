package dt.monitor;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;

import org.junit.Before;
import org.junit.Test;

import dt.monitor.injected.InteractiveInjected;

/**
 * @author zhoukewen
 * @since 2018/6/25
 */
public class InteractiveTracingTest {

    private Context appContext;

    @Before
    public void init() {
        appContext = InstrumentationRegistry.getTargetContext();
        System.out.println("on init");
    }

    @Test
    public void test_onCheckedChanged() {
        CompoundButton button = new CheckBox(appContext);
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                InteractiveInjected.onCheckedChanged(this, buttonView, isChecked);
            }
        };
        button.setOnCheckedChangeListener(listener);
        button.setChecked(false);
        button.performClick();
    }

    @Test
    public void test_onViewClick() {
        ImageView button = new ImageView(appContext);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InteractiveInjected.onViewClick(this, v);
            }
        };
        button.setOnClickListener(listener);
        button.performClick();
    }

    @Test
    public void test_onViewLongClick() {
        ImageView button = new ImageView(appContext);
        View.OnLongClickListener listener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                InteractiveInjected.onViewLongClick(this, v);
                return false;
            }
        };
        button.setOnLongClickListener(listener);
        button.performLongClick();
    }

    @Test
    public void test_onItemClick() {
        ListView listView = new ListView(appContext);
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InteractiveInjected.onItemClick(this, parent, view, position, id);
            }
        };
        listView.setOnItemClickListener(listener);
        listView.performItemClick(null, 1, 10086);
    }

    @Test
    public void test_onItemLongClick() {

        ListView listView = new ListView(appContext);
        AdapterView.OnItemLongClickListener listener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                InteractiveInjected.onItemLongClick(this, parent, view, position, id);
                return false;
            }

        };
        //TODO 可能测试不到
        listView.setOnItemLongClickListener(listener);
        listView.performLongClick();
    }

    @Test
    public void test_onItemSelected() {
        ListView listView = new ListView(appContext);
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                InteractiveInjected.onItemSelected(this, parent, view, position, id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        };
        listView.setOnItemSelectedListener(listener);
        listView.setSelection(0);
    }

}

package plugin.gradle.my;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import plugin.gradle.my.dummy.ObjectEntity;

/**
 * Created by ZhouKeWen on 2017/5/19.
 */
public class ListActivity extends Activity {

    private List<ObjectEntity> dataSource;

    private static List<ListActivity> l = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initListView();
        l.add(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void initListView() {

        setContentView(R.layout.activity_list_main);
        ListView listView = (ListView) findViewById(R.id.listView1);

        dataSource = new ArrayList<ObjectEntity>();

        for (int i = 0; i < 10; i++) {
            ObjectEntity object = new ObjectEntity();

            object.imgUrl =
                    "http://a.hiphotos.baidu.com/album/w%3D2048/sign=3da6584ff2deb48ffb69a6dec4273b29/960a304e251f95ca11945098c8177f3e670952bf.jpg";
            object.desc = "item:" + System.currentTimeMillis();

            dataSource.add(object);
        }

        MyAdapter myAdapter = new MyAdapter(dataSource, ListActivity.this);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ObjectEntity data = dataSource.get(position);

                Toast.makeText(getApplicationContext(), data.desc, Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });
    }

}

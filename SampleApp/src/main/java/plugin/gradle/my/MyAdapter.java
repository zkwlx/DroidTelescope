package plugin.gradle.my;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import plugin.gradle.my.dummy.ObjectEntity;

/**
 * Created by ZhouKeWen on 2017/5/19.
 */
public class MyAdapter extends BaseAdapter {


    private List<ObjectEntity> dataSourceArray;
    private Context activityContext;

    public MyAdapter(List<ObjectEntity> dataSource, Context context) {
        // TODO Auto-generated constructor stub

        this.dataSourceArray = dataSource;
        this.activityContext = context;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.dataSourceArray.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ObjectEntity data = dataSourceArray.get(position);

        if (null == convertView) {
            LayoutInflater inflater = ((Activity) activityContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        textView.setText(data.desc);

        return convertView;
    }

}

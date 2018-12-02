package com.example.apple.voicetest.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.apple.voicetest.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by apple on 2018/7/22.
 */

public class ArdAdapter extends BaseAdapter {
    private LayoutInflater inflater;//得到一个LayoutInfalter对象用来导入布局
    private ArrayList<HashMap<String,Object>> contentList;
    private Context context;

    //构造函数,传入的context是ListView所在界面的上下文
    public ArdAdapter(Context context, ArrayList<HashMap<String, Object>> contentList) {
        this.context = context;
        this.contentList = contentList;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return contentList.size();
    }//这个方法返回了在适配器中所代表的数据集合的条目数

    @Override
    public Object getItem(int position) {
        return contentList.get(position);
    }//这个方法返回了数据集合中与指定索引position对应的数据项

    @Override
    public long getItemId(int position) {
        return position;
    }//这个方法返回了在列表中与指定索引对应的行id

    private static class ViewHolder{
        TextView ardnum;
        TextView created;
        TextView alarm;
        GridView ard_info;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArdAdapter.ViewHolder holder;
        if(convertView == null){
            holder = new ArdAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_ard,null);
            holder.ardnum = (TextView)convertView.findViewById(R.id.ardnum);
            holder.created = (TextView)convertView.findViewById(R.id.created);
            holder.alarm = (TextView)convertView.findViewById(R.id.alarm);
            holder.ard_info = (GridView)convertView.findViewById(R.id.ard_info);
            convertView.setTag(holder);
        } else {
            holder = (ArdAdapter.ViewHolder)convertView.getTag();
        }
        holder.ardnum.setText((String)contentList.get(position).get("ardnum"));
        holder.created.setText((String)contentList.get(position).get("created"));
        String alarm = "";
        String temp = (String) contentList.get(position).get("content");
        if (temp == "暂未有数据") {
            alarm = "";
        } else {
            String[] array = temp.split(",");
            for (int i = 0; i < array.length; i++){
                if(array[i].equals("LOW_AIR_TEMPERATURE")) {
                    alarm = alarm + "温度过低；";
                    continue;
                } else if (array[i].equals("LOW_SOIL_HUMIDITY")) {
                    alarm = alarm + "湿度过低；";
                    continue;
                } else if (array[i].equals("LOW_LIGHT_INTENSITY")) {
                    alarm = alarm + "光照过低；";
                    continue;
                } else if (array[i].equals("HIGH_AIR_TEMPERATURE")) {
                    alarm = alarm + "温度过高；";
                    continue;
                }
            }
        }
        holder.alarm.setText(alarm);

        ArrayList<HashMap<String,Object>> infoList = (ArrayList<HashMap<String,Object>>)contentList.get(position).get("info");
        InfoAdapter mAdapter = new InfoAdapter(context,infoList);
        holder.ard_info.setAdapter(mAdapter);
        holder.ard_info.setClickable(false);
        holder.ard_info.setPressed(false);
        holder.ard_info.setEnabled(false);
//        View view = inflater.inflate(R.layout.list_item_area,null);
        //很多时候我们在这里设置一个null就OK了，好像也没有什么问题，但是这里边大有学问，待会着重要讲.
        return convertView;//返回的就是我们要呈现的ItemView,即每一条Item的布局.
    }
}

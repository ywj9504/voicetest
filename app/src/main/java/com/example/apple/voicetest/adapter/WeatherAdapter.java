package com.example.apple.voicetest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apple.voicetest.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by apple on 2018/10/25.
 */

public class WeatherAdapter extends BaseAdapter {
    private LayoutInflater inflater;//得到一个LayoutInfalter对象用来导入布局
    private ArrayList<HashMap<String,Object>> contentList;
    private Context context;
    private int icon = R.drawable.ic_person_yellow_24dp;

    //构造函数,传入的context是ListView所在界面的上下文
    public WeatherAdapter(Context context, ArrayList<HashMap<String, Object>> contentList) {
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
        ImageView imv;
        TextView date;
        TextView info;
        TextView tmp;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WeatherAdapter.ViewHolder holder;
        if(convertView == null){
            holder = new WeatherAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.weather_forecast_item,null);
            holder.imv = (ImageView)convertView.findViewById(R.id.weather_pic);
            holder.date = (TextView)convertView.findViewById(R.id.data_text);
            holder.info = (TextView)convertView.findViewById(R.id.info_text);
            holder.tmp = (TextView)convertView.findViewById(R.id.max_min_text);
            convertView.setTag(holder);
        } else {
            holder = (WeatherAdapter.ViewHolder)convertView.getTag();
        }
//        holder.imv.setImageResource((int)contentList.get(position).get("image"));
        holder.date.setText((String)contentList.get(position).get("date"));
        holder.info.setText((String)contentList.get(position).get("info"));
        holder.tmp.setText((String)contentList.get(position).get("tmp"));
//        InfoAdapter mAdapter = new InfoAdapter(context,contentList,null);
//
//        holder.ard_info.setAdapter();
//        View view = inflater.inflate(R.layout.list_item_area,null);
        //很多时候我们在这里设置一个null就OK了，好像也没有什么问题，但是这里边大有学问，待会着重要讲.
        return convertView;//返回的就是我们要呈现的ItemView,即每一条Item的布局.
    }
}

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
 * Created by apple on 2018/10/7.
 */

public class InfoAdapter extends BaseAdapter {
    private LayoutInflater inflater;//得到一个LayoutInfalter对象用来导入布局
    private ArrayList<HashMap<String,Object>> contentList;
    private Context context;
    private int icon = R.drawable.ic_person_yellow_24dp;

    //构造函数,传入的context是ListView所在界面的上下文
    public InfoAdapter(Context context, ArrayList<HashMap<String, Object>> contentList) {
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
        TextView desc;
        TextView value;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InfoAdapter.ViewHolder holder;
        if(convertView == null){
            holder = new InfoAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.ard_grid_item,null);
            holder.imv = (ImageView)convertView.findViewById(R.id.image_info);
            holder.desc = (TextView)convertView.findViewById(R.id.desc_info);
            holder.value = (TextView)convertView.findViewById(R.id.value_info);
            convertView.setTag(holder);
        } else {
            holder = (InfoAdapter.ViewHolder)convertView.getTag();
        }
        holder.imv.setImageResource((int)contentList.get(position).get("image"));
        holder.desc.setText((String)contentList.get(position).get("desc"));
        holder.value.setText((String)contentList.get(position).get("value"));
//        InfoAdapter mAdapter = new InfoAdapter(context,contentList,null);
//
//        holder.ard_info.setAdapter();
//        View view = inflater.inflate(R.layout.list_item_area,null);
        //很多时候我们在这里设置一个null就OK了，好像也没有什么问题，但是这里边大有学问，待会着重要讲.
        return convertView;//返回的就是我们要呈现的ItemView,即每一条Item的布局.
    }
}

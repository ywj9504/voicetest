package com.example.apple.voicetest.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
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
 * Created by apple on 2018/7/22.
 */

public class AreaAdapter extends BaseAdapter {
    private LayoutInflater inflater;//得到一个LayoutInfalter对象用来导入布局
    private ArrayList<HashMap<String,Object>> contentList;
    private Context context;

    //构造函数,传入的context是ListView所在界面的上下文
    public AreaAdapter(Context context, ArrayList<HashMap<String, Object>> contentList) {
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
        TextView name;
        TextView status;
        TextView crops;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_area,null);
            convertView.setBackgroundColor(Color.parseColor("#E9E5D3"));
            holder.imv = (ImageView) convertView.findViewById(R.id.area_image);
            holder.name = (TextView) convertView.findViewById(R.id.area_name);
            holder.crops = (TextView) convertView.findViewById(R.id.area_crops);
            holder.status = (TextView) convertView.findViewById(R.id.area_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.imv.setImageResource((int)contentList.get(position).get("image"));
        holder.name.setText((String)contentList.get(position).get("name"));
        holder.name.setTextColor(Color.parseColor("#FF000000"));
        holder.crops.setText((String)contentList.get(position).get("crops"));
        holder.status.setText((String)contentList.get(position).get("status"));
        if(((String)contentList.get(position).get("status")).equals("正常")){
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(20);
            drawable.setColor(Color.parseColor("#00FF00"));
            drawable.setStroke(1,Color.parseColor("#000000"));
            holder.status.setBackground(drawable);
            holder.status.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(20);
            drawable.setColor(Color.parseColor("#FF0000"));
            drawable.setStroke(1,Color.parseColor("#000000"));
            holder.status.setBackground(drawable);
            holder.status.setTextColor(Color.parseColor("#FFFFFF"));
        }
        //很多时候我们在这里设置一个null就OK了，好像也没有什么问题，但是这里边大有学问，待会着重要讲.
        return convertView;//返回的就是我们要呈现的ItemView,即每一条Item的布局.
    }
}

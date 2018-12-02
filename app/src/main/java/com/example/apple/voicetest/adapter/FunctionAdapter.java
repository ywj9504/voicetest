package com.example.apple.voicetest.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
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
 * Created by apple on 2018/10/6.
 */

public class FunctionAdapter extends BaseAdapter {
    private LayoutInflater inflater;//得到一个LayoutInfalter对象用来导入布局
    private ArrayList<HashMap<String,Object>> contentList;
    private int[] bcolors = {Color.parseColor("#2A84C1"), Color.parseColor("#82B12C"),
            Color.parseColor("#D7555F"),Color.parseColor("#E5A01E")};//,

    private Context context;

    public FunctionAdapter(Context context, ArrayList<HashMap<String, Object>> contentList) {
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
        TextView tv;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FunctionAdapter.ViewHolder holder;
        if(convertView == null){
            holder = new FunctionAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.function_item,null);
//            convertView.setBackgroundColor(bcolors[position]);


            GradientDrawable drawable=new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
//            drawable.setGradientType(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(20);
            drawable.setColor(bcolors[position]);
            drawable.setStroke(1,Color.parseColor("#000000"));
            convertView.setBackground(drawable);

//            convertView = LayoutInflater.from(context).inflate(R.layout.function_item, null);
            holder.imv = (ImageView) convertView.findViewById(R.id.func_imageview);
            holder.tv = (TextView)convertView.findViewById(R.id.func_textview);
            convertView.setTag(holder);
        } else {
            holder = (FunctionAdapter.ViewHolder)convertView.getTag();
        }
        holder.imv.setImageResource((int)contentList.get(position).get("image"));
        holder.tv.setText((String)contentList.get(position).get("text"));
//        View view = inflater.inflate(R.layout.list_item_area,null);
        //很多时候我们在这里设置一个null就OK了，好像也没有什么问题，但是这里边大有学问，待会着重要讲.
        return convertView;//返回的就是我们要呈现的ItemView,即每一条Item的布局.
    }
}

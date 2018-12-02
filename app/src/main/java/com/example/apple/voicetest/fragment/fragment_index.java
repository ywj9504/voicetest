package com.example.apple.voicetest.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.apple.voicetest.AlarmActivity;
import com.example.apple.voicetest.HistoryActivity;
import com.example.apple.voicetest.MapActivity;
import com.example.apple.voicetest.R;
import com.example.apple.voicetest.adapter.FunctionAdapter;
import com.example.apple.voicetest.arealistActivity;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by apple on 2018/10/5.
 */

public class fragment_index extends Fragment {
    private GridView gv;
    private ArrayList<HashMap<String, Object>> data_list;
    private FunctionAdapter madapter;

    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    String cookie;

    private int[] icons ={R.drawable.assignment_50dp,R.drawable.history_50dp,R.drawable.warning_50dp,R.drawable.ic_location_blank_24dp};//,R.drawable.new_50dp
    private String[] text = {"大棚管理","历史数据","报警记录","地图定位"};//,"新闻速递"
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        gv = view.findViewById(R.id.grid_view);

        preference = getActivity().getSharedPreferences("cookie", MODE_PRIVATE);
        cookie = preference.getString("sessionid","");

        data_list = new ArrayList<HashMap<String, Object>>();
        getData();
        //加载适配器
        madapter = new FunctionAdapter(getContext(),data_list);
        gv.setAdapter(madapter);
        //监听item每一项
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),arealistActivity.class);
                    startActivity(intent);
                } else if(i == 1) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),HistoryActivity.class);
                    startActivity(intent);
                } else if(i == 2) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),AlarmActivity.class);
                    startActivity(intent);
                } else if (i == 3) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),MapActivity.class);
                    startActivity(intent);
                }
            }
        });
        return view;
    }
    //准备数据源
    public ArrayList<HashMap<String, Object>> getData() {

        for (int i = 0; i < icons.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", icons[i]);
            map.put("text", text[i]);
            data_list.add(map);
        }
        return data_list;
    }
}

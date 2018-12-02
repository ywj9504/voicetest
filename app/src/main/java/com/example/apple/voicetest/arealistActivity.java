package com.example.apple.voicetest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.apple.voicetest.adapter.AreaAdapter;
import com.example.apple.voicetest.communicate.HttpCallback;
import com.example.apple.voicetest.communicate.HttpURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by apple on 2018/10/7.
 */

public class arealistActivity extends AppCompatActivity {
    private ArrayList<HashMap<String, Object>> data_list;
    private int icons = R.drawable.assignment_50dp;
    private AreaAdapter madapter;
    private ListView lv;
    private JSONArray jsonArray = null;
    private Toolbar toolbar;


    private SharedPreferences preferences;
    private Handler handler = null;

    private Timer timer= null;
    private TimerTask task = null;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arealist);
        lv = (ListView) findViewById(R.id.arealistview);


        preferences = getSharedPreferences("cookie", MODE_PRIVATE);
        handler = new Handler();


        data_list = new ArrayList<HashMap<String, Object>>();
        //加载适配器
        madapter = new AreaAdapter(this, data_list);
        lv.setAdapter(madapter);
        //监听item每一项
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
//                intent.putExtra("title",view.)
                intent.setClass(arealistActivity.this, ardlistActivity.class);
                try {
                    intent.putExtra("object", jsonArray.getJSONObject(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
        madapter = new AreaAdapter(arealistActivity.this, data_list);
        lv.setAdapter(madapter);


        toolbar = (Toolbar) findViewById(R.id.arealist_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_return);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        destroyTimer();
        initTimer();
        timer.schedule(task,0,10000);
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    protected void initTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                getData();
            }
        };
    }

    protected void destroyTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }


    //准备数据源
    protected void getData() {
        HttpURL myHttp = new HttpURL();
        String address = "arealist/";
        myHttp.GetWithHttpURLConnection(address,preferences.getString("sessionid", ""), new HttpCallback() {
            @Override
            public void onFinish(String response) throws JSONException {
                jsonArray = new JSONArray(response);
                data_list.clear();
                for(int i = 0; i < jsonArray.length(); i ++) {
                    JSONObject tempObject = jsonArray.getJSONObject(i);
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("image",icons);
                    map.put("number",tempObject.getString("number"));
                    map.put("name",tempObject.getString("name"));
                    map.put("status",tempObject.getString("status"));
                    map.put("crops",tempObject.getString("crops"));
                    data_list.add(map);
                }
                handler.post(runnableUi);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if (madapter != null) {
                madapter.notifyDataSetChanged();
                    lv.setSelection(0);
                } else {
                    madapter = new AreaAdapter(arealistActivity.this, data_list);
                    lv.setAdapter(madapter);
                    lv.setSelection(0);
            }
        }
    };
}

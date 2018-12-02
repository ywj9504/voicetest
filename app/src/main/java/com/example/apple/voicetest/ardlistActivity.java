package com.example.apple.voicetest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.voicetest.adapter.ArdAdapter;
import com.example.apple.voicetest.adapter.AreaAdapter;
import com.example.apple.voicetest.communicate.HttpCallback;
import com.example.apple.voicetest.communicate.HttpURL;
import com.example.apple.voicetest.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ardlistActivity extends AppCompatActivity {

    private int[] images = {R.drawable.tuwen,R.drawable.tushi,R.drawable.tuyan,
            R.drawable.tuec,R.drawable.ph,R.drawable.fengsu,
            R.drawable.kongwen,R.drawable.kongshi,R.drawable.qiya,
            R.drawable.o2,R.drawable.co2,R.drawable.guangqiang};
    private String[] descs = {"土壤温度","土壤湿度","土壤盐度",
            "土壤EC值","PH值","风速",
            "空气温度","空气湿度","气压",
            "氧气浓度","CO2浓度","光照强度"};
    private String[] keys = {"soil_Temp","soil_Humidity","soil_Salinity",
            "soil_EC","soil_PH","wind_Speed",
            "air_Temp","air_Humidity","air_Pressure",
            "O2_Concentration","CO2_Concentration","light_Intensity"};

    protected String TAG = "ard";

    protected String area_number,area_name;

    protected JSONObject areaObject = null;

    protected String temp_max,temp_min,humidity_min,light_min,light_shake;

    protected ListView lv;

    protected Button set_btn;

    protected ArrayList<HashMap<String,Object>> arddatalist;

    protected HttpURL myHttp;

    protected ArdAdapter madapter;

    protected Toolbar toolbar;

    protected TextView title;

    protected EditText set_temp_max, set_temp_min, set_humidity_min, set_light_min,set_light_shake;

    protected Handler handler;

    protected Timer timer = null;

    protected TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ardlist);
        lv = (ListView)findViewById(R.id.ardlistview);
        set_btn = (Button)findViewById(R.id.set_threshold);

        final Intent intent = getIntent();
        try {
            areaObject = new JSONObject(intent.getStringExtra("object"));
            areaObject.remove("owner");
            area_name = areaObject.getString("name");
            area_number = areaObject.getString("number");
            temp_max = areaObject.getString("temp_max");
            temp_min = areaObject.getString("temp_min");
            humidity_min = areaObject.getString("humidity_min");
            light_min = areaObject.getString("light_min");
            light_shake = areaObject.getString("light_shake");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        title = (TextView)findViewById(R.id.area_title);
        title.setText(area_name);
        handler = new Handler();

        arddatalist = new ArrayList<HashMap<String,Object>>();

        madapter = new ArdAdapter(this, arddatalist);
        lv.setAdapter(madapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ardnum = (String)arddatalist.get(position).get("ardnum");
                Intent intent1 = new Intent();
                intent1.setClass(ardlistActivity.this, ActivityRecog.class);
                intent1.putExtra("areanum",area_number);
                intent1.putExtra("ardnum",ardnum);
                startActivity(intent1);
            }
        });

        set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ardlistActivity.this);
                builder.setTitle("设置阈值");
                View view = LayoutInflater.from(ardlistActivity.this).inflate(R.layout.dialog,null);

                set_temp_max = view.findViewById(R.id.set_temp_max);
                set_temp_max.setText(temp_max);
                set_temp_min = view.findViewById(R.id.set_temp_min);
                set_temp_min.setText(temp_min);
                set_humidity_min = view.findViewById(R.id.set_humidity_min);
                set_humidity_min.setText(humidity_min);
                set_light_min = view.findViewById(R.id.set_light_min);
                set_light_min.setText(light_min);
                set_light_shake = view.findViewById(R.id.set_light_shake);
                set_light_shake.setText(light_shake);

                builder.setView(view);

                builder.setPositiveButton("确定", null);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( set_temp_max.getText().toString().trim() == ""
                                || set_temp_min.getText().toString().trim() == ""
                                || set_humidity_min.getText().toString().trim() == ""
                                || set_light_min.getText().toString().trim() == "") {
                            Toast.makeText(ardlistActivity.this,"有输入为空！",Toast.LENGTH_LONG).show();
                        } else {
                            temp_max = set_temp_max.getText().toString();
                            temp_min = set_temp_min.getText().toString();
                            if (Integer.parseInt(temp_max) <= (Integer.parseInt(temp_min) + 1)) {
                                Toast.makeText(ardlistActivity.this,"温度最大值与最小值之间差值要大于1！",Toast.LENGTH_LONG).show();
                            } else {
                                humidity_min = set_humidity_min.getText().toString();
                                light_min = set_light_min.getText().toString();
                                light_shake = set_light_shake.getText().toString();

                                try {
                                    areaObject.put("temp_max", temp_max);
                                    areaObject.put("temp_min", temp_min);
                                    areaObject.put("humidity_min", humidity_min);
                                    areaObject.put("light_min", light_min);
                                    areaObject.put("light_shake",light_shake);
                                    Log.e("light_shake",light_shake);
                                    HttpURL tempURL = new HttpURL();
                                    tempURL.SetThreshold("areas/" + areaObject.getString("number") + "/",
                                            areaObject.toString(), new HttpCallback() {
                                        @Override
                                        public void onFinish(String response) throws JSONException {
                                            Log.e("onFinish",response);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                dialog.cancel();
                            }
                        }
                    }
                });
            }
        });

        toolbar = (Toolbar)findViewById(R.id.ardlist_toolbar);
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
                initData();
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

    private void initData() {

        myHttp = new HttpURL();
        String address = "areas-devices-show/" + area_number + "/";
        myHttp.GetWithHttpURLConnection(address,"", new HttpCallback() {
            @Override
            public void onFinish(String response) throws JSONException {
                JSONObject areaObject = new JSONObject(response);
                JSONArray ardArray = areaObject.getJSONArray("distribution");

                arddatalist.clear();
                for(int i = 0; i < ardArray.length(); i ++) {
                    JSONObject ardObject = ardArray.getJSONObject(i);
                    ArrayList<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();
                    for(int j = 0; j < 12; j++){
                        HashMap<String,Object> info = new HashMap<String,Object>();
                        info.put("image", images[j]);
                        info.put("desc", descs[j]);
                        info.put("value", ardObject.getString(keys[j]));
                        data.add(info);
                    }
                    HashMap<String,Object> item = new HashMap<String,Object>();
                    item.put("ardnum", ardObject.getString("mac"));
                    long x = (long) ardObject.getInt("created") * 1000;
                    String created = "";
                    if (x != 0) {
                        created = DateUtil.getDateToString(x);
                    } else {
                        created = "";
                    }
                    item.put("created",created);
                    item.put("content", ardObject.getString("content"));
                    item.put("info",data);
                    arddatalist.add(item);
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
                madapter = new ArdAdapter(ardlistActivity.this, arddatalist);
                lv.setAdapter(madapter);
                lv.setSelection(0);
            }
        }
    };
}

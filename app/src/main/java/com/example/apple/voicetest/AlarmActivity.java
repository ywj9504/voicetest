package com.example.apple.voicetest;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.apple.voicetest.adapter.AlarmAdapter;
import com.example.apple.voicetest.adapter.AreaAdapter;
import com.example.apple.voicetest.communicate.HttpCallback;
import com.example.apple.voicetest.communicate.HttpURL;
import com.example.apple.voicetest.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AlarmActivity extends AppCompatActivity {

    private Handler handler;

    private Toolbar toolbar;

    private JSONArray alarmArray;

    private SharedPreferences preferences;

    private ListView lv;

    private AlarmAdapter madapter = null;

    private Calendar cal;

    private int year,month,day;

    private Button select_date,get_alarm;

    private Spinner select_area,select_ard;

    private JSONArray area_device_array;

    List<String> areas,ards;

    private ArrayAdapter<String> areaadapter,ardadapter;

    private HashMap<String,Integer> area_map;

    private ArrayList<HashMap<String, Object>> datalist = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        lv = (ListView) findViewById(R.id.alarm_listview);

        handler = new Handler();
        preferences = this.getSharedPreferences("cookie",MODE_PRIVATE);
        area_map = new HashMap<>();

        toolbar = (Toolbar)findViewById(R.id.alarm_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_return);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getDate();
        select_date = (Button) findViewById(R.id.select_date2);
        select_area = (Spinner) findViewById(R.id.select_area2);
        select_ard = (Spinner) findViewById(R.id.select_ard2);
        get_alarm = (Button) findViewById(R.id.get_alarm);

        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day) {
                        select_date.setText(year+"年"+(++month)+"月"+day+"日");      //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                    }
                };
                DatePickerDialog dialog=new DatePickerDialog(AlarmActivity.this,0,listener,year,month,day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            select_date.setText("");
                        }
                });
                dialog.show();
            }
        });

        get_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String get_date = select_date.getText().toString();
                String get_ard ="",get_area = "";
                if(select_ard != null && select_ard.getSelectedItem()!= null){

                    get_ard = select_ard.getSelectedItem().toString();
                }
                if(select_area != null && select_area.getSelectedItem()!= null){
                    get_area = select_area.getSelectedItem().toString();
                }

                String params = "";
                if(!get_date.equals("")) {
                    long date = DateUtil.getStringToDate(get_date + " 00:00:00") / 1000;
                    params += "time=" + String.format("%010d", date);
                }
                if(!get_area.equals("")) {
                    if (!params.equals("")) {
                        params += "&";
                    }
                    params += "area_number=" + area_map.get(get_area);
                }
                if(!get_ard.equals("")) {
                    if (!params.equals("")) {
                        params += "&";
                    }
                    params += "ard_mac=" + get_ard;
                }

                String address;
                if(params.equals("")) {
                    address = "alarmlist-user/";
                } else {
                    address = "alarmlist-condition?" + params;
                }
                HttpURL myHttp = new HttpURL();
                myHttp.GetWithHttpURLConnection(address, preferences.getString("sessionid",""), new HttpCallback() {
                    @Override
                    public void onFinish(String response) throws JSONException {
                        alarmArray = new JSONArray(response);
                        handler.post(runnableUi);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("onError",e.toString());
                    }
                });
            }
        });

        getData();
    }

    protected void getData() {
        String address = "alarmlist-user/";
        HttpURL myHttp = new HttpURL();
        myHttp.GetWithHttpURLConnection(address, preferences.getString("sessionid",""), new HttpCallback() {
            @Override
            public void onFinish(String response) throws JSONException {
                alarmArray = new JSONArray(response);
                handler.post(runnableUi);
            }

            @Override
            public void onError(Exception e) {
                Log.e("onError",e.toString());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        initSpinner();
    }

    //获取当前日期
    private void getDate() {
        cal= Calendar.getInstance();
        year=cal.get(Calendar.YEAR);       //获取年月日时分秒
        month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day=cal.get(Calendar.DAY_OF_MONTH);
    }

    protected void initSpinner() {
        HttpURL myHttp = new HttpURL();
        SharedPreferences preferences = getSharedPreferences("cookie", MODE_PRIVATE);
        String address = "areas-devices-list/";
        myHttp.GetWithHttpURLConnection(address,preferences.getString("sessionid", ""), new HttpCallback() {
            @Override
            public void onFinish(String response) throws JSONException {
                area_device_array = new JSONArray(response);
                handler.post(runnableStart);
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
            try {
                if (datalist == null) {
                    datalist = new ArrayList<>();
                } else {
                    datalist.clear();
                }
                for (int i = 0; i < alarmArray.length(); i ++) {
                    JSONObject object = alarmArray.getJSONObject(i);
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    String area_ard = object.getString("Area_name");
                    area_ard = area_ard + "   " + object.getString("Ard_mac");
                    map.put("area_ard",area_ard);
                    long s = ((long)object.getInt("created") * 1000);
                    String alarm_time = DateUtil.getDateToString(s) + "\n|\n";
                    long e = ((long)object.getInt("end_time") * 1000);
                    alarm_time += DateUtil.getDateToString(e);
                    map.put("alarm_time",alarm_time);
                    String temp = object.getString("content");
                    String alarm_type = "";
                    String[] array = temp.split(",");
                    for (int j = 0; j < array.length; j++){
                        if(array[j].equals("LOW_AIR_TEMPERATURE")) {
                            alarm_type = alarm_type + "温度过低；";
                            continue;
                        } else if (array[j].equals("LOW_SOIL_HUMIDITY")) {
                            alarm_type = alarm_type + "湿度过低；";
                            continue;
                        } else if (array[j].equals("LOW_LIGHT_INTENSITY")) {
                            alarm_type = alarm_type + "光照过低；";
                            continue;
                        } else if (array[j].equals("HIGH_AIR_TEMPERATURE")) {
                            alarm_type = alarm_type + "温度过高；";
                            continue;
                        }
                    }
                    map.put("alarm_type",alarm_type);
                    datalist.add(map);
                }
                if (madapter != null) {
                    madapter.notifyDataSetChanged();
                    lv.setSelection(0);
                } else {
                    Log.e("..","...............");
                    madapter = new AlarmAdapter(AlarmActivity.this, datalist);
                    lv.setAdapter(madapter);
                    lv.setSelection(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    Runnable runnableStart = new Runnable() {
        @Override
        public void run() {
            if(areas == null) {
                areas = new ArrayList<String>();
                areas.add("");
            } else {
                areas.clear();
                areas.add("");
            }
            if(ards == null) {
                ards = new ArrayList<String>();
            } else {
                ards.clear();
            }
            for (int i = 0; i < area_device_array.length(); i++) {
                try {
                    JSONObject object = area_device_array.getJSONObject(i);
                    areas.add(object.getString("name"));
                    area_map.put(object.getString("name"),object.getInt("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            areaadapter = new ArrayAdapter<String>(AlarmActivity.this,R.layout.spinner_text,areas);
            areaadapter.setDropDownViewResource(R.layout.spinner_text);

            ardadapter = new ArrayAdapter<String>(AlarmActivity.this,R.layout.spinner_text,ards);
            ardadapter.setDropDownViewResource(R.layout.spinner_text);

            select_area.setAdapter(areaadapter);
            select_ard.setAdapter(ardadapter);

            select_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        try {
                            JSONObject object = area_device_array.getJSONObject(position-1);
                            JSONArray ard_array = object.getJSONArray("distribution");
                            ards.clear();
                            ards.add("");
                            for (int j = 0; j < ard_array.length(); j++) {
                                ards.add(ard_array.getJSONObject(j).getString("mac"));
                            }
                            ardadapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ards.clear();
                        ardadapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    };
}

package com.example.apple.voicetest;

import android.app.DatePickerDialog;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.apple.voicetest.adapter.AreaAdapter;
import com.example.apple.voicetest.communicate.HistoryCallback;
import com.example.apple.voicetest.communicate.HttpCallback;
import com.example.apple.voicetest.communicate.HttpURL;
import com.example.apple.voicetest.util.DateUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RadioGroup radioGroup;

    private Toolbar toolbar;

    private Button select_date,get_his;
    private Spinner select_area,select_ard;
    private JSONArray area_device_array;

    private long date;

    List<String> areas,ards;
    private ArrayAdapter<String> areaadapter,ardadapter;
    private Handler handler = null;
    private int line_type = 1;

    private Calendar cal;
    private int year,month,day;

    private String[] descs = {"土壤温度","土壤湿度","土壤盐度",
            "土壤EC值","PH值","风速",
            "空气温度","空气湿度",//气压,
            "氧气浓度","CO2浓度","光照强度"};
    private String[] keys = {"soil_Temp","soil_Humidity","soil_Salinity",
            "soil_EC","soil_PH","wind_Speed",
            "air_Temp","air_Humidity",//"air_Pressure",
            "O2_Concentration","CO2_Concentration","light_Intensity" };
    private List<List<String>> data;
    private List<Long> Xlist;

    private LineChart mlineChart;
    private XAxis xAxis;
    private YAxis leftAxis,rightAxis;
    private List<ILineDataSet> mlineDataSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        handler = new Handler();

        radioGroup = (RadioGroup)findViewById(R.id.select_line);
        for(int i = 0; i < descs.length; i++)
        {
            RadioButton tempButton = new RadioButton(this);
            tempButton.setText(descs[i]);
            radioGroup.addView(tempButton);
            if (i == 0) {
                radioGroup.check(tempButton.getId());
            }
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                line_type = checkedId;
//                Log.e("button",String.valueOf(checkedId));
                if (mlineDataSets != null) {
                    setLine(line_type);
                }
            }
        });

        toolbar = (Toolbar)findViewById(R.id.history_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_return);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getDate();
        select_date = (Button)findViewById(R.id.select_date);
        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker arg0, int year, int month, int day) {
                        select_date.setText(year+"年"+(++month)+"月"+day+"日");      //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                    }
                };
                DatePickerDialog dialog=new DatePickerDialog(HistoryActivity.this,0,listener,year,month,day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
                dialog.show();
            }
        });

        select_area = (Spinner) findViewById(R.id.select_area);
        select_ard = (Spinner) findViewById(R.id.select_ard);
        get_his = (Button) findViewById(R.id.get_his);

        get_his.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String get_area = select_area.getSelectedItem().toString();
                String get_ard = "";
                if(select_ard != null){
                    get_ard = select_ard.getSelectedItem().toString();
                }
                String get_date = select_date.getText().toString();
                if (get_ard.equals("") || get_date.equals("")) {
                    Toast.makeText(HistoryActivity.this,"未选完输入条件！",Toast.LENGTH_LONG).show();
                } else {
//                    history-iot/00-00-00-00-00-01/1540051200
                    date = DateUtil.getStringToDate(get_date + " 00:00:00") / 1000;
                    String d = String.format("%010d", date);
                    String address = "history-iot/" + get_ard + "/" + d;
                    HttpURL myhttp = new HttpURL();
                    myhttp.GetHistory(address, new HistoryCallback() {
                        @Override
                        public void onFinish(String response) throws JSONException {
                            JSONArray history_array = new JSONArray(response);

                            addDataSets(history_array);
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });

        initLineChart();
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
            if(areas == null) {
                areas = new ArrayList<String>();
            } else {
                areas.clear();
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            areaadapter = new ArrayAdapter<String>(HistoryActivity.this,R.layout.spinner_text,areas);
            areaadapter.setDropDownViewResource(R.layout.spinner_text);

            ardadapter = new ArrayAdapter<String>(HistoryActivity.this,R.layout.spinner_text,ards);
            ardadapter.setDropDownViewResource(R.layout.spinner_text);

            select_area.setAdapter(areaadapter);
            select_ard.setAdapter(ardadapter);

            select_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        JSONObject object = area_device_array.getJSONObject(position);
                        JSONArray ard_array = object.getJSONArray("distribution");
                        ards.clear();
                        for(int j = 0; j < ard_array.length(); j ++) {
                            ards.add(ard_array.getJSONObject(j).getString("mac"));
                        }
                        ardadapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    };

    public void addDataSets(JSONArray jsonArray) {
        try {
            data = new ArrayList<>();
            Xlist = new ArrayList<>();
            for (int i = (jsonArray.length()-1); i >= 0; i --) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                long timestamp = jsonObject.getInt("created");
                Xlist.add(timestamp-date);
                for (int j = 0; j < keys.length ; j++) {
                    if (i == (jsonArray.length()-1)){
                        List<String> d = new ArrayList<>();
                        data.add(d);
                    }
                    String value = jsonObject.getString(keys[j]);
                    data.get(j).add(value);
                }
            }
            mlineDataSets = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                List<String> d = data.get(i);
                List<Entry> chartData = new ArrayList<>();
                for (int j = 0; j < d.size(); j ++) {
                    chartData.add(new Entry ((float)Xlist.get(j), Float.parseFloat(d.get(j))));
                }
                LineDataSet dataSet = new LineDataSet(chartData, descs[i]);
                mlineDataSets.add(dataSet);
            }
            setLine(line_type);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void initLineChart() {

        mlineChart = (LineChart) findViewById(R.id.lineChart);
        mlineChart.setNoDataText("没有数据");
        mlineChart.setDrawGridBackground(false);
        //显示边界
        mlineChart.setDrawBorders(true);

        //折线图例 标签 设置
//        Legend legend = mlineChart.getLegend();
//        legend.setForm(Legend.LegendForm.LINE);
//        legend.setTextSize(11f);
//        //显示位置
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        legend.setDrawInside(false);

        leftAxis = mlineChart.getAxisLeft();
        rightAxis = mlineChart.getAxisRight();
        rightAxis.setEnabled(false);

        //X轴设置显示位置在底部
        xAxis = mlineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(8);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int hour = (int)value / (60 * 60);
                int mm = (int)value / 60 - 60 * hour;
                int sec = (int)value - 60 * mm - 60 * 60 * hour;
                String xshow = String.format("%02d",hour) + ":" + String.format("%02d",mm) + ":" + String.format("%02d",sec);
                return xshow;
            }
        });
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return DateUtil.getDateToString((long)value+date);
//            }
//        });

        //保证Y轴从0开始，不然会上移一点
        leftAxis.setAxisMinimum(0f);
    }

    public void setLine (int i) {
        LineData lineData;
        if (i > mlineDataSets.size()) {
            lineData = new LineData(mlineDataSets.get(0));
        } else {
            lineData = new LineData(mlineDataSets.get(i-1));
        }
        mlineChart.clear();
        mlineChart.setData(lineData);
        mlineChart.notifyDataSetChanged();
        mlineChart.invalidate();
    }
}

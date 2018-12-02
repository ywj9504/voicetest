package com.example.apple.voicetest.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.apple.voicetest.R;
import com.example.apple.voicetest.adapter.WeatherAdapter;
import com.example.apple.voicetest.communicate.HttpCallback;
import com.example.apple.voicetest.communicate.HttpURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by apple on 2018/10/5.
 */

public class fragment_weather extends Fragment {

    private boolean isViewCreated = false;
    private boolean isUIVisible = false;

    private HttpURL myHttp;

    private Handler handler;

    private JSONObject weather_obj;

    private TextView degree_text,weather_info_text,update_time_text;

    private ArrayList<HashMap<String,Object>> forecast_list;

    private WeatherAdapter weatheradapter = null;

    private ListView lv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        degree_text = (TextView) view.findViewById(R.id.degree_text);
        weather_info_text = (TextView) view.findViewById(R.id.weather_info_text);
        update_time_text = (TextView) view.findViewById(R.id.update_time_text);
        forecast_list = new ArrayList<>();
        lv = (ListView) view.findViewById(R.id.forecast_layout);

        handler = new Handler();

        isViewCreated = true;
        initData();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            isUIVisible = true;
            initData();
        } else {
            isUIVisible = false;
        }
    }

    public void initData() {
        if (isViewCreated && isUIVisible) {
            String address = "https://free-api.heweather.com/s6/weather?location=北京&key=5381e039895c4b6581023364a30149db";
            myHttp = new HttpURL();
            myHttp.GetForecast(address, "", new HttpCallback() {
                @Override
                public void onFinish(String response) throws JSONException {
                    JSONObject obj = new JSONObject(response);
                    weather_obj = obj.getJSONArray("HeWeather6").getJSONObject(0);
                    Log.e("weather",weather_obj.toString());
                    handler.post(runnableUi);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("onError",e.toString());
                    e.printStackTrace();
                }
            });
            isUIVisible = false;
            isViewCreated = false;
        }
    }

    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            try {
                JSONObject update = weather_obj.getJSONObject("update");
                JSONObject now = weather_obj.getJSONObject("now");
                JSONArray daily_forecast = weather_obj.getJSONArray("daily_forecast");
                update_time_text.setText(update.getString("loc"));
                degree_text.setText(now.getString("tmp"));
                weather_info_text.setText(now.getString("cond_txt"));

                forecast_list.clear();
                for(int i = 0; i < daily_forecast.length(); i++) {
                    HashMap<String, Object> map = new HashMap<>();
                    JSONObject obj = daily_forecast.getJSONObject(i);
                    String date = obj.getString("date");
                    if (i == 0) {
                        date = "今天";
                    }
                    map.put("date",date);
                    String img_v = "w_" + obj.getString("cond_code_d");
                    img_v += ".png";
                    try {
                        Field field = R.drawable.class.getField(img_v);
                        int DrawableId = field.getInt(new R.drawable());
                        map.put("image",DrawableId);
                        Log.e("image","image");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    String info = obj.getString("cond_txt_d");
                    map.put("info",info);
                    String tmp = obj.getString("tmp_min");
                    tmp += " ~ " + obj.getString("tmp_max");
                    map.put("tmp",tmp);
                    forecast_list.add(map);
                }

                if (weatheradapter != null) {
                    weatheradapter.notifyDataSetChanged();
                    lv.setSelection(0);
                } else {
                    weatheradapter = new WeatherAdapter(getActivity(), forecast_list);
                    lv.setAdapter(weatheradapter);
                    lv.setSelection(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
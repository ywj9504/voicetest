package com.example.apple.voicetest.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.apple.voicetest.MainActivity;
import com.example.apple.voicetest.R;
import com.example.apple.voicetest.communicate.HttpCallback;
import com.example.apple.voicetest.communicate.HttpURL;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by apple on 2018/10/5.
 */

public class fragment_person extends Fragment {

    private boolean isViewCreated = false;
    private boolean isUIVisible = false;

    private TextView area_count,ard_count,alarm_count,user_name;

    private Button logout;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Handler handler = null;

    private JSONObject obj;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        area_count = (TextView) view.findViewById(R.id.area_count);
        ard_count =(TextView) view.findViewById(R.id.ard_count);
        alarm_count = (TextView) view.findViewById(R.id.alarm_count);
        user_name = (TextView) view.findViewById(R.id.user_name);

        preferences = getActivity().getSharedPreferences("cookie", MODE_PRIVATE);
        editor=preferences.edit();
        handler = new Handler();

        logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove("sessionid");
                editor.commit();
                Intent intent = new Intent();
                intent.setClass(getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        isViewCreated = true;
        initCount();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isUIVisible = true;
            initCount();
        } else {
            isUIVisible = false;
        }
    }

    public void initCount() {
        if (isUIVisible && isViewCreated) {
            String address = "areas-devices-alarms-count/";
            HttpURL myHttp = new HttpURL();
            myHttp.GetWithHttpURLConnection(address, preferences.getString("sessionid", ""), new HttpCallback() {
                @Override
                public void onFinish(String response) throws JSONException {
                    obj = new JSONObject(response);
                    handler.post(runnableUI);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
            isUIVisible = false;
            isViewCreated = false;
        }
    }

    Runnable runnableUI = new Runnable() {
        @Override
        public void run() {
            try {
                user_name.setText(preferences.getString("username",""));
                area_count.setText("" + obj.getInt("areas_count"));
                ard_count.setText("" + obj.getInt("devices_count"));
                alarm_count.setText("" + obj.getInt("alarms_count"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
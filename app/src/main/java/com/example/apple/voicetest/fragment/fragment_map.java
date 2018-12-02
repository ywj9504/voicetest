package com.example.apple.voicetest.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.apple.voicetest.R;
import com.example.apple.voicetest.communicate.HttpCallback;
import com.example.apple.voicetest.communicate.HttpURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by apple on 2018/10/5.
 */

public class fragment_map extends Fragment {

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;

    private List<OverlayOptions> options;

    private OverlayOptions center;

    private SharedPreferences preferences;
    private Handler handler = null;

    private boolean isViewCreated = false;
    private boolean isUIVisible = false;

    private BitmapDescriptor bitmap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map, container, false);
        mMapView = view.findViewById(R.id.map_view);
        preferences = getActivity().getSharedPreferences("cookie", MODE_PRIVATE);
        options = new ArrayList<OverlayOptions>();
        handler = new Handler();
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        isViewCreated = true;
        initView();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isUIVisible = true;
            initView();
        } else {
            isUIVisible = false;
        }
    }

    protected void initView() {
        mBaiduMap = mMapView.getMap();
        if (isViewCreated && isUIVisible) {
            getData();
            isViewCreated = false;
            isUIVisible = false;
        }
    }

    protected void getData() {
        HttpURL myHttp = new HttpURL();
        String address = "arealist/";
        myHttp.GetWithHttpURLConnection(address,preferences.getString("sessionid", ""), new HttpCallback() {
            @Override
            public void onFinish(String response) throws JSONException {
                options.clear();
                JSONArray jsonArray = new JSONArray(response);
                double centerlatitude = 0, centerlongitude = 0;
                for(int i = 0; i < jsonArray.length(); i ++) {
                    JSONObject tempObject = jsonArray.getJSONObject(i);
                    LatLng point = new LatLng(tempObject.getDouble("latitude"),tempObject.getDouble("longitude"));
                    OverlayOptions option =  new MarkerOptions()
                            .position(point)
                            .icon(bitmap);
                    options.add(option);
                    centerlatitude += (tempObject.getDouble("latitude") / jsonArray.length());
                    centerlongitude += (tempObject.getDouble("longitude") / jsonArray.length());
                }
                LatLng centerpoint = new LatLng(centerlatitude,centerlongitude);
                handler.post(runnableUi);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            mBaiduMap.clear();
            mBaiduMap.addOverlays(options);
        }
    };
}
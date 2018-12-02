package com.example.apple.voicetest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.example.apple.voicetest.communicate.HttpCallback;
import com.example.apple.voicetest.communicate.HttpURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 2018/10/25.
 */

public class MapActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;

    private SharedPreferences preferences;
    private Handler handler = null;

    private List<LatLng> points;

    private List<OverlayOptions> options;

    private BitmapDescriptor bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
//        initViews();
        SDKInitializer.initialize(getApplicationContext());

        mMapView = (MapView) findViewById(R.id.map_view);
        preferences = getSharedPreferences("cookie", MODE_PRIVATE);
        options = new ArrayList<OverlayOptions>();
        handler = new Handler();
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        mBaiduMap = mMapView.getMap();
        points = new ArrayList<>();

        initView();
    }

    public void initView() {
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
                    points.add(point);
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
            mBaiduMap.addOverlays(options);
            mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (LatLng p : points) {
                        builder = builder.include(p);
                    }
                    LatLngBounds latlngBounds = builder.build();
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(latlngBounds,mMapView.getWidth(),mMapView.getHeight());
                    mBaiduMap.animateMapStatus(u);
                }
            });
        }
    };
}

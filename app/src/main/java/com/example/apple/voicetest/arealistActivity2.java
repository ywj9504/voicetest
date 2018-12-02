package com.example.apple.voicetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.example.apple.voicetest.adapter.AreaAdapter;
import com.example.apple.voicetest.communicate.HttpURL;

import java.util.ArrayList;
import java.util.HashMap;

public class arealistActivity2 extends AppCompatActivity {

    protected String user;

    protected ListView lv;

    protected Button ret;
    protected Button op40;
    protected Button op41;
    protected Button op42;
    protected Button op43;
    protected Button op44;
    protected Button op45;
    protected Button op46;
    protected Button op47;
    protected Button op48;



    protected ArrayList<HashMap<String,String>> datalist;

    protected HttpURL myHttp;

    protected AreaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arealist);
//        lv = (ListView)findViewById(R.id.arealistview);
//        ret = (Button)findViewById(R.id.arearet);
//
//        Intent intent = getIntent();
//        user = intent.getStringExtra("user");
//
//        datalist = new ArrayList<HashMap<String,String>>();
//
//
//        adapter = new AreaAdapter(this, datalist);
//        lv.setAdapter(adapter);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent();
//                intent.setClass(arealistActivity.this, ardlistActivity.class);
//                intent.putExtra("areanum", datalist.get(position).get("num"));
//                startActivity(intent);
//            }
//        });
//
//        ret.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                arealistActivity.this.finish();
//            }
//        });
//        init();
    }

//    protected void init() {
//        myHttp = new HttpURL("/api/arealist/" + user);
//        myHttp.sendWithHttpURLConnection(new HttpCallback() {
//            @Override
//            public void onFinish(String response) throws JSONException {
//                ((MyApplication)getApplication()).type = 0;
//                JSONArray arr = new JSONArray(response);
//                ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
//                for(int i = 0; i < arr.length(); i ++){
//                    HashMap<String,String> map = new HashMap<>();
//                    JSONObject temp = (JSONObject) arr.get(i);
//                    map.put("num",temp.getString("number"));
//                    map.put("detail",temp.getString("detail"));
//                    map.put("statu",temp.getString("status"));
//                    map.put("owner",temp.getString("owner"));
//                    list.add(map);
//                }
//                datalist.clear();
//                datalist.addAll(list);
//                if(adapter != null) {
//                    adapter.notifyDataSetChanged();
//                    lv.setSelection(0);
//                } else {
//                    adapter = new AreaAdapter(arealistActivity.this, datalist);
//                    lv.setAdapter(adapter);
//                    lv.setSelection(0);
//                }
//            }
//        });
//    }
}

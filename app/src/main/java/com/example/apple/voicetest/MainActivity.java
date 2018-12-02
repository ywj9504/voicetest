package com.example.apple.voicetest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.voicetest.communicate.LoginCallback;
import com.example.apple.voicetest.communicate.HttpURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "login";

    private SharedPreferences preference;
    private SharedPreferences.Editor editor;

    protected Button netbtn;
    protected EditText userid;
    protected EditText pass;
    protected TextView promptText;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preference =getSharedPreferences("cookie", MODE_PRIVATE);
//        if (preference.getString("sessionid",null)!=null ) {
//            Intent intent = new Intent();
//            intent.setClass(MainActivity.this, TabLayoutPage.class);
//            finish();
//            startActivity(intent);
//        }
        setContentView(R.layout.activity_main);

        editor=preference.edit();

        netbtn = (Button)findViewById(R.id.netBtn);
        userid = (EditText)findViewById(R.id.userId);
        pass = (EditText)findViewById(R.id.pass);
        promptText = (TextView) findViewById(R.id.promptText);

        handler = new Handler();

        netbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> params = new HashMap<>();

                String username = userid.getText().toString().trim();
                String password = pass.getText().toString().trim();
                if (username.equals("") || password.equals("")) {
                    promptText.setText("用户名为空");
                    return;
                }
                params.put("username",username);
                params.put("password",password);
//                params.put("username","admin");
//                params.put("password","bupt626bupt");

                HttpURL myHttp = new HttpURL();
                myHttp.LoginHttpURLConnection("login/",params, new LoginCallback() {
//                HttpURL.POSTWithHttpURLConnection("login/",params, 1,new HttpCallback() {
                    @Override
                    public void setCookie(String sessionid) {
                        editor.putString("sessionid", sessionid);
                        editor.commit();
                    }
                    @Override
                    public void onFinish(String response) throws JSONException {
//                        editor.putString("user_id",response)
                        JSONObject object = new JSONObject(response);
                        editor.remove("user_id");
                        editor.putString("user_id",object.get("id").toString());
                        editor.remove("username");
                        editor.putString("username",object.get("username").toString());
                        editor.commit();
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, TabLayoutPage.class);
                        finish();
                        startActivity(intent);
                    }
                    @Override
                    public void onError(Exception e){
                        handler.post(runnableE);
                    }
                });
            }
        });
    }

    Runnable runnableE = new Runnable() {
        @Override
        public void run() {
            promptText.setText("无法连接服务器！");
        }
    };
}

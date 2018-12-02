package com.example.apple.voicetest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.voicetest.communicate.HttpCallback;
import com.example.apple.voicetest.communicate.HttpURL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class arealist extends AppCompatActivity {

    protected int xtype;
    protected TextView testResult;
    protected EditText puted;
    protected Button testbtn;
    protected BufferedReader reader = null;
    protected BufferedWriter writer = null;
    protected Socket socket;
    protected Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arealist);
//        testbtn = (Button)findViewById(R.id.testbtn);
//        puted = (EditText)findViewById(R.id.put);
//        testResult = (TextView)findViewById(R.id.testResult);
//
//        application = (MyApplication)this.getApplication();
//        if(application.type == 1){
//            socket = application.getSocket();
//            try {
//                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//            } catch (IOException e) {
//                Toast.makeText(getApplicationContext(), "无法再次建立链接", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        handler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                //如果来自子线程
//                if(msg.what == 0x123){
//                    testResult.append(msg.obj.toString()+"\n");
//                    //消息通知
//                }
//            }
//        };
//
//
//        testbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(application.type == 1){
//                    String inputorder = puted.getText().toString().trim();
//                    if(inputorder == ""){
//                        Toast.makeText(getApplicationContext(), "命令为空", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    else {
//                        final String or = inputorder;
//                        Log.e("TAG",or);
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Tcpsend(or);
//                            }
//                        }).start();
//                    }
//                }
//
//                else {
//                    String inputorder = puted.getText().toString().trim();
//                    if(inputorder == ""){
//                        Toast.makeText(getApplicationContext(), "命令为空", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    else {
//                        HttpURL http = new HttpURL(inputorder);
//                        http.sendWithHttpURLConnection(new HttpCallback() {
//                            @Override
//                            public void onFinish(String response) {
//                                testResult.setText("");
//                                testResult.setText(response);
//                            }
//                        });
//                    }
//                }
//                //调用线程
//                myThread();
//            }
//        });
    }

    public void myThread(){
        Thread oneThread = new Thread(new Runnable(){
            public boolean isRunning = true;
            public void run() {
                while(isRunning){
                    try {
                        Log.e("TAG", "in2");
                        Thread.sleep(1000);
                        String line;
                        while (reader.readLine() != null) {
                            line = reader.readLine();
                            //每当读到来自服务器的数据后，发送消息通知程序页面显示数据
                            Message msg = new Message();
                            msg.what = 0x123;
                            msg.obj = line;
                            Log.e("read", line);
                            handler.sendMessage(msg);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        oneThread.start();

    }

    public void Tcpsend(String order){
        try{
            writer.write(order);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("TAG", "before");
        try{
            Log.e("TAG", "start");
            String line;
            while ( (line = reader.readLine())!= null){
                Log.e("TAG", "in");
                Log.e("TAG", line);
            }
        } catch (IOException e) {
            Log.e("TAG","readere");
        }
        Log.e("TAG", "after");
    }
}

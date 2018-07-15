package com.example.apple.voicetest.communicate;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by apple on 2018/5/25.
 */

public class HttpURL {
    private static final String TAG = "Http";
    private final int READ = 1;
    private final int SEND = 2;

    private String baseurl = "http://10.105.242.74/api/";
    private String urlread = "http://10.105.242.74/api/agri";
    private String urlcommand = "http://10.105.242.74/api/order?command=";

    private int type;
    private String order;

    public HttpURL(int _type, String _order) {
        this.type = _type;
        this.order = _order;
    }

    public void sendWithHttpURLConnection(final HttpCallback listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = null;
                    if(type == READ) {
                        url = new URL(urlread);
                        Log.e(TAG, url.toString());
                    }else if(type == SEND) {
                        urlcommand += order;
                        url = new URL(urlcommand);
                        Log.e(TAG, url.toString());
                    }

                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();

                    //下面对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }

                    listener.onFinish(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (reader != null){
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}

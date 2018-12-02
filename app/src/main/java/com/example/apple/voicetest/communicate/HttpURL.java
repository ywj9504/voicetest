package com.example.apple.voicetest.communicate;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by apple on 2018/5/25.
 */

public class HttpURL {
    private static final String TAG = "Http";

//    private SharedPreferences preference;
//
//    private SharedPreferences.Editor editor;

    private static String baseurl = "http://10.101.170.22:2345/api/";

//    private static String baseurl = "http://10.103.246.23:2345/api/";

    public void HttpURL() {
//        preference = getSharedPreferences("cookie", MODE_PRIVATE);
//        editor = preference.edit();
    }

    public void GetWithHttpURLConnection(final String address,final String cookie, final HttpCallback listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = null;
                    String urlcommand = baseurl+address;
                    url = new URL(urlcommand);

                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");

//                    Log.e(TAG,(String)preference.getString("sessionid", ""));
//                    connection.addRequestProperty("Cookie", preference.getString("sessionid", ""));

                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
//                    connection.setDoInput(true);
//                    connection.setDoOutput(true);
                    connection.setRequestProperty("Accept", "application/json");
                    connection.addRequestProperty("Cookie",cookie);

//                    connection.connect();

                    //下面对获取到的输入流进行读取
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    if (listener != null) {
                        //回调Onfinish方法
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
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

    public void SetThreshold(final String address,final String obj,final HttpCallback listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = null;
                    String urlcommand = baseurl+address;
                    url = new URL(urlcommand);

                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("PUT");


		/* optional request header */
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

		/* optional request header */
                    connection.setRequestProperty("Accept", "application/json");





//                    Log.e(TAG,(String)preference.getString("sessionid", ""));
//                    connection.addRequestProperty("Cookie", preference.getString("sessionid", ""));

                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);


                    //设置请求属性
//                    byte[] requestStringBytes = requestString.getBytes(); //获取数据字节数据
//                    connection.setRequestProperty("Content-length", "" + requestStringBytes.length);
//                    connection.setRequestProperty("Content-Type", "application/octet-stream");
//                    connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
//                    connection.setRequestProperty("Charset", "UTF-8");
//
//                    connection.setInstanceFollowRedirects(true);


//                    if (HttpURLConnection.HTTP_OK == responseCode)

                    OutputStream outputStream = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                    writer.write(obj);

                    writer.flush();
                    writer.close();
                    outputStream.close();
                    connection.connect();

                    //下面对获取到的输入流进行读取
                    InputStream in = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    if (listener != null) {
                        //回调Onfinish方法
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
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

    public void GetForecast(final String address,final String cookie, final HttpCallback listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = null;
                    String urlcommand = address;
                    url = new URL(urlcommand);

                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");

//                    Log.e(TAG,(String)preference.getString("sessionid", ""));
//                    connection.addRequestProperty("Cookie", preference.getString("sessionid", ""));

                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
//                    connection.setDoInput(true);
//                    connection.setDoOutput(true);
                    connection.setRequestProperty("Accept", "application/json");
                    connection.addRequestProperty("Cookie",cookie);

//                    connection.connect();

                    //下面对获取到的输入流进行读取
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    if (listener != null) {
                        //回调Onfinish方法
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
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

    public void GetHistory(final String address,final HistoryCallback listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = null;
                    String urlcommand = baseurl+address;
                    url = new URL(urlcommand);

                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");

//                    Log.e(TAG,(String)preference.getString("sessionid", ""));
//                    connection.addRequestProperty("Cookie", preference.getString("sessionid", ""));

                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
//                    connection.setDoInput(true);
//                    connection.setDoOutput(true);

//                    connection.connect();

                    //下面对获取到的输入流进行读取
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    if (listener != null) {
                        //回调Onfinish方法
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
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

    public void LoginHttpURLConnection(final String address,final Map<String,String> params, final LoginCallback listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = null;
                    String urlcommand = baseurl+address;
                    url = new URL(urlcommand);

                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");

//                    Log.e(TAG,(String)preference.getString("sessionid", ""));
//                    connection.addRequestProperty("Cookie", preference.getString("sessionid", ""));

                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);


                    //设置请求属性
//                    byte[] requestStringBytes = requestString.getBytes(); //获取数据字节数据
//                    connection.setRequestProperty("Content-length", "" + requestStringBytes.length);
//                    connection.setRequestProperty("Content-Type", "application/octet-stream");
//                    connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
//                    connection.setRequestProperty("Charset", "UTF-8");
//
//                    connection.setInstanceFollowRedirects(true);


//                    if (HttpURLConnection.HTTP_OK == responseCode)

                    OutputStream outputStream = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                    writer.write(getStringFromOutput(params));

                    writer.flush();
                    writer.close();
                    outputStream.close();
                    connection.connect();

                    //下面对获取到的输入流进行读取
                    InputStream in = connection.getInputStream();
                        String key=null;
                        String cookieVal=null;

                        for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++ ) {
                            if (key.equalsIgnoreCase("Set-Cookie")) {
                                cookieVal = connection.getHeaderField(key);
                                String sessionId = null;
                                sessionId = cookieVal.substring(0, cookieVal.indexOf(";"));
                                if (listener!=null){
                                    listener.setCookie(sessionId);
                                }
                            }
                        }
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    if (listener != null) {
                        //回调Onfinish方法
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
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

    public void POSTHttpURLConnection(final String address,final Map<String,Object> params,
                                          final int PostType,final String cookie,final HttpCallback listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = null;
                    String urlcommand = baseurl+address;
                    url = new URL(urlcommand);

                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");


		/* optional request header */
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

		/* optional request header */
                    connection.setRequestProperty("Accept", "application/json");
                    connection.addRequestProperty("Cookie",cookie);





//                    Log.e(TAG,(String)preference.getString("sessionid", ""));
//                    connection.addRequestProperty("Cookie", preference.getString("sessionid", ""));

                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);


                    //设置请求属性
//                    byte[] requestStringBytes = requestString.getBytes(); //获取数据字节数据
//                    connection.setRequestProperty("Content-length", "" + requestStringBytes.length);
//                    connection.setRequestProperty("Content-Type", "application/octet-stream");
//                    connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
//                    connection.setRequestProperty("Charset", "UTF-8");
//
//                    connection.setInstanceFollowRedirects(true);


//                    if (HttpURLConnection.HTTP_OK == responseCode)

                    OutputStream outputStream = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(params);

                    JSONObject jsonObject = new JSONObject();
                    JSONObject distribution = new JSONObject();
                    JSONArray jsonArray1 = new JSONArray();
                    distribution.put("x",88);
                    distribution.put("y",151);
                    distribution.put("mac","10-40-10-20-10-222");
                    jsonArray1.put(distribution);
                    distribution = new JSONObject();
                    distribution.put("x",88);
                    distribution.put("y",151);
                    distribution.put("mac","10-40-10-20-10-223");
                    jsonArray1.put(distribution);
                    jsonObject.put("name","番茄大棚56号");
                    jsonObject.put("plant","香蕉");
                    jsonObject.put("longitude",105.134521);
                    jsonObject.put("latitude",106.132142);
                    jsonObject.put("distribution",jsonArray1);




                    writer.write(jsonObject.toString());

                    writer.flush();
                    writer.close();
                    outputStream.close();
                    connection.connect();

                    //下面对获取到的输入流进行读取
                    InputStream in = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    if (listener != null) {
                        //回调Onfinish方法
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
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

    /**
     * 将map转换成key1=value1&key2=value2的形式
     * @param map
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String getStringFromOutput(Map<String,String> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;

        for(Map.Entry<String,String> entry:map.entrySet()){
            if(isFirst)
                isFirst = false;
            else
                sb.append("&");

            sb.append(URLEncoder.encode(entry.getKey(),"UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue(),"UTF-8"));
        }
        return sb.toString();
    }
}

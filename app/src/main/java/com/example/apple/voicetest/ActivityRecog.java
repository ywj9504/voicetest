package com.example.apple.voicetest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.baidu.speech.asr.SpeechConstant;
import com.example.apple.voicetest.communicate.HttpCallback;
import com.example.apple.voicetest.communicate.HttpURL;
import com.example.apple.voicetest.communicate.TCPclient;
import com.example.apple.voicetest.control.MyRecognizer;
import com.example.apple.voicetest.recognization.CommonRecogParams;
import com.example.apple.voicetest.recognization.IStatus;
import com.example.apple.voicetest.recognization.MessageStatusRecogListener;
import com.example.apple.voicetest.recognization.OfflineRecogParams;
import com.example.apple.voicetest.recognization.StatusRecogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by apple on 2018/4/1.
 */

public class ActivityRecog extends ActivityCommon implements IStatus{
    /**
     * 识别控制器，使用MyRecognizer控制识别的流程
     */
    protected MyRecognizer myRecognizer;

    /*
     * Api的参数类，仅仅用于生成调用START的json字符串，本身与SDK的调用无关
     */
    protected CommonRecogParams apiParams;

    /*
     * 本Activity中是否需要调用离线命令词功能。根据此参数，判断是否需要调用SDK的ASR_KWS_LOAD_ENGINE事件
     */
    protected boolean enableOffline = true;


    /**
     * 控制UI按钮的状态
     */
    protected int status;

    /**
     * 日志使用
     */
    private static final String TAG = "ActivityRecog";

    /**
     * 记录进行操作的传感器编号
     */
    private int sensor_id = 1;

    private HttpURL myHttp;

    private TCPclient myTcp;

    ExecutorService exec = Executors.newCachedThreadPool();

    public static Context context;

    /**
     * 在onCreate中调用。初始化识别控制类MyRecognizer
     */
    protected void initRecog() {
        StatusRecogListener listener = new MessageStatusRecogListener(handler);
        myRecognizer = new MyRecognizer(this, listener);
        apiParams = getApiParams();
        status = STATUS_NONE;
        if (enableOffline) {
            myRecognizer.loadOfflineEngine(OfflineRecogParams.fetchOfflineParams());
        }
        myTcp = new TCPclient();
        exec.execute(myTcp);
//        exec.execute(new Runnable() {
//            @Override
//            public void run() {
//                myTcp.send("$connect");
//            }
//        });
    }

    /**
     * 销毁时需要释放识别资源。
     */
    @Override
    protected void onDestroy() {
        myRecognizer.release();
        Log.i(TAG, "onDestory");
        super.onDestroy();
    }

    /**
     * 开始录音，点击“开始”按钮后调用。
     */
    protected void start() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ActivityRecog.this);
        //  上面的获取是为了生成下面的Map， 自己集成时可以忽略
        Map<String, Object> params = apiParams.fetch(sp);
        // 集成时不需要上面的代码，只需要params参数。
        // 这里打印出params， 填写至您自己的app中，直接调用下面这行代码即可。

//        Map<String, Object> params = new HashMap<String, Object>();

        myRecognizer.start(params);
    }


    /**
     * 开始录音后，手动停止录音。SDK会识别在此过程中的录音。点击“停止”按钮后调用。
     */
    private void stop() {
        myRecognizer.stop();
    }

    /**
     * 开始录音后，取消这次录音。SDK会取消本次识别，回到原始状态。点击“取消”按钮后调用。
     */
    private void cancel() {
        myRecognizer.cancel();
    }

    /**
     * @return
     */
    protected CommonRecogParams getApiParams() {
        return new OfflineRecogParams(this);
    }



    // 以上为 语音SDK调用，以下为UI部分
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void initView() {
        super.initView();
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (status) {
                    case STATUS_NONE: // 初始状态
                        start();
                        status = STATUS_WAITING_READY;
                        updateBtnTextByStatus();
                        txtLog.setText("");
                        txtResult.setText("");
                        break;
                    case STATUS_WAITING_READY: // 调用本类的start方法后，即输入START事件后，等待引擎准备完毕。
                    case STATUS_READY: // 引擎准备完毕。
                    case STATUS_SPEAKING:
                    case STATUS_FINISHED: // 长语音情况
                    case STATUS_RECOGNITION:
                        stop();
                        status = STATUS_STOPPED; // 引擎识别中
                        updateBtnTextByStatus();
                        break;
                    case STATUS_STOPPED: // 引擎识别中
                        cancel();
                        status = STATUS_NONE; // 识别结束，回到初始状态
                        updateBtnTextByStatus();
                        break;
                    default:
                        break;
                }

            }
        });
    }

    protected void handleMsg(Message msg) {
        super.handleMsg(msg);

        switch (msg.what) { // 处理MessageStatusRecogListener中的状态回调
            case STATUS_FINISHED:
                if (msg.arg2 == 1) {
                    String resultstr = msg.obj.toString();
                    txtResult.setText(resultstr);
                    int IsO = IsOrder(resultstr);
                    switch (IsO){
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            final String tempord = String.valueOf(sensor_id) + "," + String.valueOf(IsO);
                            exec.execute(new Runnable() {
                                @Override
                                public void run() {
                                    myTcp.send("$command:" + tempord);
                                }
                            });
//                            myHttp = new HttpURL(2, tempord);
//                            myHttp.sendWithHttpURLConnection(new HttpCallback() {
//                                @Override
//                                public void onFinish(String response) {
//                                    txtResponse.setText(response);
//                                }
//                            });
                            break;
                        case 7:
                            myHttp = new HttpURL(1,"");
                            myHttp.sendWithHttpURLConnection(new HttpCallback() {
                                @Override
                                public void onFinish(String response) {
                                    txtResponse.setText(response);
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }
                status = msg.what;
                updateBtnTextByStatus();
                break;
            case STATUS_NONE:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                status = msg.what;
                updateBtnTextByStatus();
                break;
            default:
                break;

        }
    }

    private int IsOrder(String resultstr) {
        if(resultstr.indexOf("一")!=-1)
            sensor_id = 1;
        if(resultstr.indexOf("二")!=-1)
            sensor_id = 2;
        String[] order = new String[8];
        order[0] = "关闭灯泡";
        order[1] = "打开灯泡";
        order[2] = "打开水泵";
        order[3] = "关闭水泵";
        order[4] = "关闭温度调节";
        order[5] = "打开加热器";
        order[6] = "打开制冷器";
        order[7] = "查看数据";
        for(int i = 0; i <= 6; i++) {
            if(resultstr.indexOf(order[i]) != -1)
                return i;
        }
        return -1;
    }

    private void updateBtnTextByStatus() {
        switch (status) {
            case STATUS_NONE:
                btn.setText("开始录音");
                btn.setEnabled(true);
                setting.setEnabled(true);
                break;
            case STATUS_WAITING_READY:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                btn.setText("停止录音");
                btn.setEnabled(true);
                setting.setEnabled(false);
                break;

            case STATUS_STOPPED:
                btn.setText("取消整个识别过程");
                btn.setEnabled(true);
                setting.setEnabled(false);
                break;
            default:
                break;
        }
    }
}

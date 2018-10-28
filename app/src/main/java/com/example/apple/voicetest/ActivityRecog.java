package com.example.apple.voicetest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.asr.SpeechConstant;
import com.example.apple.voicetest.adapter.ArdAdapter;
import com.example.apple.voicetest.adapter.ControlAdapter;
import com.example.apple.voicetest.adapter.InfoAdapter;
import com.example.apple.voicetest.communicate.HttpCallback;
import com.example.apple.voicetest.communicate.HttpURL;
import com.example.apple.voicetest.control.MyRecognizer;
import com.example.apple.voicetest.custom.CustomGridView;
import com.example.apple.voicetest.recognization.CommonRecogParams;
import com.example.apple.voicetest.recognization.IStatus;
import com.example.apple.voicetest.recognization.MessageStatusRecogListener;
import com.example.apple.voicetest.recognization.OfflineRecogParams;
import com.example.apple.voicetest.recognization.StatusRecogListener;
import com.example.apple.voicetest.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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


    public static Context context;

    private String result_text = "";

    /**
     * 记录进行操作的传感器编号
     */
    private String areanum,ardnum,alarm_content,created_time;

    private TextView ard_num,alarm,created;

    private int[] images = {R.drawable.tuwen,R.drawable.tushi,R.drawable.tuyan,
            R.drawable.tuec,R.drawable.ph,R.drawable.fengsu,
            R.drawable.kongwen,R.drawable.kongshi,R.drawable.qiya,
            R.drawable.o2,R.drawable.co2,R.drawable.guangqiang,
            R.drawable.wenkong,R.drawable.dengpao,R.drawable.fengshan,R.drawable.shuibeng};
    private String[] descs = {"土壤温度","土壤湿度","土壤盐度",
            "土壤EC值","PH值","风速",
            "空气温度","空气湿度","气压",
            "氧气浓度","CO2浓度","光照强度",
            "温控片","灯泡","风扇","水泵"};
    private String[] keys = {"soil_Temp","soil_Humidity","soil_Salinity",
            "soil_EC","soil_PH","wind_Speed",
            "air_Temp","air_Humidity","air_Pressure",
            "O2_Concentration","CO2_Concentration","light_Intensity",
            "temp_control","light_control","fan_control","waterPump_control" };

    private ArrayList<HashMap<String,Object>> infodatalist,controllerlist;

    private HttpURL myHttp;

    private Timer timer;

    private TimerTask task;

    private InfoAdapter infoadapter;

    private ControlAdapter controladapter;

    private GridView info_GridView,control_GridView;

    private Toolbar toolbar;

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
//        super.initView();
        setContentView(R.layout.activity_recog);
//        txtResult = (TextView) findViewById(R.id.txtResult);
//        txtResponse = (TextView) findViewById(R.id.getMsg);
//        txtLog = (TextView) findViewById(R.id.txtLog);
//        btn = (Button) findViewById(R.id.btn);
        btn = (Button) findViewById(R.id.operate);
        ard_num = (TextView) findViewById(R.id.ardnum2);
        created = (TextView) findViewById(R.id.created2);
        alarm = (TextView) findViewById(R.id.alarm2);

        Intent intent = getIntent();
        areanum = intent.getStringExtra("areanum");
        ardnum = intent.getStringExtra("ardnum");

        infodatalist = new ArrayList<HashMap<String,Object>>();
        controllerlist = new ArrayList<HashMap<String,Object>>();
        info_GridView = (GridView) findViewById(R.id.show_info);
        control_GridView = (GridView) findViewById(R.id.show_devices);

        toolbar = (Toolbar) findViewById(R.id.ard_info_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_return);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (status) {
                    case STATUS_NONE: // 初始状态
                        start();
                        status = STATUS_WAITING_READY;
                        updateBtnTextByStatus();
//                        txtLog.setText("");
//                        txtResult.setText("");
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
//                    txtResult.setText(resultstr);
//                    Toast.makeText(ActivityRecog.this,resultstr,Toast.LENGTH_LONG).show();
                    int IsO = IsOrder(resultstr);
                    if (IsO != -1) {
                        String address = "order?area_number="+ areanum +"&ard_mac=" + ardnum + "&command=";
                        if (IsO == 0 || IsO == 1 || IsO == 3 || IsO == 4) {
                            result_text = "开灯";
                            address += "0";
                        } else if (IsO == 5 || IsO == 6 || IsO == 7 || IsO == 8 || IsO == 9 || IsO == 10 || IsO == 11 || IsO == 12){
                            result_text = "关灯";
                            address += "1";
                        } else if (IsO == 13 || IsO == 14 || IsO == 15 || IsO == 16){
                            result_text = "打开水泵";
                            address += "2";
                        } else if (IsO == 17 || IsO == 18 || IsO == 19 || IsO == 20){
                            result_text = "关闭水泵";
                            address += "3";
                        } else if (IsO == 21 || IsO == 22 || IsO == 23){
                            result_text = "停止温控";
                            address += "4";
                        } else if (IsO == 24 || IsO == 25 || IsO == 26){
                            result_text = "加热";
                            address += "5";
                        } else if (IsO == 27 || IsO == 28 || IsO == 29){
                            result_text = "制冷";
                            address += "6";
                        } else if (IsO == 30 || IsO == 31 || IsO == 32 || IsO == 33 || IsO == 34 || IsO == 35 || IsO == 36){
                            result_text = "开风扇";
                            address += "7";
                        } else if (IsO == 37 || IsO == 38 || IsO == 39 ){
                            result_text = "关风扇";
                            address += "8";
                        }
                        myHttp.GetWithHttpURLConnection(address, "", new HttpCallback() {
                            @Override
                            public void onFinish(String response) throws JSONException {
                                handler.post(runnablesucceed);
                            }

                            @Override
                            public void onError(Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        Toast.makeText(ActivityRecog.this,"识别错误:"+resultstr,Toast.LENGTH_LONG).show();
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
        String[] order = new String[40];
        order[0] = "关闭灯泡";
        order[1] = "关灯";
        order[2] = "关东";
        order[3] = "官东";
        order[4] = "广东";

        order[5] = "打开灯泡";
        order[6] = "开灯";
        order[7] = "胎动";
        order[8] = "台东";
        order[9] = "开通";
        order[10] = "台灯";
        order[11] = "开东";
        order[12] = "开动";

        order[13] = "打开水泵";
        order[14] = "开水";
        order[15] = "浇水";
        order[16] = "胶水";

        order[17] = "关闭水泵";
        order[18] = "关水";
        order[19] = "关税";
        order[20] = "灌水";

        order[21] = "关闭温度调节";
        order[22] = "关闭温控";
        order[23] = "停止温控";

        order[24] = "打开加热器";
        order[25] = "升温";
        order[26] = "加热";

        order[27] = "打开制冷器";
        order[28] = "降温";
        order[29] = "制冷";

        order[30] = "通风";
        order[31] = "痛风";
        order[32] = "开风扇";
        order[33] = "打开风扇";
        order[34] = "开封";
        order[35] = "开电扇";
        order[36] = "打开电扇";

        order[37] = "关风扇";
        order[38] = "关闭风扇";
        order[39] = "官风";
        for(int i = 0; i <= 39; i++) {
            if(resultstr.indexOf(order[i]) != -1)
                return i;
        }
        return -1;
    }

    private void updateBtnTextByStatus() {
        switch (status) {
            case STATUS_NONE:
                btn.setText("下发指令");
                btn.setEnabled(true);
//                setting.setEnabled(true);
                break;
            case STATUS_WAITING_READY:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                btn.setText("停止录音");
                btn.setEnabled(true);
//                setting.setEnabled(false);
                break;

            case STATUS_STOPPED:
                btn.setText("取消整个识别过程");
                btn.setEnabled(true);
//                setting.setEnabled(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        destroyTimer();
        initTimer();
        timer.schedule(task,0,10000);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        destroyTimer();
        initTimer();
        timer.schedule(task,0,10000);
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    protected void initTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                initData();
            }
        };
    }

    protected void destroyTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    protected void initData() {
        myHttp = new HttpURL();
        String address = "areas-devices-show/" + areanum + "/";
        myHttp.GetWithHttpURLConnection(address,"", new HttpCallback() {
            @Override
            public void onFinish(String response) throws JSONException {
                JSONObject areaObject = new JSONObject(response);
                JSONArray ardArray = areaObject.getJSONArray("distribution");

                infodatalist.clear();
                controllerlist.clear();
                for(int i = 0; i < ardArray.length(); i ++) {
                    JSONObject ardObject = ardArray.getJSONObject(i);
                    if (ardObject.getString("mac").equals(ardnum)) {
//                        ard_num.setText(ardnum);
//                        content.setText((String) ardObject.getString("content"));
                        alarm_content = ardObject.getString("content");
                        long x = ardObject.getInt("created") * 1000;
                        created_time = DateUtil.getDateToString(x);
                        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
                        for (int j = 0; j < 12; j++) {
                            HashMap<String, Object> info = new HashMap<String, Object>();
                            info.put("image", images[j]);
                            info.put("desc", descs[j]);
                            info.put("value", ardObject.getString(keys[j]));
                            infodatalist.add(info);
                        }
                        for(int j = 12; j < 16; j++){
                            HashMap<String, Object> info = new HashMap<String, Object>();
                            info.put("image", images[j]);
                            info.put("desc", descs[j]);
                            if (j == 12) {
                                if (ardObject.getInt(keys[j]) == 0) {
                                    info.put("value", "关");
                                } else if (ardObject.getInt(keys[j]) == 1) {
                                    info.put("value", "降温");
                                } else if (ardObject.getInt(keys[j]) == 2) {
                                    info.put("value", "升温");
                                }
                            } else {
                                if (ardObject.getInt(keys[j]) == 0) {
                                    info.put("value", "关");
                                } else if(ardObject.getInt(keys[j]) == 1) {
                                    info.put("value", "开");
                                }
                            }
                            controllerlist.add(info);
                        }
                        handler.post(runnableUi);
                        break;
                    }
                }
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            ard_num.setText(ardnum);
            created.setText(created_time);
            String alarmi = "";
            String[] array = alarm_content.split(",");
            for (int i = 0; i < array.length; i++){
                if(array[i].equals("LOW_AIR_TEMPERATURE")) {
                    alarmi = alarmi + "温度过低；";
                    continue;
                } else if (array[i].equals("LOW_SOIL_HUMIDITY")) {
                    alarmi = alarmi + "湿度过低；";
                    continue;
                } else if (array[i].equals("LOW_LIGHT_INTENSITY")) {
                    alarmi = alarmi + "光照过低；";
                    continue;
                } else if (array[i].equals("HIGH_AIR_TEMPERATURE")) {
                    alarmi = alarmi + "温度过高；";
                    continue;
                }
            }
            alarm.setText(alarmi);
            if (infoadapter != null) {
                infoadapter.notifyDataSetChanged();
                info_GridView.setSelection(0);
            } else {
                infoadapter = new InfoAdapter(ActivityRecog.this, infodatalist);
                info_GridView.setAdapter(infoadapter);
                info_GridView.setSelection(0);
            }

            if(controladapter != null) {
                controladapter.notifyDataSetChanged();
                control_GridView.setSelection(0);
            } else {
                controladapter = new ControlAdapter(ActivityRecog.this, controllerlist);
                control_GridView.setAdapter(controladapter);
                control_GridView.setSelection(0);
            }
        }
    };
    Runnable runnablesucceed = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(ActivityRecog.this,"指令:"+result_text+"下发成功",Toast.LENGTH_LONG).show();
        }
    };
}

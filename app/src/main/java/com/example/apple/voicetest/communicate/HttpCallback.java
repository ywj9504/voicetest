package com.example.apple.voicetest.communicate;

import org.json.JSONException;

/**
 * Created by apple on 2018/5/30.
 */

public interface HttpCallback {
    void onFinish(String response) throws JSONException;
    void onError(Exception e);
}

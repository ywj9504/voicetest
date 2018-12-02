package com.example.apple.voicetest.communicate;

import org.json.JSONException;

/**
 * Created by apple on 2018/10/19.
 */

public interface LoginCallback {
    void setCookie(String sessionid);
    void onFinish(String response) throws JSONException;
    void onError(Exception e);
}

package com.mediatek.dialer.ext;

import android.content.Intent;
import android.util.Log;

public class DialtactsExtension {
    private static final String TAG = "DialtactsExtension";

    public boolean checkComponentName(Intent intent, String commd) {
        log("checkComponentName");
        return false;
    }

    public boolean startActivity(String commd) {
        log("startActivity");
        return false;
    }

    private void log(String msg) {
        Log.d(TAG, msg + " default");
    }
}

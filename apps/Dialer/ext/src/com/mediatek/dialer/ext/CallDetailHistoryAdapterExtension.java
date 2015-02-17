package com.mediatek.dialer.ext;

import com.mediatek.dialer.PhoneCallDetailsEx;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


public class CallDetailHistoryAdapterExtension {

    private static final String TAG = "CallDetailHistoryAdapterExtension";

    /**
     * @param context
     * @param phoneCallDetails
     */
    public void init(Context context, PhoneCallDetailsEx[] phoneCallDetails) {
        log("init");
    }

    public int getItemViewType(int position) {
        log("getItemViewType");
        return -1;
    }

    public int getViewTypeCount(int currentViewTypeCount) {
        log("getViewTypeCount");
        return currentViewTypeCount;
    }

    public View getViewPre(int position, View convertView, ViewGroup parent) {
        log("getViewPre");
        return null;
    }

    public View getViewPost(int position, View convertView, ViewGroup parent) {
        log("getViewPost");
        return convertView;
    }

    private void log(String msg) {
        Log.d(TAG, msg + " default");
    }
}

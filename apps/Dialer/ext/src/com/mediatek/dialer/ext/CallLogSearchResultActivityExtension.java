package com.mediatek.dialer.ext;

import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class CallLogSearchResultActivityExtension {
    private static final String TAG = "CallLogSearchResultActivityExtension";

    public boolean onListItemClick(ListView l, View v, int position, long id) {
        log("onListItemClick");
        return false;
    }

    private void log(String msg) {
        Log.d(TAG, msg + " default");
    }
}

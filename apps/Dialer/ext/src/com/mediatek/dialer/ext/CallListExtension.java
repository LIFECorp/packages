package com.mediatek.dialer.ext;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

public class CallListExtension {
    private final static String TAG = "CallListExtension";

    public int layoutExtentionIcon(int leftBound, int topBound, int bottomBound, int rightBound,
            int mGapBetweenImageAndText, ImageView mExtentionIcon, String commd) {
        log("layoutExtentionIcon");
        return rightBound;
    }

    public void measureExtention(ImageView mExtentionIcon, String commd) {
        log("measureExtention");
    }

    public void setExtentionImageView(ImageView view, String commd) {
        log("setExtentionImageView");
    }

    public boolean setExtentionIcon(String number, String commd) {
        log("setExtentionIcon");
        return false;
    }

    public boolean checkPluginSupport(String commd) {
        log("checkPluginSupport");
        return false;
    }

    public void onCreate(ListFragment fragment) {
        log("onCreate");
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        log("onViewCreated");
    }

    public void onDestroy() {
        log("onDestroy");
    }

    public boolean onListItemClick(ListView l, View v, int position, long id) {
        log("onListItemClick");
        return false;
    }

    public boolean onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        log("onCreateContextMenu");
        return false;
    }

    public boolean onContextItemSelected(MenuItem item) {
        log("onContextItemSelected");
        return false;
    }

    private void log(String msg) {
        Log.d(TAG, msg + " default");
    }
}

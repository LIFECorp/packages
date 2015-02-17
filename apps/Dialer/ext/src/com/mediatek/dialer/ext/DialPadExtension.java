package com.mediatek.dialer.ext;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

public class DialPadExtension {

    private static final String TAG = "DialPadExtension";

    public String changeChar(String string, String string2, String commd) {
        log("changeChar");
        return string2;
    }

    public boolean handleChars(Context context, String input, String commd) {
        log("handleChars");
        return false;
    }

    public void onCreate(Fragment fragment, IDialpadFragment dialpadFragment) {
        log("onCreate");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState, View resultView) {
        log("onCreateView");
        return resultView;
    }

    public void onDestroy() {
        log("onDestroy");
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        log("onCreateOptionsMenu");
    }

    public void onPrepareOptionsMenu(Menu menu) {
        log("onPrepareOptionsMenu");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        log("onOptionsItemSelected");
        return false;
    }

    public void constructPopupMenu(PopupMenu popupMenu, View anchorView, Menu menu) {
        log("constructPopupMenu");
    }

    public boolean onMenuItemClick(MenuItem item) {
        log("onMenuItemClick");
        return false;
    }

    public boolean updateDialAndDeleteButtonEnabledState(final String lastNumberDialed) {
        log("updateDialAndDeleteButtonEnabledState");
        return false;
    }

    private void log(String msg) {
        Log.d(TAG, msg + " default");
    }
}

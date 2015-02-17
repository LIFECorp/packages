
package com.mediatek.incallui.ext;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.android.services.telephony.common.Call;

/**
 * Extension for InCallActivity UI
 * Host will call Plugin of the lifecycle, and some other thing happens
 * Plugin can call back to host, too. via the callback Interface.
 * @see com.mediatek.incallui.ext.IInCallScreen
 */
public class InCallUIExtension {

    /**
     * called when onCreate(), notify plugin to do initialization.
     * @param icicle the Bundle InCallActivity got
     * @param inCallActivity the InCallActivity instance
     * @param iInCallScreen the call back interface for UI updating
     */
    public void onCreate(Bundle icicle, Activity inCallActivity, IInCallScreen iInCallScreen) {
        InCallUIPluginDefault.log("InCallUIExtension onCreate DEFAULT");
    }

    /**
     * called when onDestroy()
     * @param inCallActivity the InCallActivity instance
     */
    public void onDestroy(Activity inCallActivity) {
        InCallUIPluginDefault.log("InCallUIExtension onDestroy DEFAULT");
    }

    /**
     * called when preparing options menu, based on InCallActivity.onPrepareOptionsMenu
     * @param menu the menu of InCallActivity
     * @param call the CallButton's mCall, the call whose state was latest changed
     */
    public void setupMenuItems(Menu menu, Call call) {
        InCallUIPluginDefault.log("InCallUIExtension setupMenuItems DEFAULT");
    }

    /**
     * called when popup menu item in CallButtonFragment clicked.
     * involved popup menus such as audio mode, vt
     * @param menuItem the clicked menu item
     * @return true if this menu event has already handled by plugin
     */
    public boolean handleMenuItemClick(MenuItem menuItem) {
        InCallUIPluginDefault.log("InCallUIExtension handleMenuItemClick DEFAULT");
        return false;
    }
}

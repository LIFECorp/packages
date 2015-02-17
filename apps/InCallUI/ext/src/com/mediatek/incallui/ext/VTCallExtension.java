
package com.mediatek.incallui.ext;

import com.android.services.telephony.common.ICallCommandService;

import android.content.Context;
import android.view.KeyEvent;

import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

/**
 * Extension for VT feature
 */
public class VTCallExtension {
    /**
     * called when VTCallFragment view created, for plugin initialize
     * @param context the Activity instance
     * @param root the VTCallFragment view
     * @param listener the listener make it possible for plugin to call host onTouch() directly,
     *                 or register it to plugin views
     * @param service current ICallCommandService instance
     */
    public void onViewCreated(Context context, View root, View.OnTouchListener listener, ICallCommandService service) {
        InCallUIPluginDefault.log("VTCallExtension onViewCreated DEFAULT");
    }

    /**
     * called in host OnTouchListener, pass the touch event to plug-in
     * @param v the View who is touched
     * @param event the touch event
     * @param isVTPeerBigger VTInCallScreenFlags.getInstance().mVTPeerBigger
     * @return true if The Plug in has already handled this event, host need do nothing
     */
    public boolean onTouch(View v, MotionEvent event, boolean isVTPeerBigger) {
        InCallUIPluginDefault.log("VTCallExtension onTouch DEFAULT");
        return false;
    }

    /**
     * called when host CallButtonFragment setting option menu for VT
     * @param menu the menu
     * @return true if plugin has handled this requirement
     */
    public boolean onPrepareOptionMenu(Menu menu) {
        InCallUIPluginDefault.log("VTCallExtension onPrepareOptionMenu DEFAULT");
        return false;
    }

    /**
     * called when host clear all VT flags, notify plugin to clear plugin-side flags, too
     */
    public void resetFlags() {
        InCallUIPluginDefault.log("VTCallExtension resetFlags DEFAULT");
    }

    /**
     * @deprecated nowhere called this API
     * @param keyCode the key code pressed
     * @param event key event
     * @return true if plugin handled this event
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        InCallUIPluginDefault.log("VTCallExtension onKeyDown DEFAULT");
        return false;
    }
}

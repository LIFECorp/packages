package com.mediatek.dialer.ext;

import android.app.Activity;
import android.content.res.Resources;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

public class ContactAccountExtension {
    private static final String TAG = "CallDetailHistoryAdapterExtension";

    /**
     * Default return Phone.getTypeLabel(res, type, label);
     * 
     * @param res
     * @param type
     * @param label
     * @param slotId
     * @return
     */
    public CharSequence getTypeLabel(Resources res, int type, CharSequence label, int slotId,
            String commd) {
        log("getTypeLabel");
        return Phone.getTypeLabel(res, type, label);
    }

    /**
     * Called when the app want to show application guide
     *
     * @param activity: The parent activity
     * @param type: The app type, such as "CONTACTS"
     */
    public void switchSimGuide(Activity activity, String type, String commd) {
        log("switchSimGuide");
    }

    private void log(String msg) {
        Log.d(TAG, msg + " default");
    }
}

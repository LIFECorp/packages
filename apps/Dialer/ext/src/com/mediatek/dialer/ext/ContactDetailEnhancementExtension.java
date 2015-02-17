package com.mediatek.dialer.ext;

import android.util.Log;

public class ContactDetailEnhancementExtension {
    private static final String TAG = "ContactDetailEnhancementExtension";

    public long getEnhancementPhotoId(int isSdnContact, int colorId, int slot, String commond) {
        log("getEnhancementPhotoId");
        return -1;
    }

    private void log(String msg) {
        Log.d(TAG, msg + " default");
    }
}

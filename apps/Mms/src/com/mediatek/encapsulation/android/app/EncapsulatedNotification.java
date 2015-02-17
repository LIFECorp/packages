
package com.mediatek.encapsulation.android.app;

import android.app.Notification;
import com.mediatek.encapsulation.EncapsulationConstant;

public class EncapsulatedNotification {

    /**
     * @hide
     * @internal
     * M: MTK proprietary flag for hiding notifications when calling setForeground.
     */
    public static final int FLAG_HIDE_NOTIFICATION = EncapsulationConstant.USE_MTK_PLATFORM ?
            Notification.FLAG_HIDE_NOTIFICATION : 0x10000000;
}

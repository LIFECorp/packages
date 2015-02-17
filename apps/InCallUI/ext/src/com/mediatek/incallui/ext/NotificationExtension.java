
package com.mediatek.incallui.ext;

import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;

import com.android.services.telephony.common.Call;

/**
 * Extension for the call notification on status bar
 */
public class NotificationExtension extends ContextWrapper {

    public NotificationExtension() {
        super(null);
    }

    public NotificationExtension(Context base) {
        super(base);
    }

    /**
     * called when checking whether a notification should appear
     * @param defValue default value that whether the notification should be suppressed
     * @return true if should do it according to plug-in's view
     */
    public boolean shouldSuppressNotification(boolean defValue) {
        return defValue;
    }

    /**
     * called when preparing the notification icon.
     * plugin can change the icon via this interface
     * @param call current call who will be shown in status bar
     * @param voicePrivacy "voice privacy" mode is active for always show
     *            notification
     * @param defResId default icon resId specified by default flow.
     * @param pluginResIds plugin will select a resId in this set to show.
     * @return the plugin specified resId
     */
    public int getInCallResId(Call call, boolean voicePrivacy, int defResId, int[][] pluginResIds) {
        return defResId;
    }
}

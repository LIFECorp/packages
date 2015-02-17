
package com.mediatek.incallui.ext;

import android.content.Context;
import android.view.View;

import com.android.services.telephony.common.Call;

import com.android.internal.telephony.PhoneConstants;
import com.mediatek.telephony.SimInfoManager.SimInfoRecord;

/**
 * Extension for CallCard
 * commonly called in CallCard related features
 */
public class CallCardExtension {

    /**
     * called when CallCard view created, based on CallCardFragment
     * lifecycle
     * @param context host context
     * @param rootView the CallCardFragment view
     */
    public void onViewCreated(Context context, View rootView) {
        InCallUIPluginDefault.log("CallCardExtension onViewCreated DEFAULT");
    }

    /**
     * called when call state changed, based on onStateChange
     * @param call the call who was changed
     */
    public void onStateChange(Call call) {
        InCallUIPluginDefault.log("CallCardExtension onStateChange DEFAULT");
    }

    /**
     * called when UI update after call state changed, based on onStateChange
     * @param state the current state, IDLE/OFFHOOK/RINGLING
     * @return true if Plug-in did something
     */
    public boolean updateCallInfoLayout(PhoneConstants.State state) {
        return false;
    }

    /**
     * called when there is an request to update the Primary call card information
     * UI. such as: call state changed, contact info retrieved, ui ready
     * @param call the primary call
     * @param simInfo the primary call's SimInfoRecord
     */
    public void updatePrimaryDisplayInfo(Call call, SimInfoRecord simInfo) {
    }
}


package com.mediatek.incallui.ext;

import java.util.HashMap;
import android.content.Context;
import android.view.View;

import com.android.services.telephony.common.Call;

/**
 * Extension for CallButton.
 * basically called in CallButton related classes
 */
public class CallButtonExtension {

    /**
     * called when CallButtonFragment view created.
     * customize this view
     * @param context host Context
     * @param rootView the CallButtonFragment view
     */
    public void onViewCreated(Context context, View rootView) {
        InCallUIPluginDefault.log("CallButtonExtension onViewCreated DEFAULT");
    }

    /**
     * called when call state changed
     * notify the foreground call to plug-in
     * @param call current foreground call
     * @param callMap a mapping of callId -> call for all current calls
     */
    public void onStateChange(Call call, HashMap<Integer, Call> callMap) {
        InCallUIPluginDefault.log("CallButtonExtension onStateChange DEFAULT");
    }
}

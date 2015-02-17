package com.mediatek.vcs;

import android.R.bool;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.contacts.R;
import com.mediatek.contacts.util.LogUtils;

public class VoiceSearchIndicator {
    private static final String TAG = VoiceSearchIndicator.class.getSimpleName();
    
    // vcs msg seciton
    private static final int MSG_UPDATE_INDICATOR_ICON = 100;
    // reactivate the icon every 600ms
    private static final long DELAY_TIME_INDICATOR = 600;
    
    private static final int ICON_INDICATOR_DISABLE = com.android.contacts.R.drawable.ic_voice_search_off;
    private static final int ICON_INDICATOR_ENABLE_LIGHT = com.android.contacts.R.drawable.ic_voice_search_holo_light;
    private static final int ICON_INDICATOR_ENABLE_DARK = com.android.contacts.R.drawable.ic_voice_search_holo_dark;
    
    private MenuItem mMenuItem = null;
    private ImageView mImageView = null;
    private boolean mIsIndicatorEnable = false;
    private int mIndicatorIcon = ICON_INDICATOR_DISABLE;
    
    public VoiceSearchIndicator(MenuItem item) {
        mMenuItem = item;
    }

    public VoiceSearchIndicator(ImageView view) {
        mImageView = view;
    }
    
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_UPDATE_INDICATOR_ICON:
                updateIndicator();
                break;
            default:
                LogUtils.i(TAG, "[handleMessage] [vcs] default message.");
                break;
            }
        }
    };
    
    public void updateIndicator(boolean enable){
        mIsIndicatorEnable = enable;
        updateIndicator();
    }

    public boolean isIndicatorEnable() {
        return mIsIndicatorEnable;
    }
    
    
    private void updateIndicator() {
        if (!isIndicatorEnable()) {
            LogUtils.i(TAG, "[updateIndicator] [vcs] Disable Indicator..");
            mHandler.removeMessages(MSG_UPDATE_INDICATOR_ICON);
            setIndicatorIcon(ICON_INDICATOR_DISABLE);
            return;
        }

        if (getIndicatorIcon() == ICON_INDICATOR_ENABLE_LIGHT) {
            setIndicatorIcon(ICON_INDICATOR_ENABLE_DARK);
        } else {
            setIndicatorIcon(ICON_INDICATOR_ENABLE_LIGHT);
        }

        Message msg = Message.obtain();
        msg.what = MSG_UPDATE_INDICATOR_ICON;
        mHandler.sendMessageDelayed(msg, DELAY_TIME_INDICATOR);
    }
    
    private void setIndicatorIcon(int iconRes) {
        mIndicatorIcon = iconRes;
        if (mImageView != null) {
            mImageView.setImageResource(mIndicatorIcon);
        } else {
            mMenuItem.setIcon(mIndicatorIcon);
        }
    }

    private int getIndicatorIcon() {
        return mIndicatorIcon;
    }
}

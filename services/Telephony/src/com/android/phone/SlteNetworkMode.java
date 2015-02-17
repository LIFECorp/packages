/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 *
 * MediaTek Inc. (C) 2010. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER ON
 * AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek Software")
 * have been modified by MediaTek Inc. All revisions are subject to any receiver's
 * applicable license agreements with MediaTek Inc.
 */

package com.android.phone;

import android.app.Activity;
import android.preference.PreferenceActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.AsyncResult;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.TelephonyProperties;
import com.android.internal.telephony.TelephonyIntents;
import com.android.internal.telephony.worldphone.LteModemSwitchHandler;
import com.mediatek.common.featureoption.FeatureOption;
//import com.mediatek.common.telephony.ITelephonyEx;
import com.mediatek.phone.PhoneInterfaceManagerEx;


import java.util.List;

public class SlteNetworkMode {
    public static final String BUTTON_NETWORK_MODE_KEY = "gsm_umts_preferred_network_mode_key";
    private static final String LOG_TAG = "My::SlteNetworkMode";
    private static final String KEY_MMDC_MODE = "mmdc_mode";
    public CustomPreference mPrefereMode = null;

    private static final int PREFERRED_NETWORK_MODE = Phone.NT_MODE_WCDMA_PREF;//0
    private Phone mPhone = null;
    private Context mCxt = null;
    private MyHandler mHandler;
    private boolean mIsUSIM = true;
    //ITelephonyEx mITelephonyEx;
	private PhoneInterfaceManagerEx mPhoneMgrEx;
    private final ITelephony mTel = ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));//mmdc cmm

    private static final int CALL_LIST_DIALOG_WAIT = 0;
    private	ProgressDialog mDialog = null;
	private boolean mSglteMode = false;
    void showDialog(int id) {
        log("showDialog process");
        mDialog = new ProgressDialog(mCxt);
        mDialog.setMessage(mCxt.getResources().getString(R.string.updating_settings));
        mDialog.setCancelable(false);
        mDialog.setIndeterminate(true);
        mDialog.show();
    }

    public void log(String msg) {
        Log.d(LOG_TAG, msg);
    }

    public void sglteGetPreferredNetworkTypeLteDc() {
        log("sglteGetPreferredNetworkTypeLteDc");
        if (FeatureOption.MTK_LTE_DC_SUPPORT) {
            int state = LteModemSwitchHandler.MD_TYPE_UNKNOWN;
            //state = ITelephonyEx.Stub.asInterface(ServiceManager.checkService("phoneEx")).getActiveModemType();	
            //state = mPhoneMgrEx.getActiveModemType();
            mPhoneMgrEx = PhoneGlobals.getInstance().phoneMgrEx;
            state = mPhoneMgrEx.getActiveModemType();
            log("customizeSgltePreferenceMode state: " + state);

            //LteModemSwitchHandler.MD_TYPE_LWG is 5--->csfb need show VT;
            //LteModemSwitchHandler.MD_TYPE_LTNG is 6 --->mmdc need hide VT;
            if (state == LteModemSwitchHandler.MD_TYPE_LWG) {
                log("sglteGetPreferredNetworkTypeLteDc is csfb");
                return ;
            } 
            mPhoneMgrEx.getPreferredNetworkTypeLteDc(mHandler.obtainMessage(MyHandler.MESSAGE_GET_PREFERRED_NETWORK_TYPE));
        }
    }

    public boolean customizeSgltePreferenceMode(Activity activity, PreferenceScreen prefsc, Phone phone) {
        log("customizeSgltePreferenceMode");
        if (!FeatureOption.MTK_LTE_DC_SUPPORT) {
            log("customizeSgltePreferenceMode support FeatureOption.MTK_LTE_DC_SUPPORT");
            return false;
        } else {
            log("customizeSgltePreferenceMode: support FeatureOption.MTK_LTE_DC_SUPPORT");
            int state = LteModemSwitchHandler.MD_TYPE_UNKNOWN;
            //state = ITelephonyEx.Stub.asInterface(ServiceManager.checkService("phoneEx")).getActiveModemType();	
            //state = mPhoneMgrEx.getActiveModemType();
            mPhoneMgrEx = PhoneGlobals.getInstance().phoneMgrEx;
            state = mPhoneMgrEx.getActiveModemType();
            log("customizeSgltePreferenceMode state: " + state);

            //LteModemSwitchHandler.MD_TYPE_LWG is 5--->csfb need show VT;
            //LteModemSwitchHandler.MD_TYPE_LTNG is 6 --->mmdc need hide VT;
            if (state == LteModemSwitchHandler.MD_TYPE_LWG) {
                log("customizeSgltePreferenceMode is csfb");
                return false;
            } /*else if (state != LteModemSwitchHandler.MD_TYPE_LTNG) {
                //not csfb and mmdc
                return false;
            }*/
            log("customizeSgltePreferenceMode mSglteMode= " + mSglteMode);
            ListPreference listPreferredNetworkMode = (ListPreference) prefsc.findPreference(BUTTON_NETWORK_MODE_KEY);
            if (listPreferredNetworkMode != null) {
                log("gsm_umts_preferred_network_mode_key");
                prefsc.removePreference(listPreferredNetworkMode);
            }
        }
        mPhone = phone;
        mHandler = new MyHandler();
        mCxt = prefsc.getContext();	

        if (mPrefereMode != null && mPrefereMode.getDialog() != null) 
        {
            mPrefereMode.getDialog().dismiss();
            log("close customizeSgltePreferenceMode dialog ");
        } //else if (mPrefereMode == null) {
        if (prefsc.findPreference(KEY_MMDC_MODE) != null) {
            log("find customizeSgltePreferenceMode and remove it ");
            prefsc.removePreference(prefsc.findPreference(KEY_MMDC_MODE));
        }
        log("new customizeSgltePreferenceMode ");
        mPrefereMode = new CustomPreference(prefsc.getContext());
        //}
        mPrefereMode.setDialogTitle(mCxt.getResources().getString(R.string.gsm_umts_network_preferences_title));
        mPrefereMode.setTitle(mCxt.getResources().getString(R.string.gsm_umts_network_preferences_title));
        mPrefereMode.setOrder(4);
        mPrefereMode.setKey(KEY_MMDC_MODE);

        mIsUSIM = phone.getIccCard().getIccCardType().equals("USIM");
        log("customizeSgltePreferenceMode mIsUSIM:" + mIsUSIM);
        mPrefereMode.setEntries(mCxt.getResources().getTextArray(R.array.sglte_network_mode_choices));
        mPrefereMode.setEntryValues(mCxt.getResources().getTextArray(R.array.sglte_network_mode_values));
       
        prefsc.addPreference(mPrefereMode);

        sglteGetPreferredNetworkTypeLteDc();
        return true;
    }

    private OnPreferenceChangeListener mPreferenceChangeListener = new OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object objValue) {
            String key = preference.getKey();log("OnPreferenceChangeListener key=" + key);
            if (preference == mPrefereMode) {
                //NOTE onPreferenceChange seems to be called even if there is no change
                //Check if the button value is changed from the System.Setting
                mPrefereMode.setValue((String) objValue);
                int buttonNetworkMode;
                buttonNetworkMode = Integer.valueOf((String) objValue).intValue();
                int settingsNetworkMode = android.provider.Settings.Global.getInt(
                    mCxt.getContentResolver(),
                    android.provider.Settings.Global.PREFERRED_NETWORK_MODE, PREFERRED_NETWORK_MODE);
                if (buttonNetworkMode != settingsNetworkMode) {
                    // M: when wait for network switch done show dialog    
                    int modemNetworkMode = 1;
                    switch(buttonNetworkMode) {
                        case 9:
                            modemNetworkMode = 9;
                            break;
                        case Phone.NT_MODE_WCDMA_PREF:
                            modemNetworkMode = Phone.NT_MODE_WCDMA_PREF;
                            break;
                        default:
                            log("onPreferenceChange  no set modemNetworkMode default=" + modemNetworkMode);
                            return true;
                    }
                    log("onPreferenceChange modemNetworkMode=" + modemNetworkMode);
                    try {
                        log("setPreferredNetworkTypeLteDc");
                        mPhoneMgrEx.setPreferredNetworkTypeLteDc(modemNetworkMode, mHandler.obtainMessage(MyHandler.MESSAGE_SET_PREFERRED_NETWORK_TYPE));
                    } catch (Exception ex) {
                        log("setPreferredNetworkTypeLteDc"  + ex.toString());
                    }
                }
            }
            return true;
        }
    };

    private class MyHandler extends Handler {
        private static final int MESSAGE_GET_PREFERRED_NETWORK_TYPE = 0;
        private static final int MESSAGE_SET_PREFERRED_NETWORK_TYPE = 1;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_GET_PREFERRED_NETWORK_TYPE:
                    handleGetPreferredNetworkTypeResponse(msg);
                    break;
                case MESSAGE_SET_PREFERRED_NETWORK_TYPE:
                    handleSetPreferredNetworkTypeResponse(msg);
                    break;
                default:
                    break;
            }
        }

        private void handleGetPreferredNetworkTypeResponse(Message msg) {
            AsyncResult ar = (AsyncResult) msg.obj;
            if (ar.exception == null) {
                int modemNetworkMode = ((int[])ar.result)[0];
                log("GetPreferredNetworkTypeResponse: modemNetworkMode = " + modemNetworkMode);
                int settingsNetworkMode = android.provider.Settings.Global.getInt(
                    mPhone.getContext().getContentResolver(),
                    Settings.Global.PREFERRED_NETWORK_MODE,
                    PREFERRED_NETWORK_MODE);
                log("GetPreferredNetworkTypeReponse: settingsNetworkMode = " + settingsNetworkMode);

                //check that modemNetworkMode is from an accepted value
                //Phone.NT_MODE_LTE_GSM_WCDMA-----9 (4G/3G/2G(Auto))
                //Phone.NT_MODE_GSM_WCDMA_LTE-----31(3G or 2G preferred)
                //Phone.NT_MODE_WCDMA_PREF----->0(3G/2G(Auto))
                //Phone.NT_MODE_GSM_ONLY------>1 (2G only)
                if (modemNetworkMode == 9 || modemNetworkMode == Phone.NT_MODE_WCDMA_PREF) {
                    log("GetPreferredNetworkTypeResponse: the expect modemNetworkMode = " + modemNetworkMode);
                    //whether to update the provider value
                    if (modemNetworkMode != settingsNetworkMode) {
                        settingsNetworkMode = modemNetworkMode;
                        android.provider.Settings.Global.putInt(
                             mPhone.getContext().getContentResolver(),
                             android.provider.Settings.Global.PREFERRED_NETWORK_MODE,
                             settingsNetworkMode);
                        log("GetPreferredNetworkTypeResponse: write new value to provider: " + settingsNetworkMode);
                    }
                    // changes the summary
                    mPrefereMode.setValue(Integer.toString(modemNetworkMode));
                    switch (modemNetworkMode) {
                        case 0:
                            mPrefereMode.mOldCurrentIndex = 1;
                            //Phone.NT_MODE_LTE_GSM_WCDMA;//9 4G/3G/2G(Auto)
                            break;
                        case 9:
                            mPrefereMode.mOldCurrentIndex = 0;
                            //Phone.NT_MODE_WCDMA_PREF;//0 3G/2G(Auto)
                            break;	
                        }
                    mPrefereMode.setSummary(mPrefereMode.getEntry());
                } else {
                    mPrefereMode.mOldCurrentIndex = -1;
                    mPrefereMode.setSummary("");
                    log("GetPreferredNetworkTypeResponse: get modemNetworkMode = " + modemNetworkMode);
                }
                log("GetPreferredNetworkTypeResponse: get mOldCurrentIndex = " + mPrefereMode.mOldCurrentIndex);
            } else {
                mPrefereMode.mOldCurrentIndex = -1;
                log("GetPreferredNetworkTypeResponse: find exception ");
                Toast.makeText(mCxt, "Find exception", 3000).show();
            }
        }

        private void handleSetPreferredNetworkTypeResponse(Message msg) {
            AsyncResult ar = (AsyncResult) msg.obj;
            /// M: when set network mode show wait dialog
            log("setPreferredNetworkTypeResponse");
            if (mDialog !=null) {
                mDialog.dismiss();
                log("GetPreferredNetworkTypeResponse: remove dialog of process");
            }
            mDialog = null;
            if (ar.exception == null) {
                /*int networkMode = Integer.valueOf(mPrefereMode.getValue()).intValue();
                Settings.Global.putInt(mPhone.getContext().getContentResolver(),
                    Settings.Global.PREFERRED_NETWORK_MODE,
                    networkMode);
                mPrefereMode.setSummary(mPrefereMode.getEntry());*/
                mPrefereMode.setSummary(mPrefereMode.mAdapter.mList[mPrefereMode.mOldCurrentIndex]);
                log("setPreferredNetworkTypeResponse no exception the summary:" + mPrefereMode.mAdapter.mList[mPrefereMode.mOldCurrentIndex]);
            } else {
                log("getPreferredNetworkTypeLteDc");
                mPhoneMgrEx.getPreferredNetworkTypeLteDc(mHandler.obtainMessage(MESSAGE_GET_PREFERRED_NETWORK_TYPE));
            }
        }

    }

    public class CustomPreference extends ListPreference{
        public SelectSingleListAdapter mAdapter;
        public int mOldCurrentIndex = -1;
        class SelectSingleListAdapter extends BaseAdapter {
            public CharSequence[] mList;
            public SelectSingleListAdapter(CharSequence[] list) {
                mList = list;
            }

            @Override
            public int getCount() {
                return mList.length;
            }

            @Override
            public Object getItem(int position) {
                return mList[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.i("LOADMORE", "getView...mOldCurrentIndex:" + mOldCurrentIndex + " position" + position);
                LayoutInflater inFlater = (LayoutInflater)mCxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inFlater.inflate(R.layout.sglte_mode_item, null);
                CheckedTextView text = (CheckedTextView)convertView.findViewById(R.id.editTextname);
                text.setText(mList[position]);
                if (position == 0 && !mIsUSIM) {
                    convertView.setEnabled(false);
                    text.setEnabled(false);
                }

                if (mOldCurrentIndex == position && mOldCurrentIndex >= 0) {
                    CheckedTextView textt = (CheckedTextView)convertView.findViewById(R.id.editTextname);
                    textt.setText(mList[position]);
                    text.setChecked(true);
                }
                return convertView;
            }
        }

        public CustomPreference(Context context) {
            super(context);
        }

        protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
            super.onPrepareDialogBuilder(builder);
            mAdapter = new SelectSingleListAdapter(super.getEntries());
            builder.setSingleChoiceItems(mAdapter, mOldCurrentIndex,  new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) {
                    log("current click item index is which = " + position + " mIsUSIM = " + SlteNetworkMode.this.mIsUSIM);
                    if (position == 0 && !SlteNetworkMode.this.mIsUSIM) {
                        log("current click item index is 0 and is sim");
                        return;
                    }
                    if (CustomPreference.this.mOldCurrentIndex == position) {
                        log("click the same position" + position);
                        dialog.dismiss();
                        return;
                    }
                    for(int i = 0; i < mAdapter.mList.length; i++) {
                        CheckedTextView text = (CheckedTextView)((AlertDialog)dialog).getListView().getChildAt(i).findViewById(R.id.editTextname);
                        text.setChecked(false);
                    }

                    mOldCurrentIndex = position;
                    int selectNetworkMode = -1 ;
                    switch (mOldCurrentIndex) {
                        case 0:
                            selectNetworkMode = Phone.NT_MODE_LTE_GSM_WCDMA;//9 4G/3G/2G(Auto)
                            break;
                        case 1:
                            selectNetworkMode = Phone.NT_MODE_WCDMA_PREF;//0 3G/2G(Auto)
                            break;
                    }
                    log("onclick selectNetworkMode=" + selectNetworkMode);
                    SlteNetworkMode.this.showDialog(CALL_LIST_DIALOG_WAIT);
                    try {
                        log("setPreferredNetworkTypeLteDc");
                        mPhoneMgrEx.setPreferredNetworkTypeLteDc(selectNetworkMode, mHandler.obtainMessage(MyHandler.MESSAGE_SET_PREFERRED_NETWORK_TYPE));
                    } catch (Exception ex) {
                        log("setPreferredNetworkTypeLteDc"  + ex.toString());
                    }
                    dialog.dismiss();
               }
            });//setSingleChoiceItems	
        }//onPrepareDialogBuilder
    }	

}

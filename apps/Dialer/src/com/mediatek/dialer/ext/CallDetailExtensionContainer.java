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
package com.mediatek.dialer.ext;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.mediatek.contacts.util.LogUtils;
import com.mediatek.dialer.PhoneCallDetailsEx;

import java.util.Iterator;
import java.util.LinkedList;

public class CallDetailExtensionContainer extends CallDetailExtension {
    private static final String TAG = "CallDetailExtensionContainer";

    private LinkedList<CallDetailExtension> mSubExtensionList;

    public void add(CallDetailExtension extension) {
        if (null == mSubExtensionList) {
            mSubExtensionList = new LinkedList<CallDetailExtension>();
        }
        mSubExtensionList.add(extension);
    }

    public void remove(CallDetailExtension extension) {
        if (null == mSubExtensionList) {
            LogUtils.i(TAG, "[CallDetailExtension]mSubExtensionList is null.");
            return;
        }
        mSubExtensionList.remove(extension);
    }

    public void setTextView(int callType, TextView durationView, String formatDuration, String commd) {
        LogUtils.i(TAG, "[setTextView] commd=" + commd);
        if (null != mSubExtensionList) {
            Iterator<CallDetailExtension> iterator = mSubExtensionList.iterator();
            while (iterator.hasNext()) {
                CallDetailExtension extension = iterator.next();
                if (extension.getCommand().equals(commd)) {
                    LogUtils.i(TAG, "[setTextView] Plugin do something command=" + commd);
                    extension.setTextView(callType, durationView, formatDuration, commd);
                    return;
                }
            }
        }
        super.setTextView(callType, durationView, formatDuration, commd);
    }

    public boolean isNeedAutoRejectedMenu(boolean isAutoRejectedFilterMode, String commd) {
        LogUtils.i(TAG, "[isNeedAutoRejectedMenu],isAutoRejectedFilterMode:" + isAutoRejectedFilterMode +
                ",command:" + commd);
        if (null == mSubExtensionList) {
            LogUtils.i(TAG, "[isNeedAutoRejectedMenu]mSubExtensionList is null.");
            return false;
        } else {
            Iterator<CallDetailExtension> iterator = mSubExtensionList.iterator();
            while (iterator.hasNext()) {
                boolean result = iterator.next().isNeedAutoRejectedMenu(isAutoRejectedFilterMode,
                        commd);
                if (result) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public String setChar(boolean notSPChar, String str, String spChar, int charType,
            boolean secondSelection, String commd) {
        LogUtils.i(TAG, "[setChar]");
        if (null == mSubExtensionList) {
            LogUtils.i(TAG, "[setChar]mSubExtensionList is null.");
            return null;
        } else {
            Iterator<CallDetailExtension> iterator = mSubExtensionList.iterator();
            while (iterator.hasNext()) {
                final String result = iterator.next().setChar(notSPChar, str, spChar, charType,
                        secondSelection, commd);
                if (result != null) {
                    return result;
                }
            }
        }
        
        return null;
    }

    /**
     * if plugin has special view can call this function to set visible
     */
    public void setViewVisibleByActivity(Activity activiy, String commd1, String commd2, int rse1,
            int res2, int res3, int res4, int res5, int res6, int res7, String commd) {
        LogUtils.i(TAG, "[setViewVisibleByActivity]");
        if (null == mSubExtensionList) {
            LogUtils.i(TAG, "[setViewVisibleByActivity]mSubExtensionList is null.");
            return;
        }
        Iterator<CallDetailExtension> iterator = mSubExtensionList.iterator();
        while (iterator.hasNext()) {
            iterator.next().setViewVisibleByActivity(activiy, commd1, commd2, rse1, res2, res3,
                    res4, res5, res6, res7, commd);
        }
    }

    /**
     * if plugin has special view can call this function to set visible
     */
    public void setViewVisible(View view, String commd1, String commd2, int rse1, int res2,
            int res3, int res4, int res5, int res6, int res7) {
        LogUtils.i(TAG, "[setViewVisible]");
        if (null == mSubExtensionList) {
            LogUtils.i(TAG, "[setViewVisible]mSubExtensionList is null.");
            return;
        }
        Iterator<CallDetailExtension> iterator = mSubExtensionList.iterator();
        while (iterator.hasNext()) {
            iterator.next().setViewVisible(view, commd1, commd2, rse1, res2, res3, res4, res5,
                    res6, res7);
        }
    }

}

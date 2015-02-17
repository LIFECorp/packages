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

import java.util.LinkedList;

import android.app.Activity;
import android.content.res.Resources;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

public class ContactAccountExtensionContainer extends ContactAccountExtension {

    private static final String TAG = "ContactAccountExtensionContainer";

    private LinkedList<ContactAccountExtension> mSubExtensionList;

    public void add(ContactAccountExtension extension) {
        if (null == mSubExtensionList) {
            mSubExtensionList = new LinkedList<ContactAccountExtension>();
        }
        mSubExtensionList.add(extension);
    }

    public void remove(ContactAccountExtension extension) {
        if (null == mSubExtensionList) {
            return;
        }
        mSubExtensionList.remove(extension);
    }

    public CharSequence getTypeLabel(Resources res, int type, CharSequence label, int slotId,
            String commd) {
        Log.i(TAG, "[getTypeLabel()]");
        CharSequence def = Phone.getTypeLabel(res, type, label);
        if (null != mSubExtensionList) {
            for (ContactAccountExtension subExtension : mSubExtensionList) {
                CharSequence result = subExtension.getTypeLabel(res, type, label, slotId, commd);
                if (!def.equals(result)) {
                    return result;
                }
            }
        }
        return def;
    }

    public void switchSimGuide(Activity activity, String type, String commd) {
        Log.i(TAG, "[switchSimGuide()]");
        if (null != mSubExtensionList) {
            for (ContactAccountExtension subExtension : mSubExtensionList) {
                subExtension.switchSimGuide(activity, type, commd);
            }
        }
    }
}

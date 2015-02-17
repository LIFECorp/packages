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

import android.util.Log;

public class PluginExtensionContainer {
    private static final String TAG = "ContactPluginExtensionContainer";

    private CallDetailExtensionContainer mCallDetailExtensionContainer;
    private CallListExtensionContainer mCallListExtensionContainer;
    private ContactAccountExtensionContainer mContactAccountExtensionContainer;
    private DialPadExtensionContainer mDialPadExtensionContainer;
    private DialtactsExtensionContainer mDialtactsExtensionContainer;
    private SpeedDialExtensionContainer mSpeedDialExtensionContainer;
    private ContactsCallOptionHandlerExtensionContainer mContactsCallOptionHandlerExtensionContainer;
    private ContactsCallOptionHandlerFactoryExtensionContainer mContactsCallOptionHandlerFactoryExtensionContainer;
    private CallLogAdapterExtensionContainer mCallLogAdapterExtensionContainer;
    private CallDetailHistoryAdapterExtensionContainer mCallDetailHistoryAdapterExtensionContainer;
    private DialerSearchAdapterExtensionContainer mDialerSearchAdapterExtensionContainer;
    private CallLogSearchResultActivityExtensionContainer mCallLogSearchResultActivityExtensionContainer;

    private ContactDetailEnhancementExtensionContainer mContactDetailEnhancementExtensionContainer;
    private CallLogSimInfoHelperExtensionContainer mCallLogSimInfoHelperExtensionContainer;

    public PluginExtensionContainer() {
        mCallDetailExtensionContainer = new CallDetailExtensionContainer();
        mCallListExtensionContainer = new CallListExtensionContainer();
        mContactAccountExtensionContainer = new ContactAccountExtensionContainer();
        mDialPadExtensionContainer = new DialPadExtensionContainer();
        mDialtactsExtensionContainer = new DialtactsExtensionContainer();
        mSpeedDialExtensionContainer = new SpeedDialExtensionContainer();
        mContactsCallOptionHandlerExtensionContainer = new ContactsCallOptionHandlerExtensionContainer();
        mContactsCallOptionHandlerFactoryExtensionContainer = new ContactsCallOptionHandlerFactoryExtensionContainer();
        mCallLogAdapterExtensionContainer = new CallLogAdapterExtensionContainer();
        mCallDetailHistoryAdapterExtensionContainer = new CallDetailHistoryAdapterExtensionContainer();
        mDialerSearchAdapterExtensionContainer = new DialerSearchAdapterExtensionContainer();
        mCallLogSearchResultActivityExtensionContainer = new CallLogSearchResultActivityExtensionContainer();
        mContactDetailEnhancementExtensionContainer = new ContactDetailEnhancementExtensionContainer();
        mCallLogSimInfoHelperExtensionContainer = new CallLogSimInfoHelperExtensionContainer();
    }

    public CallDetailExtension getCallDetailExtension() {
        Log.i(TAG, "return CallDetailExtension ");
        return mCallDetailExtensionContainer;
    }

    public CallListExtension getCallListExtension() {
        Log.i(TAG, "return CallListExtension ");
        return mCallListExtensionContainer;
    }

    public ContactAccountExtension getContactAccountExtension() {
        Log.i(TAG, "return ContactAccountExtension " + mContactAccountExtensionContainer);
        return mContactAccountExtensionContainer;
    }

    public DialPadExtension getDialPadExtension() {
        Log.i(TAG, "return DialPadExtension ");
        return mDialPadExtensionContainer;
    }

    public DialtactsExtension getDialtactsExtension() {
        Log.i(TAG, "return DialtactsExtension ");
        return mDialtactsExtensionContainer;
    }

    public SpeedDialExtension getSpeedDialExtension() {
        Log.i(TAG, "return SpeedDialExtension ");
        return mSpeedDialExtensionContainer;
    }

    public ContactsCallOptionHandlerExtension getContactsCallOptionHandlerExtension() {
        Log.i(TAG, "getContactsCallOptionHandlerExtension()");
        return mContactsCallOptionHandlerExtensionContainer;
    }

    public ContactsCallOptionHandlerFactoryExtension getContactsCallOptionHandlerFactoryExtension() {
        Log.i(TAG, "getContactsCallOptionHandlerFactoryExtension()");
        return mContactsCallOptionHandlerFactoryExtensionContainer;
    }

    public CallLogAdapterExtension getCallLogAdapterExtension() {
        Log.i(TAG, "getCallLogAdapterExtension()");
        return mCallLogAdapterExtensionContainer;
    }

    public CallDetailHistoryAdapterExtension getCallDetailHistoryAdapterExtension() {
        Log.i(TAG, "getCallDetailHistoryAdapterExtension()");
        return mCallDetailHistoryAdapterExtensionContainer;
    }

    public DialerSearchAdapterExtension getDialerSearchAdapterExtension() {
        Log.i(TAG, "getDialerSearchAdapterExtension()");
        return mDialerSearchAdapterExtensionContainer;
    }

    public CallLogSearchResultActivityExtension getCallLogSearchResultActivityExtension() {
        Log.i(TAG, "getCallLogSearchResultActivityExtension()");
        return mCallLogSearchResultActivityExtensionContainer;
    }

    public ContactDetailEnhancementExtension getContactDetailEnhancementExtension() {
        Log.i(TAG, "getContactDetailEnhancementExtension()");
        return mContactDetailEnhancementExtensionContainer;
    }

    public CallLogSimInfoHelperExtension getCallLogSimInfoHelperExtension() {
        Log.i(TAG, "getCallLogSimInfoHelperExtension()");
        return mCallLogSimInfoHelperExtensionContainer;
    }

    
    
    public void addExtensions(IDialerPlugin plugin) {
        Log.i(TAG, "Dialer Plugin : " + plugin);
        mCallDetailExtensionContainer.add(plugin.createCallDetailExtension());
        mCallListExtensionContainer.add(plugin.createCallListExtension());
        mContactAccountExtensionContainer.add(plugin.createContactAccountExtension());
        mDialPadExtensionContainer.add(plugin.createDialPadExtension());
        mDialtactsExtensionContainer.add(plugin.createDialtactsExtension());
        mSpeedDialExtensionContainer.add(plugin.createSpeedDialExtension());
        mContactsCallOptionHandlerExtensionContainer.add(plugin.createContactsCallOptionHandlerExtension());
        mContactsCallOptionHandlerFactoryExtensionContainer.add(plugin.createContactsCallOptionHandlerFactoryExtension());
        mCallLogAdapterExtensionContainer.add(plugin.createCallLogAdapterExtension());
        mCallDetailHistoryAdapterExtensionContainer.add(plugin.createCallDetailHistoryAdapterExtension());
        mDialerSearchAdapterExtensionContainer.add(plugin.createDialerSearchAdapterExtension());
        mCallLogSearchResultActivityExtensionContainer.add(plugin.createCallLogSearchResultActivityExtension());

        mContactDetailEnhancementExtensionContainer.add(plugin.createContactDetailEnhancementExtension());
        mCallLogSimInfoHelperExtensionContainer.add(plugin.createCallLogSimInfoHelperExtension());
    }

}

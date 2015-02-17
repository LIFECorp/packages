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
package com.mediatek.contacts.extension;

import android.util.Log;

import com.mediatek.contacts.ext.ContactAccountExtension;
import com.mediatek.contacts.ext.ContactDetailEnhancementExtension;
import com.mediatek.contacts.ext.ContactDetailExtension;
import com.mediatek.contacts.ext.ContactListExtension;
import com.mediatek.contacts.ext.ContactsCallOptionHandlerExtension;
import com.mediatek.contacts.ext.ContactsCallOptionHandlerFactoryExtension;
import com.mediatek.contacts.ext.IContactPlugin;
import com.mediatek.contacts.ext.IccCardExtension;
import com.mediatek.contacts.ext.QuickContactExtension;
import com.mediatek.contacts.ext.SimPickExtension;
import com.mediatek.contacts.ext.ImportExportEnhancementExtension;
import com.mediatek.contacts.ext.SimServiceExtension;
import com.mediatek.contacts.ext.LabeledEditorExtension;

public class ContactPluginExtensionContainer {
    private static final String TAG = "ContactPluginExtensionContainer";

    private ContactAccountExtensionContainer mContactAccountExtensionContainer;
    private ContactDetailExtensionContainer mContactDetailExtensionContainer;
    private ContactListExtensionContainer mContactListExtensionContainer;
    private QuickContactExtensionContainer mQuickContactExtensionContainer;
    private SimPickExtensionContainer mSimPickExtensionContainer;
    private ContactsCallOptionHandlerExtensionContainer mContactsCallOptionHandlerExtensionContainer;
    private ContactsCallOptionHandlerFactoryExtensionContainer mContactsCallOptionHandlerFactoryExtensionContainer;

    private ContactDetailEnhancementExtensionContainer mContactDetailEnhancementExtensionContainer;
    private SimServiceExtensionContainer mSimServiceExtensionContainer;
    private ImportExportEnhancementExtensionContainer mImportExportEnhancementExtensionContainer;
    private IccCardExtensionContainer mIccCardExtensionContainer;
    private LabeledEditorExtensionContainer mLabeledEditorExtensionContainer;

    public ContactPluginExtensionContainer() {
        mContactAccountExtensionContainer = new ContactAccountExtensionContainer();
        mContactDetailExtensionContainer = new ContactDetailExtensionContainer();
        mContactListExtensionContainer = new ContactListExtensionContainer();
        mSimPickExtensionContainer = new SimPickExtensionContainer();
        mQuickContactExtensionContainer = new QuickContactExtensionContainer();
        mContactsCallOptionHandlerExtensionContainer = new ContactsCallOptionHandlerExtensionContainer();
        mContactsCallOptionHandlerFactoryExtensionContainer = new ContactsCallOptionHandlerFactoryExtensionContainer();
        mContactDetailEnhancementExtensionContainer = new ContactDetailEnhancementExtensionContainer();
        mIccCardExtensionContainer = new IccCardExtensionContainer();
        mSimServiceExtensionContainer = new SimServiceExtensionContainer();
        mImportExportEnhancementExtensionContainer = new ImportExportEnhancementExtensionContainer();
        mLabeledEditorExtensionContainer = new LabeledEditorExtensionContainer();
    }

    public ContactAccountExtension getContactAccountExtension() {
        Log.i(TAG, "return ContactAccountExtension " + mContactAccountExtensionContainer);
        return mContactAccountExtensionContainer;
    }

    public ContactDetailExtension getContactDetailExtension() {
        Log.i(TAG, "return ContactDetailExtension ");
        return mContactDetailExtensionContainer;
    }

    public ContactListExtension getContactListExtension() {
        Log.i(TAG, "return ContactListExtension ");
        return mContactListExtensionContainer;
    }

    public SimPickExtension getSimPickExtension() {
        Log.i(TAG, "return SimPickExtension ");
        return mSimPickExtensionContainer;
    }

    public QuickContactExtension getQuickContactExtension() {
        Log.i(TAG, "return QuickContactExtension");
        return mQuickContactExtensionContainer;
    }

    public ContactsCallOptionHandlerExtension getContactsCallOptionHandlerExtension() {
        Log.i(TAG, "getContactsCallOptionHandlerExtension()");
        return mContactsCallOptionHandlerExtensionContainer;
    }

    public IccCardExtension getIccCardExtension() {
        return mIccCardExtensionContainer;
    }

    public ContactsCallOptionHandlerFactoryExtension getContactsCallOptionHandlerFactoryExtension() {
        Log.i(TAG, "getContactsCallOptionHandlerFactoryExtension()");
        return mContactsCallOptionHandlerFactoryExtensionContainer;
    }


    public ContactDetailEnhancementExtension getContactDetailEnhancementExtension() {
        Log.i(TAG, "getContactDetailEnhancementExtension()");
        return mContactDetailEnhancementExtensionContainer;
    }


    public SimServiceExtension getSimServiceExtension() {
        Log.i(TAG, "getSimServiceExtension()");
        return mSimServiceExtensionContainer;
    }
    
    public ImportExportEnhancementExtension getImportExportEnhancementExtension() {
        Log.i(TAG, "getImportExportEnhancementExtension()");
        return mImportExportEnhancementExtensionContainer;
    }

    public LabeledEditorExtension getLabeledEditorExtension() {
        Log.i(TAG, "getImportExportEnhancementExtension()");
        return mLabeledEditorExtensionContainer;
    }

    public void addExtensions(IContactPlugin contactPlugin) {
        Log.i(TAG, "contactPlugin : " + contactPlugin);
        mContactAccountExtensionContainer.add(contactPlugin.createContactAccountExtension());
        mContactDetailExtensionContainer.add(contactPlugin.createContactDetailExtension());
        mContactListExtensionContainer.add(contactPlugin.createContactListExtension());
        mSimPickExtensionContainer.add(contactPlugin.createSimPickExtension());
        mQuickContactExtensionContainer.add(contactPlugin.createQuickContactExtension());
        mContactsCallOptionHandlerExtensionContainer.add(contactPlugin.createContactsCallOptionHandlerExtension());
        mContactsCallOptionHandlerFactoryExtensionContainer.add(contactPlugin
                .createContactsCallOptionHandlerFactoryExtension());
        mIccCardExtensionContainer.add(contactPlugin.createIccCardExtension());

        mContactDetailEnhancementExtensionContainer.add(contactPlugin.createContactDetailEnhancementExtension());
        mSimServiceExtensionContainer.add(contactPlugin.createSimServiceExtension());
        mImportExportEnhancementExtensionContainer.add(contactPlugin.createImportExportEnhancementExtension());
        mLabeledEditorExtensionContainer.add(contactPlugin.createLabeledEditorExtension());
    }

}

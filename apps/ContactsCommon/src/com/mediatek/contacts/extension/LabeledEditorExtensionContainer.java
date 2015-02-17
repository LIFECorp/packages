package com.mediatek.contacts.extension;

import java.util.Iterator;
import java.util.LinkedList;
import android.util.Log;

import com.mediatek.contacts.ext.LabeledEditorExtension;

public class LabeledEditorExtensionContainer extends LabeledEditorExtension {
    private static final String TAG = "LabeledEditorExtension";

    private LinkedList<LabeledEditorExtension> mSubExtensionList;

    public void add(LabeledEditorExtension extension) {
        if (null == mSubExtensionList) {
            mSubExtensionList = new LinkedList<LabeledEditorExtension>();
        }
        mSubExtensionList.add(extension);
    }

    public void remove(LabeledEditorExtension extension) {
        if (null == mSubExtensionList) {
            return;
        }
        mSubExtensionList.remove(extension);
    }

    @Override
    public void onTypeSelectionChange(int position, String commond) {
        Log.i(TAG, "[onTypeSelectionChange] commond : " + commond + ",mSubExtensionList:" + mSubExtensionList);
        if (null != mSubExtensionList) {
            Iterator<LabeledEditorExtension> iterator = mSubExtensionList.iterator();
            while (iterator.hasNext()) {
                LabeledEditorExtension extension = iterator.next();
                if (extension.getCommond().equals(commond)) {
                    extension.onTypeSelectionChange(position, commond);
                }
            }
        }
    }
}

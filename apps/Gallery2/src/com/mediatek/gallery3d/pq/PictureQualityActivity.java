/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 */
/* MediaTek Inc. (C) 2010. All rights reserved.
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

/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mediatek.gallery3d.pq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.gallery3d.R;
import com.mediatek.gallery3d.pq.PresentImage.RenderingRequestListener;
import com.mediatek.gallery3d.pq.adapter.PQDataAdapter;

/**
 * @author allin
 * 
 */
public class PictureQualityActivity extends Activity implements RenderingRequestListener{

    public static final String ACTION_Main_QuickContactBadge = "com.android.quickcontactbadge.action.Main";
    private static final String TAG = "PictureQualityActivity";
    private ImageView mImageView;
    public static final int ITEM_HEIGHT = 110;
    ListView mListView;
    PQDataAdapter mAdapter;
    private int mHeight;
    String mUri;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        
        Bundle bundle = ((Activity) this).getIntent().getExtras();
        mUri = bundle.getString("PQUri");
        
        mHeight = getViewHeight();
        setContentView(R.layout.pq_main);
        
        mImageView = (ImageView)findViewById(R.id.imageview);
        mListView = (ListView)findViewById(R.id.getInfo);
        mAdapter = new PQDataAdapter((Context)this, mUri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.pq_actionbar, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home: 
            finish();
            break;
        case R.id.cancel: 
            recoverIndex();
            finish();
            break;
        case R.id.save: 
            finish();
            break;
        default:
            break;
        }
        return true;
    }
    
    @Override
    public void onBackPressed() {
        // send the back event to the top sub-state
        super.onBackPressed();
        recoverIndex();
        Log.d(TAG, "onBackPressed");
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mListView.setAdapter(mAdapter);
        mAdapter.setListView(mListView);
        setListViewHeightBasedOnChildren(mListView);
        PresentImage.getPresentImage().setListener(this, this);
        PresentImage.getPresentImage().loadBitmap(mUri);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        PresentImage.getPresentImage().stopLoadBitmap();
        Log.d(TAG, "onPause");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        releseResource();
    }
    
    private void recoverIndex () {
        mAdapter.restoreIndex();
    }
    
    private void releseResource() {
        mImageView.setImageBitmap(null);
        PresentImage.getPresentImage().free();
    }
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View list = listAdapter.getView(i, null, null);
            list.measure(0, 0);
            Log.d(TAG, "list.getMeasuredHeight()="+list.getMeasuredHeight());
            totalHeight += list.getMeasuredHeight()+listView.getDividerHeight();
        }
        int start  = 0;
        int height = mHeight;
        
        start = getActionBarHeight();
        height = mHeight- 2*getActionBarHeight();
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        ((MarginLayoutParams) params).setMargins(0, start, 0, 0); 
        ((MarginLayoutParams) params).height = height;
        listView.setLayoutParams(params);
        Log.d(TAG, "mHeight="+mHeight +" mActionBar.getHeight()="+getActionBarHeight()+" start="+start
                +"  height="+height);

    }

    public boolean available(Bitmap bitmap, String uri) {
        // TODO Auto-generated method stub
        if (mUri == uri) {
            mImageView.setImageBitmap(bitmap);
            return true;
        } else {
            bitmap.recycle();
            bitmap = null;
            return false;
        }

    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        mHeight = getViewHeight();
        Log.d(TAG, "onConfigurationChanged  height="+mHeight);
        if (mListView != null) {
            setListViewHeightBasedOnChildren(mListView);
        }

/*        if (mView != null) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            } else {
                mView.setSystemUiVisibility(View.INVISIBLE);
            }
        }*/
        toggleStatusBarByOrientation();
    }
    public int getActionBarHeight() {
        int actionBarHeight = this.getActionBar().getHeight();
        if (actionBarHeight != 0)
            return actionBarHeight;
        final TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        return actionBarHeight;
    }
    
    public int getViewHeight() {
        int height = 0;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Rect rect = new Rect();
            this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            Log.d(TAG, "rect.top=="+rect.top);
            height = this.getWindowManager().getDefaultDisplay().getHeight() 
            - rect.top;
        } else {
            height = this.getWindowManager().getDefaultDisplay().getHeight();
        }
        return height;
    }
    private void toggleStatusBarByOrientation() {

        Window win = getWindow();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            win.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
    public int getDefaultItemHeight() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return ITEM_HEIGHT;
        } else {
            return ITEM_HEIGHT - 20;
        }
    }

}

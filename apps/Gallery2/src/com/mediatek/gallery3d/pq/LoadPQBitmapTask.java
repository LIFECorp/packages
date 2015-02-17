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



import com.android.gallery3d.data.MediaItem;
import com.mediatek.gallery3d.util.MediatekFeature.Params;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class LoadPQBitmapTask extends AsyncTask<Void, Void, Bitmap> {

    private static final String TAG = "DecodeBitmapTask";
    private static Bitmap mScreenNailBitmap;
    private static Bitmap mTileBitmap;

    private static String mPQMineType;
    private static String mPqUri;
    private static Context mContext;
    private DecoderScreenNailBitmap mScreenNailDecoder;
    private DecoderTiledBitmap mDecoderTiledBitmap;
    private boolean isFrist;
    private static PresentImage mPresent;
    private int mRotation = 0;
    private String mCurrentUri;
    public static void init(Context context, PresentImage present) {
        mContext = context;
        Bundle bundle = ((Activity) context).getIntent().getExtras();
        mPQMineType =  bundle.getString("PQMineType");
        mPqUri = bundle.getString("PQUri");
        mPresent = present;
        Log.d(TAG," mPqUri="+mPqUri);
    }
    public LoadPQBitmapTask(String uri) {
        super();
        mCurrentUri = uri;
    }
    
    @Override
    protected Bitmap doInBackground(Void... params) {
        isFrist = mScreenNailBitmap == null && mTileBitmap == null;
        mRotation = PQUtils.getRotation(mContext, mPqUri);
        if (mPQMineType != null) {
            if (isFrist||!PQUtils.isSupportedByRegionDecoder(mPQMineType)) {
                mScreenNailDecoder = new DecoderScreenNailBitmap(mContext, mPqUri, Params.THUMBNAIL_TARGET_SIZE_LARGER);
                mScreenNailBitmap = mScreenNailDecoder.screenNailBitmapDecoder();
                if (mScreenNailBitmap != null ) Log.d(TAG," mScreenNailBitmap="+mScreenNailBitmap.getWidth() +" "+mScreenNailBitmap.getHeight());
                return mScreenNailBitmap;
                //mDecoder = new ImageDecoder(context, mPqUri, WindowsWidth , WindowsHeight, targetSize, viewW, viewH, level);
            } else {
                //targetSize = MediaItem.getTargetSize(MediaItem.TYPE_THUMBNAIL);
                mDecoderTiledBitmap = new DecoderTiledBitmap(mContext, mPqUri, MediaItem.getTargetSize(MediaItem.TYPE_THUMBNAIL));
                mTileBitmap = mDecoderTiledBitmap.decodeBitmap();
                //mDecoder = new TileImageDecoder(context, mPqUri, WindowsWidth , WindowsHeight, targetSize, mHandler, viewW, viewH, level);
                if (mTileBitmap != null) Log.d(TAG," mTileBitmap="+mTileBitmap.getWidth() +" "+mTileBitmap.getHeight());
                return mTileBitmap;
            }
            
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            if (mRotation != 0) {
                result = PQUtils.rotateBitmap(result, mRotation, true);
            }
            mPresent.setBitmap(result, mCurrentUri);
        }

    }

    public static boolean startLoadBitmap () {
        return (PQUtils.isSupportedByRegionDecoder(mPQMineType) && null == mTileBitmap);
    }

    public static void free () {
        if (mScreenNailBitmap != null ) {
            mScreenNailBitmap.recycle();
            mScreenNailBitmap = null;
        }
        if (mTileBitmap != null) {
            mTileBitmap.recycle();
            mTileBitmap = null;
        }
    }
}

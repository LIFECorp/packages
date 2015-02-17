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

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.DisplayMetrics;
import android.util.Log;

import com.android.gallery3d.ui.TileImageView;
import com.mediatek.gallery3d.util.MtkLog;

public class DecoderTiledBitmap {
    private static final String TAG = "Gallery2/TileImageDecoder";
    int mScreenWidth;
    int mScreenHeight;
    int mOriginalImageWidth;
    int mOriginalImageHeight;
    int mGLviewWidth;
    int mGLviewHeight;
    public String mUri = null;
    int targetSize ;
    Context mContext;
    BitmapFactory.Options options = new BitmapFactory.Options();;
    Bitmap mScreenNail = null;
    Runnable mApply = null;
    int mLevelCount;
    BitmapRegionDecoder decoder = null;
    Rect mDesRect = null;
    Handler mHandler = null;
    int mLevel;
    int TILE_SIZE;
    final static int SCALE_LIMIT = 4;
    private final int TILE_BORDER = 1;
    
    public DecoderTiledBitmap(Context context, String mPqUri ,int targetSize) {
        mContext = context;
        mUri = mPqUri;
        this.targetSize = targetSize;
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
        Bundle bundle = ((Activity) context).getIntent().getExtras();
        mPqUri = bundle.getString("PQUri");
        mGLviewWidth = bundle.getInt("PQViewWidth");
        mGLviewHeight = bundle.getInt("PQViewHeight");

        if (TileImageView.isHighResolution(mContext)) {
            TILE_SIZE = 511;
        } else {
            TILE_SIZE = 255;
        }
        MtkLog.d(TAG," TILE_SIZE===="+TILE_SIZE); 
        init();
        mLevelCount = PQUtils.calculateLevelCount(mOriginalImageWidth, mScreenWidth);
        mLevel = PQUtils.clamp(PQUtils.floorLog2(1f / getScaleMin()), 0, mLevelCount);
        Log.d(TAG, " mLevel="+mLevel +" mLevelCount="+mLevelCount);
        decoder = getBitmapRegionDecoder(mUri);
    }

    private void init() {
        FileDescriptor fd = null;
        FileInputStream fis = null;
        options.inJustDecodeBounds = true;
        try {
            fis = PQUtils.getFileInputStream(mContext, mUri);
            if (fis != null) {
                fd = fis.getFD();
                BitmapFactory.decodeFileDescriptor(fd, null, options);
            }
            }catch (FileNotFoundException e) {
                MtkLog.e(TAG, "bitmapfactory decodestream fail");
            }catch (IOException e) {
                MtkLog.e(TAG, "bitmapfactory decodestream fail");
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            float scale = 1;
            if (options.outWidth > 0 && options.outHeight > 0) {
                mOriginalImageWidth = options.outWidth;
                mOriginalImageHeight = options.outHeight;
                scale = (float) targetSize / Math.max(options.outWidth, options.outHeight);
            }
        options.inSampleSize = PQUtils.computeSampleSizeLarger(scale);
        MtkLog.d(TAG, " pq  options.inSampleSize=="+options.inSampleSize +" width=="+options.outWidth+ " height=="+options.outHeight + "targetSize=="+targetSize);
    }
    
    public float getScaleMin() {
        float s = Math.min(((float) mGLviewWidth) / mOriginalImageWidth,
                ((float) mGLviewHeight) / mOriginalImageHeight);
        MtkLog.d(TAG, " viewW=="+mGLviewWidth+"  viewH=="+mGLviewHeight +"  mOriginalImageWidth=="+ mOriginalImageWidth+ 
                "  mOriginalImageHeight=="+mOriginalImageHeight);
        return Math.min(SCALE_LIMIT, s);
    }
    
    private BitmapRegionDecoder getBitmapRegionDecoder(String mUri) {
        InputStream inputstream = null;
        BitmapRegionDecoder  decoder;
        try {
            inputstream = mContext.getContentResolver().openInputStream(Uri.parse(mUri));
            decoder = BitmapRegionDecoder.newInstance(inputstream, false);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            decoder = null;
            MtkLog.d(TAG, "FileNotFoundException!!!!!!!!!!!!!!!!!!!!"+e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            decoder = null;
            MtkLog.d(TAG, "IOException!!!!!!!!!!!!!!!!!!!!"+e.toString());
            e.printStackTrace();
        } finally {
            if (inputstream != null) {
                try{
                    inputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return decoder;
        
    }


    public Bitmap decodeBitmap() {
        return decodeTileImage(1, mLevel);
    }
    
    private Bitmap decodeTileImage(float scale, int sample) {
        int imagewidth = decoder.getWidth();
        int imageheight = decoder.getHeight();
        MtkLog.d(TAG, "scale==="+scale);
        imagewidth = (int)(imagewidth*scale);
        imageheight = (int)(imageheight*scale);
        Bitmap result = Bitmap.createBitmap(
                imagewidth >> sample, imageheight >> sample, Config.ARGB_8888); //
        Canvas canvas = new Canvas(result);
        Rect desRect = new Rect(0, 0, result.getWidth(), result.getHeight());
        Rect rect= new Rect(0, 0, decoder.getWidth(), decoder.getHeight());

        drawInTiles(canvas, decoder, rect, desRect, sample);
        return result;
    }
    
    public class Tile{
        public Tile (int x, int y, Bitmap mBitmap) {
            this.x = x;
            this.y = y;
            this.bitmap = mBitmap;
        }
        public int x;
        public int y;
        Bitmap bitmap = null;
    }
    private void drawInTiles(Canvas canvas,
            BitmapRegionDecoder decoder, Rect rect, Rect dest, int sample) {
        int tileSize = (TILE_SIZE << sample);
        int borderSize = (TILE_BORDER << sample);
        Rect tileRect = new Rect();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.inPreferQualityOverSpeed = true;
        //options.inBitmap = bitmap;
        options.inPostProc = true;
        options.inSampleSize = (1 << sample);
        MtkLog.v(TAG, "sample===="+sample);
        ArrayList<Tile> mTileList = new ArrayList<Tile>();
        boolean complate = true;
        for (int tx = rect.left, x = 0;
                tx < rect.right; tx += tileSize, x += TILE_SIZE) {
            for (int ty = rect.top, y = 0;
                    ty < rect.bottom; ty += tileSize, y += TILE_SIZE) {
                tileRect.set(tx, ty, tx + tileSize + borderSize, ty + tileSize + borderSize);
                if (tileRect.intersect(rect)) {
                    Bitmap bitmap = null;//Bitmap.createBitmap(tileRect.width(), tileRect.height(), Config.ARGB_8888);
                    try {
                        synchronized (decoder) {
                            if (decoder != null && !decoder.isRecycled()) {
                                bitmap = decoder.decodeRegion(tileRect, options);
                                mTileList.add(new Tile(x, y, bitmap));
                            } else {
                                complate = false;
                                break;
                            }
                        }
                        //canvas.drawBitmap(bitmap, x, y, paint);
                        //bitmap.recycle();
                    } catch (IllegalArgumentException e) {
                        MtkLog.w(TAG,"drawInTiles:got exception:"+e);
                    }
                }
            }
        }
        if (complate == true) {
            Paint paint = new Paint();
            int size  = mTileList.size();
            for (int i = size - 1; i >= 0; i--) {
                Bitmap bitmap = mTileList.get(i).bitmap;
                canvas.drawBitmap(mTileList.get(i).bitmap, mTileList.get(i).x, mTileList.get(i).y, paint);
                MtkLog.d(TAG, "pixelX="+mTileList.get(i).x+" pixelY="+mTileList.get(i).y);
                mTileList.get(i).bitmap.recycle();
            }
        }
    }
}

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
package com.mediatek.gallery3d.pq.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;

public abstract class Filter {
    public static final String TAG = "Filter";
    public static final String  MIN_VALUE= "textViewMinValue";
    public static final String RANGE= "textViewMaxValue";
    public static final String CURRRENT_INDEX= "textViewCurrentIndex";
    public static final String SEEKBAR_PROGRESS= "seekbarProgress";
    protected int mDefaultIndex ;
    protected int mRange;
    protected int mCurrentIndex;
    
    static {
        System.loadLibrary("PQjni");
    }
    
    public Map<String, String> map = new HashMap<String, String>();
    
    public Map<String, String> getDate() {
        return map;
    }
    public Filter() {
        init();
        map.put(MIN_VALUE, getMinValue());
        map.put(RANGE, getMaxValue());
        map.put(CURRRENT_INDEX, getCurrentValue());
        map.put(SEEKBAR_PROGRESS, getSeekbarProgressValue());
        Log.d(TAG, "Create ["+this.getClass().getName()+ " ]: MIN_VALUE="+getMinValue()
                +" RANGE="+getMaxValue()
                +" CURRRENT_INDEX="+getCurrentValue()
                +"  SEEKBAR_PROGRESS="+getSeekbarProgressValue());
    }
    
    public boolean addToList(ArrayList<Filter> list) {
        if (Integer.parseInt(getMaxValue()) > 0) {
            list.add(this);
            Log.d(TAG, ":::"+this.getClass().getName()+" has alread addToList! ");
            return true;
        }
        return false;
    }
    abstract public void init();
    abstract public String getMinValue ();
    abstract public String getMaxValue();
    abstract public String getCurrentValue();
    abstract public String getSeekbarProgressValue();
    abstract public void setIndex();
    abstract public void setCurrentIndex(int progress);
    abstract public void setDefaultIndex();
    
    public native int nativeGetContrastAdjRange();
    public native int nativeGetContrastAdjIndex();
    public native boolean nativeSetContrastAdjIndex(int index);
    
    public native int nativeGetXAxisRange();
    public native int nativeGetXAxisIndex();
    public native boolean nativeSetXAxisIndex(int index);
    
    public native int nativeGetYAxisRange();
    public native int nativeGetYAxisIndex();
    public native boolean nativeSetYAxisIndex(int index);
    
    public native int nativeGetGrassToneHRange();
    public native int nativeGetGrassToneHIndex();
    public native boolean nativeSetGrassToneHIndex(int index);
    
    public native int nativeGetGrassToneSRange();
    public native int nativeGetGrassToneSIndex();
    public native boolean nativeSetGrassToneSIndex(int index);
    
    public native int nativeGetHueAdjRange();
    public native int nativeGetHueAdjIndex();
    public native boolean nativeSetHueAdjIndex(int index);
    
    public native int nativeGetSatAdjRange();
    public native int nativeGetSatAdjIndex();
    public native boolean nativeSetSatAdjIndex(int index);
    
    public native int nativeGetSharpAdjRange();
    public native int nativeGetSharpAdjIndex();
    public native boolean nativeSetSharpAdjIndex(int index);
    
    public native int nativeGetSkinToneHRange();
    public native int nativeGetSkinToneHIndex();
    public native boolean nativeSetSkinToneHIndex(int index);
    
    public native int nativeGetSkinToneSRange();
    public native int nativeGetSkinToneSIndex();
    public native boolean nativeSetSkinToneSIndex(int index);
    
    public native int nativeGetSkyToneHRange();
    public native int nativeGetSkyToneHIndex();
    public native boolean nativeSetSkyToneHIndex(int index);
    
    public native int nativeGetSkyToneSRange();
    public native int nativeGetSkyToneSIndex();
    public native boolean nativeSetSkyToneSIndex(int index);
}

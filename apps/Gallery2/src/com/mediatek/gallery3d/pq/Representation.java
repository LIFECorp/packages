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

import java.util.Map;

import com.android.gallery3d.app.Log;
import com.mediatek.gallery3d.pq.adapter.PQDataAdapter.ViewHolder;
import com.mediatek.gallery3d.pq.filter.Filter;
import android.widget.SeekBar;
import android.widget.TextView;

public class Representation implements SeekBar.OnSeekBarChangeListener{
    private static final String TAG = "Representation";
	TextView mMinValue ;
	TextView mMaxValue;
	TextView mCurrentValue;
	SeekBar mController;
	private ViewHolder mHolder;
	private Filter mFilter;
	
	private String mUri;
	public Representation(String uri) {
	    mUri = uri;
	}
	
    public void init(ViewHolder holder, Filter enhancement) {
        mHolder = holder;
        mFilter= enhancement;
        Map<String, String> map = mFilter.getDate();
        holder.left.setText(map.get(Filter.MIN_VALUE));
        holder.right.setText(map.get(Filter.RANGE));
        holder.blow.setText(mFilter.getCurrentValue());
        holder.seekbar.setProgress(Integer.parseInt(mFilter.getSeekbarProgressValue()));
        Log.d(TAG, mFilter.getClass().getName()+":: mFilter.getCurrentValue() = "+mFilter.getCurrentValue()
                +"  Integer.parseInt(mFilter.getSeekbarProgressValue())="+Integer.parseInt(mFilter.getSeekbarProgressValue()));
        holder.seekbar.setOnSeekBarChangeListener(this);
    }
    
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
        // TODO Auto-generated method stub
        if (fromUser) {
            mFilter.setCurrentIndex(progress);
            mHolder.blow.setText(mFilter.getCurrentValue());
            PresentImage.getPresentImage().loadBitmap(mUri);
        }
    }
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        
    }
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        mFilter.setIndex();
        mHolder.blow.setText(mFilter.getCurrentValue());
    }


	
}

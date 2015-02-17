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
package com.mediatek.gallery3d.pq.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mediatek.gallery3d.pq.PictureQualityActivity;
import com.mediatek.gallery3d.pq.Representation;
import com.mediatek.gallery3d.pq.filter.Filter;
import com.mediatek.gallery3d.pq.filter.FilterContrastAdj;
import com.mediatek.gallery3d.pq.filter.FilterGetXAxis;
import com.mediatek.gallery3d.pq.filter.FilterGetYAxis;
import com.mediatek.gallery3d.pq.filter.FilterGrassToneH;
import com.mediatek.gallery3d.pq.filter.FilterGrassToneS;
import com.mediatek.gallery3d.pq.filter.FilterHueAdj;
import com.mediatek.gallery3d.pq.filter.FilterSatAdj;
import com.mediatek.gallery3d.pq.filter.FilterSharpAdj;
import com.mediatek.gallery3d.pq.filter.FilterSkinToneH;
import com.mediatek.gallery3d.pq.filter.FilterSkinToneS;
import com.mediatek.gallery3d.pq.filter.FilterSkyToneH;
import com.mediatek.gallery3d.pq.filter.FilterSkyToneS;
import com.android.gallery3d.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class PQDataAdapter extends BaseAdapter{
    private static final String TAG = "PQDataAdapter";
    private ArrayList<Filter> mData;
    private LayoutInflater mInflater;
    private HashMap<ViewHolder, Representation> mAllPresentation = new HashMap<ViewHolder, Representation>();
    private Context mContext;
    private ListView mListView;
    private String mUri;
    public final class ViewHolder{
        public RelativeLayout layout;
        public TextView left;
        public SeekBar seekbar;
        public TextView blow;
        public TextView right;
    }
    public PQDataAdapter (Context context, String uri) {
        this.mInflater = LayoutInflater.from(context);
        mData = getData();
        mContext = context;
    }
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    public void setListView(ListView listView) {
        mListView = listView;
    }
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        RelativeLayout layout = null;
        if (convertView == null) {
            
            holder=new ViewHolder();
            
            convertView = mInflater.inflate(R.layout.pq_seekbar, null);
            holder.left = (TextView)convertView.findViewById(R.id.textViewMinValue);
            holder.seekbar = (SeekBar)convertView.findViewById(R.id.seekbar);
            holder.blow = (TextView)convertView.findViewById(R.id.textViewCurrentIndex);
            holder.right = (TextView)convertView.findViewById(R.id.textViewMaxValue);
            holder.layout = (RelativeLayout)convertView.findViewById(R.id.listitem);
            convertView.setTag(holder);
            
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        Representation presentaion = mAllPresentation.get(holder);
        if (presentaion == null) {
            presentaion = new Representation(mUri);
            mAllPresentation.put(holder, presentaion);
        }
        
        presentaion.init(holder, mData.get(position));
        setItemHeight(holder, mData.size());
        return convertView;
    }
    
    private void setItemHeight(ViewHolder holder,int count) {
        Log.d(TAG, "setItemHeight~~~~~~~~~~~~~~~~~~~~~~~");
        /*if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {*/
        int height = ((Activity) mContext).getWindowManager().getDefaultDisplay().getHeight();
        int sceenHeigh = mListView.getHeight();
        if (sceenHeigh == 0) {
            sceenHeigh = ((Activity) mContext).getWindowManager().getDefaultDisplay().getHeight()
                            - ((PictureQualityActivity) mContext).getActionBarHeight();
        }
        int defaultItemH = ((PictureQualityActivity) mContext).getDefaultItemHeight();
        int itemHeight ;
        if (count*defaultItemH < sceenHeigh) {
            itemHeight = (sceenHeigh - ((PictureQualityActivity) mContext).getActionBarHeight())/count;
        } else {
            itemHeight = defaultItemH - ((PictureQualityActivity) mContext).getActionBarHeight()/count;
        }
        holder.layout.setMinimumHeight(itemHeight);
        Log.d(TAG, "params.height==="+height +"  itemHeight=="+itemHeight+ "  sceenHeigh="+sceenHeigh+" actionBarHeight="
                +((PictureQualityActivity) mContext).getActionBarHeight());
    }

    private ArrayList<Filter> getData() {
        ArrayList<Filter> list = new ArrayList<Filter>();
        (new FilterSharpAdj()).addToList(list);
        (new FilterSatAdj()).addToList(list);
        (new FilterHueAdj()).addToList(list);
        (new FilterSkinToneH()).addToList(list);
        (new FilterSkinToneS()).addToList(list);
        (new FilterSkyToneH()).addToList(list);
        (new FilterSkyToneS()).addToList(list);
        (new FilterGetXAxis()).addToList(list);
        (new FilterGetYAxis()).addToList(list);
        (new FilterGrassToneH()).addToList(list);
        (new FilterGrassToneS()).addToList(list);
        (new FilterContrastAdj()).addToList(list);
        return list;
    }

    public void restoreIndex() {
        int size = mData.size();
        for (int i = 0; i < size; i++) {
            Filter data = mData.get(i);
            if (data != null) data.setDefaultIndex(); 
        }
    }
}

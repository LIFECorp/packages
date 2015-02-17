package com.mediatek.gallery3d.data;

import java.io.FileDescriptor;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.android.gallery3d.app.PhotoDataAdapter.MavListener;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.DecodeUtils;
import com.android.gallery3d.util.ThreadPool.JobContext;

import com.mediatek.gallery3d.util.MediatekFeature;
import com.mediatek.gallery3d.util.MediatekFeature.DataBundle;
import com.mediatek.gallery3d.util.MediatekFeature.Params;

public class ImageRequest implements IMediaRequest {
    private static final String TAG = "Gallery2/ImageRequest";

    public DataBundle request(JobContext jc, Params params, String filePath) {
        if (null == params || null == filePath) {
            Log.w(TAG, "request:got null params or filePath!");
            return null;
        }
        if (null != jc && jc.isCancelled()) return null;

        params.info(TAG);

        DataBundle dataBundle = new DataBundle();
        Options options = new Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap originThumb = null;
        Bitmap largeFrame = null;

        if (params.inOriginalFrame) {
            // M: for picture quality enhancement
            MediatekFeature.enablePictureQualityEnhance(
                                options, params.inPQEnhance);

            originThumb = DecodeUtils.decodeThumbnail(
                jc, filePath, options, 
                params.inOriginalTargetSize, params.inType);
        }

        if (params.inOriginalFullFrame) {
            BitmapRegionDecoder bitmapRegionDecoder =
                DecodeUtils.createBitmapRegionDecoder(jc, filePath, false);
            if (null != bitmapRegionDecoder) {
                RegionDecoder regionDecoder = new RegionDecoder();
                regionDecoder.regionDecoder = bitmapRegionDecoder;
                dataBundle.originalFullFrame = regionDecoder;
            }
        }

        request(jc, params, dataBundle, originThumb, largeFrame);

        dataBundle.info(TAG);

        return dataBundle;
    }

    public DataBundle request(JobContext jc, Params params, byte[] data, 
                              int offset,int length) {
        if (null == params || null == data) {
            Log.w(TAG, "request:got null params or data!");
            return null;
        }
        if (null != jc && jc.isCancelled()) return null;

        params.info(TAG);

        DataBundle dataBundle = new DataBundle();
        Options options = new Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap originThumb = null;
        Bitmap largeFrame = null;

        if (params.inOriginalFrame) {
            // M: for picture quality enhancement
            MediatekFeature.enablePictureQualityEnhance(
                                options, params.inPQEnhance);

            originThumb = DecodeHelper.decodeThumbnail(jc, data, 
                             offset, length, options,
                             params.inOriginalTargetSize, params.inType);
        }

        if (params.inOriginalFullFrame) {
            BitmapRegionDecoder bitmapRegionDecoder =
                DecodeUtils.createBitmapRegionDecoder(
                                jc, data, offset, length, false);
            if (null != bitmapRegionDecoder) {
                RegionDecoder regionDecoder = new RegionDecoder();
                regionDecoder.regionDecoder = bitmapRegionDecoder;
                dataBundle.originalFullFrame = regionDecoder;
            }
        }

        request(jc, params, dataBundle, originThumb, largeFrame);

        dataBundle.info(TAG);

        return dataBundle;
    }

    public DataBundle request(JobContext jc, Params params,
                              ContentResolver cr, Uri uri) {
        if (null == params || null == cr || null == uri) {
            Log.w(TAG, "request:got null params or cr or uri!");
            return null;
        }
        if (null != jc && jc.isCancelled()) return null;

        params.info(TAG);

        DataBundle dataBundle = new DataBundle();
        Options options = new Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap originThumb = null;
        Bitmap largeFrame = null;

        ParcelFileDescriptor pfd = null;
        FileDescriptor fd = null;
        try {
            pfd = cr.openFileDescriptor(uri, "r");
            fd = pfd.getFileDescriptor();

            if (params.inOriginalFrame) {
                // M: for picture quality enhancement
                MediatekFeature.enablePictureQualityEnhance(
                                    options, params.inPQEnhance);
    
                originThumb = DecodeUtils.decodeThumbnail(jc, fd, options,
                        params.inOriginalTargetSize, params.inType);
            }

            if (params.inOriginalFullFrame) {
                BitmapRegionDecoder bitmapRegionDecoder =
                    DecodeUtils.createBitmapRegionDecoder(null, fd, false);
                if (null != bitmapRegionDecoder) {
                    RegionDecoder regionDecoder = new RegionDecoder();
                    regionDecoder.regionDecoder = bitmapRegionDecoder;
                    dataBundle.originalFullFrame = regionDecoder;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            Utils.closeSilently(pfd);
        }


        request(jc, params, dataBundle, originThumb, largeFrame);

        dataBundle.info(TAG);

        return dataBundle;
    }


    private void request(JobContext jc, Params params,DataBundle dataBundle, 
                         Bitmap originThumb, Bitmap largeFrame) {

        if (params.inOriginalFrame) {
            //first, we resize down the original bitmap
            if (null != originThumb) {
                originThumb = DecodeHelper.postScaleDown(
                    originThumb, params.inType, params.inOriginalTargetSize); // M: 6592 panorama modify
            }

            //check if we should retrieve original thumbnail
            if (params.inOriginalFrame) {
                dataBundle.originalFrame = originThumb;
            }
            if (null != dataBundle.originalFrame) {
                dataBundle.originalFrame = DecodeHelper.postScaleDown(
                    dataBundle.originalFrame, params.inType,
                    params.inOriginalTargetSize);
            }
        }
    }

    public void setMavListener(MavListener listener) {}
}

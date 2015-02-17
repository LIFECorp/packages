/*
 * Copyright (C) 2011, The Android Open Source Project
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
/*
 * Contributed by: Giesecke & Devrient GmbH.
 */

package com.mediatek.nfc.handover;

import android.util.Log;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import android.provider.MediaStore;
import android.database.Cursor;

public class Util {

    public static byte[] mergeBytes(byte[] array1, byte[] array2) {
        byte[] data = new byte[array1.length + array2.length];
        int i = 0;
        for (; i < array1.length; i++)
            data[i] = array1[i];
        for (int j = 0; j < array2.length; j++)
            data[j + i] = array2[j];
        return data;
    }

    public static byte[] getMid(byte[] array, int start, int length) {
        byte[] data = new byte[length];
        System.arraycopy(array, start, data, 0, length);
        return data;
    }

    public static String bytesToString(byte[] bytes) {
    	if(bytes == null)
    		return "";
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append(String.format("%02x ", b & 0xFF));
        }
        String str = sb.toString();
        if (str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }


	public static String getFilePathByContentUri(String tag,Context context,Uri uri) {
        Log.d(tag, "getFilePathByContentUri(), uri.toString() = " + uri.toString());
        Log.d(tag, "                           uri.getPath(): " + uri.getPath());
        
	    Uri filePathUri = uri;
	    if (uri.getScheme().toString().compareTo("content")==0) {    
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"

                    String curString = cursor.getString(column_index);
                    Log.d(tag, " return cursor.getString : " + curString);
                    filePathUri = Uri.parse(curString);
                    Log.d(tag, "filePathUri.getPath() : " + filePathUri.getPath());
                    return curString;//filePathUri.getPath();
                }
            } catch (Exception e) {
                Log.d(tag, "exception...");
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
	    } 
        Log.d(tag, "getFilePathByContentUri doesn't work, try direct getPath");
        
        Log.d(tag, "return uri.getPath(): " + uri.getPath());
	    return uri.getPath();
	}    

    //convert File:// or content:// to filePath
    public static String getFilePathByString(String tag,Context context,String input) {

        Log.d(tag, " convertToFilePath() input:"+input);

        if(input.startsWith("content")){
            Uri uri = Uri.parse(input);
            return getFilePathByContentUri(tag,context,uri);
        }
        else if(input.startsWith("file://")){
           Log.d(tag, " return  substring(7):"+input.substring(7));
           return input.substring(7);
        }

        return input;
    }
    



}

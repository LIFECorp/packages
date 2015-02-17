/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.gallery3d.glrenderer;

import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.glrenderer.GLES11Canvas;
import com.android.gallery3d.glrenderer.GLES20Canvas;
// ExtTexture is a texture whose content comes from a external texture.
// Before drawing, setSize() should be called.
public class ExtTexture extends BasicTexture {

    private int mTarget;

    public ExtTexture(GLCanvas canvas, int target) {
        GLId glId = canvas.getGLId();
        mId = glId.generateTexture();
        mTarget = target;
    }

    ///M:add for camera launch performance@{
    public ExtTexture(int target) {
        GLId glId = ApiHelper.HAS_GLES20_REQUIRED ? GLES20Canvas.getCameraGLId() : GLES11Canvas.getCameraGLId();
        mId = glId.generateTexture();
        mTarget = target;
    }
    ///@}

    /// M: @{
    // initialize canvas and mark state as loaded to ensure it can be deleted  
    public ExtTexture(GLCanvas canvas, int target, boolean markLoaded) {
        super(canvas, 0, STATE_LOADED);
        GLId glId = canvas.getGLId();
        mId = glId.generateTexture();
        mTarget = target;
    }
    /// @}

    private void uploadToCanvas(GLCanvas canvas) {
        canvas.setTextureParameters(this);
        setAssociatedCanvas(canvas);
        mState = STATE_LOADED;
    }

    @Override
    protected boolean onBind(GLCanvas canvas) {
        if (!isLoaded()) {
            uploadToCanvas(canvas);
        }

        return true;
    }

    @Override
    public int getTarget() {
        return mTarget;
    }

    @Override
    public boolean isOpaque() {
        return true;
    }

    @Override
    public void yield() {
        // we cannot free the texture because we have no backup.
    }
}

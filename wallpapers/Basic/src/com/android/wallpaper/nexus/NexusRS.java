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

package com.android.wallpaper.nexus;

import static android.renderscript.Element.RGBA_8888;
import static android.renderscript.Element.RGB_565;
import static android.renderscript.ProgramStore.DepthFunc.ALWAYS;
import static android.renderscript.Sampler.Value.LINEAR;
import static android.renderscript.Sampler.Value.CLAMP;
import static android.renderscript.Sampler.Value.WRAP;

import com.android.wallpaper.R;
import com.android.wallpaper.RenderScriptScene;

import android.app.WallpaperManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.renderscript.*;
import android.renderscript.ProgramStore.BlendDstFunc;
import android.renderscript.ProgramStore.BlendSrcFunc;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;

import java.util.TimeZone;

class NexusRS extends RenderScriptScene {
    private final BitmapFactory.Options mOptionsARGB = new BitmapFactory.Options();

    private ProgramVertexFixedFunction.Constants mPvOrthoAlloc;

    private int mInitialWidth;
    private int mInitialHeight;
    private float mWorldScaleX;
    private float mWorldScaleY;
    private float mXOffset;
    private ScriptC_nexus mScript;

    public NexusRS(int width, int height) {
        super(width, height);

        mInitialWidth = width;
        mInitialHeight = height;
        mWorldScaleX = 1.0f;
        mWorldScaleY = 1.0f;

        mOptionsARGB.inScaled = false;
        mOptionsARGB.inPreferredConfig = Bitmap.Config.ARGB_8888;

        /// M: support different dpi. @{
        getDensityMode();
        getConfig();
        /// @}
    }

    @Override
    public void setOffset(float xOffset, float yOffset, int xPixels, int yPixels) {
        mXOffset = xOffset;
        mScript.set_gXOffset(xOffset);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height); // updates mWidth, mHeight

        // android.util.Log.d("NexusRS", String.format("resize(%d, %d)", width, height));

        mWorldScaleX = (float)mInitialWidth / width;
        mWorldScaleY = (float)mInitialHeight / height;
        mScript.set_gWorldScaleX(mWorldScaleX);
        mScript.set_gWorldScaleY(mWorldScaleY);
    }

    @Override
    protected ScriptC createScript() {
        mScript = new ScriptC_nexus(mRS, mResources, R.raw.nexus);

        createProgramFragmentStore();
        createProgramFragment();
        createProgramVertex();
        createState();

        /// M: support different dpi @{
        createPulseSize();
        /// @}

        mScript.set_gTBackground(loadTexture(R.drawable.pyramid_background));
        mScript.set_gTPulse(loadTextureARGB(R.drawable.pulse));
        mScript.set_gTGlow(loadTextureARGB(R.drawable.glow));
        mScript.setTimeZone(TimeZone.getDefault().getID());
        mScript.invoke_initPulses();
        return mScript;
    }

    private void createState() {
        int mode;
        try {
            mode = mResources.getInteger(R.integer.nexus_mode);
        } catch (Resources.NotFoundException exc) {
            mode = 0; // standard nexus mode
        }

        mScript.set_gIsPreview(isPreview() ? 1 : 0);
        mScript.set_gMode(mode);
        mScript.set_gXOffset(0.f);
        mScript.set_gWorldScaleX(mWorldScaleX);
        mScript.set_gWorldScaleY(mWorldScaleY);
    }

    private Allocation loadTexture(int id) {
        return Allocation.createFromBitmapResource(mRS, mResources, id,
                                           Allocation.MipmapControl.MIPMAP_NONE,
                                           Allocation.USAGE_GRAPHICS_TEXTURE);
    }

    private Allocation loadTextureARGB(int id) {
        Bitmap b = BitmapFactory.decodeResource(mResources, id, mOptionsARGB);
        return Allocation.createFromBitmap(mRS, b,
                                           Allocation.MipmapControl.MIPMAP_NONE,
                                           Allocation.USAGE_GRAPHICS_TEXTURE);
    }


    private void createProgramFragment() {
        // sampler and program fragment for pulses
        ProgramFragmentFixedFunction.Builder builder = new ProgramFragmentFixedFunction.Builder(mRS);
        builder.setTexture(ProgramFragmentFixedFunction.Builder.EnvMode.MODULATE,
                           ProgramFragmentFixedFunction.Builder.Format.RGBA, 0);
        ProgramFragment pft = builder.create();
        /// M: using CLAPM_LINEAR to fix graphics atifacts in ALPS00395074 @{
        pft.bindSampler(Sampler.CLAMP_LINEAR(mRS), 0);
        /// M: @}
        mScript.set_gPFTexture(pft);

        // sampler and program fragment for background image
        builder = new ProgramFragmentFixedFunction.Builder(mRS);
        builder.setTexture(ProgramFragmentFixedFunction.Builder.EnvMode.MODULATE,
                           ProgramFragmentFixedFunction.Builder.Format.RGB, 0);
        ProgramFragment pft565 = builder.create();
        pft565.bindSampler(Sampler.CLAMP_NEAREST(mRS), 0);
        mScript.set_gPFTexture565(pft565);
    }

    private void createProgramFragmentStore() {
        ProgramStore.Builder builder = new ProgramStore.Builder(mRS);
        builder.setDepthFunc(ALWAYS);
        builder.setBlendFunc(BlendSrcFunc.ONE, BlendDstFunc.ONE);
        builder.setDitherEnabled(false);
        ProgramStore solid = builder.create();
        mRS.bindProgramStore(solid);

        builder.setBlendFunc(BlendSrcFunc.SRC_ALPHA, BlendDstFunc.ONE);
        mScript.set_gPSBlend(builder.create());
    }

    private void createProgramVertex() {
        mPvOrthoAlloc = new ProgramVertexFixedFunction.Constants(mRS);
        Matrix4f proj = new Matrix4f();
        proj.loadOrthoWindow(mWidth, mHeight);
        mPvOrthoAlloc.setProjection(proj);

        ProgramVertexFixedFunction.Builder pvb = new ProgramVertexFixedFunction.Builder(mRS);
        pvb.setTextureMatrixEnable(true);
        ProgramVertex pv = pvb.create();
        ((ProgramVertexFixedFunction)pv).bindConstants(mPvOrthoAlloc);
        mRS.bindProgramVertex(pv);
    }

    @Override
    public Bundle onCommand(String action, int x, int y, int z, Bundle extras,
            boolean resultRequested) {

        if (mWidth < mHeight) {
            // nexus.rs ignores the xOffset when rotated; we shall endeavor to do so as well
            x = (int) (x + mXOffset * mWidth / mWorldScaleX);
        }

        // android.util.Log.d("NexusRS", String.format(
        //     "dw=%d, bw=%d, xOffset=%g, x=%d",
        //     dw, bw, mWorldState.xOffset, x));

        if (WallpaperManager.COMMAND_TAP.equals(action)
                || WallpaperManager.COMMAND_SECONDARY_TAP.equals(action)
                || WallpaperManager.COMMAND_DROP.equals(action)) {
            mScript.invoke_addTap(x, y);
        }
        return null;
    }

    /// M: support different dpi @{
    private static final int PLUSE_SIZE = 14;
    private static final int HALF_PULSE_SIZE = 7;

    private static final int DENSITY_MODE_LOW = 0; // DisplayMetrics.DENSITY_LOW
    private static final int DENSITY_MODE_MEDIUM = 1; // DisplayMetrics.DENSITY_MEDIUM
    private static final int DENSITY_MODE_HIGH = 2; // DisplayMetrics.DENSITY_HIGH
    private static final int DENSITY_MODE_LOWER = 3; // < DisplayMetrics.DENSITY_LOW
    private static final int DENSITY_MODE_HIGHER = 4; // < DisplayMetrics.DENSITY_LOW
    
    private static final int HD_WIDTH_PORT = 720;
    private static final int HD_HEIGHT_PORT = 1280;
    private static final int HD_WIDTH_LAND = 1280;
    private static final int HD_HEIGHT_LAND = 720;
    
    private int mDensityMode;
    protected int mScaleParam0;
    protected int mScaleParam1;
    
    private void getDensityMode() {
        mDensityMode = -1;
        int dpi = DisplayMetrics.DENSITY_DEVICE;
        switch (dpi) {
            case DisplayMetrics.DENSITY_LOW:
                mDensityMode = DENSITY_MODE_LOW;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                mDensityMode = DENSITY_MODE_MEDIUM;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                mDensityMode = DENSITY_MODE_HIGH;
                break;
            default:
                mDensityMode = -1;
        }

        if (mDensityMode == -1) {
            if (dpi < DENSITY_MODE_LOW) {
                mDensityMode = DENSITY_MODE_LOWER;
            } else {
                mDensityMode = DENSITY_MODE_HIGHER;
            }
        }
    }

    private void getConfig() {
        /// M: tuing mScale_Param0 and mScale_Param1 for better appearance.
        switch (mDensityMode) {
            case DENSITY_MODE_HIGHER:
                if ((mWidth >= HD_WIDTH_PORT && mHeight >= HD_HEIGHT_PORT) || (mWidth >= HD_WIDTH_LAND && mHeight >= HD_HEIGHT_LAND)) {
                    mScaleParam0 = 2;
                    mScaleParam1 = 3;
                } else {
                    mScaleParam0 = 1;
                    mScaleParam1 = 1;
                }
                break;
            default:
                mScaleParam0 = 1;
                mScaleParam1 = 1;
        }
    }

    private void createPulseSize() {
    	int pluseSize = PLUSE_SIZE * mScaleParam1 / mScaleParam0;
    	int halfPluseSize = HALF_PULSE_SIZE * mScaleParam1 / mScaleParam0;
    	
    	mScript.set_gPluseSize(pluseSize);
    	mScript.set_gHalfPluseSize(halfPluseSize);
    }    
    /// @}
}

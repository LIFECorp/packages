/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.gallery3d.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.os.Message;
import android.util.FloatMath;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;

import com.android.gallery3d.R;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.common.ApiHelper;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.data.MediaItem;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.glrenderer.BasicTexture;
import com.android.gallery3d.glrenderer.FadeOutTexture;
import com.android.gallery3d.glrenderer.GLCanvas;
import com.android.gallery3d.glrenderer.RawTexture;
import com.android.gallery3d.glrenderer.ResourceTexture;
import com.android.gallery3d.glrenderer.StringTexture;
import com.android.gallery3d.glrenderer.Texture;
import com.android.gallery3d.ui.GLRoot;
import com.android.gallery3d.ui.TileImageView;
import com.android.gallery3d.util.GalleryUtils;
import com.android.gallery3d.util.RangeArray;

import android.app.Activity;
import com.mediatek.gallery3d.drm.DrmHelper;

import com.mediatek.gallery3d.panorama.PanoramaConfig;
import com.mediatek.gallery3d.panorama.PanoramaHelper;
import com.mediatek.gallery3d.panorama.PanoramaSwitchBarView;
import com.mediatek.gallery3d.util.MediatekFeature;
import com.mediatek.gallery3d.util.MtkLog;
import com.android.gallery3d.util.UsageStatistics;
public class PhotoView extends GLView {
    @SuppressWarnings("unused")
    private static final String TAG = "Gallery2/PhotoView";
    private final int mPlaceholderColor;

    public static final int INVALID_SIZE = -1;
    public static final long INVALID_DATA_VERSION =
            MediaObject.INVALID_DATA_VERSION;
    // M: 6592 panorama add @{
    public static final int INAVALID_PANORAMA_INDEX = -1;
    // @}

    // M: using to check if user is scrolling or flinging
    private boolean mInDown = false;
    
    private int[] mTypes = new int[2 * SCREEN_NAIL_MAX + 1];
    public static class Size {
        public int width;
        public int height;
    }

    public interface Model extends TileImageView.TileSource {
        public int getCurrentIndex();
        public void moveTo(int index);

        // Returns the size for the specified picture. If the size information is
        // not avaiable, width = height = 0.
        public void getImageSize(int offset, Size size);

        // Returns the media item for the specified picture.
        public MediaItem getMediaItem(int offset);

        // Returns the rotation for the specified picture.
        public int getImageRotation(int offset);

        // This amends the getScreenNail() method of TileImageView.Model to get
        // ScreenNail at previous (negative offset) or next (positive offset)
        // positions. Returns null if the specified ScreenNail is unavailable.
        public ScreenNail getScreenNail(int offset);

        // Set this to true if we need the model to provide full images.
        public void setNeedFullImage(boolean enabled);

        // Returns true if the item is the Camera preview.
        public boolean isCamera(int offset);

        // Returns true if the item is the Panorama.
        public boolean isPanorama(int offset);

        // Returns true if the item is a static image that represents camera
        // preview.
        public boolean isStaticCamera(int offset);

        // Returns true if the item is a Video.
        public boolean isVideo(int offset);

        // Returns true if the item can be deleted.
        public boolean isDeletable(int offset);

        public static final int LOADING_INIT = 0;
        public static final int LOADING_COMPLETE = 1;
        public static final int LOADING_FAIL = 2;

        public int getLoadingState(int offset);

        // When data change happens, we need to decide which MediaItem to focus
        // on.
        //
        // 1. If focus hint path != null, we try to focus on it if we can find
        // it.  This is used for undo a deletion, so we can focus on the
        // undeleted item.
        //
        // 2. Otherwise try to focus on the MediaItem that is currently focused,
        // if we can find it.
        //
        // 3. Otherwise try to focus on the previous MediaItem or the next
        // MediaItem, depending on the value of focus hint direction.
        public static final int FOCUS_HINT_NEXT = 0;
        public static final int FOCUS_HINT_PREVIOUS = 1;
        public void setFocusHintDirection(int direction);
        public void setFocusHintPath(Path path);

        // M: added for MAV
        public boolean isMav(int offset);
        // M: 6592 panorama add @{
        public ScreenNail getPanoramaScreenNail(int offset);
        // @}
        /// M: added for open image from local @{
        public void setIsOpenFromLocal(boolean isOpenFromLocal);
        public void showWaitingIfNeed();
        // @}
    }

    private static final boolean mIsDrmSupported = 
                                          MediatekFeature.isDrmSupported();

    public interface Listener {
        public void onSingleTapUp(int x, int y);
        // Add for Camera Feature Object Tracking
        public void onLongPress(int x, int y);
        public void onSingleTapConfirmed(int x, int y);
        public void unlockOrientation();
        public void lockOrientation();
        public void onFullScreenChanged(boolean full);
        public void onActionBarAllowed(boolean allowed);
        public void onActionBarWanted();
        public void onCurrentImageUpdated();
        public void onDeleteImage(Path path, int offset);
        public void onUndoDeleteImage();
        public void onCommitDeleteImage();
        public void onFilmModeChanged(boolean enabled);
        public void onPictureCenter(boolean isCamera);
        public void onUndoBarVisibilityChanged(boolean visible);
        /// M: for camera new feature, support more behaivor @{
        int FULLSCREEN_TYPE_MINIMAL_SCALE = 1 << 0;
        int FULLSCREEN_TYPE_CENETER = 1 << 1;
        public void onFullScreenChanged(boolean full, int type);
        public boolean canSlideToPrePicture();
        /// @}
        /// M: for mav, show or hide MavSeekBar
        public void onMavSeekBarAllowed(boolean allowed);
        // 6592 panorama add
        public void onPanoramaFrameUpdate(int frameIndex);
        // M: if user is scrolling or flinging, invisible seekbar
        public void onSeekBarVisible(boolean visible);
    }

    // The rules about orientation locking:
    //
    // (1) We need to lock the orientation if we are in page mode camera
    // preview, so there is no (unwanted) rotation animation when the user
    // rotates the device.
    //
    // (2) We need to unlock the orientation if we want to show the action bar
    // because the action bar follows the system orientation.
    //
    // The rules about action bar:
    //
    // (1) If we are in film mode, we don't show action bar.
    //
    // (2) If we go from camera to gallery with capture animation, we show
    // action bar.
    private static final int MSG_CANCEL_EXTRA_SCALING = 2;
    private static final int MSG_SWITCH_FOCUS = 3;
    private static final int MSG_CAPTURE_ANIMATION_DONE = 4;
    private static final int MSG_DELETE_ANIMATION_DONE = 5;
    private static final int MSG_DELETE_DONE = 6;
    private static final int MSG_UNDO_BAR_TIMEOUT = 7;
    private static final int MSG_UNDO_BAR_FULL_CAMERA = 8;
    // 6592 panorama add
    private static final int MSG_PANORAMA_AUTO_PLAY = 9;
    
    private static final float SWIPE_THRESHOLD = 300f;

    private static final float DEFAULT_TEXT_SIZE = 20;
    private static float TRANSITION_SCALE_FACTOR = 0.74f;
    private static final int ICON_RATIO = 6;

    // whether we want to apply card deck effect in page mode.
    private static final boolean CARD_EFFECT = true;

    // whether we want to apply offset effect in film mode.
    private static final boolean OFFSET_EFFECT = true;

    // Used to calculate the scaling factor for the card deck effect.
    private ZInterpolator mScaleInterpolator = new ZInterpolator(0.5f);

    // Used to calculate the alpha factor for the fading animation.
    private AccelerateInterpolator mAlphaInterpolator =
            new AccelerateInterpolator(0.9f);

    // We keep this many previous ScreenNails. (also this many next ScreenNails)
    public static final int SCREEN_NAIL_MAX = 3;

    // These are constants for the delete gesture.
    private static final int SWIPE_ESCAPE_VELOCITY = 500; // dp/sec
    private static final int MAX_DISMISS_VELOCITY = 2500; // dp/sec
    private static final int SWIPE_ESCAPE_DISTANCE = 150; // dp

    // The picture entries, the valid index is from -SCREEN_NAIL_MAX to
    // SCREEN_NAIL_MAX.
    private final RangeArray<Picture> mPictures =
            new RangeArray<Picture>(-SCREEN_NAIL_MAX, SCREEN_NAIL_MAX);
    private Size[] mSizes = new Size[2 * SCREEN_NAIL_MAX + 1];

    private final MyGestureListener mGestureListener;
    private final GestureRecognizer mGestureRecognizer;
    private final PositionController mPositionController;

    private Listener mListener;
    private Model mModel;
    private StringTexture mNoThumbnailText;
    private TileImageView mTileView;
    private EdgeView mEdgeView;
    private UndoBarView mUndoBar;
    private Texture mVideoPlayIcon;

    private SynchronizedHandler mHandler;

    private boolean mCancelExtraScalingPending;
    private boolean mFilmMode = false;
    private boolean mWantPictureCenterCallbacks = false;
    private int mDisplayRotation = 0;
    private int mCompensation = 0;
    private boolean mFullScreenCamera;
    private Rect mCameraRelativeFrame = new Rect();
    private Rect mCameraRect = new Rect();
    private boolean mFirst = true;

    // [mPrevBound, mNextBound] is the range of index for all pictures in the
    // model, if we assume the index of current focused picture is 0.  So if
    // there are some previous pictures, mPrevBound < 0, and if there are some
    // next pictures, mNextBound > 0.
    private int mPrevBound;
    private int mNextBound;

    // This variable prevents us doing snapback until its values goes to 0. This
    // happens if the user gesture is still in progress or we are in a capture
    // animation.
    private int mHolding;
    private static final int HOLD_TOUCH_DOWN = 1;
    private static final int HOLD_CAPTURE_ANIMATION = 2;
    private static final int HOLD_DELETE = 4;

    // mTouchBoxIndex is the index of the box that is touched by the down
    // gesture in film mode. The value Integer.MAX_VALUE means no box was
    // touched.
    private int mTouchBoxIndex = Integer.MAX_VALUE;
    // Whether the box indicated by mTouchBoxIndex is deletable. Only meaningful
    // if mTouchBoxIndex is not Integer.MAX_VALUE.
    private boolean mTouchBoxDeletable;
    // This is the index of the last deleted item. This is only used as a hint
    // to hide the undo button when we are too far away from the deleted
    // item. The value Integer.MAX_VALUE means there is no such hint.
    private int mUndoIndexHint = Integer.MAX_VALUE;

    private Context mContext;

    private boolean mIsMavLoadingFinished = false;

    public PhotoView(AbstractGalleryActivity activity) {
        mTileView = new TileImageView(activity);
        addComponent(mTileView);
        mContext = activity.getAndroidContext();
        mPlaceholderColor = mContext.getResources().getColor(
                R.color.photo_placeholder);
        mEdgeView = new EdgeView(mContext);
        addComponent(mEdgeView);
        // M: 6592 panorama add @{
        if (MediatekFeature.isPanorama3DSupported()) {
            mPanoramaSwitchBar = new PanoramaSwitchBarView(mContext);
            addComponent(mPanoramaSwitchBar);
            mPanoramaSwitchBar.setVisibility(GLView.INVISIBLE);
            mPanoramaSwitchBar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(GLView v) {
                    switch (mPanoramaSwitchBar.getFocusButtion()) {
                    case PanoramaSwitchBarView.BUTTON_NORMAL:
                        switchToPanoramaMode(PANORAMA_SHOW_MODE_NORMAL);
                        break;
                    case PanoramaSwitchBarView.BUTTON_3D:
                        switchToPanoramaMode(PANORAMA_SHOW_MODE_3D);
                        break;
                    default:
                        break;
                    }
                }
            });
        }
        // @}
        
        mUndoBar = new UndoBarView(mContext);
        addComponent(mUndoBar);
        mUndoBar.setVisibility(GLView.INVISIBLE);
        mUndoBar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(GLView v) {
                    mListener.onUndoDeleteImage();
                    hideUndoBar();
                }
            });
        mNoThumbnailText = StringTexture.newInstance(
                mContext.getString(R.string.no_thumbnail),
                DEFAULT_TEXT_SIZE, Color.WHITE);

        ///M: added for WFD @{
        DrmHelper.drmResourceInit();
        /// @}
        mHandler = new MyHandler(activity.getGLRoot());

        mGestureListener = new MyGestureListener();
        mGestureRecognizer = new GestureRecognizer(mContext, mGestureListener);

        mPositionController = new PositionController(mContext,
                new PositionController.Listener() {

            @Override
            public void invalidate() {
                PhotoView.this.invalidate();
            }

            @Override
            public boolean isHoldingDown() {
                return (mHolding & HOLD_TOUCH_DOWN) != 0;
            }

            @Override
            public boolean isHoldingDelete() {
                return (mHolding & HOLD_DELETE) != 0;
            }

            @Override
            public void onPull(int offset, int direction) {
                // M: if edge effect is showing, set seek bar visible @{
                mInDown = false;
                mListener.onSeekBarVisible(true);
                // @}
                mEdgeView.onPull(offset, direction);
            }

            @Override
            public void onRelease() {
                mEdgeView.onRelease();
            }

            @Override
            public void onAbsorb(int velocity, int direction) {
                mEdgeView.onAbsorb(velocity, direction);
            }
        });
        mVideoPlayIcon = new ResourceTexture(mContext, R.drawable.ic_control_play);
        for (int i = -SCREEN_NAIL_MAX; i <= SCREEN_NAIL_MAX; i++) {
            if (i == 0) {
                mPictures.put(i, new FullPicture());
            } else {
                mPictures.put(i, new ScreenNailPicture(i));
            }
        }
        
        // M: added for MAV
        mMavOverlay = new ResourceTexture(mContext, R.drawable.ic_mav_overlay);
    }

    public void stopScrolling() {
        mPositionController.stopScrolling();
    }

    public void setModel(Model model) {
        mModel = model;
        mTileView.setModel(mModel);
        ///M:
        mPositionController.setIsCamera(mModel.isCamera(0));
    }

    class MyHandler extends SynchronizedHandler {
        public MyHandler(GLRoot root) {
            super(root);
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MSG_CANCEL_EXTRA_SCALING: {
                    mGestureRecognizer.cancelScale();
                    mPositionController.setExtraScalingRange(false);
                    mCancelExtraScalingPending = false;
                    break;
                }
                case MSG_SWITCH_FOCUS: {
                    switchFocus();
                    break;
                }
                case MSG_CAPTURE_ANIMATION_DONE: {
                    // message.arg1 is the offset parameter passed to
                    // switchWithCaptureAnimation().
                    captureAnimationDone(message.arg1);
                    break;
                }
                case MSG_DELETE_ANIMATION_DONE: {
                    // message.obj is the Path of the MediaItem which should be
                    // deleted. message.arg1 is the offset of the image.
                    mListener.onDeleteImage((Path) message.obj, message.arg1);
                    // Normally a box which finishes delete animation will hold
                    // position until the underlying MediaItem is actually
                    // deleted, and HOLD_DELETE will be cancelled that time. In
                    // case the MediaItem didn't actually get deleted in 2
                    // seconds, we will cancel HOLD_DELETE and make it bounce
                    // back.

                    // We make sure there is at most one MSG_DELETE_DONE
                    // in the handler.
                    mHandler.removeMessages(MSG_DELETE_DONE);
                    Message m = mHandler.obtainMessage(MSG_DELETE_DONE);
                    mHandler.sendMessageDelayed(m, 2000);

                    int numberOfPictures = mNextBound - mPrevBound + 1;
                    if (numberOfPictures == 2) {
                        if (mModel.isCamera(mNextBound)
                                || mModel.isCamera(mPrevBound)) {
                            numberOfPictures--;
                        }
                    }
                    showUndoBar(numberOfPictures <= 1);
                    break;
                }
                case MSG_DELETE_DONE: {
                    if (!mHandler.hasMessages(MSG_DELETE_ANIMATION_DONE)) {
                        mHolding &= ~HOLD_DELETE;
                        snapback();
                    }
                    break;
                }
                case MSG_UNDO_BAR_TIMEOUT: {
                    checkHideUndoBar(UNDO_BAR_TIMEOUT);
                    break;
                }
                case MSG_UNDO_BAR_FULL_CAMERA: {
                    checkHideUndoBar(UNDO_BAR_FULL_CAMERA);
                    break;
                }
                // M: 6592 panorama add @{
                case MSG_PANORAMA_AUTO_PLAY: {
                    if (MediatekFeature.isPanorama3DSupported()) {
                        autoPanoramaPlayback();
                    }
                    break;
                }
                // @}
                default: throw new AssertionError(message.what);
            }
        }
    }

    public void setWantPictureCenterCallbacks(boolean wanted) {
        mWantPictureCenterCallbacks = wanted;
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Data/Image change notifications
    ////////////////////////////////////////////////////////////////////////////

    public void notifyDataChange(int[] fromIndex, int prevBound, int nextBound) {
        mPrevBound = prevBound;
        mNextBound = nextBound;

        // Update mTouchBoxIndex
        if (mTouchBoxIndex != Integer.MAX_VALUE) {
            int k = mTouchBoxIndex;
            mTouchBoxIndex = Integer.MAX_VALUE;
            for (int i = 0; i < 2 * SCREEN_NAIL_MAX + 1; i++) {
                if (fromIndex[i] == k) {
                    mTouchBoxIndex = i - SCREEN_NAIL_MAX;
                    break;
                }
            }
        }

        // Hide undo button if we are too far away
        if (mUndoIndexHint != Integer.MAX_VALUE) {
            if (Math.abs(mUndoIndexHint - mModel.getCurrentIndex()) >= 3) {
                hideUndoBar();
            }
        }

        // Update the ScreenNails.
        for (int i = -SCREEN_NAIL_MAX; i <= SCREEN_NAIL_MAX; i++) {
            Picture p =  mPictures.get(i);
            p.reload();
            mSizes[i + SCREEN_NAIL_MAX] = p.getSize();
            mTypes[i + SCREEN_NAIL_MAX] = p.getSubType();
        }

        boolean wasDeleting = mPositionController.hasDeletingBox();

        // Move the boxes
        mPositionController.moveBox(fromIndex, mPrevBound < 0, mNextBound > 0,
                mModel.isCamera(0), mSizes, mTypes);

        for (int i = -SCREEN_NAIL_MAX; i <= SCREEN_NAIL_MAX; i++) {
            setPictureSize(i);
        }

        boolean isDeleting = mPositionController.hasDeletingBox();

        // If the deletion is done, make HOLD_DELETE persist for only the time
        // needed for a snapback animation.
        if (wasDeleting && !isDeleting) {
            mHandler.removeMessages(MSG_DELETE_DONE);
            Message m = mHandler.obtainMessage(MSG_DELETE_DONE);
            mHandler.sendMessageDelayed(
                    m, PositionController.SNAPBACK_ANIMATION_TIME);
        }

        invalidate();
    }

    public boolean isDeleting() {
        return (mHolding & HOLD_DELETE) != 0
                && mPositionController.hasDeletingBox();
    }

    public void notifyImageChange(int index) {
        if (index == 0) {
            mListener.onCurrentImageUpdated();
        }
        mPictures.get(index).reload();
        setPictureSize(index);
        invalidate();
    }

    private void setPictureSize(int index) {
        Picture p = mPictures.get(index);

        //set extra info to controll image display size
        mPositionController.setImageSubType(index, p.getSubType());

        mPositionController.setImageSize(index, p.getSize(),
                index == 0 && p.isCamera() ? mCameraRect : null);
    }

    @Override
    protected void onLayout(
            boolean changeSize, int left, int top, int right, int bottom) {
        int w = right - left;
        int h = bottom - top;
        ///M: added for smartbook. @{
        mScreenCenterY = h;
        mScreenCenterX = w;
        /// }@
        mTileView.layout(0, 0, w, h);
        mEdgeView.layout(0, 0, w, h);
        mUndoBar.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        mUndoBar.layout(0, h - mUndoBar.getMeasuredHeight(), w, h);
        // M: 6592 panorama add @{
        if (MediatekFeature.isPanorama3DSupported() && mPanoramaSwitchBar != null) {
            mPanoramaSwitchBar.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int leftGap = (right - left - mPanoramaSwitchBar.getMeasuredWidth()) / 2;
            mPanoramaSwitchBar.layout(left + leftGap, mPanoramaSwitchBar.SWITCH_BAR_TOP_GAP, right - leftGap,
                    mPanoramaSwitchBar.getMeasuredHeight() + mPanoramaSwitchBar.SWITCH_BAR_TOP_GAP);
        }
        // @}
        
        GLRoot root = getGLRoot();
        int displayRotation = root.getDisplayRotation();
        int compensation = root.getCompensation();
        if (mDisplayRotation != displayRotation
                || mCompensation != compensation) {
            mDisplayRotation = displayRotation;
            mCompensation = compensation;

            // We need to change the size and rotation of the Camera ScreenNail,
            // but we don't want it to animate because the size doen't actually
            // change in the eye of the user.
            for (int i = -SCREEN_NAIL_MAX; i <= SCREEN_NAIL_MAX; i++) {
                Picture p = mPictures.get(i);
                if (p.isCamera()) {
                    p.forceSize();
                }
            }
        }

        updateCameraRect();
        mPositionController.setConstrainedFrame(mCameraRect);
        if (changeSize) {
            mPositionController.setViewSize(getWidth(), getHeight());
        }
    }

    // Update the camera rectangle due to layout change or camera relative frame
    // change.
    private void updateCameraRect() {
        // Get the width and height in framework orientation because the given
        // mCameraRelativeFrame is in that coordinates.
        int w = getWidth();
        int h = getHeight();
        if (mCompensation % 180 != 0) {
            int tmp = w;
            w = h;
            h = tmp;
        }
        int l = mCameraRelativeFrame.left;
        int t = mCameraRelativeFrame.top;
        int r = mCameraRelativeFrame.right;
        int b = mCameraRelativeFrame.bottom;

        // Now convert it to the coordinates we are using.
        switch (mCompensation) {
            case 0: mCameraRect.set(l, t, r, b); break;
            case 90: mCameraRect.set(h - b, l, h - t, r); break;
            case 180: mCameraRect.set(w - r, h - b, w - l, h - t); break;
            case 270: mCameraRect.set(t, w - r, b, w - l); break;
        }

        Log.d(TAG, "compensation = " + mCompensation
                + ", CameraRelativeFrame = " + mCameraRelativeFrame
                + ", mCameraRect = " + mCameraRect);
    }

    public void setCameraRelativeFrame(Rect frame) {
        mCameraRelativeFrame.set(frame);
        updateCameraRect();
        /* M: here we update it for some cases:
         * onLayout and setImageSize will before setCameraRelativeFrame();
         * @{
         */
        // M: update2: do NOT enable this, since it will cause unwanted refresh
        //mPositionController.setConstrainedFrame(mCameraRect);
        /* @} */
        // Originally we do
        //     mPositionController.setConstrainedFrame(mCameraRect);
        // here, but it is moved to a parameter of the setImageSize() call, so
        // it can be updated atomically with the CameraScreenNail's size change.
    }

    // Returns the rotation we need to do to the camera texture before drawing
    // it to the canvas, assuming the camera texture is correct when the device
    // is in its natural orientation.
    private int getCameraRotation() {
        return (mCompensation - mDisplayRotation + 360) % 360;
    }

    private int getPanoramaRotation() {
        // This function is magic
        // The issue here is that Pano makes bad assumptions about rotation and
        // orientation. The first is it assumes only two rotations are possible,
        // 0 and 90. Thus, if display rotation is >= 180, we invert the output.
        // The second is that it assumes landscape is a 90 rotation from portrait,
        // however on landscape devices this is not true. Thus, if we are in portrait
        // on a landscape device, we need to invert the output
        int orientation = mContext.getResources().getConfiguration().orientation;
        boolean invertPortrait = (orientation == Configuration.ORIENTATION_PORTRAIT
                && (mDisplayRotation == 90 || mDisplayRotation == 270));
        boolean invert = (mDisplayRotation >= 180);
        if (invert != invertPortrait) {
            return (mCompensation + 180) % 360;
        }
        return mCompensation;
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Pictures
    ////////////////////////////////////////////////////////////////////////////

    private interface Picture {
        void reload();
        void draw(GLCanvas canvas, Rect r);
        void setScreenNail(ScreenNail s);
        boolean isCamera();  // whether the picture is a camera preview
        boolean isDeletable();  // whether the picture can be deleted
        void forceSize();  // called when mCompensation changes
        Size getSize();

        int getSubType();
    }

    class FullPicture implements Picture {
        private int mRotation;
        private boolean mIsCamera;
        private boolean mIsPanorama;
        private boolean mIsStaticCamera;
        private boolean mIsVideo;
        private boolean mIsDeletable;
        private int mLoadingState = Model.LOADING_INIT;
        private Size mSize = new Size();
        // M: 6592 panorama add
        private MediaItem mItem;
        
        // M: added for MAV
        private boolean mIsMav;
        private int mSubType;

        public void FullPicture(TileImageView tileView) {
            mTileView = tileView;
        }

        @Override
        public void reload() {
            // M: 6592 panorama add @{
            if (MediatekFeature.isPanorama3DSupported() && 
                    mModel.getMediaItem(0) != mItem) {
                resetPanoramaMode();
                mItem = mModel.getMediaItem(0);
            }
            // @}
            // mImageWidth and mImageHeight will get updated
            mTileView.notifyModelInvalidated();

            mIsCamera = mModel.isCamera(0);
            mIsPanorama = mModel.isPanorama(0);
            // M: 6592 panorama add @{
            if (MediatekFeature.isPanorama3DSupported()) {
                setIsPanorama(mIsPanorama && !mIsCamera);
            }
            // @}
            mIsStaticCamera = mModel.isStaticCamera(0);
            mIsVideo = mModel.isVideo(0);
            
            // M: added for MAV
            mIsMav = mModel.isMav(0);

            mIsDeletable = mModel.isDeletable(0);
            mLoadingState = mModel.getLoadingState(0);
            ScreenNail screenNail = mModel.getScreenNail(0);
            setScreenNail(screenNail);
            // M: 6592 panorama add @{
            if (MediatekFeature.isPanorama3DSupported() && mIsPanorama) {
                setPanoramaScreenNail(mModel.getPanoramaScreenNail(0));
                if (mTileView.getPanoramaMode() == PANORAMA_SHOW_MODE_3D) {
                    startPanoramaAutoPlayback();
                }
            }
            // @}
            updateSize();

            updateSubType(screenNail);
        }

        @Override
        public Size getSize() {
            // M: 6592 panorama add
            updateSize();
            return mSize;
        }

        @Override
        public void forceSize() {
            updateSize();
            mPositionController.forceImageSize(0, mSize);
        }

        private void updateSize() {
            if (mIsPanorama) {
                mRotation = getPanoramaRotation();
            } else if (mIsCamera && !mIsStaticCamera) {
                mRotation = getCameraRotation();
            } else {
                mRotation = mModel.getImageRotation(0);
            }
            // M: 6592 panorama add @{
            if (MediatekFeature.isPanorama3DSupported() && mIsPanorama) {
                if (mPanoramaMode == PANORAMA_SHOW_MODE_3D) {
                    mRotation = 0;
                } else {
                    mRotation = mModel.getImageRotation(0);
                }
            }
            // @}
            int w = mTileView.mImageWidth;
            int h = mTileView.mImageHeight;
            mSize.width = getRotated(mRotation, w, h);
            mSize.height = getRotated(mRotation, h, w);
        }

        @Override
        public void draw(GLCanvas canvas, Rect r) {
            drawTileView(canvas, r);

            // We want to have the following transitions:
            // (1) Move camera preview out of its place: switch to film mode
            // (2) Move camera preview into its place: switch to page mode
            // The extra mWasCenter check makes sure (1) does not apply if in
            // page mode, we move _to_ the camera preview from another picture.

            // Holdings except touch-down prevent the transitions.
            if ((mHolding & ~HOLD_TOUCH_DOWN) != 0) return;

            if (mWantPictureCenterCallbacks && mPositionController.isCenter()) {
                mListener.onPictureCenter(mIsCamera);
            }
        }

        @Override
        public void setScreenNail(ScreenNail s) {
            mTileView.setScreenNail(s);
        }
        // M: 6592 panorama add
        public void setPanoramaScreenNail(ScreenNail s) {
            mTileView.setPanoramaScreenNail(s);
        }

        public void setIsPanorama(boolean s) {
            mPanoramaSwitchBar.setVisibility(s ? View.VISIBLE : View.GONE);
            mTileView.setIsPanorama(s);
        }
        @Override
        public boolean isCamera() {
            return mIsCamera;
        }

        @Override
        public boolean isDeletable() {
            return mIsDeletable;
        }

        private void drawTileView(GLCanvas canvas, Rect r) {
            float imageScale = mPositionController.getImageScale();
            int viewW = getWidth();
            int viewH = getHeight();
            float cx = r.exactCenterX();
            float cy = r.exactCenterY();
            float scale = 1f;  // the scaling factor due to card effect

            canvas.save(GLCanvas.SAVE_FLAG_MATRIX | GLCanvas.SAVE_FLAG_ALPHA);
            float filmRatio = mPositionController.getFilmRatio();
            
            /// M: added for WFD @{
            if(DrmHelper.drmIsNeedDrawDefault(mItem)){
                
                if(filmRatio == 0f && DrmHelper.getWfdSecurityOption() == 1){
                    canvas.translate(r.centerX(), r.centerY());
                    DrmHelper.drmDrawDefaultImage(canvas);
                }else{
                    drawPlaceHolder(canvas, r);
                }
                canvas.restore();
                return;
            }
            /// @}
            
            boolean wantsCardEffect = CARD_EFFECT && !mIsCamera
                    && filmRatio != 1f && !mPictures.get(-1).isCamera()
                    && !mPositionController.inOpeningAnimation();
            boolean wantsOffsetEffect = OFFSET_EFFECT && mIsDeletable
                    && filmRatio == 1f && r.centerY() != viewH / 2;
            if (wantsCardEffect) {
                // Calculate the move-out progress value.
                int left = r.left;
                int right = r.right;
                float progress = calculateMoveOutProgress(left, right, viewW);
                progress = Utils.clamp(progress, -1f, 1f);

                // We only want to apply the fading animation if the scrolling
                // movement is to the right.
                if (progress < 0) {
                    scale = getScrollScale(progress);
                    float alpha = getScrollAlpha(progress);
                    scale = interpolate(filmRatio, scale, 1f);
                    alpha = interpolate(filmRatio, alpha, 1f);
                    Log.d(TAG, "progress < 0, scale is: " + scale);
                    imageScale *= scale;
                    canvas.multiplyAlpha(alpha);

                    float cxPage;  // the cx value in page mode
                    if (right - left <= viewW) {
                        // If the picture is narrower than the view, keep it at
                        // the center of the view.
                        cxPage = viewW / 2f;
                    } else {
                        // If the picture is wider than the view (it's
                        // zoomed-in), keep the left edge of the object align
                        // the the left edge of the view.
                        cxPage = (right - left) * scale / 2f;
                    }
                    cx = interpolate(filmRatio, cxPage, cx);
                }
            } else if (wantsOffsetEffect) {
                float offset = (float) (r.centerY() - viewH / 2) / viewH;
                float alpha = getOffsetAlpha(offset);
                canvas.multiplyAlpha(alpha);
            }

            // Draw the tile view.
            setTileViewPosition(cx, cy, viewW, viewH, imageScale);
            renderChild(canvas, mTileView);
            
            /// M: draw holder when loading fail
            if (mLoadingState == Model.LOADING_FAIL) {
                drawPlaceHolder(canvas, r);
            }
            // Draw the play video icon and the message.
            canvas.translate((int) (cx + 0.5f), (int) (cy + 0.5f));
            //disable Icon size change
            //int s = (int) (scale * Math.min(r.width(), r.height()) + 0.5f);
            int s = getIconSize(scale);
            
            if (mIsVideo) drawVideoPlayIcon(canvas, s);
            
            // M: added for MAV
            if (mIsMav) {
                drawMavOverlay(canvas, s);
            }

            if (mLoadingState == Model.LOADING_FAIL) {
                drawLoadingFailMessage(canvas);
            }
            // M: 6592 panorama add @{
            if (MediatekFeature.isPanorama3DSupported() &&
                    mIsPanorama &&
                    !mFilmMode &&
                    mPositionController.isAtMinimalScale()) {
                drawPanoramasSwitchBar(canvas, -mPanoramaSwitchBar.getMeasuredWidth() / 2, -viewH / 2
                        + mPanoramaSwitchBar.SWITCH_BAR_TOP_GAP);
            }
            // @}

            // Draw a debug indicator showing which picture has focus (index ==
            // 0).
            //canvas.fillRect(-10, -10, 20, 20, 0x80FF00FF);
            canvas.restore();
        }

        // Set the position of the tile view
        private void setTileViewPosition(float cx, float cy,
                int viewW, int viewH, float scale) {
            // Find out the bitmap coordinates of the center of the view
            int imageW = mPositionController.getImageWidth();
            int imageH = mPositionController.getImageHeight();
            float centerX = imageW / 2f + (viewW / 2f - cx) / scale;
            float centerY = imageH / 2f + (viewH / 2f - cy) / scale;

            float inverseX = imageW - centerX;
            float inverseY = imageH - centerY;
            float x, y;
            switch (mRotation) {
                case 0: x = centerX; y = centerY; break;
                case 90: x = centerY; y = inverseX; break;
                case 180: x = inverseX; y = inverseY; break;
                case 270: x = inverseY; y = centerX; break;
                default:
                    throw new RuntimeException(String.valueOf(mRotation));
            }
            mTileView.setPosition(x, y, scale, mRotation);
        }

        private void updateSubType(ScreenNail screenNail) {
            mSubType = MediatekFeature.getScreenNailSubType(screenNail);
        }

        @Override
        public int getSubType() {
            return mSubType;
        }
    }

    private class ScreenNailPicture implements Picture {
        private int mIndex;
        private int mRotation;
        private ScreenNail mScreenNail;
        private boolean mIsCamera;
        private boolean mIsPanorama;
        private boolean mIsStaticCamera;
        private boolean mIsVideo;
        
        // M: added for MAV
        private boolean mIsMav;

        private boolean mIsDeletable;
        private int mLoadingState = Model.LOADING_INIT;
        private Size mSize = new Size();

        public ScreenNailPicture(int index) {
            mIndex = index;
        }

        @Override
        public void reload() {
            mIsCamera = mModel.isCamera(mIndex);
            mIsPanorama = mModel.isPanorama(mIndex);
            mIsStaticCamera = mModel.isStaticCamera(mIndex);
            mIsVideo = mModel.isVideo(mIndex);
            
            // M: added for MAV
            mIsMav = mModel.isMav(mIndex);
            
            mIsDeletable = mModel.isDeletable(mIndex);
            mLoadingState = mModel.getLoadingState(mIndex);
            // M: 6592 panorama add @{
            if (MediatekFeature.isPanorama3DSupported() && mIsPanorama) {
                setScreenNail(mModel.getPanoramaScreenNail(mIndex));
            } else {
            // @}
                setScreenNail(mModel.getScreenNail(mIndex));
            }
            updateSize();
        }

        @Override
        public Size getSize() {
            // M: 6592 panorama add
            updateSize();
            return mSize;
        }

        @Override
        public void draw(GLCanvas canvas, Rect r) {
            if (mScreenNail == null) {
                // Draw a placeholder rectange if there should be a picture in
                // this position (but somehow there isn't).
                if (mIndex >= mPrevBound && mIndex <= mNextBound) {
                    drawPlaceHolder(canvas, r);
                }
                return;
            }
            int w = getWidth();
            int h = getHeight();
            if (r.left >= w || r.right <= 0 || r.top >= h || r.bottom <= 0) {
                mScreenNail.noDraw(canvas);
                return;
            }

            ///M: added for WFD @{
            if(DrmHelper.drmIsNeedDrawDefault(mModel.getMediaItem(mIndex))){
                drawPlaceHolder(canvas, r);
                return;
            }
            /// @}
            float filmRatio = mPositionController.getFilmRatio();
            boolean wantsCardEffect = CARD_EFFECT && mIndex > 0
                    && filmRatio != 1f && !mPictures.get(0).isCamera();
            boolean wantsOffsetEffect = OFFSET_EFFECT && mIsDeletable
                    && filmRatio == 1f && r.centerY() != h / 2;
            int cx = wantsCardEffect
                    ? (int) (interpolate(filmRatio, w / 2, r.centerX()) + 0.5f)
                    : r.centerX();
            int cy = r.centerY();
            canvas.save(GLCanvas.SAVE_FLAG_MATRIX | GLCanvas.SAVE_FLAG_ALPHA);
            canvas.translate(cx, cy);
            if (wantsCardEffect) {
                float progress = (float) (w / 2 - r.centerX()) / w;
                progress = Utils.clamp(progress, -1, 1);
                float alpha = getScrollAlpha(progress);
                float scale = getScrollScale(progress);
                alpha = interpolate(filmRatio, alpha, 1f);
                scale = interpolate(filmRatio, scale, 1f);
                canvas.multiplyAlpha(alpha);
                canvas.scale(scale, scale, 1);
            } else if (wantsOffsetEffect) {
                float offset = (float) (r.centerY() - h / 2) / h;
                float alpha = getOffsetAlpha(offset);
                canvas.multiplyAlpha(alpha);
            }
            if (mRotation != 0) {
                canvas.rotate(mRotation, 0, 0, 1);
            }
            int drawW = getRotated(mRotation, r.width(), r.height());
            int drawH = getRotated(mRotation, r.height(), r.width());
            mScreenNail.draw(canvas, -drawW / 2, -drawH / 2, drawW, drawH);
            if (isScreenNailAnimating()) {
                invalidate();
            }
            
            // M: for following kinds of icon/info, 
            // do NOT draw on rotated canvas, 
            // so we rotate back and then draw
            if (mRotation != 0) {
                canvas.rotate(-mRotation, 0, 0, 1);
            }
            
            //disable Icon size change
            //int s = Math.min(drawW, drawH);
            int s = getIconSize((float)1);
            if (mIsVideo) drawVideoPlayIcon(canvas, s);
            
            // M: added for MAV
            if (mIsMav) {
                drawMavOverlay(canvas, s);
            }
            
            if (mLoadingState == Model.LOADING_FAIL) {
                drawLoadingFailMessage(canvas);
            }
            canvas.restore();
        }

        private boolean isScreenNailAnimating() {
            // M: change to BitmapTexture from TiledTexture
            return (mScreenNail instanceof BitmapScreenNail)
                    && ((BitmapScreenNail) mScreenNail).isAnimating();
        }

        @Override
        public void setScreenNail(ScreenNail s) {
            mScreenNail = s;
        }

        @Override
        public void forceSize() {
            updateSize();
            mPositionController.forceImageSize(mIndex, mSize);
        }

        private void updateSize() {
            if (mIsPanorama) {
                mRotation = getPanoramaRotation();
            } else if (mIsCamera && !mIsStaticCamera) {
                mRotation = getCameraRotation();
            } else {
                mRotation = mModel.getImageRotation(mIndex);
            }
            // M: 6592 panorama add @{
            if (MediatekFeature.isPanorama3DSupported() && mIsPanorama) {
                mRotation = 0;
            }
            // @}
            if (mScreenNail != null) {
                mSize.width = mScreenNail.getWidth();
                mSize.height = mScreenNail.getHeight();
                // M: we may need to adjust image size
                MediatekFeature.updateSizeForSubtype(mSize, mScreenNail);
            } else {
                // If we don't have ScreenNail available, we can still try to
                // get the size information of it.
                mModel.getImageSize(mIndex, mSize);
            }

            int w = mSize.width;
            int h = mSize.height;
            mSize.width = getRotated(mRotation, w, h);
            mSize.height = getRotated(mRotation, h, w);
        }

        @Override
        public boolean isCamera() {
            return mIsCamera;
        }

        @Override
        public boolean isDeletable() {
            return mIsDeletable;
        }

        @Override
        public int getSubType() {
            return MediatekFeature.getScreenNailSubType(mScreenNail);
        }
    }

    // Draw a gray placeholder in the specified rectangle.
    private void drawPlaceHolder(GLCanvas canvas, Rect r) {
        canvas.fillRect(r.left, r.top, r.width(), r.height(), mPlaceholderColor);
    }

    private int getIconSize(float scale){
        int size = (int) (scale * Math.min(getWidth(), getHeight()) + 0.5f);
        if(mFilmMode){
            return size/2;
        }
        else{
            return size;
        }
    }

    // Draw the video play icon (in the place where the spinner was)
    private void drawVideoPlayIcon(GLCanvas canvas, int side) {
        int s = side / ICON_RATIO;
        // Draw the video play icon at the center
        mVideoPlayIcon.draw(canvas, -s / 2, -s / 2, s, s);
    }
    
    // Draw the "no thumbnail" message
    private void drawLoadingFailMessage(GLCanvas canvas) {
        StringTexture m = mNoThumbnailText;
        m.draw(canvas, -m.getWidth() / 2, -m.getHeight() / 2);
    }

    private static int getRotated(int degree, int original, int theother) {
        return (degree % 180 == 0) ? original : theother;
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Gestures Handling
    ////////////////////////////////////////////////////////////////////////////

    @Override
    protected boolean onTouch(MotionEvent event) {
        mGestureRecognizer.onTouchEvent(event);
        return true;
    }
    ///M: added for Smartbook zoom in/out. @{
    
    private float mScreenCenterX;
    private float mScreenCenterY;
    @Override
    protected void onGenericMotionEvent(MotionEvent event) {
        
      if ((event.getSource() & InputDevice.SOURCE_CLASS_POINTER) != 0) {
      switch (event.getAction()) {
          case MotionEvent.ACTION_SCROLL :
              float vscroll;
              float hscroll;
              vscroll = -event.getAxisValue(MotionEvent.AXIS_VSCROLL);
              hscroll = event.getAxisValue(MotionEvent.AXIS_HSCROLL);
              if (hscroll != 0 || vscroll != 0) {
                  /**
                   * M: SmartBook special key for zoom in/out.
                   * Ctrl + mouse scrollUp    --> zoom in;
                   * Ctrl + mouse scrollDown  --> zoom out. 
                   */
                  if ((event.getMetaState() & KeyEvent.META_CTRL_ON) != 0) {
                      if (hscroll > 0 || vscroll > 0) {
                          zoomOutView();
                      } else {
                          zoomInView();
                      }
                  }
              }
              break;

          default :
              break;
          }
      }
        
    }
    
    @Override
    protected void onKeyEvent(KeyEvent event) {
        /**
         * M: SmartBook special key for zoom in/out. 
         * Ctrl + "+" --> zoom in;
         * Ctrl + "-" --> zoom out;
         * Ctrl + "=" --> zoom in.
         */
        if ((event.isCtrlPressed() && event.getKeyCode() == KeyEvent.KEYCODE_PLUS)
                || (event.isCtrlPressed() && event.getKeyCode() == KeyEvent.KEYCODE_EQUALS)) {
            zoomInView();
        } else if (event.isCtrlPressed() && event.getKeyCode() == KeyEvent.KEYCODE_MINUS) {
            zoomOutView();
        }
    }
    
    protected void zoomInView() {
        if (mPictures.get(0).isCamera()) return;
        float scale = mPositionController.getImageScale();
        // onDoubleTap happened on the second ACTION_DOWN.
        // We need to ignore the next UP event.
        if (scale <= .75f || mPositionController.isAtMinimalScale()) {
            mPositionController.zoomIn(mScreenCenterX, mScreenCenterY, Math.max(1.5f, Math.min(scale * 1.5f, 4.0f)));
       }
    }
    
    protected void zoomOutView() {
        if (mPictures.get(0).isCamera()) return;
        float scale = mPositionController.getImageScale();
        // onDoubleTap happened on the second ACTION_DOWN.
        // We need to ignore the next UP event.
        mPositionController.resetToFullView();
    }
    
    
    private class MyGestureListener implements GestureRecognizer.Listener {
        private boolean mIgnoreUpEvent = false;
        // If we can change mode for this scale gesture.
        private boolean mCanChangeMode;
        // If we have changed the film mode in this scaling gesture.
        private boolean mModeChanged;
        // If this scaling gesture should be ignored.
        private boolean mIgnoreScalingGesture;
        // whether the down action happened while the view is scrolling.
        private boolean mDownInScrolling;
        // If we should ignore all gestures other than onSingleTapUp.
        private boolean mIgnoreSwipingGesture;
        // If a scrolling has happened after a down gesture.
        private boolean mScrolledAfterDown;
        // If the first scrolling move is in X direction. In the film mode, X
        // direction scrolling is normal scrolling. but Y direction scrolling is
        // a delete gesture.
        private boolean mFirstScrollX;
        // The accumulated Y delta that has been sent to mPositionController.
        private int mDeltaY;
        // The accumulated scaling change from a scaling gesture.
        private float mAccScale;
        // If an onFling happened after the last onDown
        private boolean mHadFling;
        /// M: ignore doubleTap, onScale, onScaleBegin, onScaleEnd 
        /// when current item is mpo_mav file
        private boolean mIgnoreScaleGesture;

        @Override
        public boolean onSingleTapConfirmed(float x, float y) {
            // On crespo running Android 2.3.6 (gingerbread), a pinch out gesture results in the
            // following call sequence: onDown(), onUp() and then onSingleTapUp(). The correct
            // sequence for a single-tap-up gesture should be: onDown(), onSingleTapUp() and onUp().
            // The call sequence for a pinch out gesture in JB is: onDown(), then onUp() and there's
            // no onSingleTapUp(). Base on these observations, the following condition is added to
            // filter out the false alarm where onSingleTapUp() is called within a pinch out
            // gesture. The framework fix went into ICS. Refer to b/4588114.
            if (Build.VERSION.SDK_INT < ApiHelper.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if ((mHolding & HOLD_TOUCH_DOWN) == 0) {
                    return true;
                }
            }

            // We do this in addition to onUp() because we want the snapback of
            // setFilmMode to happen.
            mHolding &= ~HOLD_TOUCH_DOWN;

            if (mFilmMode && !mDownInScrolling) {
                switchToHitPicture((int) (x + 0.5f), (int) (y + 0.5f));

                // If this is a lock screen photo, let the listener handle the
                // event. Tapping on lock screen photo should take the user
                // directly to the lock screen.
                MediaItem item = mModel.getMediaItem(0);
                int supported = 0;
                if (item != null) supported = item.getSupportedOperations();
                if ((supported & MediaItem.SUPPORT_ACTION) == 0) {
                    setFilmMode(false);
                    /// M: mask this line to fix bug:
                    /// single tap to exit film mode, then photo will stay where it is and doesn't slide to next one
                    /// when scroll the photo.
                    // mIgnoreUpEvent = true;
                    return true;
                }
            }

            if (mListener != null) {
                // Do the inverse transform of the touch coordinates.
                if (getGLRoot() == null) {
                    return true;
                }
                Matrix m = getGLRoot().getCompensationMatrix();
                Matrix inv = new Matrix();
                m.invert(inv);
                float[] pts = new float[] {x, y};
                inv.mapPoints(pts);
                mListener.onSingleTapConfirmed((int) (pts[0] + 0.5f), (int) (pts[1] + 0.5f));
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(float x, float y) {
            if (mListener != null) {
                if (getGLRoot() == null) {
                    return true;
                }
                Matrix m = getGLRoot().getCompensationMatrix();
                Matrix inv = new Matrix();
                m.invert(inv);
                float[] pts = new float[] {x, y};
                inv.mapPoints(pts);
                mListener.onSingleTapUp((int) (pts[0] + 0.5f), (int) (pts[1] + 0.5f));
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(float x, float y) {
            /// M: ignore double tap gesture for mav
            if (mIgnoreScaleGesture) return true;
            if (mIgnoreSwipingGesture) return true;
            if (mPictures.get(0).isCamera()) return false;
            // M: 6592 panorama add @{
            if (MediatekFeature.isPanorama3DSupported() && mTileView.getIsPanorama() && !mFilmMode
                    && mPanoramaMode == PANORAMA_SHOW_MODE_3D) {
                return true;
            }
            // @}
            PositionController controller = mPositionController;
            float scale = controller.getImageScale();
            // onDoubleTap happened on the second ACTION_DOWN.
            // We need to ignore the next UP event.
            mIgnoreUpEvent = true;
            if (scale <= 0.75f || controller.isAtMinimalScale()) {
                 controller.zoomIn(x, y, Math.max(1.5f, Math.min(scale * 1.5f, 4.0f)));
            } else {
                controller.resetToFullView();
            }
            return true;
        }
        
        // Add for Camera Feature Object Tracking
        @Override
        public void onLongPress(float x, float y){
            Log.d(TAG, "LongPressed");
            if (mListener != null) {
                // Do the inverse transform of the touch coordinates.
                if (getGLRoot() == null) {
                    return;
                }
                Matrix m = getGLRoot().getCompensationMatrix();
                Matrix inv = new Matrix();
                m.invert(inv);
                float[] pts = new float[] {x, y};
                inv.mapPoints(pts);
                mListener.onLongPress((int) (pts[0] + 0.5f), (int) (pts[1] + 0.5f));
            }
        }

        @Override
        public boolean onScroll(float dx, float dy, float totalX, float totalY) {
            if (mIgnoreSwipingGesture) return true;
            if (!mScrolledAfterDown) {
                mScrolledAfterDown = true;
                mFirstScrollX = (Math.abs(dx) > Math.abs(dy));
            }
            /// M: added for open image from local
            mModel.showWaitingIfNeed();
            
            int dxi = (int) (-dx + 0.5f);
            int dyi = (int) (-dy + 0.5f);
            if (mFilmMode) {
                if (mFirstScrollX) {
                    mPositionController.scrollFilmX(dxi);
                } else {
                    if (mTouchBoxIndex == Integer.MAX_VALUE) return true;
                    int newDeltaY = calculateDeltaY(totalY);
                    int d = newDeltaY - mDeltaY;
                    if (d != 0) {
                        mPositionController.scrollFilmY(mTouchBoxIndex, d);
                        mDeltaY = newDeltaY;
                    }
                }
            } else {
                mPositionController.scrollPage(dxi, dyi);
                // M: if user is scrolling or flinging, invisible seekbar @{
                if (mInDown) {
                    mListener.onSeekBarVisible(false);
                }
                // @}
            }
            return true;
        }

        private int calculateDeltaY(float delta) {
            if (mTouchBoxDeletable) return (int) (delta + 0.5f);

            // don't let items that can't be deleted be dragged more than
            // maxScrollDistance, and make it harder and harder to drag.
            int size = getHeight();
            float maxScrollDistance = 0.15f * size;
            if (Math.abs(delta) >= size) {
                delta = delta > 0 ? maxScrollDistance : -maxScrollDistance;
            } else {
                delta = maxScrollDistance *
                        FloatMath.sin((delta / size) * (float) (Math.PI / 2));
            }
            return (int) (delta + 0.5f);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (mIgnoreSwipingGesture) return true;
            if (mModeChanged) return true;
            if (swipeImages(velocityX, velocityY)) {
                mIgnoreUpEvent = true;
            } else {
                flingImages(velocityX, velocityY, Math.abs(e2.getY() - e1.getY()));
            }
            // M: if user is scrolling or flinging, invisible seekbar @{
            if (mInDown) {
                mListener.onSeekBarVisible(false);
            }
            // @}
            mHadFling = true;
            return true;
        }

        private boolean flingImages(float velocityX, float velocityY, float dY) {
            int vx = (int) (velocityX + 0.5f);
            int vy = (int) (velocityY + 0.5f);
            if (!mFilmMode) {
                return mPositionController.flingPage(vx, vy);
            }
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                return mPositionController.flingFilmX(vx);
            }
            // If we scrolled in Y direction fast enough, treat it as a delete
            // gesture.
            if (!mFilmMode || mTouchBoxIndex == Integer.MAX_VALUE
                    || !mTouchBoxDeletable) {
                return false;
            }
            int maxVelocity = GalleryUtils.dpToPixel(MAX_DISMISS_VELOCITY);
            int escapeVelocity = GalleryUtils.dpToPixel(SWIPE_ESCAPE_VELOCITY);
            int escapeDistance = GalleryUtils.dpToPixel(SWIPE_ESCAPE_DISTANCE);
            int centerY = mPositionController.getPosition(mTouchBoxIndex)
                    .centerY();
            boolean fastEnough = (Math.abs(vy) > escapeVelocity)
                    && (Math.abs(vy) > Math.abs(vx))
                    && ((vy > 0) == (centerY > getHeight() / 2))
                    && dY >= escapeDistance;
            if (fastEnough) {
                vy = Math.min(vy, maxVelocity);
                int duration = mPositionController.flingFilmY(mTouchBoxIndex, vy);
                if (duration >= 0) {
                    mPositionController.setPopFromTop(vy < 0);
                    deleteAfterAnimation(duration);
                    // We reset mTouchBoxIndex, so up() won't check if Y
                    // scrolled far enough to be a delete gesture.
                    mTouchBoxIndex = Integer.MAX_VALUE;
                    return true;
                }
            }
            return false;
        }

        private void deleteAfterAnimation(int duration) {
            MediaItem item = mModel.getMediaItem(mTouchBoxIndex);
            if (item == null) return;
            mListener.onCommitDeleteImage();
            mUndoIndexHint = mModel.getCurrentIndex() + mTouchBoxIndex;
            mHolding |= HOLD_DELETE;
            Message m = mHandler.obtainMessage(MSG_DELETE_ANIMATION_DONE);
            m.obj = item.getPath();
            m.arg1 = mTouchBoxIndex;
            mHandler.sendMessageDelayed(m, duration);
        }

        @Override
        public boolean onScaleBegin(float focusX, float focusY) {
            /// M: ignore double onScaleBegin for mav
            if (mIgnoreScaleGesture) return true;
            if (mIgnoreSwipingGesture) return true;
            // M: 6592 panorama add @{
            if (MediatekFeature.isPanorama3DSupported() && mTileView.getIsPanorama() && !mFilmMode
                    && mPanoramaMode == PANORAMA_SHOW_MODE_3D) {
                return true;
            }
            // @}
            // We ignore the scaling gesture if it is a camera preview.
            mIgnoreScalingGesture = mPictures.get(0).isCamera();
            if (mIgnoreScalingGesture) {
                return true;
            }
            mPositionController.beginScale(focusX, focusY);
            // We can change mode if we are in film mode, or we are in page
            // mode and at minimal scale.
            mCanChangeMode = mFilmMode
                    || mPositionController.isAtMinimalScale();
            mAccScale = 1f;
            return true;
        }

        @Override
        public boolean onScale(float focusX, float focusY, float scale) {
            /// M: ignore onScale gesture for mav
            if (mIgnoreScaleGesture) return true;
            if (mIgnoreSwipingGesture) return true;
            if (mIgnoreScalingGesture) return true;
            if (mModeChanged) return true;
            // M: 6592 panorama add @{
            if (MediatekFeature.isPanorama3DSupported() && mTileView.getIsPanorama() && !mFilmMode
                    && mPanoramaMode == PANORAMA_SHOW_MODE_3D) {
                return true;
            }
            // @}
            if (Float.isNaN(scale) || Float.isInfinite(scale)) return false;
            int outOfRange = mPositionController.scaleBy(scale, focusX, focusY);

            // We wait for a large enough scale change before changing mode.
            // Otherwise we may mistakenly treat a zoom-in gesture as zoom-out
            // or vice versa.
            mAccScale *= scale;
            boolean largeEnough = (mAccScale < 0.97f || mAccScale > 1.03f);

            // If mode changes, we treat this scaling gesture has ended.
            if (mCanChangeMode && largeEnough) {
                if ((outOfRange < 0 && !mFilmMode) ||
                        (outOfRange > 0 && mFilmMode)) {
                    stopExtraScalingIfNeeded();

                    // Removing the touch down flag allows snapback to happen
                    // for film mode change.
                    mHolding &= ~HOLD_TOUCH_DOWN;
                    if (mFilmMode) {
                        UsageStatistics.setPendingTransitionCause(
                                UsageStatistics.TRANSITION_PINCH_OUT);
                    } else {
                        UsageStatistics.setPendingTransitionCause(
                                UsageStatistics.TRANSITION_PINCH_IN);
                    }
                    setFilmMode(!mFilmMode);

                    // We need to call onScaleEnd() before setting mModeChanged
                    // to true.
                    onScaleEnd();
                    mModeChanged = true;
                    return true;
                }
           }

            if (outOfRange != 0) {
                startExtraScalingIfNeeded();
            } else {
                stopExtraScalingIfNeeded();
            }
            return true;
        }

        @Override
        public void onScaleEnd() {
            /// M: ignore onScaleEnd gesture for mav
            if (mIgnoreScaleGesture) return;
            if (mIgnoreSwipingGesture) return;
            if (mIgnoreScalingGesture) return;
            if (mModeChanged) return;
            // M: 6592 panorama add @{
            if (MediatekFeature.isPanorama3DSupported() && mTileView.getIsPanorama() && !mFilmMode
                    && mPanoramaMode == PANORAMA_SHOW_MODE_3D) {
                return;
            }
            // @}
            mPositionController.endScale();
        }

        private void startExtraScalingIfNeeded() {
            if (!mCancelExtraScalingPending) {
                mHandler.sendEmptyMessageDelayed(
                        MSG_CANCEL_EXTRA_SCALING, 700);
                mPositionController.setExtraScalingRange(true);
                mCancelExtraScalingPending = true;
            }
        }

        private void stopExtraScalingIfNeeded() {
            if (mCancelExtraScalingPending) {
                mHandler.removeMessages(MSG_CANCEL_EXTRA_SCALING);
                mPositionController.setExtraScalingRange(false);
                mCancelExtraScalingPending = false;
            }
        }

        @Override
        public void onDown(float x, float y) {
            // M: using to check if user is scrolling or flinging @{
            mInDown = true;
            // @}
            checkHideUndoBar(UNDO_BAR_TOUCHED);

            mDeltaY = 0;
            mModeChanged = false;

            if (mIgnoreSwipingGesture) return;

            mHolding |= HOLD_TOUCH_DOWN;

            if (mFilmMode && mPositionController.isScrolling()) {
                mDownInScrolling = true;
                mPositionController.stopScrolling();
            } else {
                mDownInScrolling = false;
            }
            mHadFling = false;
            mScrolledAfterDown = false;
            if (mFilmMode) {
                int xi = (int) (x + 0.5f);
                int yi = (int) (y + 0.5f);
                // We only care about being within the x bounds, necessary for
                // handling very wide images which are otherwise very hard to fling
                mTouchBoxIndex = mPositionController.hitTest(xi, getHeight() / 2);

                if (mTouchBoxIndex < mPrevBound || mTouchBoxIndex > mNextBound) {
                    mTouchBoxIndex = Integer.MAX_VALUE;
                } else {
                    mTouchBoxDeletable =
                            mPictures.get(mTouchBoxIndex).isDeletable();
                }
            } else {
                mTouchBoxIndex = Integer.MAX_VALUE;
            }
        }

        @Override
        public void onUp() {
            // M: scrolling or flinging end, set seek bar visible @{
            mInDown = false;
            mListener.onSeekBarVisible(true);
            // @}
            if (mIgnoreSwipingGesture) return;

            mHolding &= ~HOLD_TOUCH_DOWN;
            mEdgeView.onRelease();

            // If we scrolled in Y direction far enough, treat it as a delete
            // gesture.
            if (mFilmMode && mScrolledAfterDown && !mFirstScrollX
                    && mTouchBoxIndex != Integer.MAX_VALUE) {
                Rect r = mPositionController.getPosition(mTouchBoxIndex);
                int h = getHeight();
                if (Math.abs(r.centerY() - h * 0.5f) > 0.4f * h) {
                    int duration = mPositionController
                            .flingFilmY(mTouchBoxIndex, 0);
                    if (duration >= 0) {
                        mPositionController.setPopFromTop(r.centerY() < h * 0.5f);
                        deleteAfterAnimation(duration);
                    }
                }
            }

            if (mIgnoreUpEvent) {
                mIgnoreUpEvent = false;
                return;
            }

            if (!(mFilmMode && !mHadFling && mFirstScrollX
                    && snapToNeighborImage())) {
                snapback();
            }
        }

        public void setSwipingEnabled(boolean enabled) {
            mIgnoreSwipingGesture = !enabled;
        }

        public void setScaleGestureEnabled(boolean enabled) {
            mIgnoreScaleGesture = !enabled;
        }
    }

    public void setSwipingEnabled(boolean enabled) {
        mGestureListener.setSwipingEnabled(enabled);
    }
    
    public void setScaleGestureEnabled(boolean enabled) {
        mGestureListener.setScaleGestureEnabled(enabled);
    }

    public GestureRecognizer.Listener setGestureListener(GestureRecognizer.Listener listener) {
        return mGestureRecognizer.setGestureListener(listener);
    }

    private void updateActionBar() {
        boolean isCamera = mPictures.get(0).isCamera();
        if (isCamera && !mFilmMode) {
            // Move into camera in page mode, lock
            mListener.onActionBarAllowed(false);
        } else {
            mListener.onActionBarAllowed(true);
            if (mFilmMode) mListener.onActionBarWanted();
        }
    }

    public void setFilmMode(boolean enabled) {
        if (mFilmMode == enabled) return;
        mFilmMode = enabled;
        mPositionController.setFilmMode(mFilmMode);
        mModel.setNeedFullImage(!enabled);
        mModel.setFocusHintDirection(
                mFilmMode ? Model.FOCUS_HINT_PREVIOUS : Model.FOCUS_HINT_NEXT);
        updateActionBar();
        // M: when film mode, in order to show mav overlay normally,
        // set mIsMavLoadingFinished == false
        if (enabled) {
            mIsMavLoadingFinished = false;
        }
        mListener.onFilmModeChanged(enabled);
        /// M: added for mav feature, show or hide MavSeekBar
        mListener.onMavSeekBarAllowed(!enabled);
    }

    public boolean getFilmMode() {
        return mFilmMode;
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Framework events
    ////////////////////////////////////////////////////////////////////////////

    public void pause() {
        /// M: Change Gallery animation of unlocking screen:
        /// just reset picture to center of view other than snapping back. @{
        if (!mFilmMode) {
            mPositionController.resetToFullView();
        }
        /// @}

        mPositionController.skipAnimation();
        mTileView.freeTextures();
        for (int i = -SCREEN_NAIL_MAX; i <= SCREEN_NAIL_MAX; i++) {
            mPictures.get(i).setScreenNail(null);
            // M: 6592 panorama add @{
            if (MediatekFeature.isPanorama3DSupported() &&
                    mPictures.get(i) instanceof FullPicture) {
                ((FullPicture) mPictures.get(i)).setPanoramaScreenNail(null);
            }
            // @}
        }
        hideUndoBar();
        // M: 6592 panorama add @{
        if (MediatekFeature.isPanorama3DSupported()) {
            stopPanoramaAutoPlayBack();
        }
        // @}
    }

    public void resume() {
        mTileView.prepareTextures();
        mPositionController.skipToFinalPosition();
    }

    // move to the camera preview and show controls after resume
    public void resetToFirstPicture() {
        mModel.moveTo(0);
        setFilmMode(false);
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Undo Bar
    ////////////////////////////////////////////////////////////////////////////

    private int mUndoBarState;
    private static final int UNDO_BAR_SHOW = 1;
    private static final int UNDO_BAR_TIMEOUT = 2;
    private static final int UNDO_BAR_TOUCHED = 4;
    private static final int UNDO_BAR_FULL_CAMERA = 8;
    private static final int UNDO_BAR_DELETE_LAST = 16;

    // "deleteLast" means if the deletion is on the last remaining picture in
    // the album.
    private void showUndoBar(boolean deleteLast) {
        mHandler.removeMessages(MSG_UNDO_BAR_TIMEOUT);
        mUndoBarState = UNDO_BAR_SHOW;
        if(deleteLast) mUndoBarState |= UNDO_BAR_DELETE_LAST;
        mUndoBar.animateVisibility(GLView.VISIBLE);
        mHandler.sendEmptyMessageDelayed(MSG_UNDO_BAR_TIMEOUT, 3000);
        if (mListener != null) mListener.onUndoBarVisibilityChanged(true);
    }

    private void hideUndoBar() {
        mHandler.removeMessages(MSG_UNDO_BAR_TIMEOUT);
        mListener.onCommitDeleteImage();
        mUndoBar.animateVisibility(GLView.INVISIBLE);
        mUndoBarState = 0;
        mUndoIndexHint = Integer.MAX_VALUE;
        mListener.onUndoBarVisibilityChanged(false);
    }

    // Check if the one of the conditions for hiding the undo bar has been
    // met. The conditions are:
    //
    // 1. It has been three seconds since last showing, and (a) the user has
    // touched, or (b) the deleted picture is the last remaining picture in the
    // album.
    //
    // 2. The camera is shown in full screen.
    private void checkHideUndoBar(int addition) {
        mUndoBarState |= addition;
        if ((mUndoBarState & UNDO_BAR_SHOW) == 0) return;
        boolean timeout = (mUndoBarState & UNDO_BAR_TIMEOUT) != 0;
        boolean touched = (mUndoBarState & UNDO_BAR_TOUCHED) != 0;
        boolean fullCamera = (mUndoBarState & UNDO_BAR_FULL_CAMERA) != 0;
        boolean deleteLast = (mUndoBarState & UNDO_BAR_DELETE_LAST) != 0;
        if ((timeout && deleteLast) || fullCamera || touched) {
            hideUndoBar();
        }
    }

    public boolean canUndo() {
        return (mUndoBarState & UNDO_BAR_SHOW) != 0;
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Rendering
    ////////////////////////////////////////////////////////////////////////////

    @Override
    protected void render(GLCanvas canvas) {
        if (mFirst) {
            // Make sure the fields are properly initialized before checking
            // whether isCamera()
            mPictures.get(0).reload();
        }
        // Check if the camera preview occupies the full screen.
        boolean center = mPositionController.isCenter();
        boolean minimalScale = mPositionController.isAtMinimalScale();
        boolean full = !mFilmMode && mPictures.get(0).isCamera() && center && minimalScale;
        if (mFirst || full != mFullScreenCamera) {
            mFullScreenCamera = full;
            mFirst = false;
            /// M: for camera new feature, support more behavior @{
            //mListener.onFullScreenChanged(full);
            int type = 0x0000;
            if (center) {
                type |= Listener.FULLSCREEN_TYPE_CENETER;
            }
            if (minimalScale) {
                type |= Listener.FULLSCREEN_TYPE_MINIMAL_SCALE;
            }
            mListener.onFullScreenChanged(full, type);
            /// @}

            if (full) mHandler.sendEmptyMessage(MSG_UNDO_BAR_FULL_CAMERA);
        }
        
        // M: if we're requested to render full picture only
        // (most probably by Camera), we skip all other flow
        if (mRenderFullPictureOnly) {
            Rect r = mPositionController.getPosition(0);
            mPictures.get(0).draw(canvas, r);
            return;
        }

        // Determine how many photos we need to draw in addition to the center
        // one.
        int neighbors;
        if (mFullScreenCamera) {
            neighbors = 0;
        } else {
            // In page mode, we draw only one previous/next photo. But if we are
            // doing capture animation, we want to draw all photos.
            boolean inPageMode = (mPositionController.getFilmRatio() == 0f);
            boolean inCaptureAnimation =
                    ((mHolding & HOLD_CAPTURE_ANIMATION) != 0);
            if (inPageMode && !inCaptureAnimation) {
                neighbors = 1;
            } else {
                neighbors = SCREEN_NAIL_MAX;
            }
        }

        // Draw photos from back to front
        for (int i = neighbors; i >= -neighbors; i--) {
            Rect r = mPositionController.getPosition(i);
            mPictures.get(i).draw(canvas, r);
        }

        renderChild(canvas, mEdgeView);
        renderChild(canvas, mUndoBar);

        mPositionController.advanceAnimation();
        checkFocusSwitching();
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Film mode focus switching
    ////////////////////////////////////////////////////////////////////////////

    // Runs in GL thread.
    private void checkFocusSwitching() {
        if (!mFilmMode) return;
        if (mHandler.hasMessages(MSG_SWITCH_FOCUS)) return;
        if (switchPosition() != 0) {
            mHandler.sendEmptyMessage(MSG_SWITCH_FOCUS);
        }
    }

    // Runs in main thread.
    private void switchFocus() {
        if (mHolding != 0) return;
        switch (switchPosition()) {
            case -1:
                switchToPrevImage();
                break;
            case 1:
                switchToNextImage();
                break;
        }
    }

    // Returns -1 if we should switch focus to the previous picture, +1 if we
    // should switch to the next, 0 otherwise.
    private int switchPosition() {
        Rect curr = mPositionController.getPosition(0);
        int center = getWidth() / 2;

        if (curr.left > center && mPrevBound < 0) {
            Rect prev = mPositionController.getPosition(-1);
            int currDist = curr.left - center;
            int prevDist = center - prev.right;
            if (prevDist < currDist) {
                return -1;
            }
        } else if (curr.right < center && mNextBound > 0) {
            Rect next = mPositionController.getPosition(1);
            int currDist = center - curr.right;
            int nextDist = next.left - center;
            if (nextDist < currDist) {
                return 1;
            }
        }

        return 0;
    }

    // Switch to the previous or next picture if the hit position is inside
    // one of their boxes. This runs in main thread.
    private void switchToHitPicture(int x, int y) {
        MtkLog.d(TAG, "switchToHitPicture: x=" + x + ", y=" + y);
//        if (mPrevBound < 0) {
//            Rect r = mPositionController.getPosition(-1);
//            if (r.right >= x) {
//                slideToPrevPicture();
//                return;
//            }
//        }
//
//        if (mNextBound > 0) {
//            Rect r = mPositionController.getPosition(1);
//            if (r.left <= x) {
//                slideToNextPicture();
//                return;
//            }
//        }
        // M: we replace Google default logic with hitTest
        int hitIndex = mPositionController.hitTestIgnoreVertical(x, y);
        MtkLog.d(TAG, "switchToHitPicture: hit test result index=" + hitIndex);
        if (hitIndex == Integer.MAX_VALUE) {
            return;
        }
        int curIndex = mModel.getCurrentIndex();
        boolean canSwitch = hitIndex < 0 && mPrevBound < 0;
        canSwitch |= hitIndex > 0 && mNextBound > 0;
        if (canSwitch) {
            /// M: fix JE
            if (curIndex + hitIndex < 0) {
                mModel.moveTo(0);
                MtkLog.i(TAG, "updateCurrentIndex: curIndex + hitIndex < 0");
            } else {
                mModel.moveTo(curIndex + hitIndex);
                MtkLog.i(TAG, "switchToHitPicture: move to " + hitIndex);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Page mode focus switching
    //
    //  We slide image to the next one or the previous one in two cases: 1: If
    //  the user did a fling gesture with enough velocity.  2 If the user has
    //  moved the picture a lot.
    ////////////////////////////////////////////////////////////////////////////

    private boolean swipeImages(float velocityX, float velocityY) {
        if (mFilmMode) return false;

        // Avoid swiping images if we're possibly flinging to view the
        // zoomed in picture vertically.
        PositionController controller = mPositionController;
        boolean isMinimal = controller.isAtMinimalScale();
        int edges = controller.getImageAtEdges();
        if (!isMinimal && Math.abs(velocityY) > Math.abs(velocityX))
            if ((edges & PositionController.IMAGE_AT_TOP_EDGE) == 0
                    || (edges & PositionController.IMAGE_AT_BOTTOM_EDGE) == 0)
                return false;

        // If we are at the edge of the current photo and the sweeping velocity
        // exceeds the threshold, slide to the next / previous image.
        if (velocityX < -SWIPE_THRESHOLD && (isMinimal
                || (edges & PositionController.IMAGE_AT_RIGHT_EDGE) != 0)) {
            return slideToNextPicture();
        } else if (velocityX > SWIPE_THRESHOLD && (isMinimal
                || (edges & PositionController.IMAGE_AT_LEFT_EDGE) != 0)) {
            return slideToPrevPicture();
        }

        return false;
    }

    private void snapback() {
        if ((mHolding & ~HOLD_DELETE) != 0) return;
        if (mFilmMode || !snapToNeighborImage()) {
            mPositionController.snapback();
        }
    }

    private boolean snapToNeighborImage() {
        Rect r = mPositionController.getPosition(0);
        int viewW = getWidth();
        // Setting the move threshold proportional to the width of the view
        int moveThreshold = viewW / 5 ;
        int threshold = moveThreshold + gapToSide(r.width(), viewW);

        // If we have moved the picture a lot, switching.
        if (viewW - r.right > threshold) {
            return slideToNextPicture();
        } else if (r.left > threshold) {
            return slideToPrevPicture();
        }

        return false;
    }

    private boolean slideToNextPicture() {
        if (mNextBound <= 0) return false;
        switchToNextImage();
        mPositionController.startHorizontalSlide();
        return true;
    }

    private boolean slideToPrevPicture() {
        if (mPrevBound >= 0) return false;
        ///M:@{
        if(!mListener.canSlideToPrePicture()) return false;
        ///@}
        switchToPrevImage();
        mPositionController.startHorizontalSlide();
        return true;
    }

    private static int gapToSide(int imageWidth, int viewWidth) {
        return Math.max(0, (viewWidth - imageWidth) / 2);
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Focus switching
    ////////////////////////////////////////////////////////////////////////////

    public void switchToImage(int index) {
        mModel.moveTo(index);
    }

    private void switchToNextImage() {
        Log.d(TAG, "switchToNextImage");
        mModel.moveTo(mModel.getCurrentIndex() + 1);
    }

    private void switchToPrevImage() {
        Log.d(TAG, "switchToPrevImage");
        mModel.moveTo(mModel.getCurrentIndex() - 1);
    }

    private void switchToFirstImage() {
        Log.d(TAG, "switchToFirstImage");
        mModel.moveTo(0);
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Opening Animation
    ////////////////////////////////////////////////////////////////////////////

    public void setOpenAnimationRect(Rect rect) {
        mPositionController.setOpenAnimationRect(rect);
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Capture Animation
    ////////////////////////////////////////////////////////////////////////////

    public boolean switchWithCaptureAnimation(int offset) {
        GLRoot root = getGLRoot();
        if(root == null) return false;
        root.lockRenderThread();
        try {
            return switchWithCaptureAnimationLocked(offset);
        } finally {
            root.unlockRenderThread();
        }
    }

    private boolean switchWithCaptureAnimationLocked(int offset) {
        Log.d(TAG, "switchWithCaptureAnimationLocked: offset=" + offset + ", holding=" + mHolding);
        Log.d(TAG, "switchWithCaptureAnimationLocked: prevbound=" + mPrevBound + ", nextbound=" + mNextBound);
        if (mHolding != 0) return true;
        if (offset == 1) {
            if (mNextBound <= 0) return false;
            // Temporary disable action bar until the capture animation is done.
            if (!mFilmMode) mListener.onActionBarAllowed(false);
            switchToNextImage();
            mPositionController.startCaptureAnimationSlide(-1);
        } else if (offset == -1) {
            if (mPrevBound >= 0) return false;
            if (mFilmMode) setFilmMode(false);

            // If we are too far away from the first image (so that we don't
            // have all the ScreenNails in-between), we go directly without
            // animation.
            if (mModel.getCurrentIndex() > SCREEN_NAIL_MAX) {
                switchToFirstImage();
                mPositionController.skipToFinalPosition();
                return true;
            }

            switchToFirstImage();
            mPositionController.startCaptureAnimationSlide(1);
        } else {
            return false;
        }
        mHolding |= HOLD_CAPTURE_ANIMATION;
        Message m = mHandler.obtainMessage(MSG_CAPTURE_ANIMATION_DONE, offset, 0);
        mHandler.sendMessageDelayed(m, PositionController.CAPTURE_ANIMATION_TIME);
        return true;
    }

    private void captureAnimationDone(int offset) {
        mHolding &= ~HOLD_CAPTURE_ANIMATION;
        if (offset == 1 && !mFilmMode) {
            // Now the capture animation is done, enable the action bar.
            mListener.onActionBarAllowed(true);
            mListener.onActionBarWanted();
        }
        snapback();
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Card deck effect calculation
    ////////////////////////////////////////////////////////////////////////////

    // Returns the scrolling progress value for an object moving out of a
    // view. The progress value measures how much the object has moving out of
    // the view. The object currently displays in [left, right), and the view is
    // at [0, viewWidth].
    //
    // The returned value is negative when the object is moving right, and
    // positive when the object is moving left. The value goes to -1 or 1 when
    // the object just moves out of the view completely. The value is 0 if the
    // object currently fills the view.
    private static float calculateMoveOutProgress(int left, int right,
            int viewWidth) {
        // w = object width
        // viewWidth = view width
        int w = right - left;

        // If the object width is smaller than the view width,
        //      |....view....|
        //                   |<-->|      progress = -1 when left = viewWidth
        //          |<-->|               progress = 0 when left = viewWidth / 2 - w / 2
        // |<-->|                        progress = 1 when left = -w
        if (w < viewWidth) {
            int zx = viewWidth / 2 - w / 2;
            if (left > zx) {
                return -(left - zx) / (float) (viewWidth - zx);  // progress = (0, -1]
            } else {
                return (left - zx) / (float) (-w - zx);  // progress = [0, 1]
            }
        }

        // If the object width is larger than the view width,
        //             |..view..|
        //                      |<--------->| progress = -1 when left = viewWidth
        //             |<--------->|          progress = 0 between left = 0
        //          |<--------->|                          and right = viewWidth
        // |<--------->|                      progress = 1 when right = 0
        if (left > 0) {
            return -left / (float) viewWidth;
        }

        if (right < viewWidth) {
            return (viewWidth - right) / (float) viewWidth;
        }

        return 0;
    }

    // Maps a scrolling progress value to the alpha factor in the fading
    // animation.
    private float getScrollAlpha(float scrollProgress) {
        return scrollProgress < 0 ? mAlphaInterpolator.getInterpolation(
                     1 - Math.abs(scrollProgress)) : 1.0f;
    }

    // Maps a scrolling progress value to the scaling factor in the fading
    // animation.
    private float getScrollScale(float scrollProgress) {
        float interpolatedProgress = mScaleInterpolator.getInterpolation(
                Math.abs(scrollProgress));
        float scale = (1 - interpolatedProgress) +
                interpolatedProgress * TRANSITION_SCALE_FACTOR;
        return scale;
    }


    // This interpolator emulates the rate at which the perceived scale of an
    // object changes as its distance from a camera increases. When this
    // interpolator is applied to a scale animation on a view, it evokes the
    // sense that the object is shrinking due to moving away from the camera.
    private static class ZInterpolator {
        private float focalLength;

        public ZInterpolator(float foc) {
            focalLength = foc;
        }

        public float getInterpolation(float input) {
            return (1.0f - focalLength / (focalLength + input)) /
                (1.0f - focalLength / (focalLength + 1.0f));
        }
    }

    // Returns an interpolated value for the page/film transition.
    // When ratio = 0, the result is from.
    // When ratio = 1, the result is to.
    private static float interpolate(float ratio, float from, float to) {
        return from + (to - from) * ratio * ratio;
    }

    // Returns the alpha factor in film mode if a picture is not in the center.
    // The 0.03 lower bound is to make the item always visible a bit.
    private float getOffsetAlpha(float offset) {
        offset /= 0.5f;
        float alpha = (offset > 0) ? (1 - offset) : (1 + offset);
        return Utils.clamp(alpha, 0.03f, 1f);
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Simple public utilities
    ////////////////////////////////////////////////////////////////////////////

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public Rect getPhotoRect(int index) {
        return mPositionController.getPosition(index);
    }

    public PhotoFallbackEffect buildFallbackEffect(GLView root, GLCanvas canvas) {
        Rect location = new Rect();
        Utils.assertTrue(root.getBoundsOf(this, location));

        Rect fullRect = bounds();
        PhotoFallbackEffect effect = new PhotoFallbackEffect();
        for (int i = -SCREEN_NAIL_MAX; i <= SCREEN_NAIL_MAX; ++i) {
            MediaItem item = mModel.getMediaItem(i);
            if (item == null) continue;
            ScreenNail sc = mModel.getScreenNail(i);
            // M: change to BitmapTexture from TiledTexture
            if (!(sc instanceof BitmapScreenNail)
                    || ((BitmapScreenNail) sc).isShowingPlaceholder()) continue;

            // Now, sc is BitmapScreenNail and is not showing placeholder
            Rect rect = new Rect(getPhotoRect(i));
            if (!Rect.intersects(fullRect, rect)) continue;
            rect.offset(location.left, location.top);

            int width = sc.getWidth();
            int height = sc.getHeight();

            int rotation = mModel.getImageRotation(i);
            RawTexture texture;
            if ((rotation % 180) == 0) {
                texture = new RawTexture(width, height, true);
                canvas.beginRenderTarget(texture);
                canvas.translate(width / 2f, height / 2f);
            } else {
                texture = new RawTexture(height, width, true);
                canvas.beginRenderTarget(texture);
                canvas.translate(height / 2f, width / 2f);
            }

            canvas.rotate(rotation, 0, 0, 1);
            canvas.translate(-width / 2f, -height / 2f);
            sc.draw(canvas, 0, 0, width, height);
            canvas.endRenderTarget();
            effect.addEntry(item.getPath(), rect, texture);
        }
        return effect;
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Mediatek added features
    ////////////////////////////////////////////////////////////////////////////

    //return true if the picture is set to original zoomed state
    //return false if reset failed, possibly because there is an anmation
    //ongoing
    public boolean onResetZoomedState() {
        PositionController controller = mPositionController;
        float scale = controller.getImageScale();
        controller.resetToFullView();

        return true;
    }

    // M: added for MAV
    private BasicTexture mMavOverlay;
    private FadeOutTexture fadeOutMavOverlay;
    private void drawMavOverlay(GLCanvas canvas, int side) {
        if(mIsMavLoadingFinished) {
            int s = side / ICON_RATIO;
            if(fadeOutMavOverlay == null) {
                fadeOutMavOverlay = new FadeOutTexture(mMavOverlay);
            }
            fadeOutMavOverlay.draw(canvas, -s / 2, -s / 2, s, s);
            if(fadeOutMavOverlay.isAnimating()) {
                invalidate();
            }
        } else {
            int s = side / ICON_RATIO;
            // M: draw MAV overlay at the center of the thumbnail
            mMavOverlay.draw(canvas, -s / 2, -s / 2, s, s);
            fadeOutMavOverlay = null;
        }
    }
    
    public void setMavLoadingFinished(boolean isFinished) {
        mIsMavLoadingFinished = isFinished;
        mListener.onMavSeekBarAllowed(true);
        invalidate();
    }
    
    // M: when leaving camera preview, we should unlock the orientation,
    // no matter how we leave camera preview
    // (either by tapping the camera thumbnail or by sliding the page)
    public void leaveCameraPreview() {
        if (!mFilmMode) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    GLRoot root = getGLRoot();
                    if (root != null) {
                        root.setLightsOutMode(true);
                    }
                    mListener.unlockOrientation();
                }
            });
        }
        mListener.onFullScreenChanged(false, Listener.FULLSCREEN_TYPE_MINIMAL_SCALE);
    }
    // M: when entering camera preview, make sure "lights are out"
    public void enterCameraPreview() {
        Log.d(TAG, "enterCameraPreview: lights out!");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // M: onDetachFromRoot is called from main thread,
                // so we check this in main thread should be OK as well.
                GLRoot root = getGLRoot();
                if (root != null) {
                    root.setLightsOutMode(false);
                }
                mListener.lockOrientation();
                /// M: fix seldom JE caused by nullPointer. @{
                Menu menu = ((AbstractGalleryActivity)mContext).getGalleryActionBar().getMenu();
                if (menu != null) {
                    menu.close();
                }
                /// @}
            }
        });
    }
    
    private boolean mRenderFullPictureOnly = false;
    // M: added to control render full picture only for Camera
    public void renderFullPictureOnly(boolean fullPictureOnly) {
        mRenderFullPictureOnly = fullPictureOnly;
    }

    //M: added to judge whether current box is camera and next box is ready.
    public boolean isFirstBoxReady() {
        return mFullScreenCamera && mPositionController.isFirstAtMinimalScale();
    }
    // M: 6592 panorama add @{
    public static final int PANORAMA_SHOW_MODE_NORMAL = 1;
    public static final int PANORAMA_SHOW_MODE_3D = 2;
    private boolean mPanoramaAutoPlaying = false;
    private boolean mPanoramaForward = true;
    private int mPanoramaFrameTimeGap;
    private float mPanoramaFrameDegreeGap;
    private int mPanoramaCurrentFrame = 0;
    private int mPanoramaFrameCount = 0;
    private PanoramaSwitchBarView mPanoramaSwitchBar;
    private int mPanoramaMode = PANORAMA_SHOW_MODE_3D;
    
    public boolean setPanoramaFrame(int frame) {
        //before set Panorama frame, we must stop panorma play back
        assert(mPanoramaAutoPlaying == false);
        assert(frame >= 0 && frame < mPanoramaFrameCount);
        mPanoramaForward = (frame > mPanoramaCurrentFrame);
        mPanoramaCurrentFrame = frame;
        mTileView.setPanoramaDegree(mPanoramaCurrentFrame * mPanoramaFrameDegreeGap);
        invalidate();
        return true;
    }

    public int getPanoramaFrameCount() {
        return mPanoramaFrameCount;
    }

    public int getPanoramaMode() {
        return mTileView.getPanoramaMode();
    }

    public void startPanoramaAutoPlayback() {
        if (mTileView.getIsColorPanorama()) return;
        if (mPanoramaAutoPlaying) return;
        mHandler.sendMessage(mHandler.obtainMessage(MSG_PANORAMA_AUTO_PLAY));
        mPanoramaAutoPlaying = true;
        int width;
        int height;
        MediaItem item = mModel.getMediaItem(0);
        if(item.getRotation() == 90 || item.getRotation() == 270) {
            height = item.getWidth();
            width = item.getHeight();
        } else {
            height = item.getHeight();
            width = item.getWidth();
        }
        width = PanoramaHelper.getProperRatioBitmapWidth(width, height);
        PanoramaConfig config = new PanoramaConfig(width, height,
                PanoramaHelper.getPanoramaScreenNailWidth(), 
                PanoramaHelper.getPanoramaScreenNailHeight());
        mPanoramaFrameCount = config.mFrameTotalCount;
        mPanoramaFrameTimeGap = config.mFrameTimeGap;
        mPanoramaFrameDegreeGap = config.mFrameDegreeGap;
    }
    
    public void stopPanoramaAutoPlayBack() {
        mHandler.removeMessages(MSG_PANORAMA_AUTO_PLAY);
        mPanoramaAutoPlaying = false;
    }
    
    private void resetPanoramaMode() {
        mPanoramaMode = PANORAMA_SHOW_MODE_3D;
        mPanoramaSwitchBar.setFocusButton(PanoramaSwitchBarView.BUTTON_3D, false);
        stopPanoramaAutoPlayBack();
        mTileView.resetPanoramaMode();
    }
    
    private void autoPanoramaPlayback() {
        if (mPanoramaForward) {
            mPanoramaCurrentFrame++;
            if (mPanoramaCurrentFrame >= mPanoramaFrameCount) {
                mPanoramaCurrentFrame -= 2;
                mPanoramaForward = false;
            }
        } else {
            mPanoramaCurrentFrame--;
            if (mPanoramaCurrentFrame < 0) {
                mPanoramaCurrentFrame += 2;
                mPanoramaForward = true;
            }
        }
        mTileView.setPanoramaDegree(mPanoramaCurrentFrame * mPanoramaFrameDegreeGap);
        invalidate();
        mListener.onPanoramaFrameUpdate(mPanoramaCurrentFrame);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_PANORAMA_AUTO_PLAY), mPanoramaFrameTimeGap);
    }

    private void drawPanoramasSwitchBar(GLCanvas canvas, int x, int y) {
        if(mPanoramaSwitchBar != null && mPanoramaSwitchBar.getVisibility() == GLView.VISIBLE)
            mPanoramaSwitchBar.draw(canvas, x, y);
    }
    
    private int getNextPanoramaMode() {
        if (mPanoramaMode == PANORAMA_SHOW_MODE_3D) {
            return PANORAMA_SHOW_MODE_NORMAL;
        } else {
            return mPanoramaMode + 1;
        }
    }
    
    private void switchToPanoramaMode(int mode) {
        if (mode == mPanoramaMode) {
            return;
        }
        Log.i(TAG, "<switchToPanoramaMode> mode = " + mode);
        mPanoramaMode = mode;
        mTileView.switchToPanoramaMode(mode);
        switch (mode) {
        case PANORAMA_SHOW_MODE_NORMAL:
            mPanoramaSwitchBar.setFocusButton(PanoramaSwitchBarView.BUTTON_NORMAL, false);
            break;
        case PANORAMA_SHOW_MODE_3D:
            mPanoramaSwitchBar.setFocusButton(PanoramaSwitchBarView.BUTTON_3D, false);
            break;
        default:
            break;
        }
        setPictureSize(0);
        if (mTileView.getPanoramaMode() == PANORAMA_SHOW_MODE_3D) {
            startPanoramaAutoPlayback();
        } else {
            stopPanoramaAutoPlayBack();
            mListener.onPanoramaFrameUpdate(PhotoView.INAVALID_PANORAMA_INDEX);
        }
        invalidate();
    }
    // @}
}

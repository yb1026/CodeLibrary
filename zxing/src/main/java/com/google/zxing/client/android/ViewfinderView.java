/*
 * Copyright (C) 2008 ZXing authors
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

package com.google.zxing.client.android;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 */
public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192,
            128, 64};
    private static final long ANIMATION_DELAY = 80L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;

    private final Paint paint;
    private Paint animaPaint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int frameColor;
    private final int laserColor;
    private final int resultPointColor;
    private int scannerAlpha;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;
    private float currentY;
    private ValueAnimator anim;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every
        // time in onDraw().
        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(resources.getIdentifier("viewfinder_mask", "color", getContext().getPackageName()));
        resultColor = resources.getColor(resources.getIdentifier("result_view", "color", getContext().getPackageName()));
        frameColor = resources.getColor(resources.getIdentifier("viewfinder_frame", "color", getContext().getPackageName()));
        laserColor = resources.getColor(resources.getIdentifier("viewfinder_laser", "color", getContext().getPackageName()));
        resultPointColor = resources.getColor(resources.getIdentifier("possible_result_points", "color", getContext().getPackageName()));
        scannerAlpha = 0;
        possibleResultPoints = new ArrayList<ResultPoint>(5);
        lastPossibleResultPoints = null;
        currentY = 0;
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
                paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);


        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                getResources().getIdentifier("barcode_focus_img", "mipmap", getContext().getPackageName())
        );
        // canvas.drawBitmap(bitmap, frame.left, frame.top, paint);
        RectF dst = new RectF();
        dst.bottom = frame.bottom;
        dst.left = frame.left;
        dst.top = frame.top;
        dst.right = frame.right;
        canvas.drawBitmap(bitmap, null, dst, paint);


        if (animaPaint == null) {
            animaPaint = new Paint();
            LinearGradient lg = new LinearGradient(80, 0, 100, 100, Color.WHITE, Color.GREEN, Shader.TileMode.REPEAT);
            animaPaint.setShader(lg);
            animaPaint.setColor(Color.GREEN);
            animaPaint.setAlpha(80);
        }

        if (currentY == 0) {
            currentY = frame.top;
        }

        canvas.drawRect(frame.left + 100, currentY, frame.right - 100, currentY + 8, animaPaint);


        startAnimator(frame);

    }

    private void initAnim(Rect frame) {
        if (anim != null) {
            return;
        }
        anim = ValueAnimator.ofInt(frame.top, frame.bottom - 10);
        anim.setDuration(3000);
        anim.setRepeatCount(Animation.RESTART);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                currentY = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
            }

            public void onAnimationCancel(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    private void startAnimator(Rect frame) {
        if (anim == null) {
            initAnim(frame);
        }

        if (!anim.isStarted()) {
            anim.start();
        }
    }

    public void endAnimator() {
        if (anim != null) {
            anim.end();
        }
    }


    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live
     * scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (point) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }


}

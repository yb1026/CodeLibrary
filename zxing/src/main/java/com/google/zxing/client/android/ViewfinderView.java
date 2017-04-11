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
    private static final long ANIMATION_DELAY = 300L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;

    private final Paint paint;
    private Paint animaPaint;
    private final int maskColor;

    //扫描动画隐藏标记 同为false才有动画
    private boolean hideAnima = false;
    private boolean endAnima = false;


    //强行隐藏
    public void setHideAnima(boolean hide){
        hideAnima = hide;
    }


    private List<ResultPoint> possibleResultPoints;
    private float currentY;
    private ValueAnimator anim;

    private Rect frame;
    private Context context;
    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // Initialize these once for performance rather than calling them every
        // time in onDraw().
        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(resources.getIdentifier("viewfinder_mask", "color", getContext().getPackageName()));

        possibleResultPoints = new ArrayList<ResultPoint>(5);
        currentY = 0;


    }

    @Override
    public void onDraw(Canvas canvas) {

        if (frame == null) {
            return;
        }


        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(maskColor);

        //四周区域颜色
        canvas.drawRect(0, 0, width, frame.top, paint);//上部分
        canvas.drawRect(0, frame.top, frame.left, frame.bottom, paint);//左部
        canvas.drawRect(frame.right, frame.top, width, frame.bottom, paint);//右部
        canvas.drawRect(0, frame.bottom , width, height, paint);//底部


//        //绿色聚焦框
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                getResources().getIdentifier("barcode_focus_img",
                   "mipmap", getContext().getPackageName()));
//        Bitmap bitmap = FileUtil.getResourceBitmap(context,
//                R.mipmap.barcode_focus_img);
        // canvas.drawBitmap(bitmap, frame.left, frame.top, paint);
        RectF dst = new RectF();
        dst.bottom = frame.bottom;
        dst.left = frame.left;
        dst.top = frame.top;
        dst.right = frame.right;
        //if (bitmap !=null) {
        canvas.drawBitmap(bitmap, null, dst, paint);
//        } else {
//            return;
//        }


        //那根动画
        if (!hideAnima&&!endAnima) {

            float animaWidth = frame.right-frame.left;
            float animaHeight =frame.bottom-frame.top;

            if (animaPaint == null) {
                animaPaint = new Paint();
                LinearGradient lg = new LinearGradient(80, 0, 100, 100, Color.WHITE, Color.GREEN, Shader.TileMode.REPEAT);
                animaPaint.setShader(lg);
                animaPaint.setColor(Color.GREEN);
                animaPaint.setAlpha(80);
            }

            if (currentY == 0) {
                currentY = frame.top-animaHeight/100;
            }


            canvas.drawRect(frame.left + (animaWidth/8), currentY, frame.right - (animaWidth/8), currentY + animaHeight/100, animaPaint);
        }

    }

    private void initAnim() {
        if (anim != null) {
            return;
        }

        if (frame == null) {
            return;
        }

        anim = ValueAnimator.ofInt(frame.top, frame.bottom - 10);
        anim.setDuration(3000);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setStartDelay(ANIMATION_DELAY);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                currentY = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

    }

    private void startAnimator() {

        endAnima = false;

        if (anim == null) {
            initAnim();
        }

        if (!anim.isStarted()) {
            anim.start();
        }
    }

    public void endAnimator() {

        endAnima = true;
        if (anim != null) {
            anim.end();
        }
    }

    //此时camera已经初始化，并且初始化是decodeThread已经启动
    public void drawViewfinder() {
        invalidate();

        if (frame == null) {
            frame = CameraManager.get().getFramingRect();
        }


        startAnimator();
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

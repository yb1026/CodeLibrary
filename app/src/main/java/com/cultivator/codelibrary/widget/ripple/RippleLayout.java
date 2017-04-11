package com.cultivator.codelibrary.widget.ripple;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Created by yb1026 on 16/6/6.
 */

public class RippleLayout extends FrameLayout {
    public RippleLayout(Context context) {
        super(context);
    }

    public RippleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RippleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /*
     *
     *
     */


    private int centerX;
    private int centerY;
    private int fromRadius;
    private int toRadius;
    private int duration;
    private int mColor;
    private Interpolator mInterpolator;
    private LayoutParams childParams;
    private boolean hasInit = false;


    /*
     *
     *
     */


    /**
     * @param x            中心点x
     * @param y            中心点y
     * @param fromRadius   初始半径
     * @param toRadius     目标半径
     * @param duration     duration
     * @param color        颜色
     * @param interpolator 插补器
     */
    public void init(int x, int y, int fromRadius, int toRadius, int duration, int color, Interpolator interpolator) {
        this.centerX = x;
        this.centerY = y;
        this.fromRadius = fromRadius;
        this.toRadius = toRadius;
        this.duration = duration;
        this.mColor = color;
        this.mInterpolator = interpolator;

        childParams = new LayoutParams(toRadius * 2, toRadius * 2);
        childParams.topMargin = centerY - toRadius;
        childParams.leftMargin = centerX - toRadius;

        hasInit = true;
    }

    //让View复用的缓存list
    private ArrayList<OnceRippleView> cacheList = new ArrayList<>(0);

    /**
     * 做波纹动画 , 要先init
     */
    public void doRipple() {
        if (!hasInit)
            return;

        final OnceRippleView rView = cacheList.isEmpty() ? new OnceRippleView(getContext()) : cacheList.get(0);
        if (!cacheList.isEmpty())
            cacheList.remove(rView);

        rView.init(fromRadius, toRadius, mColor, duration, mInterpolator);
        addView(rView, childParams);
        rView.start(new Runnable() {
            public void run() {
                removeView(rView);
                if (!cacheList.contains(rView))
                    cacheList.add(rView);
            }
        });
    }

}



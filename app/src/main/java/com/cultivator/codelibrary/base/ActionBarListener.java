package com.cultivator.codelibrary.base;

import android.view.View;

/**
 * @TODO: 自定义actionbar
 */
public interface ActionBarListener {


    /**
     * 改变导航栏背景颜色或图片
     * @param resid
     */
    void setBackground(int resid);


    /**
     * 设置导航栏右侧按钮图片
     * @param resid
     */
    void setImageRightIcon(int resid);

    /**
     * 导航中间标题
     * @param resid
     */
    void setTitle(int resid);

    /**
     * 导航中间标题
     * @param title
     */
    void setTitle(String title);

    /**
     * 导航左侧标题
     * @param resid
     */
    void setTitleLeft(int resid);

    /**
     * 导航左侧标题
     * @param title
     */
    void setTitleLeft(String title);


    /**
     * 导航右侧标题
     * @param resid
     */
    void setTitleRight(String resid);

    /**
     * 导航右侧标题
     * @param resId
     */
    void setTitleRight(int resId);


    /**
     * 导航右侧文本颜色
     * @param resid
     */
    void setColorRight(int resid);

    /**
     * 导航中间文本颜色
     * @param resid
     */
    void setTitleColor(int resid);

    /**
     * 设置导航栏左侧按钮图片
     * @param resid
     */
    void setImageLeftIcon(int resid);


    /**
     * 隐藏左侧标题栏
     */
    void hideTitleLeft();

    /**
     * 隐藏全部导航栏
     */
    void hideActionBar();

    /**
     * 隐藏右侧按钮
     */
    void hideImageRightBar();

    /**
     * 显示右侧按钮
     */
    void showImageRightBar();

    /**
     * 显示左侧按钮
     */
    void showImageLeftBar();

    /**
     * 隐藏左侧按钮
     */
    void hideImageLeftBar();

    /**
     * 隐藏右侧布局
     */
    void hideLayoutRight();

    /**
     * 设置导航栏左侧按钮点击事件
     */
    void setLeftActionBarOnClickListener(View.OnClickListener onClickListener);

    /**
     * 设置导航栏右侧按钮点击事件
     */
    void setRightActionBarOnClickListener(View.OnClickListener onClickListener);


    /**
     * 导航栏是否可见：true 可见，false：不可见
     */
    void setBarVisibility(boolean bool);
}

package com.cultivator.codelibrary.common.util;

import android.content.Context;
import android.widget.Toast;


/**
 * TODO 自定义Toast
 *
 * @author simon.xin
 * @version V1.0
 * @FileName com.simon.view.toast.WepiToastUtil.java
 * @date 2014-2-13 上午11:47:24
 */
public class ToastUtil {

    /**
     * @param context
     * @param text
     * @author simon.xin
     * @date 2014-2-13 上午11:47:58
     * @returnType void
     */
    public static void show (Context context, String text) {
        if(StringUtil.isEmpty(text)){
            return;
        }
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }


    /**
     * @param context
     * @param text
     * @author simon.xin
     * @date 2014-2-13 上午11:47:58
     * @returnType void
     */
    public static void showLong (Context context, String text) {
        if(StringUtil.isEmpty(text)){
            return;
        }
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    /**
     * TODO toast
     *
     * @FileName ToastUtil.java
     * @author Simon.xin
     */
    public static void show (Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}

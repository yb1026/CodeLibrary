package com.cultivator.codelibrary.common.log;

import android.util.Log;

/**
 * @author: xin.wu
 * @create time: 2016/4/7 17:49
 * @TODO: 描述
 */
public final class MyLog {

    public static int LOG_LEV= 0;
    private static final String LOG_TAG = "mproject";


    public static final void d(Class var0, String var1) {
        if(LOG_LEV>=2) {
            Log.d(LOG_TAG, "debug>>>>>>>>>>>" + var0.getSimpleName() + " " + var1);
        }
    }

    public static final void d(String var1) {
       d(MyLog.class,var1);
    }


    public static final void i(Class var0, String var1) {
        if(LOG_LEV>=1) {
            Log.i(LOG_TAG, "info>>>>>>>>>>>" + var0.getSimpleName() + " " + var1);
        }

    }

    public static final void i(String var1) {
        i(MyLog.class,var1);
    }



    public static final void e(Class var0, String var1, Throwable var2) {
        if(LOG_LEV>=0) {
            Log.e(LOG_TAG, "error>>>>>>>>>>>" + var0.getSimpleName() + " " + var1, var2);
        }

    }

    public static final void e(Class var0, String var1) {
        if(LOG_LEV>=0) {
            Log.e(LOG_TAG, "error>>>>>>>>>>>" + var0.getSimpleName() + " " + var1);
        }

    }

    public static final void e(String var1) {
        e(MyLog.class,var1);
    }

    public static final void e(String var1,Throwable var2) {
        e(MyLog.class, var1,var2);
    }

}

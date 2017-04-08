package com.cultivator.codelibrary.common;

import android.app.Application;
import android.content.Intent;

import com.bumptech.glide.Glide;
import com.cultivator.codelibrary.common.log.MyLog;
import com.cultivator.codelibrary.common.net.CallServer;
import com.cultivator.codelibrary.common.net.NetworkManager;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;

import java.util.Stack;

/**
 * @author: xin.wu
 * @create time: 2016/3/31 11:35
 * @TODO: 描述
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;

    public boolean isRegister;



    public synchronized static MyApplication getInstance() {
        if (mInstance == null) {
            mInstance = new MyApplication();
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        ObServerManager.getInstance(this);
        NoHttp.init(this);
        NetworkManager.init(this);
        Logger.setDebug(false);// 开始NoHttp的调试模式, 这样就能看到请求过程和日志
        MyLog.LOG_LEV = 2;


        //个推
        //PushManager.getInstance().initialize(this);

    }


    public void setMessageListener(int msgTypes, ObServerListener messageListener) {
        ObServerManager.getInstance(this).put(msgTypes, messageListener);
    }

    public void sendMessageBroadcast(int messageType, Intent intent) {
        ObServerManager.getInstance(this).sendMessageBroadcast(messageType, intent);
    }

    /**
     * 1，OnLowMemory被回调时，已经没有后台进程;
     * 2，OnLowMemory是在最后一个后台进程被杀时调用，一般情况是low memory killer 杀进程后触发；
     * 而OnTrimMemory的触发更频繁，每次计算进程优先级时，只要满足条件，都会触发。
     * 3，通过一键清理后，OnLowMemory不会被触发，而OnTrimMemory会被触发一次。
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * onTrimMemory被回调时，还有后台进程。
     *
     * @param level
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        destoryAll();
    }




    /**
     * TODO clearAll
     *
     * @FileName BaseApplication.java
     * @author Simon.xin
     */
    public void destoryAll() {
        CallServer.getRequestInstance().cancelAll();
        Glide.get(getApplicationContext()).clearMemory();
        /** 子线程运行 */
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(getApplicationContext()).clearDiskCache();
                ObServerManager.getInstance(mInstance).destory();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }).start();
    }
}

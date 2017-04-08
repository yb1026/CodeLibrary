package com.cultivator.codelibrary.common.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cultivator.codelibrary.common.log.MyLog;


/**
 * @author: xin.wu
 * @create time: 2016/4/14 11:38
 * @TODO: 网络监听器
 */
public class NetworkManager {

    private static NetworkManager mNetworkManager = null;
    private Context mContext = null;
    private boolean mConnected = true; // 网络状态 true：当前网络正常 false:网络已断开

    public synchronized static NetworkManager getInstance(Context context) {
        if (mNetworkManager == null) {
            mNetworkManager = new NetworkManager(context);
        }
        return mNetworkManager;
    }

    public static void init(Context context){
        if (mNetworkManager == null) {
            mNetworkManager = new NetworkManager(context);
        }
    }

    private NetworkManager(Context context) {
        mContext = context;
        registerNetworkReceiver();
        mConnected = getNetWorkType(mContext);
    }

    /**
     * 注册网络变化监听
     */
    public void registerNetworkReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mNetworkReceiver, filter);
    }

    /**
     * 取消注册
     */
    public void unRegisterNetworkReceiver() {
        try {
            if (mContext != null && mNetworkReceiver != null) {
                mContext.unregisterReceiver(mNetworkReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyLog.d(getClass(), "NetworkReceiver...");
            String action = intent.getAction();
            synchronized (this) {
                if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    try {
                        if (getNetWorkType(context)) {
                            mConnected = true;
                        } else {
                            mConnected = false;
                        }
                    } catch (Exception e) {
                        MyLog.e(getClass(), "e:" + e.getLocalizedMessage());
                    }
                }
            }
        }
    };

    /**
     * 检测网络是否存在
     * @param context
     * @return boolean
     */
    private boolean getNetWorkType(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    int infolength = info.length;
                    for (int i = 0; i < infolength; i++) {
                        if (info[i].isConnected()) {
                            MyLog.i(getClass(),
                                    "Active Network Type........................"
                                            + info[i].getTypeName());
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            MyLog.e("network  throw exception",e);
            return false;
        }
        return false;
    }

    /**
     * 是否已经连上网络
     */
    public boolean isConnected() {
        return mConnected;
    }
}

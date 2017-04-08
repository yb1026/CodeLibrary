/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cultivator.codelibrary.common.net;

import android.content.Context;
import android.content.DialogInterface;


import com.cultivator.codelibrary.base.BaseActivity;
import com.cultivator.codelibrary.base.ViewListener;
import com.cultivator.codelibrary.common.dialog.WaitDialog;
import com.cultivator.codelibrary.common.log.MyLog;
import com.cultivator.codelibrary.common.net.HttpListener;
import com.cultivator.codelibrary.common.net.model.BaseResp;
import com.cultivator.codelibrary.common.util.Utils;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.Response;
import com.yolanda.nohttp.error.ClientError;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.ServerError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.UnKnownHostError;

import org.json.JSONObject;

/**
 * Created in Nov 4, 2015 12:02:55 PM.
 *
 * @author YOLANDA;
 */
public class HttpResponseListener<T> implements OnResponseListener<T> {


    private Context mContext;
    /**
     * Dialog.
     */
    private WaitDialog mWaitDialog;
    private Request<?> mRequest;

    private ViewListener viewListener;

    /**
     * 结果回调.
     */
    private HttpListener callback;

    /**
     * 是否显示dialog.
     */
    private boolean isLoading;

    /**
     * @param context      context用来实例化dialog.
     * @param request      请求对象.
     * @param httpCallback 回调对象.
     * @param canCancel    是否允许用户取消请求.
     * @param isLoading    是否显示dialog.
     */
    public HttpResponseListener(Context context, Request<?> request, HttpListener httpCallback, ViewListener mViewListener, boolean canCancel, boolean isLoading) {
        this.mContext = context;
        this.mRequest = request;
        if (context != null && isLoading) {
            mWaitDialog = new WaitDialog(context);
            mWaitDialog.setCancelable(canCancel);
            mWaitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mRequest.cancel(true);
                }
            });
        }
        this.viewListener = mViewListener;
        this.callback = httpCallback;
        this.isLoading = isLoading;
    }


    /**
     * 开始请求, 这里显示一个dialog
     */
    @Override
    public void onStart(int what) {
        if (isLoading && mWaitDialog != null && !mWaitDialog.isShowing() && mContext != null) {
            mWaitDialog.show();
        }
    }

    /**
     * 结束请求, 这里关闭dialog.
     */
    @Override
    public void onFinish(int what) {
        if (isLoading && mWaitDialog != null && mWaitDialog.isShowing() && !mContext.isRestricted()) {
            if (mContext instanceof BaseActivity) {
                if (!((BaseActivity) mContext).isFinishing())
                    mWaitDialog.dismiss();
            } else {
                mWaitDialog.dismiss();
            }
            mWaitDialog = null;
            mContext = null;
        }
    }

    /**
     * 成功回调.
     */
    @Override
    public void onSucceed(int what, Response<T> response) {
        installData(what, response);
    }


    /**
     * @param what
     * @param rp
     * @TODO 数据校验,成功返回
     */
    public void installData(int what, Response<T> rp) {
        if (Utils.isNull(rp.get())) {
            onFailed(what, "");
            return;
        }
        MyLog.d(com.cultivator.codelibrary.common.net.HttpResponseListener.class, "response：" + rp.get().toString());
        BaseResp response = new BaseResp();
        Object obj = rp.get();
        try {
            JSONObject reponseObject = new JSONObject(obj.toString());

            response.data= reponseObject;

            if (!Utils.isNull(callback)) {
                callback.onSucceed(what,response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onFailed(what, e.toString());
        }
    }



    /**
     * @param what
     * @param errorMsg
     * @TODO 失败
     */
    private void onFailed(int what, String errorMsg) {
        if (!Utils.isNull(callback)) {
            callback.onFailed(what, errorMsg,"9999",null);
        }
    }

    /**
     * @param what
     * @param errorMsg
     * @TODO 失败
     */
    private void onFailed(int what, String errorMsg,String errorCode,Exception e) {
        if (!Utils.isNull(callback)) {
            callback.onFailed(what, errorMsg,errorCode,e);
        }
    }

    /**
     * 失败回调.自定义处理
     */
    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        MyLog.d(com.cultivator.codelibrary.common.net.HttpResponseListener.class, "onFailed：" + exception);
        String msg = "";
        onFailed(what, !Utils.isNull(msg) ? msg : exception.getMessage(),String.valueOf(responseCode),exception);

//        if (exception instanceof ClientError) {// 客户端错误
//        } else if (exception instanceof ServerError) {// 服务器错误
//        } else if (exception instanceof NetworkError) {// 网络不好
//        } else if (exception instanceof TimeoutError) {// 请求超时
//        } else if (exception instanceof UnKnownHostError) {// 找不到服务器
//        } else {
//        }

    }

}

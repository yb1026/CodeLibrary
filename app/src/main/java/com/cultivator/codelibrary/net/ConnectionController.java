package com.cultivator.codelibrary.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.cultivator.codelibrary.base.ViewListener;
import com.cultivator.codelibrary.log.MyLog;
import com.cultivator.codelibrary.util.JsonUtil;
import com.cultivator.codelibrary.util.Utils;
import com.cultivator.codelibrary.net.model.BaseRequest;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author: xin.wu
 * @create time: 2016/4/29 15:36
 * @TODO: ConnectionController
 */
public class ConnectionController {

    private Context mContext;

    private HttpListener mHttpListener;

    private ViewListener mViewListener;


    /**
     * @param context
     * @param httpListener
     */
    public ConnectionController(Context context, HttpListener httpListener, ViewListener viewListener) {
        this.mViewListener = viewListener;
        this.mContext = context;
        mHttpListener = httpListener;
    }

    /***************************************http begin********************************************/

    /**
     * @param what
     * @param request
     * @param isLoading
     */
    public void doGet(int what, BaseRequest request, boolean isLoading) {
        connection(what, request, RequestMethod.GET, isLoading);
    }

    /**
     * @param what
     * @param request
     */
    public void doGet(int what, BaseRequest request) {
        connection(what, request, RequestMethod.GET, true);
    }

    /**
     * @param what
     * @param request
     */
    public void doPost(int what, BaseRequest request) {
        connection(what, request, RequestMethod.POST, true);
    }

    /**
     * @param what
     * @param request
     * @param isLoading
     */
    public void doPost(int what, BaseRequest request, boolean isLoading) {
        connection(what, request, RequestMethod.POST, isLoading);
    }


    public boolean isSync;

    /**
     * @param what
     * @param request
     * @param requestMethod
     * @param isLoading
     */
    private void connection(int what, BaseRequest request, RequestMethod requestMethod, boolean isLoading) {


        MyLog.d(ConnectionController.class, "url:" + request.getUrl());

        Request<JSONObject> mRequest = null;

        if (requestMethod == RequestMethod.GET) {
            mRequest = NoHttp.createJsonObjectRequest(request.getUrl(), requestMethod);
            if (request.getJson() != null) {
                Map<String, String> maps = JsonUtil.parseJsonMap(request.getJson().toString());
                printMap(maps);
                mRequest.add(maps);
            }
        } else {
            MyLog.d(ConnectionController.class, "request:" + request.getJson().toString());
            mRequest = new FastJsonRequest(request.getUrl(), requestMethod);
            if (!request.getHeaders().isEmpty()) {
                setHeaders(request.getHeaders(), mRequest);
            }
            mRequest.setRequestBody(request.getJson().toString());
        }


        if (!request.getHeaders().isEmpty()) {
            setHeaders(request.getHeaders(), mRequest);
        }
        mRequest.setConnectTimeout(NetworkConstant.HTTP_TIMEOUT);
        mRequest.setReadTimeout(NetworkConstant.READ_TIMEOUT);
        mRequest.setSSLSocketFactory(SSLContextManager.getDefaultSLLContext().
                getSocketFactory());
        CallServer.getRequestInstance().add(mContext, what, mRequest, mHttpListener,
                mViewListener, true, isLoading);
    }


    public void connection(BaseRequest request, RequestMethod requestMethod, boolean isLoading) {
        MyLog.d(ConnectionController.class, "url:" + request.getUrl());
        Response<JSONObject> response = null;
        if (requestMethod == RequestMethod.GET) {
            Map<String, String> maps = JsonUtil.parseJsonMap(request.getJson().toString());
            printMap(maps);
            Request<JSONObject> mRequest = NoHttp.createJsonObjectRequest(request.getUrl(), requestMethod);
            mRequest.add(maps);
            if (!request.getHeaders().isEmpty()) {
                setHeaders(request.getHeaders(), mRequest);
            }
            mRequest.setSSLSocketFactory(SSLContextManager.getDefaultSLLContext().getSocketFactory());
            response = NoHttp.startRequestSync(mRequest);
        } else {
            MyLog.d(ConnectionController.class, "request:" + request.getJson().toString());
            FastJsonRequest fjr = new FastJsonRequest(request.getUrl(), requestMethod);
            if (!request.getHeaders().isEmpty()) {
                setHeaders(request.getHeaders(), fjr);
            }
            fjr.setRequestBody(request.getJson().toString());
            response = NoHttp.startRequestSync(fjr);
        }
        HttpResponseListener listener = new HttpResponseListener(mContext, (Request<?>) request, mHttpListener, mViewListener, false, isLoading);
        Message msg = handler.obtainMessage();
        Object[] objs = {listener, response};
        handler.obtainMessage(0, listener).sendToTarget();
    }


    /**
     * handler接受子线程结果
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {// 如果是同步请求发回来的结果
                Object[] objs = (Object[]) msg.obj;
                HttpResponseListener listener = (HttpResponseListener) objs[0];
                Response<JSONObject> response = (Response<JSONObject>) objs[1];
                listener.installData(0, response);
            }
        }
    };

    /**
     * add http Header
     *
     * @param map
     * @param fjr
     */
    private void setHeaders(Map<String, String> map, Request<JSONObject> fjr) {
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if (!Utils.isNull(key) && !Utils.isNull(value)) {
                MyLog.d(ConnectionController.class, "add header:" + key + "    " + value);
                fjr.addHeader(key, value);
            }
        }
        map.clear();
    }

    /**
     * add http Header
     *
     * @param map
     */
    private void printMap(Map<String, String> map) {
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            MyLog.d(ConnectionController.class, "param:" + key + "    " + value);
        }
    }

    /**
     * add http Header
     *
     * @param map
     * @param fjr
     */
    private void setHeaders(TreeMap<String, String> map, FastJsonRequest fjr) {
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if (!Utils.isNull(key) && !Utils.isNull(value)) {
                MyLog.d(ConnectionController.class, "add header:" + key + "    " + value);
                fjr.addHeader(key, value);
            }
        }
        map.clear();
    }


}

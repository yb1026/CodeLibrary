package com.cultivator.codelibrary.net;

import android.text.TextUtils;

import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RestRequest;
import com.yolanda.nohttp.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: xin.wu
 * @create time: 2016/5/3 14:30
 * @TODO: 描述
 */
public class FastJsonRequest extends RestRequest<JSONObject> {

    public FastJsonRequest(String url) {
        super(url);
    }

    public FastJsonRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    @Override
    public JSONObject parseResponse(String url, Headers headers, byte[] responseBody) {
        String result = StringRequest.parseResponseString(url, headers, responseBody);
        JSONObject jsonObject = null;
        if (!TextUtils.isEmpty(result)) {
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    @Override
    public String getAccept() {
        // 告诉服务器你接受什么类型的数据, 会添加到请求头的Accept中
        return "application/json;q=1";
    }

    @Override
    public String getContentType () {
        return "application/json; charset="+getParamsEncoding();
    }
}
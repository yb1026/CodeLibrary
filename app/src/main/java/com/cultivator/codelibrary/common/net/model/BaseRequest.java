package com.cultivator.codelibrary.common.net.model;

import com.cultivator.codelibrary.common.net.model.*;
import com.cultivator.codelibrary.common.net.model.BaseParam;

import java.util.TreeMap;

/**
 * @author: xin.wu
 * @create time: 2016/4/25 11:42
 * @TODO: BaseParam
 */
public  class BaseRequest extends BaseParam {

    /**
     * http url
     */
    private String url;
    /**
     * http headers POST/GET
     */
    private TreeMap<String,String> mHeaders = new TreeMap<String,String>();

    /**
     * http request json
     */
    public String json;

    public TreeMap<String,String> getHeaders(){
        return this.mHeaders;
    }

    public void addHeader(String key, String value) {
        this.mHeaders.put(key, value);
    }

    public String getUrl () {
        return url;
    }

    public void setUrl (String url) {
        this.url = url;
    }

    public String getJson () {
        return json;
    }

    public void setJson (String json) {
        this.json = json;
    }


}

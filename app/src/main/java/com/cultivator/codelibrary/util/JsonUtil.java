package com.cultivator.codelibrary.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.GsonBuilder;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author: han.zhang
 * @create time: 2016/10/18
 */
public final class JsonUtil {

    public static final String TIME_FORMAT_1 = "yyyy-MM-dd HH:mm:ss.SSS";




    /**
     * 将java对象转换成json对象,不过滤NULL值
     *
     * @param obj
     * @return
     */
    public static String parseObjJson(Object obj) {

        if (null == obj)
            return null;

        return new GsonBuilder().serializeNulls().setDateFormat(TIME_FORMAT_1).disableHtmlEscaping().create().toJson(obj);

    }

    /**
     * 将java对象转换成json对象。
     *
     * @param obj
     * @return
     */
      public static String parseObjJsonFilterNull(Object obj) {

        if (null == obj)
            return null;

        return new GsonBuilder().setDateFormat(TIME_FORMAT_1).disableHtmlEscaping().create().toJson(obj);

      }


    /**
     * 将json对象转换成java对象
     *
     * @param <T>
     * @param jsonData
     * @param c
     * @return
     */
    public static <T> T parseJson(String jsonData, Class<T> c) {
        T info = null;
        try {
            info = (T) JsonUtil.parseJsonObj(jsonData, c);
        } catch (Exception e) {
        }
        return info;
    }

    private static <T> Object parseJsonObj(String jsonData, Class<T> c) {

        if (null == jsonData)
            return null;

        Object obj = JSON.parseObject(jsonData.trim(), c);
        return obj;
    }

    /**
     * 将json对象转换成数组对象
     *
     * @param <T>
     * @param jsonData
     * @param c
     * @return
     * @throws JSONException
     */
    public static <T> ArrayList<T> parseJsonList(String jsonData, Class<T> c) {

        if (null == jsonData || "".equals(jsonData))
            return null;
        ArrayList<T> list = (ArrayList<T>) JSON.parseArray(jsonData.trim(), c);
        return list;
    }



    /**
     * @param jsonStr
     * @return HashMap
     */
    public static HashMap<String, String> parseJsonMap(String jsonStr) {
        HashMap<String, String> map = JSON.parseObject(jsonStr, new TypeReference<HashMap<String, String>>() {
        }.getType());
        return map;
    }

}

package com.cultivator.codelibrary.net;

import java.util.Iterator;
import java.util.Map;

/**
 * @author: xin.wu
 * @create time: 2016/5/5 17:47
 * @TODO: 描述
 */
public class ParametersUtils {


    /**
     *
     * @param params
     * @return
     */
    public static String getRequestParamStr(Map<String,String> params)  {
        StringBuilder urlBuilder = new StringBuilder();
        Iterator<?> iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            try {
                String value = val.toString().trim();
                if (value.contains("&") || value.contains("=")) {
                    urlBuilder.append("&" + key + "=" + value);
                } else {
                    urlBuilder.append("&"+ key + "="  + String.valueOf(val));
                }
            } catch (Exception e) {
                urlBuilder.append("&" + key + "=" + val);
            }
        }
        String reqURL = "";
        if (!urlBuilder.toString().equals("")) {
            reqURL = urlBuilder.toString().replaceFirst("&", "?");
        }

        return reqURL;
    }
}

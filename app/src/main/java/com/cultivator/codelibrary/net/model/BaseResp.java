package com.cultivator.codelibrary.net.model;

/**
 * 接口返回json对象，可修改
 */
public class BaseResp implements java.io.Serializable {


    /* false: 失败    true:   成功 */
    public boolean isSuceed;

    /* JavaBean */
    public Object data;

    /* error msg  */
    public String errMsg;

    public String code;

    /**
     * token丢失 需要重新登录
     */
    public boolean isTokenMiss = false;
}

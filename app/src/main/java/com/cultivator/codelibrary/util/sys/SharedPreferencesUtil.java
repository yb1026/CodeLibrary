package com.cultivator.codelibrary.util.sys;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

import com.cultivator.codelibrary.log.MyLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * @author xiaoping.shan
 */
public class SharedPreferencesUtil {


    private static final String FILE_NAME = "myproject_share";


    /**
     * @serialField 保存在手机里面的文件名
     */


    public static void saveObject(SharedPreferences preferences, String key, Object object) {

        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(object);
            // 将字节流编码成base64的字符窜
            String oAuth_Base64 = new String(Base64.encode(baos
                    .toByteArray(), Base64.DEFAULT));
            Editor editor = preferences.edit();
            editor.putString(key, oAuth_Base64);

            editor.commit();
        } catch (IOException e) {
            MyLog.e("saveObject", e);
        }

    }


    public static Object readObject(SharedPreferences preferences, String key) {

        Object object = null;
        String productBase64 = preferences.getString(key, "");

        //读取字节
        byte[] base64 = Base64.decode(productBase64.getBytes(), Base64.DEFAULT);

        //封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            //再次封装
            ObjectInputStream bis = new ObjectInputStream(bais);
            try {
                //读取对象
                object = bis.readObject();
            } catch (ClassNotFoundException e) {
                MyLog.e("readObject", e);
            }
        } catch (StreamCorruptedException e) {
            MyLog.e("readObject", e);
        } catch (IOException e) {
            MyLog.e("readObject", e);
        }
        return object;
    }

    /**
     * todo 保存数据String
     *
     * @param context 上下文联系菜单
     * @param key     键
     * @param value   值
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(key, value == null ? "" : value);
        editor.apply();
    }


    /**
     * todo 保存数据String
     *
     * @param context 上下文联系菜单
     * @param key     键
     * @param value   值
     */
    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * todo 获取数据的方法
     *
     * @param context      上下文联系菜单
     * @param key          键
     * @param defaultValue 默认数据
     * @return String 数据
     */
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }


    /**
     * todo 获取数据的方法
     *
     * @param context      上下文联系菜单
     * @param key          键
     * @param defaultValue 默认数据
     * @return String 数据
     */
    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    /**
     * 清除当前Model的本地存储；
     *
     * @param mContext mContext
     * @param fileName fileName
     * @throws
     * @Method: removeObject
     */
    public static void clearObject(Context mContext, String fileName) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 清除当前Model的本地存储；
     *
     * @param mContext mContext
     * @throws
     * @Method: removeObject
     */
    public static void removeObject(Context mContext, String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }
}

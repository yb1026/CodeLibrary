package com.cultivator.codelibrary.util.encrypt;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtil {

    /*
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    private static String ivParameter = "0123456789abcdef";

    // 加密

    /**
     * AES加密
     *
     * @param sSrc 待加密字符串
     * @param sKey 密钥（16个字符）
     * @return
     * @throws Exception
     */
    public static String encryptAES(String sSrc, String sKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        //return encrypted.toString();

        return Base64.encodeToString(encrypted, Base64.DEFAULT);
        //return new BASE64Encoder().encode(encrypted);// 此处使用BASE64做转码。
    }

    public static String encrypt(String sSrc, String sKey) throws Exception {

//		if(RuntimeProperties.TEST==RuntimeProperties.getInstance().getEnvironment()){
//			return sSrc;
//		}

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        //return encrypted.toString();
        return Base64.encodeToString(encrypted, Base64.NO_PADDING).trim();
    }

    // 解密

    /**
     * AES解密
     *
     * @param sSrc 待解密字符串
     * @param sKey 密钥（16个字符）
     * @return
     * @throws Exception
     */
    public static String decryptAES(String sSrc, String sKey) throws Exception {
        try {
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);


            byte[] encrypted1 = Base64.decode(sSrc, Base64.DEFAULT);
            //byte[] encrypted1 = decodeBuffer(sSrc);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }

    public static String decrypt(String sSrc, String sKey) throws Exception {
        try {
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = Base64.decode(sSrc, Base64.NO_PADDING);
            //byte[] encrypted1 = decodeBuffer(sSrc);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }

    public static void main(String[] args) {
        String enc1 = null;
        String enc2 = null;
        String sKey = "1375500524912345";
        String dec1 = null;
        String dec2 = null;
        try {
            enc1 = encrypt("13755005249", sKey);
            enc2 = encryptAES("13755005249", sKey);
            dec1 = decrypt(enc1, sKey);
            dec2 = decryptAES(enc2, sKey);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("befo1:" + enc1);
        System.out.println("befo2:" + enc2);
        System.out.println("over1:" + dec1);
        System.out.println("over2:" + dec2);

    }
}

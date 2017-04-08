package com.cultivator.codelibrary.common.util.encrypt;

import java.security.MessageDigest;

/**
 * 采用SHAA加密
 * 
 * @author yb1026
 * @datetime 2014-6-1
 */
public class SHAUtil {
	
	public final static String MD5 = "MD5";
	public final static String NONE = "NONE";
	public final static String SHA_256 = "SHA-256";
	public final static String SHA_512 = "SHA-512";
	public final static String SHA_384 = "SHA-384";
	
	

	/***
	 * SHA加密 
	 * @param
	 */
	public static String shaEncode(String inStr, String algorithm)
			throws Exception {
		MessageDigest sha = null;
		try {
			sha = MessageDigest.getInstance(algorithm);
		} catch (Exception e) {
			return "";
		}

		byte[] byteArray = inStr.getBytes("UTF-8");

		sha.update(byteArray);

		return getFormattedText(sha.digest());
	}

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	// 加密算法 把密文转成16进制的字符串形式
	public static String getFormattedText(byte[] data) {
		int l = data.length;
		char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = HEX_DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = HEX_DIGITS[0x0F & data[i]];
		}
		return new String(out);
	}

}
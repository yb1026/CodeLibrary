package com.cultivator.codelibrary.util.encrypt;


import android.util.Base64;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * *****
 * 
 * @author:Kevin
 */

public class Base64Util implements Serializable {

	private static final long serialVersionUID = 1186452756776508240L;

	private static final String CHARSET_UTF8 = "utf-8";

	// 加密
	public static String encode(String s) {
		String result = null;
		byte[] b = null;
		try {
			b = s.getBytes(CHARSET_UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (b != null) {
			result = Base64.encodeToString(b, Base64.NO_PADDING);
		}
		return result.trim();
	}

	// 解密
	public static String decode(String s) {
		byte[] b = null;
		String result = null;
		if (s != null) {
			try {
				b = Base64.decode(s, Base64.NO_PADDING);
				result = new String(b, CHARSET_UTF8);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result.trim();
	}

}

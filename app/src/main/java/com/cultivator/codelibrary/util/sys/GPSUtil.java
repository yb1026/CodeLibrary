package com.cultivator.codelibrary.util.sys;

import android.content.Context;
import android.location.LocationManager;

/**
 * android GPS 工具类
 * 
 * @author tinyliu
 * 
 */
public class GPSUtil {


	
	/**
	 * 判断当前GPS是否打开
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isGPSOpen(Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		return locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	


}

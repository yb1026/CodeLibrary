package com.cultivator.codelibrary.common.location;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.cultivator.codelibrary.common.log.MyLog;

/**
 * 定位数据源
 * 
 * @author yb1026
 * 
 */
public class SimpleLocationResource {


	private static SimpleLocationResource simpleLocationResource;

	public static SimpleLocationResource getInstance(){
		if(simpleLocationResource==null){
			simpleLocationResource = new SimpleLocationResource();
		}
		return simpleLocationResource;
	}

	private LocationClient mLocationClient;

	private MLocation location;

	private Runnable runnable;
	/**
	 * 是否已获取过地址
	 *
	 * @return
	 */
	public boolean hasLocation() {
		return location != null;
	}

	public  MLocation getLocation() {
		return location;
	}

	public void requestLocation(Context ctx,Runnable runnable) {

		init(ctx);
		this.runnable = runnable;
		mLocationClient.requestLocation();

	}

	public void unRegister(){
		this.unRegisterLocation();
		location=null;
	}



	private void init(Context ctx) {
		if (mLocationClient == null) {
			LocationListenner myListener = new LocationListenner();
			mLocationClient = new LocationClient(ctx);
			mLocationClient.registerLocationListener(myListener);
		}
		initLocation();
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}

	}



	private void unRegisterLocation() {
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
	}

	private class LocationListenner implements BDLocationListener {

		public void onReceiveLocation(BDLocation bdLocation) {

			if (bdLocation == null || (bdLocation.getLatitude() < 0.00000000001 && bdLocation.getLatitude() > 0)
					|| (bdLocation.getLatitude() < -0.00000000001 && bdLocation.getLatitude() < 0)
					|| (bdLocation.getLongitude() < 0.00000000001 && bdLocation.getLongitude() > 0)
					|| (bdLocation.getLongitude() > -0.00000000001 && bdLocation.getLongitude() < 0)) {
				mLocationClient.requestLocation();

			} else {

				if (location == null) {
					location = new MLocation();
				}
				location.setAddress(bdLocation.getAddrStr());
				location.setSpecLatitude(bdLocation.getLatitude());
				location.setSpecLongitude(bdLocation.getLongitude());
				
				location.setCity(bdLocation.getCity());
				location.setCountry(bdLocation.getDistrict());
				location.setTown("");
				location.setVillage(bdLocation.getStreet());

				if(runnable!=null){
					new Handler(Looper.getMainLooper()).post(runnable);
				}
				unRegisterLocation();

			}
		}

		@Override
		public void onConnectHotSpotMessage(String s, int i) {
			MyLog.d(s + ">>>>>I=" + i);
		}
	}

	private void initLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

		option.setCoorType("bd09ll");
		//可选，默认gcj02，设置返回的定位结果坐标系

		int span=1000;
		option.setScanSpan(span);
		//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

		option.setIsNeedAddress(true);
		//可选，设置是否需要地址信息，默认不需要

		option.setOpenGps(false);
		//可选，默认false,设置是否使用gps

		option.setLocationNotify(true);
		//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

		option.setIsNeedLocationDescribe(true);
		//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

		option.setIsNeedLocationPoiList(true);
		//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

		option.setIgnoreKillProcess(false);
		//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

		option.SetIgnoreCacheException(false);
		//可选，默认false，设置是否收集CRASH信息，默认收集

		option.setEnableSimulateGps(false);
		//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

		mLocationClient.setLocOption(option);
	}


}

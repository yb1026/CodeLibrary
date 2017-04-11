package com.cultivator.codelibrary.location;

import java.io.Serializable;

public class MLocation implements Serializable {

	private static final long serialVersionUID = -273590526993871024L;
	
	// 地址
	public String address;
	// 经度
	public double longitude;
	// 纬度
	public double latitude;
	// 百度经度
	public double specLongitude;
	// 百度纬度
	public double specLatitude;
	// 高德经度
	public double amapLongitude;
	// 高德纬度
	public double amapLatitude;
	
	
	
	public String city;
	public String country;
	public String town;
	public String village;
	
	

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getSpecLongitude() {
		return specLongitude;
	}

	public void setSpecLongitude(double specLongitude) {
		this.specLongitude = specLongitude;
	}

	public double getSpecLatitude() {
		return specLatitude;
	}

	public void setSpecLatitude(double specLatitude) {
		this.specLatitude = specLatitude;
	}

	public double getAmapLongitude() {
		return amapLongitude;
	}

	public void setAmapLongitude(double amapLongitude) {
		this.amapLongitude = amapLongitude;
	}

	public double getAmapLatitude() {
		return amapLatitude;
	}

	public void setAmapLatitude(double amapLatitude) {
		this.amapLatitude = amapLatitude;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getVillage() {
		return village;
	}

	public void setVillage(String village) {
		this.village = village;
	}

	
	
	
	
	
}

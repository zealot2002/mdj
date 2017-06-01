package com.mdj.moudle.city;
import java.io.Serializable;

public class MapPoint implements Serializable {
	private static final long serialVersionUID = 1L;
	private String lat;
	private String lng;
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
}
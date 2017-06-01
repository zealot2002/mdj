package com.mdj.moudle.city;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CityBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String imageUrl;
	private String serviceScope;

	private List<List<MapPoint>> areaPointList = new ArrayList<>();
	private Integer isHot;
	private String pys;//拼音排序首字母

    public CityBean(){
        id = "1";
        name = "北京";
    }
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getServiceScope() {
		return serviceScope;
	}
	public void setServiceScope(String serviceScope) {
		this.serviceScope = serviceScope;
	}
	public String getPys() {
		return pys;
	}
	public void setPys(String pys) {
		this.pys = pys;
	}

	public Integer getIsHot() {
		return isHot;
	}
	public void setIsHot(Integer isHot) {
		this.isHot = isHot;
	}
	
	public List<List<MapPoint>> getAreaPointList() {
		return areaPointList;
	}
	public void setAreaPointList(List<List<MapPoint>> areaPointList) {
		this.areaPointList = areaPointList;
	}
	public String getSortKey() {
		return pys.substring(0, 1);
	}
	
}

package com.mdj.moudle.address;

import java.io.Serializable;

/**
 * Created by tt on 2016/6/12.
 */
public class AddressBean implements Serializable{
    private String id = "";
    private String userName = "";
    private String userPhone = "";
    private String name = "";//地标 mdj逻辑专用  使用范围大
    private String address = "";//地址 搜索列表专用,使用范围小
    private String doorNum = "";//门牌号
    private String lat = "";
    private String lng = "";
    private String cityId = "";
    private boolean isDefault;

    public AddressBean(){

    }
    public AddressBean(String userName, String userPhone){
        this(userName,userPhone,"");
    }
    public AddressBean(String userName, String userPhone,String address){
        this("",userName,userPhone,address);
    }
    public AddressBean(String id,String userName, String userPhone,String address){
        this(id,userName,userPhone,address,"","","");
    }
    public AddressBean(String id,String userName, String userPhone, String address,String lat,String lng,String cityId) {
        this(id,userName, userPhone, address,"",lat,lng,cityId);
    }
    public AddressBean(String id,String userName, String userPhone, String name, String doorNum,String lat,String lng,String cityId) {
        this.id = id;
        this.userName = userName;
        this.userPhone = userPhone;
        this.name = name;
        this.doorNum = doorNum;
        this.lat = lat;
        this.lng = lng;
        this.cityId = cityId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDoorNum() {
        return doorNum;
    }

    public void setDoorNum(String doorNum) {
        this.doorNum = doorNum;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
}

package com.mdj.moudle.referee;

import java.io.Serializable;

/**
 * Created by tt on 2016/6/6.
 */
public class RefereeBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private String id;   //美容师id
    private String name;
    private String phone;
    private String imageUrl;
    private String orderId;
    private String couponId;
/********************************************************************************************************/
    public RefereeBean(){

    }
    public RefereeBean(String id,String name, String phone, String imageUrl,String couponId) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.couponId = couponId;
        this.orderId = "";
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }
}

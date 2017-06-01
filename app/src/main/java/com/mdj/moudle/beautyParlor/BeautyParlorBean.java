package com.mdj.moudle.beautyParlor;

import java.io.Serializable;

/**
 * Created by tt on 2016/6/17.
 */
public class BeautyParlorBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;
    private final String tel;
    private final String distance;
    private final String address;
    private final String imgUrl;
    private final String intruduction;
    private final String orderNum;

    private BeautyParlorBean(Builder b) {
        id = b.id;
        name = b.name;
        tel = b.tel;
        distance = b.distance;
        address = b.address;
        imgUrl = b.imgUrl;
        intruduction = b.intruduction;
        orderNum = b.orderNum;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTel() {
        return tel;
    }

    public String getDistance() {
        return distance;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public String getAddress() {
        return address;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getIntruduction() {
        return intruduction;
    }

    public static class Builder {
        private String id;
        private String name;
        private String tel;
        private String distance;
        private String address;
        private String imgUrl;
        private String intruduction;
        private String orderNum;

        public Builder() {}
        public Builder id(String val) {
            id = val;
            return this;
        }
        public Builder name(String val) {
            name = val;
            return this;
        }
        public Builder tel(String val) {
            tel = val;
            return this;
        }
        public Builder distance(String val) {
            distance = val;
            return this;
        }
        public Builder address(String val) {
            address = val;
            return this;
        }
        public Builder imgUrl(String val) {
            imgUrl = val;
            return this;
        }
        public Builder intruduction(String val) {
            intruduction = val;
            return this;
        }
        public Builder orderNum(String val) {
            orderNum = val;
            return this;
        }

        public BeautyParlorBean build(){
            return new BeautyParlorBean(this);
        }
    }
}

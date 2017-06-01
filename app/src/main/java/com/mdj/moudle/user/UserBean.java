package com.mdj.moudle.user;

import java.io.Serializable;

public class UserBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;
    private final String phone;
    private final String imgUrl;
    private final int availablePackageNum;
    private final int availableCouponNum;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public int getAvailablePackageNum() {
        return availablePackageNum;
    }

    public int getAvailableCouponNum() {
        return availableCouponNum;
    }

    public static class Builder {
        private String id;
        private String name;
        private String phone;
        private String imgUrl;
        private int availablePackageNum;
        private int availableCouponNum;

        public Builder(String val) {
            name = val;
        }
        public Builder id(String val) {
            id = val;
            return this;
        }
        public Builder phone(String val) {
            phone = val;
            return this;
        }
        public Builder imgUrl(String val) {
            imgUrl = val;
            return this;
        }
        public Builder availablePackageNum(int val) {
            availablePackageNum = val;
            return this;
        }
        public Builder availableCouponNum(int val) {
            availableCouponNum = val;
            return this;
        }
        public UserBean build() {
            return new UserBean(this);
        }
    }
    private UserBean(Builder b) {
        id = b.id;
        name = b.name;
        phone = b.phone;
        imgUrl = b.imgUrl;
        availableCouponNum = b.availableCouponNum;
        availablePackageNum = b.availablePackageNum;
    }
}

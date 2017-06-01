package com.mdj.moudle.coupon;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tt on 2016/6/17.
 */
public class CouponBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String price;//价格或折扣率
    private final String name;//优惠券的名字：体验券、满500可用、折扣券
    private final int type;//代金券；折扣券
    private final int state;//即将开启、即将过期、已使用、已过期、正常
    private final String fitableProject;//适用项目列表
    private final String fitableCity;//适用城市列表
    private final String expiredTime;//过期时间
    private final int serviceType;//上门、到店
    private final String remark;//注意事项
    private final int useScope;//使用范围

    public String getId() {
        return id;
    }

    public String getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public int getState() {
        return state;
    }

    public String getFitableProject() {
        return fitableProject;
    }

    public String getFitableCity() {
        return fitableCity;
    }

    public String getExpiredTime() {
        return expiredTime;
    }

    public int getServiceType() {
        return serviceType;
    }

    public String getRemark() {
        return remark;
    }

    public int getUseScope() {
        return useScope;
    }

    public static class Builder {
        private String id;
        private String price;//价格或折扣率
        private String name;//优惠券的名字：体验券、满500可用、折扣券
        private int type;//代金券；折扣券
        private int state;//即将开启、即将过期、已使用、已过期、正常
        private String fitableProject;//适用项目列表
        private String fitableCity;//适用城市列表
        private String expiredTime;//过期时间
        private int serviceType;//上门、到店
        private String remark;//注意事项
        private int useScope;//使用范围

        public Builder(String val) {
            name = val;
        }
        public Builder id(String val) {
            id = val;
            return this;
        }
        public Builder type(int val) {
            type = val;
            return this;
        }
        public Builder state(int val) {
            state = val;
            return this;
        }
        public Builder fitableProject(String val) {
            fitableProject = val;
            return this;
        }
        public Builder fitableCity(String val) {
            fitableCity = val;
            return this;
        }
        public Builder expiredTime(String val) {
            expiredTime = val;
            return this;
        }
        public Builder serviceType(int val) {
            serviceType = val;
            return this;
        }
        public Builder remark(String val) {
            remark = val;
            return this;
        }
        public Builder price(String val) {
            price = val;
            return this;
        }
        public Builder useScope(int val) {
            useScope = val;
            return this;
        }

        public CouponBean build() {
            return new CouponBean(this);
        }
    }
    private CouponBean(Builder b) {
        id = b.id;
        price = b.price;
        name = b.name;
        type = b.type;
        state = b.state;
        fitableProject = b.fitableProject;
        fitableCity = b.fitableCity;
        expiredTime = b.expiredTime;
        serviceType = b.serviceType;
        remark = b.remark;
        useScope = b.useScope;
    }
}

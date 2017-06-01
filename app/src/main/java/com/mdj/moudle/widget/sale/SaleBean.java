package com.mdj.moudle.widget.sale;

import java.io.Serializable;

/**
 * Created by tt on 2016/6/17.
 */
public class SaleBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private final String startTime;
    private final String endTime;
    private final int total;          //总数
    private final int soldNum;        //售出数量
    private final int perCount;       //限购数量
    private final int privilegePrice;//特惠价格

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getTotal() {
        return total;
    }

    public int getSoldNum() {
        return soldNum;
    }

    public int getPerCount() {
        return perCount;
    }

    public int getPrivilegePrice() {
        return privilegePrice;
    }

    public static class Builder {
        private String startTime;
        private String endTime;
        private int total;          //总数
        private int soldNum;        //售出数量
        private int perCount;       //限购数量
        private int privilegePrice;//特惠价格


        public Builder() { }
        public Builder startTime(String val) {
            startTime = val;
            return this;
        }
        public Builder endTime(String val) {
            endTime = val;
            return this;
        }
        public Builder total(int val) {
            total = val;
            return this;
        }
        public Builder soldNum(int val) {
            soldNum = val;
            return this;
        }
        public Builder perCount(int val) {
            perCount = val;
            return this;
        }
        public Builder privilegePrice(int val) {
            privilegePrice = val;
            return this;
        }

        public SaleBean build() {
            return new SaleBean(this);
        }
    }
    public SaleBean(Builder b) {
        startTime = b.startTime;
        endTime = b.endTime;
        total = b.total;
        soldNum = b.soldNum;
        perCount = b.perCount;
        privilegePrice = b.privilegePrice;
    }
}

package com.mdj.moudle.beautician.bean;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by tt on 2016/6/17.
 */
public class BeauticianBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;
    private final String imgUrl;
    private final String intruduction;
    private final String orderNum;               //订单数
    private final String goodAppraiseNum;       //好评数
    private final String experience;             //经验
    private final String department;      		//归属店名称
    private final Set<BeauticianTag> tagSet;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public String getGoodAppraiseNum() {
        return goodAppraiseNum;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getIntruduction() {
        return intruduction;
    }

    public String getExperience() {
        return experience;
    }

    public String getDepartment() {
        return department;
    }

    public Set<BeauticianTag> getTagSet() {
        return tagSet;
    }

    public static class Builder {
        private String id;
        private String name;
        private String imgUrl;
        private String intruduction;
        private String orderNum;
        private String goodAppraiseNum;
        private String experience;
        private String department;
        private Set<BeauticianTag> tagSet;

        public Builder(String val) {
            name = val;
        }
        public Builder id(String val) {
            id = val;
            return this;
        }
        public Builder orderNum(String val) {
            orderNum = val;
            return this;
        }
        public Builder goodAppraiseNum(String val) {
            goodAppraiseNum = val;
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
        public Builder experience(String val) {
            experience = val;
            return this;
        }

        public Builder department(String val) {
            department = val;
            return this;
        }
        public Builder tagSet(Set<BeauticianTag> val) {
            tagSet = val;
            return this;
        }
        public BeauticianBean build() {
            return new BeauticianBean(this);
        }
    }
    private BeauticianBean(Builder b) {
        id = b.id;
        name = b.name;
        orderNum = b.orderNum;
        goodAppraiseNum = b.goodAppraiseNum;
        imgUrl = b.imgUrl;
        intruduction = b.intruduction;
        experience = b.experience;
        department = b.department;
        tagSet = b.tagSet;
    }
}

package com.mdj.moudle.beautician.bean;

import java.io.Serializable;

/**
 * Created by tt on 2016/6/17.
 */
public class BeauticianCommentNumBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private final int goodNum;
    private final int middleNum;
    private final int badNum;

    public int getGoodNum() {
        return goodNum;
    }

    public int getMiddleNum() {
        return middleNum;
    }

    public int getBadNum() {
        return badNum;
    }

    public static class Builder {
        private int goodNum;
        private int middleNum;
        private int badNum;


        public Builder() {
        }
        public Builder goodNum(int val) {
            goodNum = val;
            return this;
        }
        public Builder middleNum(int val) {
            middleNum = val;
            return this;
        }
        public Builder badNum(int val) {
            badNum = val;
            return this;
        }


        public BeauticianCommentNumBean build() {
            return new BeauticianCommentNumBean(this);
        }
    }
    private BeauticianCommentNumBean(Builder b) {
        goodNum = b.goodNum;
        middleNum = b.middleNum;
        badNum = b.badNum;
    }
}

package com.mdj.moudle.beautician.bean;

import java.io.Serializable;

/**
 * Created by tt on 2016/6/17.
 */
public class BeauticianTag implements Serializable{
    private static final long serialVersionUID = 1L;

    private String tagName;
    private String num;

    public BeauticianTag(String tagName, String num) {
        this.tagName = tagName;
        this.num = num;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}

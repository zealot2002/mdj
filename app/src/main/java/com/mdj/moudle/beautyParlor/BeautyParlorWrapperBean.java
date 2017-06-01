package com.mdj.moudle.beautyParlor;

/**
 * Created by tt on 2016/6/21.
 */
public class BeautyParlorWrapperBean {
    private boolean isExpanded;
    private BeautyParlorBean beautyParlorBean;

    public BeautyParlorWrapperBean() {
//        this(false,new BeautyParlorBean());
    }

    public BeautyParlorWrapperBean(boolean isExpanded, BeautyParlorBean beautyParlorBean) {
        this.isExpanded = isExpanded;
        this.beautyParlorBean = beautyParlorBean;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public BeautyParlorBean getBeautyParlorBean() {
        return beautyParlorBean;
    }

    public void setBeautyParlorBean(BeautyParlorBean beautyParlorBean) {
        this.beautyParlorBean = beautyParlorBean;
    }
}

package com.mdj.moudle.widget.shoppingCart;

import java.io.Serializable;

/**
 * Created by tt on 2016/6/17.
 */
public class GoodsWrapperBean implements Serializable{
    public static final long serialVersionUID = 1L;
    private GoodsBean goodsBean;
    private int num;

    public GoodsWrapperBean(GoodsBean goodsBean, int num) {
        this.goodsBean = goodsBean;
        this.num = num;
    }

    public GoodsBean getGoodsBean() {
        return goodsBean;
    }

    public void setGoodsBean(GoodsBean goodsBean) {
        this.goodsBean = goodsBean;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}

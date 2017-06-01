package com.mdj.moudle.widget.shoppingCart;

import java.io.Serializable;

/**
 * Created by tt on 2016/6/17.
 */
public class GoodsBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;//名字
    private final int price;//价格
    private final int duration;//时长
    private final boolean isExtraProject;  //是否为附加项目 附加项目不可单独下单

    public String getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isExtraProject() {
        return isExtraProject;
    }

    public static class Builder {
        private String id;
        private String name;//名字
        private int price;//价格
        private int duration;//时长
        private boolean isExtraProject;  //是否为附加项目 附加项目不可单独下单

        public Builder(String val) {
            name = val;
        }
        public Builder id(String val) {
            id = val;
            return this;
        }
        public Builder price(int val) {
            price = val;
            return this;
        }
        public Builder duration(int val) {
            duration = val;
            return this;
        }
        public Builder isExtraProject(boolean val) {
            isExtraProject = val;
            return this;
        }
        public GoodsBean build() {
            return new GoodsBean(this);
        }
    }
    public GoodsBean(Builder b) {
        id = b.id;
        price = b.price;
        name = b.name;
        duration = b.duration;
        isExtraProject = b.isExtraProject;
    }
}

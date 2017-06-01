package com.mdj.moudle.order.bean;

/**
 * Created by tt on 2016/6/12.
 */
public class ProjectBean {
    private String name;
    private int duration;//时长
    private int price;

    public ProjectBean(){

    }
    public ProjectBean(String name, int duration, int price) {
        this.name = name;
        this.duration = duration;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String toString(){
        return "\n name : "+name+"\n duration : "+duration+"\n price : "+price;
    }
}

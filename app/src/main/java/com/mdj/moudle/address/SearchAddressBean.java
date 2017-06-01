package com.mdj.moudle.address;

import java.io.Serializable;

/**
 * Created by tt on 2016/6/12.
 */
public class SearchAddressBean implements Serializable{
    private String name;//地址名称
    private String address;//详细地址

    public SearchAddressBean(){
        this("","");
    }

    public SearchAddressBean(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

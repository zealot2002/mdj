package com.mdj.moudle;

import org.apache.http.cookie.Cookie;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by tt on 2016/4/19.
 */
public class MdjCookie implements Serializable, Cookie {
    private static final long serialVersionUID = 1L;
    private String value,name,domain,path;
    private Date expiryDate;
    public MdjCookie(String name, String value){
        this(name, value, null, null, null);
    }

    public MdjCookie(String name, String value, String domain, String path, Date expiryDate) {
        this.value = value;
        this.name = name;
        this.domain = domain;
        this.path = path;
        this.expiryDate = expiryDate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public String getCommentURL() {
        return null;
    }

    @Override
    public Date getExpiryDate() {
        return expiryDate;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public int[] getPorts() {
        return new int[0];
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public boolean isExpired(Date date) {
        return false;
    }
}

package com.mdj.moudle.order.serviceHour;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by tt on 2016/6/6.
 */
public class ServiceHourBean implements Serializable{
    private static final long serialVersionUID = 1420672609912364060L;
    private String week;
    private String date;
    private HashSet<String> availableHours;  //可用时段     transient to fix unSerializableException!
    private String selectedHour;

    public ServiceHourBean() {
        this("","",new HashSet<String>());
    }

    public ServiceHourBean(String week, String date, HashSet<String> availableHours) {
        this(week,date,availableHours,"");
    }

    public ServiceHourBean(String week, String date, HashSet<String> availableHours, String selectedHour) {
        this.week = week;
        this.date = date;
        this.availableHours = availableHours;
        this.selectedHour = selectedHour;
    }

    public void cloneObj(ServiceHourBean serviceHourBean){
        this.week = serviceHourBean.getWeek();
        this.date = serviceHourBean.getDate();
        this.availableHours = serviceHourBean.getAvailableHours();
        this.selectedHour = serviceHourBean.getSelectedHour();
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public HashSet<String> getAvailableHours() {
        return availableHours;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setAvailableHours(HashSet<String> availableHours) {
        this.availableHours = availableHours;
    }

    public String getSelectedHour() {
        return selectedHour;
    }

    public void setSelectedHour(String selectedHour) {
        this.selectedHour = selectedHour;
    }

    public void clear() {
        this.week = "";
        this.date = "";
        this.availableHours = null;
        this.selectedHour = "";
    }
}

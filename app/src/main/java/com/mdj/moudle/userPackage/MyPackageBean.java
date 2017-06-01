package com.mdj.moudle.userPackage;

import com.mdj.moudle.referee.RefereeBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tt on 2015/11/4.
 */
public class MyPackageBean implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum PACKAGE_STATUS {
        NORMAL, // 正常
        NOT_PAY, // 未支付
        CANCELED,// 已取消
        EXPIRED, // 已过期
        ALL_USED// 已用完
    }

    private String id;// id
    private String price;// 价格
    private String imgUrl;// 图片url
    private String name;// 名字
    private String orderSn;// 订单号
    private String orderCreateTime;// 下单时间
    private String payStatus;// 支付状态 0:等待支付 1:支付成功 2:支付失败 3:退款
    private String expireTime;// 过期时间
    private int leftDays;   //剩余有效天数 -1:永久有效  0:已过期 N:剩余N天

    private String payType;
    private int showSetReferee;//是否显示关联推荐人按钮
    private RefereeBean refereeBean = new RefereeBean();
    /** 1:支付宝 2:微信 3:百度支付 **/

    private List<ProjectBean> projectList;
    private List<CityBean> cityList = new ArrayList<>();

    private boolean limitBeautyParlor;
    private String homeRefereeId;// 门店关联id

    /*********************************************************************************************************/

    public RefereeBean getRefereeBean() {
        return refereeBean;
    }

    public void setRefereeBean(RefereeBean refereeBean) {
        this.refereeBean = refereeBean;
    }
    public int getShowSetReferee() {
        return showSetReferee;
    }

    public void setShowSetReferee(int showSetReferee) {
        this.showSetReferee = showSetReferee;
    }

    public MyPackageBean() {
        projectList = new ArrayList<>();
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(String orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public List<ProjectBean> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<ProjectBean> projectList) {
        this.projectList = projectList;
    }

    public List<CityBean> getCityList() {
        return cityList;
    }

    public void setCityList(List<CityBean> cityList) {
        this.cityList = cityList;
    }

    public boolean isLimitBeautyParlor() {
        return limitBeautyParlor;
    }

    public void setLimitBeautyParlor(boolean limitBeautyParlor) {
        this.limitBeautyParlor = limitBeautyParlor;
    }

    public String getHomeRefereeId() {
        return homeRefereeId;
    }

    public void setHomeRefereeId(String homeRefereeId) {
        this.homeRefereeId = homeRefereeId;
    }

    public int getLeftDays() {
        return leftDays;
    }

    public void setLeftDays(int leftDays) {
        this.leftDays = leftDays;
    }

    public final class ProjectBean implements Serializable {
        private static final long serialVersionUID = 1L;
        private String id;
        private String name;// 名称
        private int duration;
        private int price;
        private String leftTimes;// 剩余次数
        private String totalTimes;// 总次数
        private String serviceType;// 项目的服务类型


        public ProjectBean(String id, String name, String leftTimes, String totalTimes, String serviceType,int duration,int price) {
            this.id = id;
            this.name = name;
            this.leftTimes = leftTimes;
            this.totalTimes = totalTimes;
            this.serviceType = serviceType;
            this.duration = duration;
            this.price = price;
        }

        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLeftTimes() {
            return leftTimes;
        }

        public void setLeftTimes(String leftTimes) {
            this.leftTimes = leftTimes;
        }

        public String getTotalTimes() {
            return totalTimes;
        }

        public void setTotalTimes(String totalTimes) {
            this.totalTimes = totalTimes;
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


    }
    public final class CityBean implements Serializable {
        private static final long serialVersionUID = 1L;
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }// 名称
    }
}

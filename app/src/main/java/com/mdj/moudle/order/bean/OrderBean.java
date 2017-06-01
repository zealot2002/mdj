package com.mdj.moudle.order.bean;

import com.mdj.moudle.address.AddressBean;
import com.mdj.moudle.beautyParlor.BeautyParlorBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tt on 2016/6/17.
 */
public class OrderBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private final String id;//订单号
    private final String createTime;//下单时间
    private final int state;//未支付、
    private final int serviceType;//上门、到店
    private final String beauticianId;//美容师id
    private final String beauticianName;//美容师姓名
    private final String beauticianImgUrl;//美容师头像
    private final String serviceStartTime;//服务开始时间
    private final String serviceEndTime;//服务结束时间
    private final String price;//价格
    private final String payType;//支付类型
    private final String cityId;
    private final AddressBean addressBean;
    private final BeautyParlorBean beautyParlorBean;
    private final int isShowReferee;//当前是否显示关联推荐人button
    private final int cancelAdd;//当前是否允许取消或是追加订单
    private final List<String> projectList;//项目列表
    private final String projectListStr;//项目列表字符串，为了适配某个老接口，一般情况应使用List<String> projectList
    private final String remarks;
    private final String orderStatusRemark; //对于每一个state，都有对应的orderStatusRemark作为说明
    private final String mainProjectId; //主项目id

    public String getId() {
        return id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public int getState() {
        return state;
    }

    public List<String> getProjectList() {
        return projectList;
    }

    public int getServiceType() {
        return serviceType;
    }

    public String getBeauticianName() {
        return beauticianName;
    }

    public String getBeauticianImgUrl() {
        return beauticianImgUrl;
    }

    public String getServiceStartTime() {
        return serviceStartTime;
    }

    public String getPrice() {
        return price;
    }

    public String getPayType() {
        return payType;
    }

    public String getCityId() {
        return cityId;
    }

    public String getBeauticianId() {
        return beauticianId;
    }

    public String getServiceEndTime() {
        return serviceEndTime;
    }

    public AddressBean getAddressBean() {
        return addressBean;
    }

    public BeautyParlorBean getBeautyParlorBean() {
        return beautyParlorBean;
    }

    public int getIsShowReferee() {
        return isShowReferee;
    }

    public int getCancelAdd() {
        return cancelAdd;
    }

    public String getProjectListStr() {
        return projectListStr;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getOrderStatusRemark() {
        return orderStatusRemark;
    }

    public String getMainProjectId() {
        return mainProjectId;
    }

    public static class Builder {
        private String id;
        private String createTime;//下单时间
        private int state;//未支付、
        private int serviceType;//上门、到店
        private String beauticianId;//美容师id
        private String beauticianName;//美容师姓名
        private String beauticianImgUrl;//美容师头像
        private String serviceStartTime;//服务开始时间
        private String serviceEndTime;//服务结束时间
        private String price;//价格
        private String payType;//支付类型
        private String cityId;
        private AddressBean addressBean;
        private BeautyParlorBean beautyParlorBean;
        private int isShowReferee;//是否显示关联推荐人button
        private int cancelAdd;//当前是否允许取消或是追加订单
        private List<String> projectList;//项目列表
        private String projectListStr;//项目列表字符串，为了适配某个老接口，一般情况应使用List<String> projectList
        private String remarks;
        private String orderStatusRemark; //对于每一个state，都有对应的orderStatusRemark作为说明
        private String mainProjectId; //主项目id

        public Builder(String val) {
            id = val;
        }
        public Builder createTime(String val) {
            createTime = val;
            return this;
        }
        public Builder state(int val) {
            state = val;
            return this;
        }
        public Builder serviceType(int val) {
            serviceType = val;
            return this;
        }
        public Builder projectList(List<String> val) {
            projectList = val;
            return this;
        }
        public Builder projectListStr(String val) {
            projectListStr = val;
            return this;
        }

        public Builder beauticianId(String val) {
            beauticianId = val;
            return this;
        }
        public Builder beauticianName(String val) {
            beauticianName = val;
            return this;
        }
        public Builder beauticianImgUrl(String val) {
            beauticianImgUrl = val;
            return this;
        }
        public Builder serviceStartTime(String val) {
            serviceStartTime = val;
            return this;
        }
        public Builder serviceEndTime(String val) {
            serviceEndTime = val;
            return this;
        }
        public Builder price(String val) {
            price = val;
            return this;
        }

        public Builder payType(String val) {
            payType = val;
            return this;
        }
        public Builder cityId(String val) {
            cityId = val;
            return this;
        }

        public Builder addressBean(AddressBean val) {
            addressBean = val;
            return this;
        }
        public Builder beautyParlorBean(BeautyParlorBean val) {
            beautyParlorBean = val;
            return this;
        }
        public Builder isShowReferee(int val) {
            isShowReferee = val;
            return this;
        }
        public Builder cancelAdd(int val) {
            cancelAdd = val;
            return this;
        }
        public Builder remarks(String val) {
            remarks = val;
            return this;
        }
        public Builder orderStatusRemark(String val) {
            orderStatusRemark = val;
            return this;
        }
        public Builder mainProjectId(String val) {
            mainProjectId = val;
            return this;
        }
        public OrderBean build() {
            return new OrderBean(this);
        }
    }
    private OrderBean(Builder b) {
        id = b.id;
        createTime = b.createTime;
        state = b.state;
        projectList = b.projectList;
        projectListStr = b.projectListStr;
        serviceType = b.serviceType;
        beauticianId = b.beauticianId;
        beauticianName = b.beauticianName;
        beauticianImgUrl = b.beauticianImgUrl;
        serviceStartTime = b.serviceStartTime;
        serviceEndTime = b.serviceEndTime;
        price = b.price;
        payType = b.payType;
        cityId = b.cityId;
        addressBean = b.addressBean;
        beautyParlorBean = b.beautyParlorBean;
        isShowReferee = b.isShowReferee;
        cancelAdd = b.cancelAdd;
        remarks = b.remarks;
        orderStatusRemark = b.orderStatusRemark;
        mainProjectId = b.mainProjectId;
    }
}

package com.mdj.moudle.order.bean;

import java.io.Serializable;

/**
 * Created by tt on 2016/8/24.
 */
public class RecommendCouponAndPackageBean {
    private PackageInfo packageInfo;
    private CouponInfo couponInfo;

    public RecommendCouponAndPackageBean() {
        packageInfo = new PackageInfo();
        couponInfo = new CouponInfo();
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public CouponInfo getCouponInfo() {
        return couponInfo;
    }

    public void setCouponInfo(CouponInfo couponInfo) {
        this.couponInfo = couponInfo;
    }

    public class CouponInfo implements Serializable {
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
        }

    }
    public class PackageInfo implements Serializable {
        private static final long serialVersionUID = 2982590774908941894L;
        private String detail;
        private int num;

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

    }
}

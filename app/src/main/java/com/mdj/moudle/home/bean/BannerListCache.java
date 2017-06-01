package com.mdj.moudle.home.bean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class BannerListCache implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<BannerBean> bannerList;

	public BannerListCache(){
		bannerList = new ArrayList<>();
	}

    public List<BannerBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BannerBean> bannerList) {
        this.bannerList = bannerList;

    }
}

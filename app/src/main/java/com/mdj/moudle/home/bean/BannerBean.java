package com.mdj.moudle.home.bean;

import java.io.Serializable;

public class BannerBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String title;
	private String imgUrl;
	private String linkUrl;

	private String shareContent;
	private String shareImageUrl;
	private String shareTargetUrl;

    public BannerBean(){}
    public BannerBean(String title, String imgUrl, String linkUrl, String shareContent, String shareImageUrl, String shareTargetUrl) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.linkUrl = linkUrl;
        this.shareContent = shareContent;
        this.shareImageUrl = shareImageUrl;
        this.shareTargetUrl = shareTargetUrl;
    }

    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getShareContent() {
		return shareContent;
	}

	public void setShareContent(String shareContent) {
		this.shareContent = shareContent;
	}

	public String getShareImageUrl() {
		return shareImageUrl;
	}

	public void setShareImageUrl(String shareImageUrl) {
		this.shareImageUrl = shareImageUrl;
	}

	public String getShareTargetUrl() {
		return shareTargetUrl;
	}

	public void setShareTargetUrl(String shareTargetUrl) {
		this.shareTargetUrl = shareTargetUrl;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

package com.push;

import java.io.Serializable;

/**
 * 友盟后台推送过来的消息 com.vmei.bean-- 365vmei DATE: 2015-6-16下午3:32:56
 * 
 * @author JALEN
 * @MAIL:c9n9m@163.com TODO
 */
public class YoumengMsg implements Serializable {
	private static final long serialVersionUID = 29734032876777379L;
	private String className;
	/** 跳转到WEB界面的标题 */
	private String title;
	/** 跳转到不同界面标识 0:跳转到WEB界面 1:跳转到订单详情界面 */
	private int flag;
	/** 订单号 */
	private String orderSn;

	/** 分享页面展示 */
	private String link_url;
	/** 分享标题 */
	private String share_title;
	/** 分享文本 */
	private String share_content;
	/** 分享图片 */
	private String share_image_url;
	/** 分享链接 */
	private String share_target_url;

	public String getLink_url() {
		return link_url;
	}

	public void setLink_url(String link_url) {
		this.link_url = link_url;
	}

	public String getShare_title() {
		return share_title;
	}

	public void setShare_title(String share_title) {
		this.share_title = share_title;
	}

	public String getShare_content() {
		return share_content;
	}

	public void setShare_content(String share_content) {
		this.share_content = share_content;
	}

	public String getShare_image_url() {
		return share_image_url;
	}

	public void setShare_image_url(String share_image_url) {
		this.share_image_url = share_image_url;
	}

	public String getShare_target_url() {
		return share_target_url;
	}

	public void setShare_target_url(String share_target_url) {
		this.share_target_url = share_target_url;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOrderSn() {
		return orderSn;
	}

	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}

	@Override
	public String toString() {
		return "YoumengMsg [className=" + className + ", title=" + title + ", flag=" + flag + ", orderSn=" + orderSn + ", link_url=" + link_url
				+ ", share_title=" + share_title + ", share_content=" + share_content + ", share_image_url=" + share_image_url + ", share_target_url="
				+ share_target_url + "]";
	}

}

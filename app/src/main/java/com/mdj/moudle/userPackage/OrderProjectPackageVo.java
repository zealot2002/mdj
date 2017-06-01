package com.mdj.moudle.userPackage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class OrderProjectPackageVo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;//id
	private String name;//名称
	private int buyNum;//购买数量
	private int freeNum;//抵用数量
	private String imgUrl;//图片url
	private List<PackageUseForOrderProjectVo> packageUseForOrderProjectVoList;
	
	public OrderProjectPackageVo(){
		packageUseForOrderProjectVoList = new ArrayList<>();
	}

	public final class PackageUseForOrderProjectVo implements Serializable{
		private static final long serialVersionUID = 1L;
		private String id;
		private String orderId;
		private String name;
		private int availableNum;//可用次数
		private int allocNum;//推荐次数、当前分配次数
		private int validDuration;//有效天数
	
		public PackageUseForOrderProjectVo(String id, String name,
                                           int availableNum, int allocNum, int validDuration){
			this.id = id;
			this.name = name;
			this.availableNum = availableNum;
			this.allocNum = allocNum;
			this.validDuration = validDuration;
		}

		public PackageUseForOrderProjectVo() {
		}
		
		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
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

		public int getAvailableNum() {
			return availableNum;
		}

		public void setAvailableNum(int availableNum) {
			this.availableNum = availableNum;
		}

		public int getAllocNum() {
			return allocNum;
		}

		public void setAllocNum(int allocNum) {
			this.allocNum = allocNum;
		}

		public int getValidDuration() {
			return validDuration;
		}

		public void setValidDuration(int validDuration) {
			this.validDuration = validDuration;
		}
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

	public int getBuyNum() {
		return buyNum;
	}

	public void setBuyNum(int buyNum) {
		this.buyNum = buyNum;
	}

	public int getFreeNum() {
		return freeNum;
	}

	public void setFreeNum(int freeNum) {
		this.freeNum = freeNum;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public List<PackageUseForOrderProjectVo> getPackageUseForOrderProjectVoList() {
		return packageUseForOrderProjectVoList;
	}

	public void setPackageUseForOrderProjectVoList(
            List<PackageUseForOrderProjectVo> packageUseForOrderProjectVoList) {
		this.packageUseForOrderProjectVoList = packageUseForOrderProjectVoList;
	}

}

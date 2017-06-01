package com.mdj.moudle.project.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tt on 2015/11/4.
 */
public class ProjectBean implements Serializable {
	private static final long serialVersionUID = -1678754010780229655L;
	private String id;
	private String name;// 名称
	private String efficiency;// 功效
    private List<String> efficiencySet = new ArrayList<>();// 功效
	private Integer inHomeDuration;// 上门时长：单位：分钟（当为套餐时，单位为天）
	private Integer toShopDuration;// 到店时长：单位：分钟
    private Integer duration;// 时长：单位：分钟
	private Integer inHomePrice;// 上门价格
	private Integer toShopPrice;// 到店价格
	private Integer oldPrice;// 原价
    private Integer price;// 现价
	private Integer privilegePrice;//特惠价格
	private Integer recommendNum;// 推荐人数
	private String startTime;// 开始时间
	private String endTime;// 结束时间
	private Integer total;// 总数
	private Integer soldNum;// 售出数
	private Integer type;// 类型： 1：普通，2：特惠，3：限时限量
	private Integer serviceType;//服务类型1：上门，2：到店，3：上门到店
	private String imageUrl;
    private Integer perCount;

	private String inHomeServiceRemark;//服务说明（上门）
	private String toShopServiceRemark;//服务说明（到店）

    private boolean isExtraProject;  //是否为附加项目 附加项目不可单独下单

    /*项目详情使用*/
    private String originPlace;
    private int totalOrderNum;
    private List<ProjectQualityBean> projectQualityList;//质量说明
    private List<String> projectTagList; //亮点
    private List<String> extraImgUrlList;
    private String remarksTitle;
    private String remarks;
    private int assessmentNum; /*评论数量*/

/*****************************************************************************************************/
    public ProjectBean(){
        projectQualityList = new ArrayList<>();
        projectTagList = new ArrayList<>();
        extraImgUrlList = new ArrayList<>();
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

	public String getEfficiency() {
		return efficiency;
	}

	public void setEfficiency(String efficiency) {
		this.efficiency = efficiency;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Integer getInHomeDuration() {
		return inHomeDuration;
	}

	public void setInHomeDuration(Integer inHomeDuration) {
		this.inHomeDuration = inHomeDuration;
	}

	public Integer getToShopDuration() {
		return toShopDuration;
	}

	public void setToShopDuration(Integer toShopDuration) {
		this.toShopDuration = toShopDuration;
	}

	public Integer getInHomePrice() {
		return inHomePrice;
	}

	public void setInHomePrice(Integer inHomePrice) {
		this.inHomePrice = inHomePrice;
	}

	public Integer getToShopPrice() {
		return toShopPrice;
	}

	public void setToShopPrice(Integer toShopPrice) {
		this.toShopPrice = toShopPrice;
	}

	public Integer getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(Integer oldPrice) {
		this.oldPrice = oldPrice;
	}

	public Integer getPrivilegePrice() {
		return privilegePrice;
	}

	public void setPrivilegePrice(Integer privilegePrice) {
		this.privilegePrice = privilegePrice;
	}

	public Integer getRecommendNum() {
		return recommendNum;
	}

	public void setRecommendNum(Integer recommendNum) {
		this.recommendNum = recommendNum;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getSoldNum() {
		return soldNum;
	}

	public void setSoldNum(Integer soldNum) {
		this.soldNum = soldNum;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getServiceType() {
		return serviceType;
	}

	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}

	public String getInHomeServiceRemark() {
		return inHomeServiceRemark;
	}

	public void setInHomeServiceRemark(String inHomeServiceRemark) {
		this.inHomeServiceRemark = inHomeServiceRemark;
	}

	public String getToShopServiceRemark() {
		return toShopServiceRemark;
	}

	public void setToShopServiceRemark(String toShopServiceRemark) {
		this.toShopServiceRemark = toShopServiceRemark;
	}

    public List<String> getEfficiencySet() {
        return efficiencySet;
    }

    public void setEfficiencySet(List<String> efficiencySet) {
        this.efficiencySet = efficiencySet;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public boolean isExtraProject() {
        return isExtraProject;
    }

    public void setIsExtraProject(boolean isExtraProject) {
        this.isExtraProject = isExtraProject;
    }

    public List<String> getExtraImgUrlList() {
        return extraImgUrlList;
    }

    public void setExtraImgUrlList(List<String> extraImgUrlList) {
        this.extraImgUrlList = extraImgUrlList;
    }

    public String getOriginPlace() {
        return originPlace;
    }

    public void setOriginPlace(String originPlace) {
        this.originPlace = originPlace;
    }

    public int getTotalOrderNum() {
        return totalOrderNum;
    }

    public void setTotalOrderNum(int totalOrderNum) {
        this.totalOrderNum = totalOrderNum;
    }

    public List<String> getProjectTagList() {
        return projectTagList;
    }

    public void setProjectTagList(List<String> projectTagList) {
        this.projectTagList = projectTagList;
    }

    public List<ProjectQualityBean> getProjectQualityList() {
        return projectQualityList;
    }

    public void setProjectQualityList(List<ProjectQualityBean> projectQualityList) {
        this.projectQualityList = projectQualityList;
    }

    public String getRemarksTitle() {
        return remarksTitle;
    }

    public void setRemarksTitle(String remarksTitle) {
        this.remarksTitle = remarksTitle;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getAssessmentNum() {
        return assessmentNum;
    }

    public void setAssessmentNum(int assessmentNum) {
        this.assessmentNum = assessmentNum;
    }

    public Integer getPerCount() {
        return perCount;
    }

    public void setPerCount(Integer perCount) {
        this.perCount = perCount;
    }
}

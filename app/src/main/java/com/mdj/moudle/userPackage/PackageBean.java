package com.mdj.moudle.userPackage;

import com.mdj.moudle.project.bean.ProjectQualityBean;
import com.mdj.moudle.project.bean.ProjectWrapperBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class PackageBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;// id
    private int price;// 价格
    private int oldPrice;// 价格
    private String imgUrl;// 图片url
    private String name;// 名字
    private String validityDays;//有效期
    private List<String> effectList;
    private List<ProjectWrapperBean> projectList;

    private String startTime;// 开始时间
    private String endTime;// 结束时间
    private Integer total;// 总数
    private Integer soldNum;// 售出数
    private Integer privilegePrice;//特惠价格
    private Integer type;// 类型： 1：普通，2：特惠，3：限时限量
    private Integer perCount;
    /*项目详情使用*/
    private String originPlace;
    private int totalOrderNum;
    private List<ProjectQualityBean> qualityList;//质量说明
    private List<String> tagList; //亮点
    private List<String> extraImgUrlList;
    private String remarksTitle;
    private String remarks;
    private String guideHtml;
    private int SaveMoney;
/**********************************************************************************************************/
    public PackageBean() {
        projectList = new ArrayList<>();
        effectList = new ArrayList<>();
        qualityList = new ArrayList<>();
        extraImgUrlList = new ArrayList<>();
        tagList = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(int oldPrice) {
        this.oldPrice = oldPrice;
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

    public String getValidityDays() {
        return validityDays;
    }

    public void setValidityDays(String validityDays) {
        this.validityDays = validityDays;
    }

    public List<String> getEffectList() {
        return effectList;
    }

    public void setEffectList(List<String> effectList) {
        this.effectList = effectList;
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

    public List<ProjectQualityBean> getQualityList() {
        return qualityList;
    }

    public void setQualityList(List<ProjectQualityBean> qualityList) {
        this.qualityList = qualityList;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public List<String> getExtraImgUrlList() {
        return extraImgUrlList;
    }

    public void setExtraImgUrlList(List<String> extraImgUrlList) {
        this.extraImgUrlList = extraImgUrlList;
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

    public List<ProjectWrapperBean> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<ProjectWrapperBean> projectList) {
        this.projectList = projectList;
    }

    public String getGuideHtml() {
        return guideHtml;
    }

    public void setGuideHtml(String guideHtml) {
        this.guideHtml = guideHtml;
    }

    public int getSaveMoney() {
        return SaveMoney;
    }

    public void setSaveMoney(int saveMoney) {
        SaveMoney = saveMoney;
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

    public Integer getPrivilegePrice() {
        return privilegePrice;
    }

    public void setPrivilegePrice(Integer privilegePrice) {
        this.privilegePrice = privilegePrice;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPerCount() {
        return perCount;
    }

    public void setPerCount(Integer perCount) {
        this.perCount = perCount;
    }
}

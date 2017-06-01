package com.mdj.moudle.mine.comment;

import java.util.List;

/**
 * @author 吴世文
 * @Description:
 */
public class CommentBean {

    /**
     * goodsImpressionList : ["态度好","颜值高","手法专业","效果好","体验舒适","环境舒适","设备齐全","位置好找"]
     * middleImpressionList : ["态度好","颜值高","手法专业","效果好","体验舒适","环境舒适","设备齐全","位置好找"]
     * badImpressionList : ["态度好","颜值高","手法专业","效果好","体验舒适","环境舒适","设备齐全","位置好找"]
     * serviceType :  1上门 3到店
     * "content":"1450415618774",
     "shopscore":"5",
     "servicelevel":"1",
     "isAnonymous":0,
     */

    private String serviceType;
    private String content;
    private String shopscore;
    private String serviceleve;
    private boolean isAnonymous;
    private String projectName;

    private List<String> impressionList;
    private List<String> goodsImpressionList;
    private List<String> middleImpressionList;
    private List<String> badImpressionList;

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getShopscore() {
        return shopscore;
    }

    public void setShopscore(String shopscore) {
        this.shopscore = shopscore;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getServiceleve() {
        return serviceleve;
    }

    public void setServiceleve(String serviceleve) {
        this.serviceleve = serviceleve;
    }

    public List<String> getGoodsImpressionList() {
        return goodsImpressionList;
    }

    public void setGoodsImpressionList(List<String> goodsImpressionList) {
        this.goodsImpressionList = goodsImpressionList;
    }

    public List<String> getMiddleImpressionList() {
        return middleImpressionList;
    }

    public void setMiddleImpressionList(List<String> middleImpressionList) {
        this.middleImpressionList = middleImpressionList;
    }

    public List<String> getBadImpressionList() {
        return badImpressionList;
    }

    public void setBadImpressionList(List<String> badImpressionList) {
        this.badImpressionList = badImpressionList;
    }

    public List<String> getImpressionList() {
        return impressionList;
    }

    public void setImpressionList(List<String> impressionList) {
        this.impressionList = impressionList;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}

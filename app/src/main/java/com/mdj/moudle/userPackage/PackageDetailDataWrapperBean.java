package com.mdj.moudle.userPackage;


import com.mdj.moudle.project.bean.ProjectWrapperBean;

import java.io.Serializable;

public class PackageDetailDataWrapperBean implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int TYPE_PROJECT = 0;
    public static final int TYPE_PIC = 1;
    public static final int TYPE_MAX_COUNT = TYPE_PIC + 1;

    private int dataType;
    private String imageUrl;
    private ProjectWrapperBean projectWapperBean = new ProjectWrapperBean();

/*********************************************************************************************************/
    public PackageDetailDataWrapperBean(){}
    public PackageDetailDataWrapperBean(int dataType, String imageUrl){
        this.dataType = dataType;
        this.imageUrl = imageUrl;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ProjectWrapperBean getProjectWapperBean() {
        return projectWapperBean;
    }

    public void setProjectWapperBean(ProjectWrapperBean projectWapperBean) {
        this.projectWapperBean = projectWapperBean;
    }
}

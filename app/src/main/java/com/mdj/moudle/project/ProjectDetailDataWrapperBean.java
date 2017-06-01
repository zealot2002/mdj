package com.mdj.moudle.project;

import com.mdj.moudle.project.bean.CommentBean;

import java.io.Serializable;

public class ProjectDetailDataWrapperBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private int dataType;
    private String imageUrl;
    private CommentBean commentBean;

/*********************************************************************************************************/
    public ProjectDetailDataWrapperBean(){}
    public ProjectDetailDataWrapperBean(int dataType,String imageUrl){
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

    public CommentBean getCommentBean() {
        return commentBean;
    }

    public void setCommentBean(CommentBean commentBean) {
        this.commentBean = commentBean;
    }
}

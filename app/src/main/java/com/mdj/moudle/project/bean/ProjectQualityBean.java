package com.mdj.moudle.project.bean;

import java.io.Serializable;

public class ProjectQualityBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String imageUrl;


/*****************************************************************************************************/
    public ProjectQualityBean(){}

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

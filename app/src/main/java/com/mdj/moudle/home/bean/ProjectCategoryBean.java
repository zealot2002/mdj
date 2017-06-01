package com.mdj.moudle.home.bean;

import java.io.Serializable;

public class ProjectCategoryBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String id;

    public ProjectCategoryBean(){}
    public ProjectCategoryBean(String name,String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}

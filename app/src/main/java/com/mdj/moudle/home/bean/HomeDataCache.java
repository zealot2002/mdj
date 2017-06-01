package com.mdj.moudle.home.bean;
import com.mdj.moudle.project.bean.ProjectBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
* 上门、到店，需要2个cache
*
* */
public final class HomeDataCache implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<ProjectCategoryBean> projectCategoryList;
    private List<ProjectBean> suggestProjectList;

	public HomeDataCache(){
        projectCategoryList = new ArrayList<>();
        suggestProjectList = new ArrayList<>();
	}

    public List<ProjectCategoryBean> getProjectCategoryList() {
        return projectCategoryList;
    }

    public void setProjectCategoryList(List<ProjectCategoryBean> projectCategoryList) {
        this.projectCategoryList = projectCategoryList;
    }

    public List<ProjectBean> getSuggestProjectList() {
        return suggestProjectList;
    }

    public void setSuggestProjectList(List<ProjectBean> suggestProjectList) {
        this.suggestProjectList = suggestProjectList;
    }

    public void clear() {
        projectCategoryList.clear();
        suggestProjectList.clear();
    }
}

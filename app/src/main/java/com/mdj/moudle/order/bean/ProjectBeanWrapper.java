package com.mdj.moudle.order.bean;

import com.mdj.moudle.order.bean.ProjectBean;

/**
 * Created by tt on 2016/6/12.
 */
public class ProjectBeanWrapper {
    private int num;
    private ProjectBean projectBean;

    public ProjectBeanWrapper(){
        this(0,new ProjectBean());
    }
    public ProjectBeanWrapper(int num, ProjectBean projectBean) {
        this.num = num;
        this.projectBean = projectBean;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public ProjectBean getProjectBean() {
        return projectBean;
    }

    public void setProjectBean(ProjectBean projectBean) {
        this.projectBean = projectBean;
    }
}

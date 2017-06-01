package com.mdj.moudle.home.bean;

import com.mdj.moudle.project.bean.ProjectBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
* 上门、到店，需要2个cache
* */
public final class TabProjectListCache implements Serializable{
	private static final long serialVersionUID = 1L;
	private Map<Integer,TabProjectListWrapper> tabProjectListMap = new HashMap<>();
	
	public TabProjectListCache(){

	}

	public Map<Integer,TabProjectListWrapper> getTabProjectListMap() {
		return tabProjectListMap;
	}

	public void setTabProjectListMap(Map<Integer,TabProjectListWrapper> tabProjectListMap) {
		this.tabProjectListMap = tabProjectListMap;
	}
	public void clear(){
		this.tabProjectListMap.clear();
	}

    public void deepCloneMap(Map<Integer,TabProjectListWrapper> src){
        clear();
        for(Iterator it=src.keySet().iterator();it.hasNext();){
            int key=(int)it.next();
            TabProjectListWrapper value=src.get(key);
            tabProjectListMap.put((int)key,value);
        }
    }

    public static class TabProjectListWrapper implements Serializable{
        private static final long serialVersionUID = 1L;
        public List<ProjectBean> projectList = new ArrayList<>();
        public int pageIndex;
        public boolean isNoMoreData = false;
    }
}

package com.mdj.moudle.beautician.bean;

import java.io.Serializable;

/**
 * Created by tt on 2016/6/17.
 */
public class CommentBean implements Serializable{
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String userName;//用户姓名
    private final String userHeaderUrl;//用户头像
    private final String content;//评价内容
    private final String time;//评价时间
    private final String projectName;//项目名称


    public String getProjectName() {
        return projectName;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserHeaderUrl() {
        return userHeaderUrl;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public static class Builder {
        private String id;
        private String userName;//用户姓名
        private String userHeaderUrl;//用户头像
        private String content;//评价内容
        private String time;//评价时间
        private String projectName;//项目名称


        public Builder(String val) {
            id = val;
        }
        public Builder userName(String val) {
            userName = val;
            return this;
        }
        public Builder userHeaderUrl(String val) {
            userHeaderUrl = val;
            return this;
        }
        public Builder content(String val) {
            content = val;
            return this;
        }

        public Builder time(String val) {
            time = val;
            return this;
        }
        public Builder projectName(String val) {
            projectName = val;
            return this;
        }

        public CommentBean build() {
            return new CommentBean(this);
        }
    }
    private CommentBean(Builder b) {
        id = b.id;
        userName = b.userName;
        userHeaderUrl = b.userHeaderUrl;
        content = b.content;
        time = b.time;
        projectName = b.projectName;
    }
}

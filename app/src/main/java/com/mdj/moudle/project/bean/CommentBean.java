package com.mdj.moudle.project.bean;

import java.io.Serializable;

public class CommentBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String userName;
    private final String userImageUrl;
    private final String content;
    private final String createTime;
    private final String beauticianName;
    private final String beauticianImageUrl;

    public String getUserName() {
        return userName;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public String getContent() {
        return content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getBeauticianName() {
        return beauticianName;
    }

    public String getBeauticianImageUrl() {
        return beauticianImageUrl;
    }

    public static class Builder {
        private String userName;
        private String userImageUrl;
        private String content;
        private String createTime;
        private String beauticianName;
        private String beauticianImageUrl;

        public Builder() {}
        public Builder userName(String val) {
            userName = val;
            return this;
        }
        public Builder userImageUrl(String val) {
            userImageUrl = val;
            return this;
        }
        public Builder content(String val) {
            content = val;
            return this;
        }
        public Builder createTime(String val) {
            createTime = val;
            return this;
        }
        public Builder beauticianName(String val) {
            beauticianName = val;
            return this;
        }
        public Builder beauticianImageUrl(String val) {
            beauticianImageUrl = val;
            return this;
        }
        public CommentBean build() {
            return new CommentBean(this);
        }
    }
    private CommentBean(Builder b) {
        userName = b.userName;
        userImageUrl = b.userImageUrl;
        content = b.content;
        createTime = b.createTime;
        beauticianName = b.beauticianName;
        beauticianImageUrl = b.beauticianImageUrl;
    }

}

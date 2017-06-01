package com.mdj.moudle.appUpdate;


import java.io.Serializable;

public class VersionBean implements Serializable {
    private static final long serialVersionUID = 1;
    private InstallPackageFile installPackageFile;
    private Bundle bundle;

    public VersionBean(){
        installPackageFile = new InstallPackageFile();
        bundle = new Bundle();
    }

    public InstallPackageFile getInstallPackageFile() {
        return installPackageFile;
    }

    public void setInstallPackageFile(InstallPackageFile installPackageFile) {
        this.installPackageFile = installPackageFile;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public class InstallPackageFile implements  Serializable{
        private static final long serialVersionUID = 1;
        private int versionCode;
        private String versionName;
        private String url;
        private String changeList;
        private int appId;//1用户端；2技师端
        private int platformType;//1android；2ios
        private int isMandatory;
        private int isSilent;
        private String createTime;

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getChangeList() {
            return changeList;
        }

        public void setChangeList(String changeList) {
            this.changeList = changeList;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }

        public int getPlatformType() {
            return platformType;
        }

        public void setPlatformType(int platformType) {
            this.platformType = platformType;
        }

        public int getIsMandatory() {
            return isMandatory;
        }

        public void setIsMandatory(int isMandatory) {
            this.isMandatory = isMandatory;
        }

        public int getIsSilent() {
            return isSilent;
        }

        public void setIsSilent(int isSilent) {
            this.isSilent = isSilent;
        }
    }
    public class Bundle implements  Serializable{
        private static final long serialVersionUID = 1;
        private int versionCode;
        private String url;
        private String changeList;
        private int appId;//1用户端；2技师端
        private int platformType;//1android；2ios
        private String md5CheckSum;
        private String createTime;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getChangeList() {
            return changeList;
        }

        public void setChangeList(String changeList) {
            this.changeList = changeList;
        }

        public String getMd5CheckSum() {
            return md5CheckSum;
        }

        public void setMd5CheckSum(String md5CheckSum) {
            this.md5CheckSum = md5CheckSum;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }

        public int getPlatformType() {
            return platformType;
        }

        public void setPlatformType(int platformType) {
            this.platformType = platformType;
        }


    }
}

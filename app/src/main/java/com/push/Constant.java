package com.push;

/**
 * Created by tt on 2016/5/13.
 */
public class Constant {
    public static String PUSH_BROADCAST_ACTION = "com.push.broadcasttest.doTask";


    public enum DefaultTagsEnum {
        CITY_NAME("cityName"),
        OS_VERSION("androidVersion");

        private String name;
        private DefaultTagsEnum(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum ActionEnum{
        UPLOAD_LOG_FILE(1);

        private int code;
        private ActionEnum(int code) {
            this.code = code;
        }
    }
}

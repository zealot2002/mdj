package com.mdj.constant;

/**
 * Created by tt on 2016/5/20.
 */
public class H5Constant {
    public enum ActionEnum{
        OPEN_NATIVE_WINDOW(2),
        CALL_PHONE(3),
        LOCATION(4),
        COPY_DATA(5),
        CLOSE_SELF_WINDOW(6),
        LOGOUT(7),
        REFRESH_CURRENT_SCREEN(8),
        GET_CURRENT_LOCATION(9),
        GOBACK_HISTORY(10);

        private int value;
        ActionEnum(int value) {
            this.value = value;
        }
        public int value() {
            return this.value;
        }
    }

    public enum ScreenEnum {
        CUSTOMER_DETAIL_SCREEN("1"),
        COMMENT_INFO_SCREEN("2"),
        ADDRESS_SCREEN("3"),
        MESSAGE_CENTRE_SCREEN("4"),
        MY_CARD_SCREEN("5"),
        VERSION_INFO_SCREEN("6"),
        AUTH_MSG_SCREEN("7"),
		CUSTOMER_DOCUMENT_SCREEN("8"),
        ADD_ADDRESS_SCREEN("9");

        private String name;
        ScreenEnum(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            return this.name;
        }
    }
}

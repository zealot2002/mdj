package com.mdj.constant;

public class CommonConstant {
	public static final String LINK_URL = "linkUrl";
	public static final int PAGE_SIZE = 6;

    /** 首页项目类型 **/
	public static final String PROJECT_LIST_TYPE = "0";
	/** 首页套餐类型 **/
	public static final String PACKAGE_LIST_TYPE = "1";

    public static final String MDJ_HOTLINE = "4006600777";
//
//    public static final int SUCCESS = 1;
//    public static final int FAIL = 0;
    public static final Integer PROJECT_OR_PACKAGE_TYPE_NORMAL = 1;//普通
    public static final Integer PROJECT_OR_PACKAGE_TYPE_PRIVILEGE = 2;//特惠
    public static final Integer PROJECT_OR_PACKAGE_TYPE_LIMIT_PRIVILEGE = 3;//限时限量特惠

    public static final Integer SERVICE_TYPE_IN_HOME = 1;//上门
    public static final Integer SERVICE_TYPE_TO_SHOP = 2;//到店
    public static final Integer SERVICE_TYPE_IN_HOME_AND_TO_SHOP = 3;//上门到店

    public static final Integer PROJECT_TYPE_EXTRA = 5;//附加项目

    public static final int COMMENT_TYPE_GOOD = 1;//好评
    public static final int COMMENT_TYPE_MIDDLE = 2;//中评
    public static final int COMMENT_TYPE_BAD = 3;//差评
    public static final int COMMENT_TYPE_ALL = 4;//全部

    public static final int ORDER_TYPE_ONGOING = 1;//进行中
    public static final int ORDER_TYPE_FINISHED = 2;//已完成
    public static final int ORDER_TYPE_CANCELED = 3;//已取消

    public static final int COUPON_USER_SCOPE_LIMIT = 1;//优惠券使用范围：限制项目
    public static final int COUPON_USER_SCOPE_ALL = 2;//优惠券使用范围：通用

    /*优惠券state*/
    public static final int COUPON_STATE_COMING = 1; /*即将到来*/
    public static final int COUPON_STATE_USED = 2; /*已使用*/
    public static final int COUPON_STATE_EXPIRED = 3; /*已过期*/
    public static final int COUPON_STATE_EXPIRING = 4; /*即将过期*/
    public static final int COUPON_STATE_NORMAL = 5; /*正常*/

    /*优惠券type*/
    public static final int COUPON_TYPE_DISCOUNT = 2; /*折扣券*/

    /*选择美容师，排序类型*/
    public static final Integer BEAUTICIAN_LIST_TYPE_ALL = 0;//默认排序
    public static final Integer BEAUTICIAN_LIST_TYPE_MOST_ORDER_NUM = 1;//月订单最多
    public static final Integer BEAUTICIAN_LIST_TYPE_MOST_APPRAISE_NUM = 2;//月好评最多

    /*订单详情：追单或取消订单button文案*/
    public static final Integer ORDER_DETAIL_CANCEL_OR_APPEND_NONE = 0;//不能追加也不能取消
    public static final Integer ORDER_DETAIL_CANCEL_OR_APPEND_CANCEL = 1;//可以取消订单
    public static final Integer ORDER_DETAIL_CANCEL_OR_APPEND_APPEND = 2;//可以追加订单

    /*订单状态*/
    public static final int ORDER_STATUS_WAIT_TO_CHARGE = 1;//待支付|确认下单
    public static final int ORDER_STATUS_CHARGE_SUCCESS = 2;//支付成功
    public static final int ORDER_STATUS_BEAUTY_ALREADY_GO = 3;//美容师已出门
    public static final int ORDER_STATUS_BEAUTY_ARRIVED = 4;//美容师已到达|开始服务
    public static final int ORDER_STATUS_SERVICE_OVER = 5;//服务完成|待用户确认
    public static final int ORDER_STATUS_WAIT_TO_COMMENT = 6;//待评价
    public static final int ORDER_STATUS_COMMENT_OVER = 7;//已评价|订单完成
    public static final int ORDER_STATUS_FAIL_TO_CHARGE = 8;//支付失败，或者用户取消了订单，或者订单超过30分钟未支付被自动取消
    public static final int ORDER_STATUS_CANCELED_BY_CUSTOMER = 9;//客服取消，待退款
    public static final int ORDER_STATUS_DRAWBACK_SUCCESS = 10;//退款成功
    public static final int ORDER_STATUS_ERROR = 11;//异常错误

    /*分享flag*/
    public static final String FLAG = "flag";
    public static final String SHARE_BANNER= "0"; //0是 banner
    public static final String SHARE_PUSH= "2"; //0是 push

    public enum ENTRY{
        NORMAL(1),         //正常情况：从首页或项目详情页进入
        APPEND_ORDER(2),  //追单情况
        MY_BEAUTICIAN(3), //从我的美容师下单
        BEAUTY_PARLOR(4);  //从美容院详情下单

        private int value;
        private ENTRY(int value) {
            this.value = value;
        }
        public int value(){
            return value;
        }
    }

    public enum PAY_TYPE{
        alipay("alipay"),     //支付宝
        wx("wx"),            //微信
        bfb("bfb");        //百付宝

        private String value;
        private PAY_TYPE(String value) {
            this.value = value;
        }
        public String value(){
            return value;
        }
    }

    public enum REQUEST_CODE{
        SCAN_QRCODE(999); //扫描二维码

        private int value;
        private REQUEST_CODE(int value) {
            this.value = value;
        }
        public int value(){
            return value;
        }
    }
}

package com.mdj.constant;

public class HttpConstant {
    public static final int SUCCESS = 1;
    public static final int FAIL = 0;

    public static final String EMPTY_DATA_ERROR = "server return:empty";/**服务器返回空**/
    public static final String HTTP_ERROR = "Http errCode:";/**服务器返回错误码**/
    public static final String HTML_DATA_PRE = "<!DOCTYPE html PUBLIC ";/**服务器返回了一个html页**/
    public static final String HTML_DATA_ERROR = "server return: <!DOCTYPE html PUBLIC... ";/**服务器返回了一个html页**/

    public static final String HTTP_METHOD_GET = "get";
    public static final String HTTP_METHOD_POST = "post";
    public static final String HTTP_METHOD_PUT = "put";
    public static final String HTTP_METHOD_DEL = "del";

	public static final String ACTION_TYPE_GET_HOME_DATA = "1";
//	测试环境
//	套餐详情
//	public static final String PACKAGE_DETAIL_URL = "http://dev.365vmei.cn/Package/packageInfo/id/";//54?from=app";
//	项目详情
//	public static final String PROJECT_DETAIL_URL = "http://dev.365vmei.cn/Index/project/project/";//21?from=app";
	
//	生产环境
//	套餐详情
	public static final String PACKAGE_DETAIL_URL = "http://m.emeidaojia.com/Package/packageInfo/id/";//54?from=app";

//	


//    public static final String OLD_SERVER_URL = "http://api.emeidaojia.com";//正式环境
    public static final String OLD_SERVER_URL = "http://a.emeidaojia.com";// 测试环境

//    public static final String NEW_SERVER_URL = "https://open.emeidaojia.com";//新正式环境
	public static final String NEW_SERVER_URL = "https://open.dev.emeidaojia.com";// 新版测试域名

//    public static final String H5_URl = "http://m.emeidaojia.com"; //H5 静态页 正式环境
    public static final String H5_URl = "http://dev.emeidaojia.com"; //H5 静态页 测试环境

    public static final String FILE_SERVER_URL = "http://dimg.365vmei.cn";// 加载图片的url
    public static final String RED_PACKETS_PIC_URL = "http://static.emeidaojia.com/static/wx_share.jpg";//红包图片
    public static final String RED_PACKETS_TARGET_URL = "http://m.emeidaojia.com/Static/appDistributionRedShow/orderSn/";
/*H5页面URL start*/
    //用户协议
    public static final String PROTOCAL_URL = H5_URl+"/UserCenter/agreement?from=app";
    //常见问题解答
    public static final String URL_COMMON_QA = H5_URl+"/UserCenter/setting_question?from=app";
    //服务条款
    public static final String URL_AGREEMENT = H5_URl+"/UserCenter/setting_service?from=app";
    //承诺书
    public static final String URL_PROMISE = H5_URl+"/UserCenter/setting_insure?from=app";
    //介绍我们
    public static final String URL_ABOUT = "http://www.emeidaojia.com/oem.php/About/index";
    //用户档案
    public static final String URL_USER_DATUM = "http://m.emeidaojia.com/UserCenter/getUserQuestion?from=app";

/*H5页面URL end*/
    /*设置模块*/
    public static final String EXCHANGE_CODE = "/V1/Beauty/CouponApi/exchange.json";//优惠券兑换
    public static final String FEEDBACK = "/V1/User/UserApi/addfeedback.json"; //意见反馈
    public static final String DEFAULT_IMPRESSION_LIST = "/V1/Order/AppraiseApi/appraiseinit.json";//获取评价初始化数据
    public static final String SUBMIT_RATED = "/V1/Order/AppraiseApi/Appraise.json";//提交评价内容
    public static final String SHOPRATED_DETAILS_INFO = "/V1/Order/AppraiseApi/Info.json";//获取评价详情
    public static final String INVITATION_FRIEND = "/V1/User/UserApi/invitefriend.json";//邀请好友
    public static final String MODIFY_INFORMATION = "/V1/User/UserApi/update.json";//上传头像


    /*登陆模块*/
    public static final String GET_SMS_CODE = "/V1/Api/PhoneApi/sendverifycode.json";// 获取验证码
    public static final String LOGIN = "/V1/User/UserApi/login.json";// 获取验证码
    /*用户模块*/
    public static final String GET_USER_INFO = "/V1/User/UserApi/info.json";//获取用户信息
    /*美容师模块*/
    public static final String GET_MY_BEAUTICIAN_LIST = "/V1/Api/UserApi/myBeauticianList.json";//获取我的美容师列表
    public static final String GET_BEAUTICIAN_DETAIL = "/V1/Beauty/BeautyApi/info.json";//获取美容师详情
    public static final String GET_BEAUTICIAN_PROJECT_LIST = "/V1/Beauty/BeautyApi/projectList.json";//获取美容师项目列表
    public static final String GET_BEAUTICIAN_COMMENT_NUM_LIST = "/V1/Beauty/BeautyApi/assessmentnum.json";//获取美容师评价数量
    public static final String GET_BEAUTICIAN_COMMENT_LIST = "/V1/Api/BeauticianApi/assessmentlist.json";//获取美容师评价列表

    public static final String GET_AVAILABLE_BEAUTICIAN_LIST = "/V1/Beauty/BeautyTimeApi/beauticianlist.json";//获取可预约美容师列表

    /*地址模块*/
    public static final String GET_MY_ADDRESS_LIST = "/V1/Api/UserApi/addressList.json";
    public static final String ADD_OR_UPDATE_ADDRESS = "/V1/Api/UserApi/address.json";/** 添加、修改地址 **/
    public static final String SET_DEFAULT_ADDRESS = "/V1/Api/UserApi/addressDefault.json";/** 设置默认地址 **/
    public static final String GET_DEFAULT_ADDRESS = "/V1/Api/UserApi/addressdefault.json";/*获取默认地址*/

/*套餐模块*/
    public static final String GET_MY_PACKAGE_LIST = "/V1/Api/PackageApi/packageList.json";/** 我的套餐列表**/

    public static final String GET_SUGGEST_PACKAGE_LIST = "/V1/Package/PackageApi/recommendList.json";/** 套餐首页：推荐套餐列表**/
    public static final String GET_NORMAL_PACKAGE_LIST = "/V1/Package/PackageApi/packageList.json";/** 套餐首页：普通套餐列表**/
    public static final String GET_BEAUTY_PRALOR_PACKAGE_LIST = "/V1/Beauty/HomeSiteApi/packagelist.json";/** 美容院套餐列表**/
    public static final String GET_PACKAGE_INFO = "/V1/Package/PackageApi/info.json";/** 获取套餐详情**/

/*美容院模块*/
    public static final String GET_BEAUTY_PARLOR_DETAIL = "/V1/Beauty/BeautyApi/homesiteinfo.json";/**美容院详情**/
    public static final String GET_AVAILABLE_BEAUTY_PARLOR_LIST = "/V1/Beauty/BeautyTimeApi/salonlist.json";/**可预约美容院列表**/
    public static final String GET_BEAUTY_PARLOR_PROJECT_LIST = "/V1/Beauty/HomeSiteApi/projectlist.json";/**美容院项目列表**/

/*订单模块*/
    public static final String GET_MY_ORDER_LIST = "/V1/Api/OrderApi/list.json";/**我的订单列表**/
    public static final String FINISH_ORDER = "/V1/Order/OrderApi/finish.json";/**结束订单**/
    public static final String ORDER_DETAIL = "/V1/Api/OrderApi/info.json";/**订单详情**/
    public static final String CANCEL_ORDER = "/V1/Api/OrderApi/state.json";/**取消订单**/

/*确认订单*/
    public static final String GET_RECOMMAND_COUPON_AND_PACKAGE = "/V1/Api/OrderApi/discountMethod.json";/**获取推荐的套餐和优惠券策略**/
    public static final String GET_AVAILABLE_SERVICE_HOURS = "/V1/Beauty/BeautyTimeApi/bookabletime.json";/**获取可用时间段**/
    public static final String CAL_REAL_PRICE = "/V1/Api/OrderApi/realPrice.json";/**计算实付金额**/

    public static final String CREATE_PROJECT_ORDER = "/V1/Order/OrderApi/add.json";/**项目下单**/
    public static final String PAY_ORDER = "/V1/Order/OrderApi/pay.json";/**支付**/
    public static final String GET_PAY_COUNTDOWN_TIME = "/V1/Order/OrderApi/payabletime.json";/**支付倒计时**/
    public static final String CREATE_PACKAGE_ORDER = "/V1/Package/PackageApi/packagePay.json";/**套餐下单**/

    public static final String GET_AVAILABLE_COUPON_LIST = "/V1/Beauty/CouponApi/reccouponlist.json";/**获取可用优惠券列表**/
    public static final String GET_AVAILABLE_PACKAGE_LIST = "/V1/Package/PackageApi/recpackagelist.json";/**获取可用套餐列表**/

    public static final String GET_PROJECT_INFO = "/V1/Project/ProjectApi/info.json";/**获取项目详情**/

    public static final String GET_FITABLE_BEAUTY_PARLOR_LIST = "/V1/Package/PackageApi/packagelimitstore.json";/**获取套餐适用门店列表**/

/*首页*/
    public static final String GET_BANNER_LIST = "/V1/Ad/AdApi/appadlist.json";/**banner列表**/
    public static final String GET_SUGGEST_PROJECT_LIST = "/V1/Project/ProjectApi/recommendList.json";/**推荐项目列表**/
    public static final String GET_HOME_PROJECT_LIST = "/V1/Project/ProjectApi/projectList.json";/**普通项目列表**/

    /*个人中心*/
    public static final String GET_MY_COUPON_LIST = "/V1/User/CouponApi/IndividualCoupons.json";/**优惠券列表**/

    /*关联推荐人*/
    public static final String SET_REFEREE = "/V1/Package/RecommendApi/recommend.json";/**关联推荐人**/


    /*项目详情*/
    public static final String GET_PROJECT_DETAIL = "/V1/Project/ProjectApi/info.json";
    public static final String GET_PROJECT_COMMENT_LIST = "/V1/Project/ProjectApi/assessmentlist.json";


    /*优惠券*/
    public static final String GET_COUPON_DETAIL = "/V1/Beauty/CouponApi/info.json";//获取优惠券详情
}

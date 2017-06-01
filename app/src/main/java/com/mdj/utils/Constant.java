package com.mdj.utils;

import android.graphics.Color;

public class Constant {

	/** 再次的城市id **/
	public static final String SP_CITY_ID_AGAIN = "SP_CITY_ID_AGAIN";
	/** 再次的城市name **/
	public static final String SP_CITY_NAME_AGAIN = "SP_CITY_NAME_AGAIN";
	/** 追单的城市id **/
	public static final String SP_CITY_ID_ADDORDER = "SP_CITY_ID_ADDORDER";
	/** 追单的城市name **/
	public static final String SP_CITY_NAME_ADDORDER = "SP_CITY_NAME_ADDORDER";
	/** 我的美容师的城市id **/
	public static final String SP_CITY_ID_MYBEAUTY = "SP_CITY_ID_MYBEAUTY";
	/** 我的美容师的城市name **/
	public static final String SP_CITY_NAME_MYBEAUTY = "SP_CITY_NAME_MYBEAUTY";
	/** 我的套餐的城市id **/
	public static final String SP_CITY_ID_MYPACKAGE = "SP_CITY_ID_MYPACKAGE";
	/** 我的套餐的城市name **/
	public static final String SP_CITY_NAME_MYPACKAGE = "SP_CITY_NAME_MYPACKAGE";

	/** aes加密密码 **/
	public static final String AES_PASSWORD = "2WH96NC74QOP0186";
	/** aes加密向量 **/
	public static final String AES_IV = "8408369129837502";

	public static final String DESCRIPTOR = "com.mdj";
	public static final int IO_BUFFER_SIZE = 2 * 1024;
	public static final String UMENG_SHARE = "com.umeng.share";
	public static final String UMENG_LOGIN = "com.umeng.login";

	public static final String REFRESH_TIME_TAB1 = "refresh_time_tab1";
	public static final String REFRESH_TIME_TAB2 = "refresh_time_tab2";
	public static final String REFRESH_TIME_TAB3 = "refresh_time_tab3";
	public static final String CACHE_MY_OREDER_LIST = "cache_my_oreder_list.txt";
	public static final String CACHE_BANNER = "banner.txt";
	public static final Long CACHE_MY_OREDER_LIST_TIME = 1000l;
	public static final Long CACHE_BANNER_TIME = 1000 * 100l;

	public static final String WEIXIN_APPID = "wx5bc79e7f695ec623";
	public static final String WEIXIN_APPSECRET = "1aad3c94a943ef0bba0f0b29b9232b2a";

	public static final String QQ_APPID = "1104617632";
	public static final String QQ_APPKEY = "eEEqsUhh5y2N9GDu";
	public static final String SINA_APPKEY = "1932805293";

	public static final String DEVICE_TYPE = "MA7BOY3DJRFIGZL5SWN89Q0VC4H62XP1TKEU";

	public static final String SHARE_SDK_KEY = "6af15cd1b597";
	public static final String SHARE_BAIDU_KEY = "zBlB5fpxW1c9FkgnkQF2W38k";
	public static final String SHARE_SDK_SEC = "76b17b3281e66b6aaa6eb577ea6e6437";

	public static final String PICURL = "http://dimg.365vmei.cn";// 加载图片的url

//    public static final String URL = "http://api.emeidaojia.com";//正式环境
//	public static final String URL = "http://a.emeidaojia.com";// 测试环境

//	 public static final String NEWURL = "https://open.emeidaojia.com";//新正式环境
//	public static final String NEWURL = "https://open.dev.emeidaojia.com";// 新版测试域名

	public static final String URL_COMMON_QA = "http://www.emeidaojia.com/oem.php/About/answer.html";// 调用接口的url
	public static final String URL_PROMISE = "http://www.emeidaojia.com/oem.php/About/promise";// 调用接口的url
	public static final String URL_AGREEMENT = "http://www.emeidaojia.com/oem.php/About/agreement";// 调用接口的url
	public static final String URL_ABOUT = "http://www.emeidaojia.com/oem.php/About/index";// 调用接口的url
	public static final String PICADD = "http://static.emeidaojia.com/static/wx_share.jpg";// 分享
																							// 红包时候用的图片

	public static final String SP_UA = "sp_ua";
	public static final String SP_ADDRESS = "sp_address";
	public static final String SP_STREET_ID = "sp_street_id";
	public static final String SP_PHONE = "sp_phone";
	public static final String SP_PROVINCE = "sp_province";
	public static final String SP_LON = "sp_lon";
	public static final String SP_LAN = "sp_lan";
	public static final String SP_CITY_NAME = "sp_city_name";
	public static final String SP_CITY_ID = "sp_city_id";
	public static final String SP_LOCATION_TRUE = "sp_locaition_true";
	public static final String SP_BANNNER_OPEN = "sp_banner_open";

	public static final String BJ_LA = "39.945";
	public static final String BJ_LO = "116.404";
	public static final String SH_LA = "31.227";
	public static final String SH_LO = "121.481";
	public static final String GZ_LA = "23.155";
	public static final String GZ_LO = "113.264";
	public static final String SZ_LA = "22.560";
	public static final String SZ_LO = "114.064";
	// 北纬30.67度，东经104.06度
	public static final String CD_LA = "30.67";
	public static final String CD_LO = "104.06";

	/** 服务地址是否和当前地址一致 */
	public static final String SP_IS_CURRENT_CITY = "sp_is_current_city";
	/** 再次预约 */
	public static final String SP_AGAIN_LA = "sp_again_la";
	public static final String SP_AGAIN_LO = "sp_again_lo";
	public static final String SP_AGAIN_NAME = "sp_again_name";
	public static final String SP_AGAIN_CITY = "sp_again_city";
	public static final String SP_AGAIN_CITY_ID = "sp_again_city_id";
	public static final String SP_AGAIN_PHONE = "sp_again_phone";
	public static final String SP_AGAIN_NAME_OTHER = "sp_again_name_other";
	public static final String SP_AGAIN_PHONE_OTHER = "sp_again_phone_other";
	public static final String SP_AGAIN_ADDRESS = "sp_again_address";
	public static final String SP_AGAIN_POSTSCRICT = "sp_again_postscrict";

	// streetId, userName, userPhone, forUserName, forUserPhone,
	// lati, lonti, cityId, cityName, address,postscrict;

	/** 服务过的美容师 */
	public static final String SP_SERVICED_LA = "sp_serviced_la";
	public static final String SP_SERVICED_LO = "sp_serviced_lo";
	public static final String SP_SERVICED_NAME = "sp_serviced_name";
	public static final String SP_SERVICED_CITY = "sp_serviced_city";
	public static final String SP_SERVICED_CITY_ID = "sp_serviced_city_id";
	public static final String SP_SERVICED_PHONE = "sp_serviced_phone";
	public static final String SP_SERVICED_NAME_OTHER = "sp_serviced_name_other";
	public static final String SP_SERVICED_PHONE_OTHER = "sp_serviced_phone_other";
	public static final String SP_SERVICED_ADDRESS = "sp_serviced_address";
	public static final String SP_SERVICED_POSTSCRICT = "sp_serviced_postscrict";

	public static final String SERVER_MYORDER_LIST = "Order.getOrderListForApp.Logic";
	public static final String SERVER_SENDCODE = "User.sendCode.Logic";
	public static final String SERVER_CHECKCODE = "User.checkCode.Logic";
	public static final String SERVER_ORDER_DETAIL = "Order.getOrderInfoForApp.Logic";
	public static final String SERVER_ADDASSESSMENT = "Assessment.addAssessmentForApp.Logic";
	public static final String SERVER_ORDERPROCESS = "Order.getOrderProcess.Logic";
	public static final String SERVER_CHANGEORDER = "Order.changeOrderStatus.Logic";
	public static final String SERVER_PAY_CONTINUE = "Order.payOrderForApp.Logic";
	public static final String SERVER_RESERVETIME = "ReserveTime.getCanUserHourBybidAndProjectTime.Logic";
	public static final String SERVER_ADD_ORDER = "Order.appAddOrder.Logic";
	public static final String SERVER_COUPONLISTFORAPP = "Coupon.getCouponListForApp.Logic";
	public static final String SERVER_GETSTREETIDBYGEO = "Region.getStreetIdByGeo";
	public static final String ACTION_FRAGMENT_KEY = "com.mdj.keyback";
	public static final String ACTION_LOGINOK = "com.mdj.loginok";
	public static final String ACTION_LOGINOUT = "com.mdj.loginout";
	public static final String ACTION_LOGIN_CHANGE = "com.mdj.loginchange";

	public static final String PAY_ALY = "alipay";
	public static final String PAY_WEIXIN = "wx";
	public static final String PAY_BANK = "bank";

	public static final int MDJ_PINK = Color.rgb(254, 102, 114);
	public static final int MDJ_YELLOW = Color.rgb(254, 174, 3);
	public static final int MDJ_SHAWLLOW_GRAY = Color.rgb(205, 205, 205);

	/** **/
	/**************** 新版接口名 ************************/
	/** 订单列表 **/
	public static final String ORDER_LIST = "/V1/Api/OrderApi/list.json";
	/** 订单详情 **/
	public static final String ORDER_DETAIL = "/V1/Api/OrderApi/info.json";
	/** 取消订单 **/
	public static final String ORDER_CANCEL = "/V1/Api/OrderApi/state.json";
	/** 追加订单,返回时间 **/
	public static final String ORDER_ADD = "/V1/Api/OrderApi/additional.json";

	/** 美容师项目列表 **/
	public static final String BEAUTY_RROJECT_LIST = "/V1/Api/ProjectApi/beauticianProjectList.json";

	/** 地址列表 **/
	public static final String ADDRESS_LIST = "/V1/Api/UserApi/addressList.json";
	/** 添加、修改地址 **/
	public static final String ADDRESS_ADD = "/V1/Api/UserApi/address.json";
	/** 设置默认地址 **/
	public static final String ADDRESS_DEFALT = "/V1/Api/UserApi/addressDefault.json";

	/** 获取体验店列表 **/
	public static final String ADDRESS_EXPERIENCE_SHOP = "/V1/Api/StoreApi/storeList.json";

	/** 项目多选模块 **/
	/** 推荐项目、及套餐列表 **/
	public static final String PROJECT_RECOMMEND_LIST = "/V1/Api/ProjectApi/recommendList.json";
	/** 除推荐项目外，其他项目列表 **/
	public static final String PROJECT_ELSE_LIST = "/V1/Api/ProjectApi/moreProjectList.json";

	/** 个人中心 **/
	/** 获取用户信息 **/
	public static final String USER_INFO = "/V1/Api/UserApi/info.json";

	/** 我的美容师列表.包括服务过美容师和收藏美容师 **/
	public static final String USER_BEAUTY_LIST = "/V1/Api/UserApi/myBeauticianList.json";

	/** 美容师详情模块 **/
	/** 美容师详情 **/
	public static final String BEAUTY_DETAIL = "/V1/Api/BeauticianApi/info.json";
	/** 美容师项目列表 **/
	public static final String BEAUTY_PROJECT_LIST = "/V1/Api/BeauticianApi/projectList.json";
	/** 美容师评价列表 **/
	public static final String BEAUTY_APPRAISE_LIST = "/V1/Api/BeauticianApi/assessmentList.json";
	/** 美容师收藏、取消收藏 **/
	public static final String BEAUTY_COLLECT = "/V1/Api/BeauticianApi/collectBeautician.json";

	/** 订单确认模块 **/
	/** 获取默认地址 **/
	public static final String GET_DEFAULT_ADDRESS = "/V1/Api/userApi/addressDefault.json";
	/** 获取推荐的套餐和优惠券 **/
	public static final String GET_RECOMMEND_COUPONANDPACKAGE = "/V1/Api/OrderApi/discountMethod.json";
	/** 选择服务时间 **/
	public static final String GET_SERVICE_TIME = "/V1/Api/OrderApi/validTimeList.json";
	/** 获取可用美容师列表 **/
	public static final String GET_AVAILABLE_BEAUTY_LIST = "/V1/Api/OrderApi/validBeauticianList.json";
	/** 获取可用优惠券列表 **/
	public static final String GET_AVAILABLE_COUPON_LIST = "/V1/Api/OrderApi/couponList.json";
	/** 获取套餐详情 **/
	public static final String GET_PACKAGE_DETAIL = "/V1/Api/PackageApi/packageInfo.json";
	/** 计算实付金额 **/
	public static final String GET_COUNT_PRICE = "/V1/Api/OrderApi/realPrice.json";

	// 投诉订单
	public static final String COMPLAINT_ORDER = "/V1/Api/UserApi/suggest.json";

	// 获取首页信息
	public static final String GET_HOME_DATA = "/V1/Api/HomeApi/index.json";

	// 获取项目列表
	public static final String GET_PROJECT_lIST = "/V1/Api/ProjectApi/projectList.json";

	
	// 获取城市列表
	public static final String GET_CITY_lIST = "/V1/Api/CityApi/serviceCityList.json";

	// 获取订单评价内容
	public static final String GET_ORDER_COMMENT_INFO = "/V1/Api/OrderApi/appraise.json";

	// 新增评价订单
	public static final String CREATE_COMMENT_ORDER = "/V1/Api/OrderApi/appraise.json";

	// 修改评价订单
	public static final String MODIFY_COMMENT_ORDER = "/V1/Api/OrderApi/appraise.json";

	// 获取我的套餐列表
	public static final String GET_MY_PACKAGE_LIST = "/V1/Api/PackageApi/packageList.json";

	// 获取评价初始化数据
	public static final String GET_COMMENT_INIT_INFO = "/V1/Api/OrderApi/appraiseInit.json";

	// 获取订单我的推荐套餐列表
	public static final String GET_MY_RECOMMEND_PACKAGE_LIST = "/V1/Api/OrderApi/packageList.json";

	// 获取美容师印象列表
	public static final String GET_BEAUTY_IMPRESSION_LIST = "/V1/Api/BeauticianApi/tagsList.json";
	
	// 获取项目详情
	public static final String GET_PROJECT_DETAIL = "/V1/Api/ProjectApi/projectInfo.json";
	
	/** 体验店相关 **/
	/** 体验店美容师列表 **/
	public static final String GET_SHOP_BEAUTY_LIST = "/V1/Api/StoreApi/storelist.json";
	/** 体验店美容师时间 **/
	public static final String GET_SHOP_BEAUTY_TIME = "/V1/Api/storeApi/validTimeList.json";

	// 获取体验店项目列表
	public static final String GET_SHOP_PROJECT_lIST = "/V1/Api/storeApi/projectList.json";
	
	/** 获取追单可用美容师列表 **/
	public static final String GET_APPEND_ORDER_BEAUTY_LIST = "/V1/Api/storeApi/appendBeauticianList.json";
	
	
}

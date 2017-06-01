package com.push;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.common.CommonWebviewActivity;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.order.OrderDetailActivity;
import com.mdj.utils.MdjLog;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.UmengRegistrar;
import com.umeng.message.entity.UMessage;
import com.umeng.message.tag.TagManager;

import org.json.JSONTokener;

import java.util.Map;

/**
 * Created by tt on 2016/5/12.
 */
public class PushHelper {
    private static final String TAG = "PushHelper";
    private static PushHelper instance = new PushHelper();
    private PushAgent mPushAgent;
    private Context context;

    private PushHelper(){
    }
    public static PushHelper getInstance(){
        return instance;
    }
    //init
    public void init(){
        this.context = MyApp.getInstance();
        mPushAgent = PushAgent.getInstance(context);
//        mPushAgent.setDebugMode(true);
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        mPushAgent.onAppStart();

        //开启推送并设置注册的回调处理
//        mPushAgent.enable(mRegisterCallback);
        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            /**
             * 参考集成文档的1.6.3
             * http://dev.umeng.com/push/android/integration#1_6_3
             * */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if(isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(context).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(context).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, "dealWithCustomMessage :"+msg.custom, Toast.LENGTH_LONG).show();
                        Toast.makeText(context, "dealWithCustomMessage :"+msg.extra.toString(), Toast.LENGTH_LONG).show();
                        handlePushMessage(msg.extra);
                    }
                });
            }

            /**
             * 参考集成文档的1.6.4
             * http://dev.umeng.com/push/android/integration#1_6_4
             * */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                        builder.setContent(myNotificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);

                        return builder.build();

                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 该Handler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * 参考集成文档的1.6.2
         * http://dev.umeng.com/push/android/integration#1_6_2
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
//                String custom;
                try {
                    JSONTokener jsonParser = new JSONTokener(msg.custom);
                    org.json.JSONObject obj = (org.json.JSONObject) jsonParser.nextValue();
                    int flag = Integer.parseInt(obj.getString("flag"));
//                    custom = msg.custom;
//                    YoumengMsg vo = JSONObject.parseObject(custom, YoumengMsg.class);
                    if (flag == 0) {// 跳转到webactivity vo.getFlag()
                        Intent in = new Intent(context, CommonWebviewActivity.class);//need modify
                        in.putExtra(CommonConstant.FLAG, "2");// 获取web分享的标识。。。string类型
                        in.putExtra("TITLE", "美道家");
                        in.putExtra(BundleConstant.LINK_URL, msg.extra.get("link_url"));
                        in.putExtra(BundleConstant.TITLE,  msg.extra.get("share_title"));
                        in.putExtra(BundleConstant.CONTENT,  msg.extra.get("share_content"));
                        in.putExtra(BundleConstant.IMAGE_URL,  msg.extra.get("share_image_url"));
                        in.putExtra(BundleConstant.TARGET_URl,  msg.extra.get("share_target_url"));

                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(in);
                    } else if (flag == 1) { // 订单详情 vo.getFlag()
                        Intent intent = new Intent(context, OrderDetailActivity.class);
                        intent.putExtra("orderId", msg.extra.get("orderSn"));
                        intent.putExtra("FLAG", 1);// int类型
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知
        //参考http://bbs.umeng.com/thread-11112-1-1.html
        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }

    //start
    public void start(){
        try{
            mPushAgent.enable(mRegisterCallback);
            String device_token = UmengRegistrar.getRegistrationId(context);
            MdjLog.logE(TAG, "device_token : " + device_token);
            new AddAliasTask(CacheManager.getInstance().getUserBean().getId(),"userId").execute();
            new AddAliasTask(CacheManager.getInstance().getUserBean().getPhone(),"phone").execute();

            //标签
            new AddTagTask(Constant.DefaultTagsEnum.OS_VERSION.toString()+":"+android.os.Build.VERSION.RELEASE).execute();
            new AddTagTask(Constant.DefaultTagsEnum.CITY_NAME.toString()+":"+CacheManager.getInstance().getGlobalCity().getName()).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //stop
    public void stop(){
        mPushAgent.disable(mUnregisterCallback);
    }
    //此处是注销的回调处理
    //参考集成文档的1.7.10
    //http://dev.umeng.com/push/android/integration#1_7_10
    public IUmengUnregisterCallback mUnregisterCallback = new IUmengUnregisterCallback() {

        @Override
        public void onUnregistered(String registrationId) {
            // TODO Auto-generated method stub
            MdjLog.logE(TAG, "onUnregistered registrationId :" + registrationId);
        }
    };

    //此处是注册的回调处理
    //参考集成文档的1.7.10
    //http://dev.umeng.com/push/android/integration#1_7_10
    public IUmengRegisterCallback mRegisterCallback = new IUmengRegisterCallback() {
        @Override
        public void onRegistered(String registrationId) {
            MdjLog.logE(TAG,"onRegistered registrationId :"+registrationId);

        }
    };

//add 别名
    class AddAliasTask extends AsyncTask<Void, Void, Boolean> {
        String alias;
        String aliasType;

        public AddAliasTask(String aliasString,String aliasTypeString) {
            // TODO Auto-generated constructor stub
            this.alias = aliasString;
            this.aliasType = aliasTypeString;
        }

        protected Boolean doInBackground(Void... params) {
            try {
                return mPushAgent.addAlias(alias, aliasType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (Boolean.TRUE.equals(result))
                Log.i(TAG, "alias was set successfully.");
        }
    }
//add tag
    class AddTagTask extends AsyncTask<Void, Void, String>{
        String tagString;
        String[] tags;
        public AddTagTask(String tag) {
            // TODO Auto-generated constructor stub
            tagString = tag;
            tags = tagString.split(",");
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                TagManager.Result result = mPushAgent.getTagManager().add(tags);
                Log.d(TAG, result.toString());
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Fail";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.w(TAG,"Add Tag:\n" + result);
        }
    }
//处理自定义消息
    private void handlePushMessage(Map map) {
        try{
            parsePushMessage(map);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void parsePushMessage(Map<String,String> map) throws Exception {
        Constant.ActionEnum action=Constant.ActionEnum.values()[Integer.valueOf(map.get("action"))];
        switch (action){

            case UPLOAD_LOG_FILE:
                uploadLog();
                break;

        }
    }

    private void uploadDeviceInfo() {

    }

    private void uploadLog() {

    }




}

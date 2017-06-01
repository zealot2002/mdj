package com.mdj.application;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.gdt.adapter.GdtUtils;
import com.mdj.constant.SPConstant;
import com.mdj.utils.LogcatHelper;
import com.mdj.utils.MdjLog;
import com.mdj.utils.PreferencesUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.push.PushHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class MyApp extends Application {
	public static Context applicationContext;
	public static MyApp instance;
	private ImageLoader mImageLoader;
	private List<Activity> activityList = new LinkedList<Activity>();

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			applicationContext = this;
			instance = this;
			LogcatHelper.getInstance(this).start();
			SDKInitializer.initialize(this);

			PlatformConfig.setWeixin("wx5bc79e7f695ec623","1aad3c94a943ef0bba0f0b29b9232b2a");
			PlatformConfig.setQQZone("1104617632","eEEqsUhh5y2N9GDu");

			initUniversalImgLoader();
			initGdt();//广点通
			MobclickAgent.openActivityDurationTrack(false);//禁止默认的Activity页面统计方式 
			MobclickAgent.setSessionContinueMillis(1000*60*60);//1Сʱ

            try{
                PushHelper.getInstance().init();
            }catch (Exception e){
                e.printStackTrace();
            }
		} catch (Exception e) {
			e.printStackTrace();
            Toast.makeText(this," MyApp init err:"+e.toString(),Toast.LENGTH_LONG);
		}
	}

	private void initGdt(){
		//广点通app激活统计 start
		String appRegisterFlag = PreferencesUtils.getStringPreference(MyApp.getInstance(),
				SPConstant.SP_FLAG_APP_REGISTER, SPConstant.SP_FLAG_APP_REGISTER, "");
		if(appRegisterFlag.equals("")){
			new Thread(new Runnable() {
				@Override
				public void run() {
					GdtUtils.sendAppRegister();
				}
			}).start();
		}
		//广点通app激活统计 end
	}

	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 遍历所有Activity并finish
	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}

	private void initUniversalImgLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(this,
                "mdj/Cache");// 获取到缓存的目录地址
        MdjLog.logE("cacheDir", cacheDir.getPath());
        // 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                // max width, max height，即保存的每个缓存文件的最大长宽
                .memoryCacheExtraOptions(480, 800)
                        // Can slow ImageLoader, use it carefully (Better don't use it)设置缓存的详细信息，最好不要设置这个
//				 .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null)
                        // 线程池内加载的数量
                .threadPoolSize(3)
                        // 线程优先级
                .threadPriority(Thread.NORM_PRIORITY - 2)
				/*
				 * When you display an image in a small ImageView
				 *  and later you try to display this image (from identical URI) in a larger ImageView
				 *  so decoded image of bigger size will be cached in memory as a previous decoded image of smaller size.
				 *  So the default behavior is to allow to cache multiple sizes of one image in memory.
				 *  You can deny it by calling this method:
				 *  so when some image will be cached in memory then previous cached size of this image (if it exists)
				 *   will be removed from memory cache before.
				 */
//				.denyCacheImageMultipleSizesInMemory()

                        // You can pass your own memory cache implementation你可以通过自己的内存缓存实现
                        // .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                        // .memoryCacheSize(2 * 1024 * 1024)
                        //硬盘缓存50MB
                .diskCacheSize(50 * 1024 * 1024)
                        //将保存的时候的URI名称用MD5
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                        // 加密
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())//将保存的时候的URI名称用HASHCODE加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheFileCount(100) //缓存的File数量
                .diskCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
                         .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                         .imageDownloader(new BaseImageDownloader(this, 5 * 1000,
                                 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);// 全局初始化此配置

        mImageLoader = ImageLoader.getInstance();
	}


	public ImageLoader getImageLoad() {
		return mImageLoader;
	}

	public static synchronized MyApp getInstance() {
		return instance;
	}

	public static void setInstance(MyApp instance) {
		MyApp.instance = instance;
	}

}

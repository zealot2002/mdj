package com.mdj.utils;

import android.graphics.Bitmap;

import com.mdj.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by tt on 2016/3/21.
 */
public class DisplayImageOptionsUtil {
    /**
     * 通用缓存option
     */
    public static DisplayImageOptions getCommonCacheOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                // 设置图片在下载期间显示的图片
                .showImageOnLoading(R.drawable.imageloading)
                        // 设置图片Uri为空或是错误的时候显示的图片
                .showImageForEmptyUri(R.drawable.imageloading)
                        // 设置图片加载/解码过程中错误时候显示的图片
                .showImageOnFail(R.drawable.imageloading)
                        // 设置下载的图片是否缓存在内存中
                .cacheInMemory(true)
                        // 设置下载的图片是否缓存在SD卡中
                .cacheOnDisc(true)
                        // 保留Exif信息
                .considerExifParams(true)
                        // 设置图片以如何的编码方式显示
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                        // 设置图片的解码类型
                .bitmapConfig(Bitmap.Config.RGB_565)
                        // .decodingOptions(android.graphics.BitmapFactory.Options
                        // decodingOptions)//设置图片的解码配置
                .considerExifParams(true)
                        // 设置图片下载前的延迟
                .delayBeforeLoading(100)// int
                        // delayInMillis为你设置的延迟时间
                        // 设置图片加入缓存前，对bitmap进行设置
                        // .preProcessor(BitmapProcessor preProcessor)
                .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
                        // .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
//                .displayer(new FadeInBitmapDisplayer(100))// 淡入
                .build();
        return options;
    }

    /**
     * banner缓存option
     */
    public static DisplayImageOptions getSmallImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                // 设置图片在下载期间显示的图片
                .showImageOnLoading(R.mipmap.imageloading_small)
                        // 设置图片Uri为空或是错误的时候显示的图片
                .showImageForEmptyUri(R.mipmap.imageloading_small)
                        // 设置图片加载/解码过程中错误时候显示的图片
                .showImageOnFail(R.mipmap.imageloading_small)
                        // 设置下载的图片是否缓存在内存中
                .cacheInMemory(true)
                        // 设置下载的图片是否缓存在SD卡中
                .cacheOnDisc(true)
                        // 保留Exif信息
                .considerExifParams(true)
                        // 设置图片以如何的编码方式显示
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                        // 设置图片的解码类型
                .bitmapConfig(Bitmap.Config.RGB_565)
                        // .decodingOptions(android.graphics.BitmapFactory.Options
                        // decodingOptions)//设置图片的解码配置
                .considerExifParams(true)
                        // 设置图片下载前的延迟
                .delayBeforeLoading(100)// int
                        // delayInMillis为你设置的延迟时间
                        // 设置图片加入缓存前，对bitmap进行设置
                        // .preProcessor(BitmapProcessor preProcessor)
                .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
                        // .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(100))// 淡入
                .build();
        return options;
    }

    /**
     * banner缓存option
     */
    public static DisplayImageOptions getBannerOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                // 设置图片在下载期间显示的图片
                .showImageOnLoading(R.mipmap.default_banner)
                        // 设置图片Uri为空或是错误的时候显示的图片
                .showImageForEmptyUri(R.mipmap.default_banner)
                        // 设置图片加载/解码过程中错误时候显示的图片
                .showImageOnFail(R.mipmap.default_banner)
                        // 设置下载的图片是否缓存在内存中
                .cacheInMemory(true)
                        // 设置下载的图片是否缓存在SD卡中
                .cacheOnDisc(true)
                        // 保留Exif信息
                .considerExifParams(true)
                        // 设置图片以如何的编码方式显示
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                        // 设置图片的解码类型
                .bitmapConfig(Bitmap.Config.RGB_565)
                        // .decodingOptions(android.graphics.BitmapFactory.Options
                        // decodingOptions)//设置图片的解码配置
                .considerExifParams(true)
                        // 设置图片下载前的延迟
                .delayBeforeLoading(100)// int
                        // delayInMillis为你设置的延迟时间
                        // 设置图片加入缓存前，对bitmap进行设置
                        // .preProcessor(BitmapProcessor preProcessor)
                .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
                        // .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(100))// 淡入
                .build();
        return options;
    }
}

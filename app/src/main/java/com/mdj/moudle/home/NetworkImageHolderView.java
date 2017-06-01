package com.mdj.moudle.home;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.CBPageAdapter;
import com.bigkoo.convenientbanner.OnItemClickListener;
import com.mdj.R;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Sai on 15/8/4.
 * 网络图片加载例子
 */
public class NetworkImageHolderView implements CBPageAdapter.Holder<String>{
    private ImageView imageView;
    private OnItemClickListener listener;
    public NetworkImageHolderView(OnItemClickListener listener){
        this.listener = listener;
    }
    @Override
    public View createView(Context context) {
        //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, final int position, String data) {
        imageView.setImageResource(R.mipmap.default_banner);
        ImageLoader.getInstance().displayImage(data,imageView, DisplayImageOptionsUtil.getBannerOptions());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position);
//                点击事件
//                Toast.makeText(view.getContext(), "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
            }
        });
    }
//    public void UpdateUI(Context context,int position, String data) {
//        imageView.setImageResource(R.drawable.abc_ab_bottom_solid_light_holo);
//        ImageLoader.getInstance().displayImage(data,imageView);
//    }
}

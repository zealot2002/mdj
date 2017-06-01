package com.bigkoo.convenientbanner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.convenientbanner.R;

/**
 * 椤甸潰缈昏浆鎺т欢锛屾瀬鏂逛究鐨勫箍鍛婃爮
 * 鏀寔鏃犻檺寰幆锛岃嚜鍔ㄧ炕椤碉紝缈婚〉鐗规晥
 * @author Sai 鏀寔鑷姩缈婚〉
 */
public class ConvenientBanner<T> extends LinearLayout {
    private CBViewHolderCreator holderCreator;
    private List<T> mDatas;
    private int[] page_indicatorId;
    private ArrayList<ImageView> mPointViews = new ArrayList<ImageView>();
    private CBPageChangeListener pageChangeListener;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private OnItemClickListener listener;
    private CBPageAdapter pageAdapter;
    private CBLoopViewPager viewPager;
    private ViewPagerScroller scroller;
    private ViewGroup loPageTurningPoint;
    private long autoTurningTime;
    private boolean turning;
    private boolean canTurn = false;
    private boolean manualPageable = true;
    private boolean canLoop = true;
    public enum PageIndicatorAlign{
        ALIGN_PARENT_LEFT,ALIGN_PARENT_RIGHT,CENTER_HORIZONTAL
    }

    public enum Transformer {
        DefaultTransformer("DefaultTransformer"), AccordionTransformer(
                "AccordionTransformer"), BackgroundToForegroundTransformer(
                "BackgroundToForegroundTransformer"), CubeInTransformer(
                "CubeInTransformer"), CubeOutTransformer("CubeOutTransformer"), DepthPageTransformer(
                "DepthPageTransformer"), FlipHorizontalTransformer(
                "FlipHorizontalTransformer"), FlipVerticalTransformer(
                "FlipVerticalTransformer"), ForegroundToBackgroundTransformer(
                "ForegroundToBackgroundTransformer"), RotateDownTransformer(
                "RotateDownTransformer"), RotateUpTransformer(
                "RotateUpTransformer"), StackTransformer("StackTransformer"), TabletTransformer(
                "TabletTransformer"), ZoomInTransformer("ZoomInTransformer"), ZoomOutSlideTransformer(
                "ZoomOutSlideTransformer"), ZoomOutTranformer(
                "ZoomOutTranformer");

        private final String className;

        // 鏋勯�鍣ㄩ粯璁や篃鍙兘鏄痯rivate, 浠庤�淇濊瘉鏋勯�鍑芥暟鍙兘鍦ㄥ唴閮ㄤ娇鐢�       
        Transformer(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }
    }

    private Handler timeHandler = new Handler();
    private Runnable adSwitchTask = new Runnable() {
        @Override
        public void run() {
            if (viewPager != null && turning) {
                int page = viewPager.getCurrentItem() + 1;
                viewPager.setCurrentItem(page);
                timeHandler.postDelayed(adSwitchTask, autoTurningTime);
            }
        }
    };

    public ConvenientBanner(Context context,boolean canLoop) {
        this(context, null);
        this.canLoop = canLoop;
    }
    public ConvenientBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.ConvenientBanner);
        canLoop = a.getBoolean(R.styleable.ConvenientBanner_canLoop,true);
        init(context);
    }

    private void init(Context context) {
        View hView = LayoutInflater.from(context).inflate(
                R.layout.include_viewpager, this, true);
        viewPager = (CBLoopViewPager) hView.findViewById(R.id.cbLoopViewPager);
        loPageTurningPoint = (ViewGroup) hView
                .findViewById(R.id.loPageTurningPoint);
        initViewPagerScroll();
    }

    public ConvenientBanner setPages(CBViewHolderCreator holderCreator,List<T> datas){
        this.mDatas = datas;
        this.holderCreator = holderCreator;
        pageAdapter = new CBPageAdapter(holderCreator,mDatas);
        viewPager.setAdapter(pageAdapter,canLoop);
        viewPager.setBoundaryCaching(true);

        if (page_indicatorId != null)
            setPageIndicator(page_indicatorId);
        return this;
    }

    /**
     * 鍔犲叆鏂版暟鎹紝閫氱煡鏁版嵁鍙樺寲
     */
    public void notifyDataSetAdd(){
        pageAdapter.notifyDataSetChanged();
        if (page_indicatorId != null)
            setPageIndicator(page_indicatorId);
    }

    /**
     * 閫氱煡鏁版嵁鍙樺寲
     * 濡傛灉鍙槸澧炲姞鏁版嵁寤鸿浣跨敤 notifyDataSetAdd()
     */
    public void notifyDataSetChanged(){
//        pageAdapter = new CBPageAdapter(holderCreator,mDatas);
        pageAdapter.notifyDataSetChanged();
        viewPager.setAdapter(pageAdapter, canLoop);
        if (page_indicatorId != null)
            setPageIndicator(page_indicatorId);
    }

    /**
     * 璁剧疆搴曢儴鎸囩ず鍣ㄦ槸鍚﹀彲瑙�     *
     * @param visible
     */
    public ConvenientBanner setPointViewVisible(boolean visible) {
        loPageTurningPoint.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 搴曢儴鎸囩ず鍣ㄨ祫婧愬浘鐗�     *
     * @param page_indicatorId
     */
    public ConvenientBanner setPageIndicator(int[] page_indicatorId) {
        loPageTurningPoint.removeAllViews();
        mPointViews.clear();
        this.page_indicatorId = page_indicatorId;
        if(mDatas==null)return this;
        for (int count = 0; count < mDatas.size(); count++) {
            // 缈婚〉鎸囩ず鐨勭偣
            ImageView pointView = new ImageView(getContext());
            pointView.setPadding(5, 0, 5, 0);
            if (mPointViews.isEmpty())
                pointView.setImageResource(page_indicatorId[1]);
            else
                pointView.setImageResource(page_indicatorId[0]);
            mPointViews.add(pointView);
            loPageTurningPoint.addView(pointView);
        }
        pageChangeListener = new CBPageChangeListener(mPointViews,
                page_indicatorId);
        viewPager.setOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(viewPager.getCurrentItem());
        if(onPageChangeListener != null)pageChangeListener.setOnPageChangeListener(onPageChangeListener);

        return this;
    }

    /**
     * 鎸囩ず鍣ㄧ殑鏂瑰悜
     * @param align  涓変釜鏂瑰悜锛氬眳宸�锛圧elativeLayout.ALIGN_PARENT_LEFT锛夛紝灞呬腑 锛圧elativeLayout.CENTER_HORIZONTAL锛夛紝灞呭彸 锛圧elativeLayout.ALIGN_PARENT_RIGHT锛�     * @return
     */
    public ConvenientBanner setPageIndicatorAlign(PageIndicatorAlign align) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) loPageTurningPoint.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, align == PageIndicatorAlign.CENTER_HORIZONTAL ? RelativeLayout.TRUE : 0);
        loPageTurningPoint.setLayoutParams(layoutParams);
        return this;
    }

    /***
     * 鏄惁寮�惎浜嗙炕椤�     * @return
     */
    public boolean isTurning() {
        return turning;
    }

    /***
     * 寮�缈婚〉
     * @param autoTurningTime 鑷姩缈婚〉鏃堕棿
     * @return
     */
    public ConvenientBanner startTurning(long autoTurningTime) {
        //濡傛灉鏄鍦ㄧ炕椤电殑璇濆厛鍋滄帀
        if(turning){
            stopTurning();
        }
        //璁剧疆鍙互缈婚〉骞跺紑鍚炕椤�        canTurn = true;
        this.autoTurningTime = autoTurningTime;
        turning = true;
        timeHandler.postDelayed(adSwitchTask, autoTurningTime);
        return this;
    }

    public void stopTurning() {
        turning = false;
        timeHandler.removeCallbacks(adSwitchTask);
    }

    /**
     * 鑷畾涔夌炕椤靛姩鐢绘晥鏋�     *
     * @param transformer
     * @return
     */
    public ConvenientBanner setPageTransformer(PageTransformer transformer) {
        viewPager.setPageTransformer(true, transformer);
        return this;
    }

    /**
     * 鑷畾涔夌炕椤靛姩鐢绘晥鏋滐紝浣跨敤宸插瓨鍦ㄧ殑鏁堟灉
     *
     * @param transformer
     * @return
     */
    public ConvenientBanner setPageTransformer(Transformer transformer) {
        try {
            String pkName = getClass().getPackage().getName();
            viewPager.setPageTransformer(true, (PageTransformer) Class.forName(pkName +
                    ".transforms." + transformer.getClassName()).newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 璁剧疆ViewPager鐨勬粦鍔ㄩ�搴�     * */
    private void initViewPagerScroll() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new ViewPagerScroller(
                    viewPager.getContext());
//			scroller.setScrollDuration(1500);
            mScroller.set(viewPager, scroller);
            viewPager.setScroller(scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean isManualPageable() {
        return viewPager.isCanScroll();
    }

    public void setManualPageable(boolean manualPageable) {
        viewPager.setCanScroll(manualPageable);
    }

    //瑙︾鎺т欢鐨勬椂鍊欙紝缈婚〉搴旇鍋滄锛岀寮�殑鏃跺�濡傛灉涔嬪墠鏄紑鍚簡缈婚〉鐨勮瘽鍒欓噸鏂板惎鍔ㄧ炕椤�    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP||action == MotionEvent.ACTION_CANCEL||action == MotionEvent.ACTION_OUTSIDE) {
            // 寮�缈婚〉
            if (canTurn)startTurning(autoTurningTime);
        } else if (action == MotionEvent.ACTION_DOWN) {
            // 鍋滄缈婚〉
            if (canTurn)stopTurning();
        }
        return super.dispatchTouchEvent(ev);
    }

    //鑾峰彇褰撳墠鐨勯〉闈ndex
    public int getCurrentPageIndex(){
        if (viewPager!=null) {
            return viewPager.getCurrentItem();
        }
        return -1;
    }

    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }

    /**
     * 璁剧疆缈婚〉鐩戝惉鍣�     * @param onPageChangeListener
     * @return
     */
    public ConvenientBanner setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        //濡傛灉鏈夐粯璁ょ殑鐩戝惉鍣紙鍗虫槸浣跨敤浜嗛粯璁ょ殑缈婚〉鎸囩ず鍣級鍒欐妸鐢ㄦ埛璁剧疆鐨勪緷闄勫埌榛樿鐨勪笂闈紝鍚﹀垯灏辩洿鎺ヨ缃�       
        if(pageChangeListener != null)pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        else viewPager.setOnPageChangeListener(onPageChangeListener);
        return this;
    }

    public boolean isCanLoop() {
        return viewPager.isCanLoop();
    }

    /**
     * 鐩戝惉item鐐瑰嚮
     * @param onItemClickListener
     */
    public ConvenientBanner setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener == null) {
            viewPager.setOnClickListener(null);
            return this;
        }
        this.listener = onItemClickListener;
        if(pageAdapter!=null){
        	pageAdapter.setOnItemClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(getCurrentPageIndex());
                }
            });
        }
        return this;
    }

    /**
     * 璁剧疆ViewPager鐨勬粴鍔ㄩ�搴�     * @param scrollDuration
     */
    public void setScrollDuration(int scrollDuration){
        scroller.setScrollDuration(scrollDuration);
    }

    public int getScrollDuration() {
        return scroller.getScrollDuration();
    }
}

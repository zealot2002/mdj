package com.mdj.moudle.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Created by tt on 2016/9/7.
 */
public class TabMenuWidget extends HorizontalScrollView {
    private LinearLayout llTabMenu;
    public interface CustomTabMenuBridge {
        View getView(int position, ViewGroup parent);
        int getCount();
        void onClick(int i, ViewGroup parent);
    }
    private CustomTabMenuBridge customTabMenuBridge;
    /**************************************************************************************/

    public TabMenuWidget(Context context) {
        this(context, null);
    }

    public TabMenuWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabMenuWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);
        llTabMenu = new LinearLayout(context);
        addView(llTabMenu, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public void setCustomTabView(CustomTabMenuBridge customTabMenuBridge) {
        this.customTabMenuBridge = customTabMenuBridge;
        populateTabStrip();
    }

    public View getLlTabMenu(){
        return llTabMenu;
    }

    private void populateTabStrip() {
        final OnClickListener tabClickListener = new TabClickListener();
        for (int i = 0; i < customTabMenuBridge.getCount(); i++) {
            View tabView = customTabMenuBridge.getView(i,llTabMenu);
            tabView.setOnClickListener(tabClickListener);
            llTabMenu.addView(tabView);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        if (mViewPager != null) {
//            scrollToTab(mViewPager.getCurrentItem(), 0);
//        }
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
//        final int tabStripChildCount = mTabStrip.getChildCount();
//        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
//            return;
//        }
//
//        View selectedChild = mTabStrip.getChildAt(tabIndex);
//        if (selectedChild != null) {
//            int targetScrollX = selectedChild.getLeft() + positionOffset;
//
//            if (tabIndex > 0 || positionOffset > 0) {
//                // If we're not at the first child and are mid-scroll, make sure we obey the offset
//                targetScrollX -= mTitleOffset;
//            }
//            scrollTo(targetScrollX, 0);
//        }
    }

    private class TabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < llTabMenu.getChildCount(); i++) {
                if (v == llTabMenu.getChildAt(i)) {
                    customTabMenuBridge.onClick(i,llTabMenu);
                    return;
                }
            }
        }
    }
}

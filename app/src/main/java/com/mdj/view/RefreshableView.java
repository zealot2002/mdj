package com.mdj.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.utils.CommonUtil;

import java.util.Calendar;

/**
 * 刷新控制view
 * 
 * @author Nono
 * 
 */
public class RefreshableView extends LinearLayout {
	private static final int INVALID_DOWN_Y = 9999;
	private static final String TAG = "LILITH";
	private Scroller scroller;
    private static final float SCROLL_RATIO = 0.3f;// 阻尼系数

	private View refreshView;
	// private ImageView refreshIndicatorView;
	private int refreshTargetTop;

	private TextView downTextView;
	private TextView timeTextView;
	private LinearLayout reFreshTimeLayout;// 显示上次刷新时间的layout
	private RefreshListener refreshListener;

	private String downTextString;
	private String releaseTextString;

	// private Long refreshTime = null;
	private int lastX;
	private int lastY;
	// 拉动标记
	private boolean isDragging = false;
	// 是否可刷新标记
	private boolean isRefreshEnabled = true;
	// 在刷新中标记
	private boolean isRefreshing = false;
	Calendar LastRefreshTime;

	private Context mContext;

	private int lastResourceIndex = 0;

	public RefreshableView(Context context) {
		super(context);
		mContext = context;
	}

	public RefreshableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
        refreshTargetTop = 0-CommonUtil.dip2px(mContext,70/*@dimen/refresh_view_height*/);
		init();

	}

	private void init() {
		// TODO Auto-generated method stub
		// 滑动对象，
		LastRefreshTime = Calendar.getInstance();
		scroller = new Scroller(mContext);

		// 刷新视图顶端的的view
		refreshView = LayoutInflater.from(mContext).inflate(
				R.layout.home_refresh_top_item, null);
		// 指示器view
		// refreshIndicatorView = (ImageView)
		// refreshView.findViewById(R.id.indicator);

		// 下拉显示text
		downTextView = (TextView) refreshView.findViewById(R.id.refresh_hint);
		// 下来显示时间
		timeTextView = (TextView) refreshView.findViewById(R.id.refresh_time);
		reFreshTimeLayout = (LinearLayout) refreshView
				.findViewById(R.id.refresh_time_layout);

		LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, -refreshTargetTop);
		lp.topMargin = refreshTargetTop;
		lp.gravity = Gravity.CENTER;
		addView(refreshView, lp);
		downTextString = mContext.getResources().getString(
				R.string.refresh_down_text);
		releaseTextString = mContext.getResources().getString(
				R.string.refresh_release_text);

	}

	/**
	 * 设置上次刷新时间
	 *
	 */
	private void setLastRefreshTimeText() {
		reFreshTimeLayout.setVisibility(View.VISIBLE);
		Calendar NowTime = Calendar.getInstance();
		long l = NowTime.getTimeInMillis() - LastRefreshTime.getTimeInMillis();
		int days = new Long(l / (1000 * 60 * 60 * 24)).intValue();
		int hour = new Long(l / (1000 * 60 * 60)).intValue();
		int min = new Long(l / (1000 * 60)).intValue();
		if (days != 0) {
			timeTextView.setText(days + "天");
		} else if (hour != 0) {
			timeTextView.setText(hour + "小时");
		} else if (min != 0) {
			timeTextView.setText(min + "分钟");
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int y = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(refreshListener.canPullDownRefresh()){
				// 记录下y坐标
				lastY = y;
			}else{
				lastY = INVALID_DOWN_Y;
				return false;
			}
			break;

		case MotionEvent.ACTION_MOVE:
			if(lastY == INVALID_DOWN_Y)
				return false;
			// y移动坐标
			int m = y - lastY;
			if (((m < 6) && (m > -1)) || (!isDragging)) {
				setLastRefreshTimeText();
				doMovement(m);
			}
			// 记录下此刻y坐标
			this.lastY = y;
			break;

		case MotionEvent.ACTION_UP:
			if(lastY == INVALID_DOWN_Y)
				return false;
			fling();
			break;
		}
		return true;
	}

	/**
	 * up事件处理
	 */
	private void fling() {
		// TODO Auto-generated method stub
        LinearLayout.LayoutParams lp = (LayoutParams) refreshView
				.getLayoutParams();
		if (lp.topMargin > 0) {// 拉到了触发可刷新事件
			refresh();
		} else {
			returnInitState();
		}
	}

	private void returnInitState() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		scroller.startScroll(0, i, 0, refreshTargetTop,1);
		lastResourceIndex = 0;
		this.refreshView.invalidate();
		invalidate();
	}

	public boolean isRefreshing(){
		return isRefreshing;
	}
	private void refresh() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		scroller.startScroll(0, i, 0, 0-i,1);
		invalidate();/*异步行为，所以下面的行为要sleep一下，保证这里执行完毕*/
		if (refreshListener != null) {
			isRefreshing = true;
            final RefreshableView view = this;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshListener.onRefresh(view);
                        }
                    });
                }
            }).start();
		}
	}

	/**
     * 
     */
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (scroller.computeScrollOffset()) {
			int i = this.scroller.getCurrY();
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
					.getLayoutParams();
			int k = Math.max(i, refreshTargetTop);
			lp.topMargin = k;
			this.refreshView.setLayoutParams(lp);
			this.refreshView.invalidate();
			invalidate();
		}
	}

	/**
	 * 下拉move事件处理
	 * 
	 * @param moveY
	 */
	private void doMovement(int moveY) {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams lp = (LayoutParams) refreshView
				.getLayoutParams();
		if (moveY > 0) {
			// 获取view的上边距
			float f1 = lp.topMargin;
			float f2 = moveY * 0.3F;
			int i = (int) (f1 + f2);
			// 修改上边距
			lp.topMargin = i;
			// 修改后刷新
			refreshView.setLayoutParams(lp);
			refreshView.invalidate();
			invalidate();
		} else {
			float f1 = lp.topMargin;
			int i = (int) (f1 + moveY * 0.9F);
			if (i >= refreshTargetTop) {
				lp.topMargin = i;
				// 修改后刷新
				refreshView.setLayoutParams(lp);
				refreshView.invalidate();
				invalidate();
			} else {

			}
		}

		timeTextView.setVisibility(View.VISIBLE);
		// if(refreshTime!= null){
		// setRefreshTime(refreshTime);
		// }
		downTextView.setVisibility(View.VISIBLE);

		// refreshIndicatorView.setVisibility(View.VISIBLE);
		if (lp.topMargin > 0) {
			downTextView.setText(R.string.refresh_release_text);
			// refreshIndicatorView.setImageResource(R.mipmap.refresh_arrow_up);
		} else {
			downTextView.setText(R.string.refresh_down_text);
			// refreshIndicatorView.setImageResource(R.mipmap.refresh_arrow_down);
		}
	}

	public void setRefreshEnabled(boolean b) {
		this.isRefreshEnabled = b;
	}

	public void setRefreshListener(RefreshListener listener) {
		this.refreshListener = listener;
	}

	/**
	 * 结束刷新事件
	 */
	public void finishRefresh() {
        if(!isRefreshing){
            return;
        }
		isRefreshing = false;
		LastRefreshTime = Calendar.getInstance();
		timeTextView.setVisibility(View.VISIBLE);
		returnInitState();
	}

	/*
	 * 该方法一般和ontouchEvent 一起用 (non-Javadoc)
	 * 
	 * @see
	 * android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		int action = e.getAction();
		int y = (int) e.getRawY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if(refreshListener.canPullDownRefresh()){
				// 记录下y坐标
				lastY = y;
			}else{
				lastY = INVALID_DOWN_Y;
				return false;
			}
			break;

		case MotionEvent.ACTION_MOVE:
			if(lastY == INVALID_DOWN_Y)
				return false;
			// y移动坐标
			int m = y - lastY;

			// 记录下此刻y坐标
			this.lastY = y;
			if (m > 6 && canScroll()) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:

			break;

		case MotionEvent.ACTION_CANCEL:

			break;
		}
		return false;
	}

	private boolean canScroll() {
		View childView;
		
		if (getChildCount() > 1) {
			childView = this.getChildAt(1);
			if (childView instanceof ListView) {
				int top = ((ListView) childView).getChildAt(0).getTop();
				int pad = ((ListView) childView).getListPaddingTop();
				if ((Math.abs(top - pad)) < 3
						&& ((ListView) childView).getFirstVisiblePosition() == 0) {
					return true;
				} else {
					return false;
				}
			} else if (childView instanceof ScrollView) {
				if (((ScrollView) childView).getScrollY() == 0) {
					return true;
				} else {
					return false;
				}
			} else if (childView instanceof RelativeLayout) {
				if (((RelativeLayout) childView).getScrollY() == 0) {
					return true;
				} else {
					return false;
				}
			} else if (childView instanceof LinearLayout) {
				if (((LinearLayout) childView).getScrollY() == 0) {
					return true;
				} else {
					return false;
				}
			} else if (childView instanceof FrameLayout) {
                if (((FrameLayout)childView).getChildCount() > 1) {//need modify
                    View childrenView = ((FrameLayout) childView).getChildAt(1);
                    if (childrenView instanceof ListView) {
                        int top = ((ListView) childrenView).getChildAt(0).getTop();
                        int pad = ((ListView) childrenView).getListPaddingTop();
                        if ((Math.abs(top - pad)) < 3
                                && ((ListView) childrenView).getFirstVisiblePosition() == 0) {
                            return true;
                        } else {
                            return false;
                        }
                    }else if (childrenView instanceof LinearLayout) {
                        if (((LinearLayout) childrenView).getScrollY() == 0) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }else {

			}

		}else{

		}
		return false;
	}

	/**
	 * 刷新监听接口
	 * 
	 * @author Nono
	 * 
	 */
	public interface RefreshListener {
		void onRefresh(RefreshableView view);//通知使用者
		boolean canPullDownRefresh();//使用者负责实现
	}

}
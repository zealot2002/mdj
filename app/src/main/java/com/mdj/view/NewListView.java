package com.mdj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

public class NewListView extends ListView {
	private boolean MMIEnable = true;
	
	public NewListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public NewListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public NewListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	public void setMMIEnable(boolean MMIEnable){
		this.MMIEnable = MMIEnable;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if(!MMIEnable)//消耗掉事件
			return true;
		return super.dispatchTouchEvent(event);
	}
	
}
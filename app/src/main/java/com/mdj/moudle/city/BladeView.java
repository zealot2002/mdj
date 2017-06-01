package com.mdj.moudle.city;

import com.mdj.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BladeView extends View {
	private OnItemClickListener mOnItemClickListener;
	String[] KEYS = null;
	int choose = -1;
	Paint paint = new Paint();

	public BladeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BladeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BladeView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		if (showBkg) {
//			canvas.drawColor(getResources().getColor(R.color.text_black));
//		}
		if(KEYS==null){
			return;
		}
		int height = getHeight();
		int width = getWidth();
		int singleHeight = (height / KEYS.length);
		for (int i = 0; i < KEYS.length; i++) {
//			paint.setColor(Color.parseColor("#ff2f2f2f"));
			paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.bladeview_fontsize));//设置字体的大小
			if (i == choose) {
//				paint.setColor(Color.parseColor("#3399ff"));
			}
			float xPos = width / 2 - paint.measureText(KEYS[i]) / 2;
			float yPos = singleHeight * i +singleHeight;
			canvas.drawText(KEYS[i], xPos, yPos, paint);
			paint.reset();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final int c = (int) (y / getHeight() * KEYS.length);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (oldChoose != c) {
				if (c >= 0 && c < KEYS.length) {	//让第一个字母响应点击事件
					performItemClicked(c);
					choose = c;
					invalidate();
				}
			}

			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c) {
				if (c >= 0 && c < KEYS.length) {	//让第一个字母响应点击事件
					performItemClicked(c);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			choose = -1;
			invalidate();
			break;
		}
		return true;
	}

	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	private void performItemClicked(int item) {
		if (mOnItemClickListener != null) {
			mOnItemClickListener.onItemClick(KEYS[item]);
		}
	}

	public interface OnItemClickListener {
		void onItemClick(String s);
	}

	public void setKeys(String[] array) {
		KEYS = array;
	}

}

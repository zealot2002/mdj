package com.mdj.view.textView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ChineseButton extends Button {

	public ChineseButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		/*
		 * 必须事先在assets底下创建一fonts文件夹 并放入要使用的字体文件(.ttf)
		 * 并提供相对路径给creatFromAsset()来创建Typeface对象
		 */
//		Typeface fontFace = Typeface.createFromAsset(context.getAssets(),"fonts/mdj.ttf");
		// 字体文件必须是true type font的格式(ttf)；
		// 当使用外部字体却又发现字体没有变化的时候(以 Droid Sans代替)，通常是因为
		// 这个字体android没有支持,而非你的程序发生了错误
//		setTypeface(fontFace);
	}
}

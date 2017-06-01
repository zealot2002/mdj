package com.mdj.view.textView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;
/**
* @Title: ChineseRadioButton.java 
* @Package com.mdj.view.textView 
* @Description: 更改字体的选择按钮
* @author Jalen  c9n9m@163.com  
* @date 2015年3月28日 下午1:40:08 
* @version V1.0
 */
public class ChineseRadioButton extends RadioButton {

	public ChineseRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		/*
		 * 必须事先在assets底下创建一fonts文件夹 并放入要使用的字体文件(.ttf)
		 * 并提供相对路径给creatFromAsset()来创建Typeface对象
		 */
		Typeface fontFace = Typeface.createFromAsset(context.getAssets(),"fonts/mdj.ttf");
		// 字体文件必须是true type font的格式(ttf)；
		// 当使用外部字体却又发现字体没有变化的时候(以 Droid Sans代替)，通常是因为
		// 这个字体android没有支持,而非你的程序发生了错误
		setTypeface(fontFace);
	}
}

package com.guohao.custom;

import com.guohao.graduationdesign_app.R;
import com.guohao.util.Util;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MyBg extends FrameLayout implements OnClickListener {
	private View view;
	private TextView textView01,textView02;
	private static int TAG = 0;

	public MyBg(Context context, AttributeSet attrs) {
		super(context, attrs);
		view = LayoutInflater.from(context).inflate(R.layout.custom_my_bg, this);
		textView01 = (TextView) view.findViewById(R.id.id_textview_text01);
		textView02 = (TextView) view.findViewById(R.id.id_textview_text02);
		
		
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyBg);
		String text01 = array.getString(R.styleable.MyBg_text01);
		String text02 = array.getString(R.styleable.MyBg_text02);
		
		if (textView01 != null) {
			textView01.setText(text01);
		}
		if (textView02 != null && !text02.equals("")) {
			textView02.setText(Html.fromHtml("<u>"+text02+"</u>"));
			textView02.setTag(TAG++);
			textView02.setOnClickListener(this);
		}
		array.recycle();
	}

	@Override
	public void onClick(View v) {
		int TAG = (int) v.getTag();
		switch (TAG) {
		case 0:
			Util.showToast(getContext(), "用户名");
			break;
		case 1:
			Util.showToast(getContext(), "密码");
			break;
		case 2:
			Util.showToast(getContext(), "图片质量");
			break;
		case 3:
			Util.showToast(getContext(), "视频质量");
			break;
		}
	}
	
}

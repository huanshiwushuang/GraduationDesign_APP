package com.guohao.custom;

import com.guohao.graduationdesign_app.R;
import com.guohao.util.HttpUtil;
import com.guohao.util.Util;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MyBg extends FrameLayout implements OnClickListener {
	private View view;
	private ImageView image;
	private TextView textView01,textView02;
	public static int TAG = 0;

	public MyBg(Context context, AttributeSet attrs) {
		super(context, attrs);
		view = LayoutInflater.from(context).inflate(R.layout.custom_my_bg, this);
		
		image = (ImageView) view.findViewById(R.id.id_imageview_pic);
		textView01 = (TextView) view.findViewById(R.id.id_textview_text01);
		textView02 = (TextView) view.findViewById(R.id.id_textview_text02);
		
		
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyBg);
		int imgId = array.getResourceId(R.styleable.MyBg_image, R.drawable.user_id);
		String text01 = array.getString(R.styleable.MyBg_text01);
		String text02 = array.getString(R.styleable.MyBg_text02);
		
		image.setImageResource(imgId);
		textView01.setText(text01);
		if (textView02 != null && !text02.equals("")) {
			textView02.setText(Html.fromHtml("<u>"+text02+"</u>"));
			textView02.setTag(TAG++);
			textView02.setOnClickListener(this);
		}
		array.recycle();
	}

	public void setText(String text) {
		if (textView01 != null) {
			textView01.setText(text);
		}
	}
	
	@Override
	public void onClick(View v) {
		int TAG = (int) v.getTag();
		switch (TAG) {
		case 0:
			Util.showAlertDialog03(getContext(), "更新昵称", "请输入新昵称", HttpUtil.VISIT_USER_TABLE_UPDATE_USERNAME);
			break;
		case 1:
			Util.showAlertDialog03(getContext(), "更新密码", "请输入新密码", HttpUtil.VISIT_USER_TABLE_UPDATE_PWD);
			break;
		case 2:
			Util.showToast(getContext(), "图片质量");
			break;
		case 3:
			Util.showToast(getContext(), "视频质量");
			break;
		case 4:
			Util.showAlertDialog04(getContext(), "注销", "将清除账户信息,是否确定注销？");
			break;
		}
	}
	
}

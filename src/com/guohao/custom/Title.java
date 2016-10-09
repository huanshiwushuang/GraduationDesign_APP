package com.guohao.custom;



import com.guohao.graduationdesign_app.R;
import com.guohao.util.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Title extends LinearLayout implements OnTouchListener,OnClickListener {
	private ImageView imageView; 
	private Context mContext;
	private EditText editText;

	public Title(Context context, AttributeSet attrs) {
		super(context, attrs);
		View view = LayoutInflater.from(context).inflate(R.layout.custom_title, this);
		imageView = (ImageView) view.findViewById(R.id.id_imageview_search);
		imageView.setOnTouchListener(this);
		imageView.setOnClickListener(this);
		mContext = context;
	}

	public void onClick(View v) {
		Util.showAlertDialog01(mContext);
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			imageView.setImageResource(R.drawable.search_blue);
			break;
		case MotionEvent.ACTION_UP:
			//可能是防止 单击事件 被屏蔽
			v.performClick();
			imageView.setImageResource(R.drawable.search_white);
			break;
		default:
			break;
		}
		return true;
	}

}

package com.guohao.custom;

import com.guohao.graduationdesign_app.MoviePlayActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class PlayButton extends TextView {

	public PlayButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public PlayButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public PlayButton(Context context) {
		super(context);
	}
	
	public void setClickRequest(final String url) {
		this.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MoviePlayActivity.actionStart(getContext(), url);
			}
		});
	}
	
}

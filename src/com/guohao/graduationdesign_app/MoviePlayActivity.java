package com.guohao.graduationdesign_app;

import com.guohao.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MoviePlayActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Vitamio.isInitialized(this);
		Util.showToast(MoviePlayActivity.this, getIntent().getStringExtra("playUrl"));
	}
	
	
	
	
	
	
	
	
	
	
	public static void actionStart(Context c, String playUrl) {
		Intent intent = new Intent(c, MoviePlayActivity.class);
		intent.putExtra("playUrl", playUrl);
		c.startActivity(intent);
	}
}

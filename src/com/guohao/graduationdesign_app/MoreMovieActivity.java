package com.guohao.graduationdesign_app;

import com.guohao.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MoreMovieActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initView();
	}
	
	
	
	
	private void initView() {
		Intent intent = getIntent();
		Util.showToast(MoreMovieActivity.this, intent.getStringExtra("flag"));
	}




	public static void actionStart(Context c, String flag) {
		Intent intent = new Intent(c, MoreMovieActivity.class);
		intent.putExtra("flag", flag);
		c.startActivity(intent);
	}
}

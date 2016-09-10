package com.guohao.graduationdesign_app;


import com.guohao.util.Data;
import com.guohao.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class ShowActivity extends Activity {
	private Handler handler;
	private Runnable r;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);
		initView();
	}

	private void initView() {
		handler = new Handler();
		r = new Runnable() {
			
			public void run() {
				SharedPreferences pre = Util.getPreferences(ShowActivity.this);
				String lastIntoTime = pre.getString(Data.K_Time_Last_Into, "");
				if (Util.isEmpty(lastIntoTime)) {
					LoginActivity.actionStart(ShowActivity.this);
				}else if ((System.currentTimeMillis()-Long.valueOf(lastIntoTime))/1000.0/60.0/60.0 > 3*24) {
					//如果此时登录时间距离上次大于了3天，请重新登录。
					LoginActivity.actionStart(ShowActivity.this);
					showToast("登录状态失效，请重新登录");
				}else {
					MainActivity.actionStart(ShowActivity.this);
				}
				finish();
			}
		};
		handler.postDelayed(r, 3*1000);
	}
	
	private void showToast(String msg) {
		Toast.makeText(ShowActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		handler.removeCallbacks(r);
	}
}

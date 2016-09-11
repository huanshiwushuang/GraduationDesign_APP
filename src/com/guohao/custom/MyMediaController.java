package com.guohao.custom;

import com.guohao.graduationdesign_app.R;
import com.guohao.util.Util;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class MyMediaController extends MediaController {
	private View v;
	//感知屏幕手势变化
	private GestureDetector gestureDetector;

	//界面布局 top 
	private TextView flowTextView, nameTextView, batteryTextView, timeTextView;
	private ImageView batteryImageView;

	//界面布局 bottom
	private TextView currentTextView, totalTextView;
	private ImageView statusImageView;
	private SeekBar seekBar;

	//构造函数---传进来用于通过 MediaController 控制视频的播放
	private VideoView videoView;
	//Activity 也是上下文，经常用到
	private Activity activity;
//	private int controllerWidth = 0;// 设置controllerWidth宽度为了使横屏时top显示在屏幕顶端

	// videoview 用于对视频进行控制的等，activity为了退出
	public MyMediaController(VideoView videoView, Activity activity) {
		super(activity);
		this.videoView = videoView;
		this.activity = activity;
		initView();

//		DisplayMetrics metrics = new DisplayMetrics();
//		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//		controllerWidth = metrics.widthPixels;
//		gestureDetector = new GestureDetector(activity, new MyGestureListener());
		
		
	}

	private void initView() {
		v = LayoutInflater.from(activity).inflate(R.layout.custom_mediacontroller, null);
		
		flowTextView = (TextView) v.findViewById(R.id.id_textview_flow);
		nameTextView = (TextView) v.findViewById(R.id.id_textview_file_name);
		batteryTextView = (TextView) v.findViewById(R.id.id_textview_battery);
		batteryImageView = (ImageView) v.findViewById(R.id.id_imageview_battery);
		timeTextView = (TextView) v.findViewById(R.id.id_textview_time);
		
		statusImageView = (ImageView) findViewById(R.id.mediacontroller_pause); 
		currentTextView = (TextView) findViewById(R.id.mediacontroller_time_current);
		seekBar = (SeekBar) findViewById(R.id.mediacontroller_progress);
		totalTextView = (TextView) findViewById(R.id.mediacontroller_time_total);
	}

	@Override
	protected View makeControllerView() {
		return v;
	}

//	@Override
//	public boolean dispatchKeyEvent(KeyEvent event) {
//		Log.d("guohao", "这里是：dispatchKeyEvent");
//		return true;
//		// return super.dispatchKeyEvent(event);
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if (gestureDetector.onTouchEvent(event))
//			return true;
//		// 处理手势结束
//		switch (event.getAction() & MotionEvent.ACTION_MASK) {
//		case MotionEvent.ACTION_UP:
//			break;
//		default:
//			break;
//		}
//		return super.onTouchEvent(event);
//	}
//
//	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
//		@Override
//		public boolean onSingleTapUp(MotionEvent e) {
//			return false;
//		}
//
//		@Override
//		public boolean onSingleTapConfirmed(MotionEvent e) {
//			// 当收拾结束，并且是单击结束时，控制器隐藏/显示
//			toggleMediaControlsVisiblity();
//			return super.onSingleTapConfirmed(e);
//		}
//
//		@Override
//		public boolean onDown(MotionEvent e) {
//			return true;
//		}
//
//		@Override
//		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//			return super.onScroll(e1, e2, distanceX, distanceY);
//		}
//
//		// 双击暂停或开始
//		@Override
//		public boolean onDoubleTap(MotionEvent e) {
//			playOrPause();
//			return true;
//		}
//	}
	
	//显示---瞬时流量
	public void setFlow(String flow) {
		if (flowTextView != null) {
			flowTextView.setText(flow);
		}
	}
	
	//显示电影名字。  疑惑：使用 Vitamio 的方法设置文件名，不行？
	public void setVideoName(String name) {
		if (nameTextView != null) {
			nameTextView.setText(name);
		}
	}
	
	// 显示---电量
	public void setBattery(String stringBattery) {
		if (timeTextView != null && batteryImageView != null) {
			batteryTextView.setText(stringBattery + "%");
			int battery = Integer.valueOf(stringBattery);
			if (battery < 15) {
				batteryImageView.setImageResource(R.drawable.dianchi_0);
			}else if (battery < 30 && battery >= 15) {
				batteryImageView.setImageResource(R.drawable.dianchi_1);
			}else if (battery < 45 && battery >= 30) {
				batteryImageView.setImageResource(R.drawable.dianchi_2);
			}else if (battery < 60 && battery >= 45) {
				batteryImageView.setImageResource(R.drawable.dianchi_3);
			}else if (battery < 75 && battery >= 60) {
				batteryImageView.setImageResource(R.drawable.dianchi_4);
			}else if (battery < 90 && battery >= 75) {
				batteryImageView.setImageResource(R.drawable.dianchi_5);
			}else if (battery > 90) {
				batteryImageView.setImageResource(R.drawable.dianchi_6);
			}
		}
	}

	//设置文字显示---时间
	public void setTime(String time) {
		if (timeTextView != null) {
			timeTextView.setText(time);
		}
	}

	
	

	// 隐藏/显示
//	private void toggleMediaControlsVisiblity() {
//		if (isShowing()) {
//			hide();
//		} else {
//			show();
//		}
//	}

	// 播放与暂停
//	private void playOrPause() {
//		if (videoView != null) {
//			if (videoView.isPlaying()) {
//				videoView.pause();
//			}else {
//				videoView.start();
//			}
//		}
//	}
}

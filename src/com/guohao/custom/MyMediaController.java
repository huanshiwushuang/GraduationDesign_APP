package com.guohao.custom;

import com.guohao.graduationdesign_app.R;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class MyMediaController extends MediaController implements OnClickListener,OnTouchListener,OnSeekBarChangeListener {
	private View v;
	private Runnable r;
	private Handler handler;
	//感知屏幕手势变化
	private GestureDetector gestureDetector;

	//界面布局 top 
	private TextView flowTextView, nameTextView, batteryTextView, timeTextView;
	private ImageView batteryImageView;

	//界面布局 bottom
//	private TextView currentTextView, totalTextView;
	private ImageView statusImageView;
	private SeekBar seekBar;

	//构造函数---传参
	private VideoView videoView;
	private Activity activity;

	// videoview 用于对视频进行控制的等，activity用处较大
	public MyMediaController(VideoView videoView, Activity activity) {
		super(activity);
		this.videoView = videoView;
		this.activity = activity;
		initView();
	}

	private void initView() {
		v = LayoutInflater.from(activity).inflate(R.layout.custom_mediacontroller, null);
		
		//获取布局界面top
		flowTextView = (TextView) v.findViewById(R.id.id_textview_flow);
		nameTextView = (TextView) v.findViewById(R.id.id_textview_file_name);
		batteryTextView = (TextView) v.findViewById(R.id.id_textview_battery);
		batteryImageView = (ImageView) v.findViewById(R.id.id_imageview_battery);
		timeTextView = (TextView) v.findViewById(R.id.id_textview_time);
		
		//获取布局界面bottom
		statusImageView = (ImageView) v.findViewById(R.id.mediacontroller_pause); 
		statusImageView.setOnClickListener(this);
		seekBar = (SeekBar) v.findViewById(R.id.mediacontroller_progress);
		seekBar.setOnSeekBarChangeListener(this);
		
//		因为这个 TextView 的id是Vitamio指定的id，就可以自动获取时间显示
//		currentTextView = (TextView) v.findViewById(R.id.mediacontroller_time_current);
//		因为这个 TextView 的id是Vitamio指定的id，就可以自动获取时间显示
//		totalTextView = (TextView) v.findViewById(R.id.mediacontroller_time_total);
		
		handler = new Handler();
		//手势操作
		gestureDetector = new GestureDetector(activity, new MyGestureListener());
		videoView.setOnTouchListener(this);
		v.setOnTouchListener(this);
	}
	
	//自定义 MediaController 返回 View
	@Override
	protected View makeControllerView() {
		return v;
	}
	
	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			toggleMediaControlsVisiblity();
			return true;
		}
		
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			playOrPause();
			return true;
		}
	}
	
	//------------------------------------------------------------------------------
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mediacontroller_pause:
			playOrPause();
			break;
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		v.performClick();
		return true;
	}
	
	//SeekBar 改变事件
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			show();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		long currentPosition = seekBar.getProgress();
		long videoPosition = currentPosition*videoView.getDuration()/1000;
		videoView.seekTo(videoPosition);
	}
	
	//------------------------------------------------------------------------------
	// 隐藏/显示
	private void toggleMediaControlsVisiblity() {
		if (isShowing()) {
			hide();
		} else {
			show();
		}
	}
	
	// 播放与暂停
	private void playOrPause() {
		if (videoView != null) {
			if (videoView.isPlaying()) {
				videoView.pause();
				statusImageView.setImageResource(R.drawable.mediacontroller_play);
			}else {
				videoView.start();
				statusImageView.setImageResource(R.drawable.mediacontroller_pause);
			}
		}
	}
	
	//------------------------------------------------------------------------------
	
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
	
	//设置是否常亮
	public void setScreenOn(Boolean keepScreenOn) {
		videoView.setKeepScreenOn(keepScreenOn);
	}

}

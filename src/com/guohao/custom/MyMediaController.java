package com.guohao.custom;

import com.guohao.graduationdesign_app.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

@SuppressLint("ClickableViewAccessibility")
public class MyMediaController extends MediaController implements OnCompletionListener,OnInfoListener,OnErrorListener,OnBufferingUpdateListener,OnClickListener,OnTouchListener,OnSeekBarChangeListener,OnSeekCompleteListener,OnPreparedListener {
	//在Activity销毁时，停止子线程
	public Boolean IsDestroy = false;
	//感知屏幕手势变化
	private GestureDetector gestureDetector;

	//界面布局 top、center和 bottom
	private TextView flowTextView, nameTextView, batteryTextView, timeTextView;
	private ImageView batteryImageView;
	//---------------------------------
	//来自---播放的 Activity 界面的布局文件的。
	private LinearLayout loadLinearLayout;
	private TextView loadTextView;
	//---------------------------------
	private ImageView statusImageView;
	private SeekBar seekBar;

	//构造函数---传参
	private VideoView videoView;
	private Activity activity;

	//其他
	private View v;
	private long LastSeek = 0;
	private Boolean IsLoadEnd = true;
	
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
		
		//获取---主---布局界面center  必须有 传进来的 activity 得到，如果用LayoutInflater得到的就是新的。
		loadLinearLayout = (LinearLayout) activity.findViewById(R.id.id_linearlayout_loading);
		loadTextView = (TextView) activity.findViewById(R.id.id_textview_load_percent);
		
		//获取布局界面bottom
		statusImageView = (ImageView) v.findViewById(R.id.mediacontroller_pause); 
		seekBar = (SeekBar) v.findViewById(R.id.mediacontroller_progress);
		
		statusImageView.setOnClickListener(this);
		seekBar.setOnSeekBarChangeListener(this);
		
		//手势操作
		gestureDetector = new GestureDetector(activity, new MyGestureListener());
		videoView.setOnTouchListener(this);
		videoView.setOnPreparedListener(this);
		videoView.setOnSeekCompleteListener(this);
		videoView.setOnBufferingUpdateListener(this);
		videoView.setOnInfoListener(this);
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
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		Log.d("guohao", "加载进度："+percent);
		loadTextView.setText(percent+"%");
	}
	@Override
	public void onPrepared(MediaPlayer mp) {
		Log.d("guohao", "最大的："+(int)videoView.getDuration());
		seekBar.setMax((int)videoView.getDuration());
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!IsDestroy) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Log.d("heihei", "这个IsLoadEnd："+IsLoadEnd);
					if (IsLoadEnd) {
						int i = (int) videoView.getCurrentPosition();
						seekBar.setProgress(i);
						Log.d("heihei", i+"");
					}
				}
			}
		}).start();
	}
	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
	    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
	    	pause();
	    	loadLinearLayout.setVisibility(View.VISIBLE); 
	    	loadTextView.setText("");
	    	IsLoadEnd = false;
	    	Log.d("guohao", "开始缓冲");
	      break;
	    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
	    	play();
	    	loadLinearLayout.setVisibility(View.GONE);
	    	IsLoadEnd = true;
	    	Log.d("guohao", "结束缓冲");
	      break;
	    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
	    	flowTextView.setText(extra+"kb/s");
	    	Log.d("guohao", "缓冲速度："+extra);
	      break;
	    }
	    return true;
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
		statusImageView.setImageResource(R.drawable.mediacontroller_play);
	}
	//------------------------------------------------------------------------------
	//SeekBar 改变事件
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		//这里必须判断，因为在子线程中让 SeekBar 跟随VideoView 播放进度自动前进的时候，是代码控制？？？？
		if (fromUser) {
			show();
			LastSeek = progress;
			videoView.seekTo(progress);
			Log.d("guohao", "当前进度："+progress);
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
	@Override
	public void onSeekComplete(MediaPlayer mp) {
		videoView.seekTo(LastSeek);
		Log.d("guohao", "最后进度："+LastSeek);
	}
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.d("guohao", "错误："+extra+"---"+what);
		return true;
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
	private void play() {
		videoView.start();
		statusImageView.setImageResource(R.drawable.mediacontroller_pause);
	}
	private void pause() {
		videoView.pause();
		statusImageView.setImageResource(R.drawable.mediacontroller_play);
	}
	//------------------------------------------------------------------------------
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

}

package com.guohao.graduationdesign_app;


import com.guohao.custom.MyMediaController;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class MoviePlayActivity extends Activity implements Callback,OnBufferingUpdateListener,
		OnErrorListener,OnSeekCompleteListener,OnCompletionListener,OnPreparedListener,OnVideoSizeChangedListener {
	private String playUrl;
	
	private VideoView videoView;
	private MyMediaController controller;
	private Handler handler;
	private BroadcastReceiver batteryBroadcastReceiver;
	private final int IS_TIME = 0;
	private final int IS_BATTERY = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//定义全屏参数
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		//获取当前窗体对象
		Window window = getWindow();
		//设置当前窗体为全屏显示
		window.setFlags(flag, flag);
		
		Vitamio.isInitialized(this);
		setContentView(R.layout.activity_movie_play);
		initData();
		initView();
		registerBatteryReceiver();
		startPlay();
		
		
	}

	private void registerBatteryReceiver() {
		//注册电量广播监听
	    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	    registerReceiver(batteryBroadcastReceiver, intentFilter);
	}

	private void startPlay() {
		videoView.setVideoPath(playUrl);
		videoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
		videoView.setMediaController(controller);
		videoView.requestFocus();
	}

	private void initView() {
		videoView = (VideoView) findViewById(R.id.id_videoview);
		controller = new MyMediaController(this, videoView, this);
		
		videoView.setOnBufferingUpdateListener(this);
		videoView.setOnPreparedListener(this);
		videoView.setOnCompletionListener(this);
		videoView.setOnErrorListener(this);
		videoView.setOnSeekCompleteListener(this);
		videoView.setKeepScreenOn(true);
		
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case IS_TIME:
					
					break;
				case IS_BATTERY:
					
					break;
				default:
					break;
				}
			}
		};
		
		batteryBroadcastReceiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
		            //获取当前电量
		            int level = intent.getIntExtra("level", 0);
		            //电量的总刻度
		            int scale = intent.getIntExtra("scale", 100);
		            //把它转成百分比
		            //tv.setText("电池电量为"+((level*100)/scale)+"%");
		            Message msg = new Message();
		            msg.obj = (level*100)/scale+"";
		            msg.what = IS_BATTERY;
		            handler.sendMessage(msg);
		        }
		    }
		};
	}

	private void initData() {
		playUrl = getIntent().getStringExtra("playUrl");
	}
	
	//------------------------------------------------------------------------
	
	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.setPlaybackSpeed(1.0f);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		Log.d("guohao", "更新进度："+percent);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
//		mp.reset();
		return false;
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		
	}
	
	//------------------------------------------------------------------------
	
	public static void actionStart(Context c, String playUrl) {
		Intent intent = new Intent(c, MoviePlayActivity.class);
		intent.putExtra("playUrl", playUrl);
		c.startActivity(intent);
	}
}

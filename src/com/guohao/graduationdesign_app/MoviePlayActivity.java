package com.guohao.graduationdesign_app;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.guohao.custom.MyMediaController;
import com.guohao.util.Data;
import com.guohao.util.Util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

public class MoviePlayActivity extends Activity {
	//播放的视频 Url
	private String playUrl;
	private String movieName;
	
	//显示视频的控件
	private VideoView videoView;
	//控制视频的控件
	private MyMediaController controller;
	private Handler handler;
	//用于接收系统发出的---电量改变的广播
	private BroadcastReceiver batteryBroadcastReceiver;
	//用于 Handler switch的
	private final int IS_TIME = 0;//这个 message 是侦测时间的
	private final int IS_BATTERY = 1;//这个 message 是侦测电量的
	
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
		
		//1-初始化数据---获取播放 Url
		initData();
		//2-初始化识图---获取 xml 文件中的控件 和 new 一些控件
		initView();
		//3-注册广播---监听电量变化
		registerBatteryReceiver();
		//4-根据时间 和 电量变化---改变 UI 的显示
		initSet();
		//5-开始播放视频
		startPlay();
	}

	

	private void startPlay() {
		videoView.setVideoPath(playUrl);
		int VideoQuality = Util.getPreferences(MoviePlayActivity.this).getInt(Data.K_Video_Quality, Data.V_Video_Quality_High);
		videoView.setVideoQuality(VideoQuality);
		videoView.setMediaController(controller);
		videoView.setAlpha(0.7f);
		videoView.requestFocus();
	}
	
	private void initSet() {
		//设置视频---名字
		controller.setVideoName(movieName);
		//设置屏幕常亮
		controller.setScreenOn(true);
		
		//每秒检查更新一次时间
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					//时间读取线程
			        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",Locale.CHINESE);
			        String str = sdf.format(new Date());
			        Message msg = new Message();
			        msg.obj = str;
			        msg.what = IS_TIME;
			        handler.sendMessage(msg);
			        try {
			            Thread.sleep(1000);
			        } catch (InterruptedException e) {
			            e.printStackTrace();
			        }
				}
			}
		}).start();
	}
	
	private void registerBatteryReceiver() {
		//注册电量广播监听
	    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	    registerReceiver(batteryBroadcastReceiver, intentFilter);
	}
	
	private void initView() {
		Util.showProgressDialog(this);
		videoView = (VideoView) findViewById(R.id.id_videoview);
		controller = new MyMediaController(videoView, this);
		videoView.setKeepScreenOn(true);
		
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case IS_TIME:
					controller.setTime((String)msg.obj);
					break;
				case IS_BATTERY:
					controller.setBattery((String)msg.obj);
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
		            Message msg = handler.obtainMessage();
		            msg.obj = (level*100)/scale+"";
		            msg.what = IS_BATTERY;
		            handler.sendMessage(msg);
		        }
		    }
		};
		
		
	}

	private void initData() {
		Intent i = getIntent();
		movieName = i.getStringExtra("movieName");
		playUrl = i.getStringExtra("playUrl");
	}
	
	//------------------------------------------------------------------------
	
	public static void actionStart(Context c, String movieName, String playUrl) {
		Intent intent = new Intent(c, MoviePlayActivity.class);
		intent.putExtra("movieName", movieName);
		intent.putExtra("playUrl", playUrl);
		c.startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(batteryBroadcastReceiver);
		//销毁更新 SeekBar 的子线程
		MyMediaController.IsDestroy = true;
	}
}

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
	//���ŵ���Ƶ Url
	private String playUrl;
	private String movieName;
	
	//��ʾ��Ƶ�Ŀؼ�
	private VideoView videoView;
	//������Ƶ�Ŀؼ�
	private MyMediaController controller;
	private Handler handler;
	//���ڽ���ϵͳ������---�����ı�Ĺ㲥
	private BroadcastReceiver batteryBroadcastReceiver;
	//���� Handler switch��
	private final int IS_TIME = 0;//��� message �����ʱ���
	private final int IS_BATTERY = 1;//��� message ����������
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//����ȫ������
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		//��ȡ��ǰ�������
		Window window = getWindow();
		//���õ�ǰ����Ϊȫ����ʾ
		window.setFlags(flag, flag);
		
		Vitamio.isInitialized(this);
		setContentView(R.layout.activity_movie_play);
		
		//1-��ʼ������---��ȡ���� Url
		initData();
		//2-��ʼ��ʶͼ---��ȡ xml �ļ��еĿؼ� �� new һЩ�ؼ�
		initView();
		//3-ע��㲥---���������仯
		registerBatteryReceiver();
		//4-����ʱ�� �� �����仯---�ı� UI ����ʾ
		initSet();
		//5-��ʼ������Ƶ
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
		//������Ƶ---����
		controller.setVideoName(movieName);
		//������Ļ����
		controller.setScreenOn(true);
		
		//ÿ�������һ��ʱ��
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					//ʱ���ȡ�߳�
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
		//ע������㲥����
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
		            //��ȡ��ǰ����
		            int level = intent.getIntExtra("level", 0);
		            //�������̶ܿ�
		            int scale = intent.getIntExtra("scale", 100);
		            //����ת�ɰٷֱ�
		            //tv.setText("��ص���Ϊ"+((level*100)/scale)+"%");
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
		//���ٸ��� SeekBar �����߳�
		MyMediaController.IsDestroy = true;
	}
}

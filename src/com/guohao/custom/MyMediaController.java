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
	//��֪��Ļ���Ʊ仯
	private GestureDetector gestureDetector;

	//���沼�� top 
	private TextView flowTextView, nameTextView, batteryTextView, timeTextView;
	private ImageView batteryImageView;

	//���沼�� bottom
	private TextView currentTextView, totalTextView;
	private ImageView statusImageView;
	private SeekBar seekBar;

	//���캯��---����������ͨ�� MediaController ������Ƶ�Ĳ���
	private VideoView videoView;
	//Activity Ҳ�������ģ������õ�
	private Activity activity;
//	private int controllerWidth = 0;// ����controllerWidth���Ϊ��ʹ����ʱtop��ʾ����Ļ����

	// videoview ���ڶ���Ƶ���п��Ƶĵȣ�activityΪ���˳�
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
//		Log.d("guohao", "�����ǣ�dispatchKeyEvent");
//		return true;
//		// return super.dispatchKeyEvent(event);
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if (gestureDetector.onTouchEvent(event))
//			return true;
//		// �������ƽ���
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
//			// ����ʰ�����������ǵ�������ʱ������������/��ʾ
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
//		// ˫����ͣ��ʼ
//		@Override
//		public boolean onDoubleTap(MotionEvent e) {
//			playOrPause();
//			return true;
//		}
//	}
	
	//��ʾ---˲ʱ����
	public void setFlow(String flow) {
		if (flowTextView != null) {
			flowTextView.setText(flow);
		}
	}
	
	//��ʾ��Ӱ���֡�  �ɻ�ʹ�� Vitamio �ķ��������ļ��������У�
	public void setVideoName(String name) {
		if (nameTextView != null) {
			nameTextView.setText(name);
		}
	}
	
	// ��ʾ---����
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

	//����������ʾ---ʱ��
	public void setTime(String time) {
		if (timeTextView != null) {
			timeTextView.setText(time);
		}
	}

	
	

	// ����/��ʾ
//	private void toggleMediaControlsVisiblity() {
//		if (isShowing()) {
//			hide();
//		} else {
//			show();
//		}
//	}

	// ��������ͣ
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

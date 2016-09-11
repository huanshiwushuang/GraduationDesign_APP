package com.guohao.custom;

import com.guohao.graduationdesign_app.R;

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
	private GestureDetector gestureDetector;

	private TextView flowTextView, nameTextView, batteryTextView, timeTextView;
	private ImageView batteryImageView;

	private TextView currentTextView, totalTextView;
	private ImageView statusImageView;
	private SeekBar seekBar;

	private VideoView videoView;
	private Activity activity;
	private Context context;
	private int controllerWidth = 0;// ����controllerWidth���Ϊ��ʹ����ʱtop��ʾ����Ļ����

	public MyMediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyMediaController(Context context) {
		super(context);
	}

	// videoview ���ڶ���Ƶ���п��Ƶĵȣ�activityΪ���˳�
	public MyMediaController(Context context, VideoView videoView, Activity activity) {
		super(context);
		this.context = context;
		this.videoView = videoView;
		this.activity = activity;

		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		controllerWidth = metrics.widthPixels;
		gestureDetector = new GestureDetector(context, new MyGestureListener());
	}

	@Override
	protected View makeControllerView() {
		View v = LayoutInflater.from(context).inflate(R.layout.custom_mediacontroller, null);
		v.setMinimumHeight(controllerWidth);

		flowTextView = (TextView) findViewById(R.id.id_textview_flow);
		nameTextView = (TextView) findViewById(R.id.id_textview_name);
		batteryTextView = (TextView) v.findViewById(R.id.id_textview_battery);
		batteryImageView = (ImageView) v.findViewById(R.id.id_imageview_battery);
		timeTextView = (TextView) v.findViewById(R.id.id_textview_time);

		return v;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.d("guohao", "�����ǣ�dispatchKeyEvent");
		return true;
		// return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
		// �������ƽ���
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// ����ʰ�����������ǵ�������ʱ������������/��ʾ
			toggleMediaControlsVisiblity();
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		// ˫����ͣ��ʼ
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			playOrPause();
			return true;
		}
	}

	public void setTime(String time) {
		if (timeTextView != null)
			timeTextView.setText(time);
	}

	// ��ʾ������
	public void setBattery(String stringBattery) {
		if (timeTextView != null && batteryImageView != null) {
			batteryTextView.setText(stringBattery + "%");
			int battery = Integer.valueOf(stringBattery);
			if (battery < 15)
				batteryImageView.setImageResource(R.drawable.dianchi_0);
			;
			if (battery < 30 && battery >= 15)
				batteryImageView.setImageResource(R.drawable.dianchi_1);
			if (battery < 45 && battery >= 30)
				batteryImageView.setImageResource(R.drawable.dianchi_2);
			if (battery < 60 && battery >= 45)
				batteryImageView.setImageResource(R.drawable.dianchi_3);
			if (battery < 75 && battery >= 60)
				batteryImageView.setImageResource(R.drawable.dianchi_4);
			if (battery < 90 && battery >= 75)
				batteryImageView.setImageResource(R.drawable.dianchi_5);
			if (battery > 90)
				batteryImageView.setImageResource(R.drawable.dianchi_6);
		}
	}

	// ����/��ʾ
	private void toggleMediaControlsVisiblity() {
		if (isShowing()) {
			hide();
		} else {
			show();
		}
	}

	// ��������ͣ
	private void playOrPause() {
		if (videoView != null) {
			if (videoView.isPlaying()) {
				videoView.pause();
			}else {
				videoView.start();
			}
		}
	}
}

package com.guohao.custom;

import com.guohao.graduationdesign_app.R;
import com.guohao.util.StringUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
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
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

@SuppressLint("ClickableViewAccessibility")
public class MyMediaController extends MediaController implements OnClickListener,OnTouchListener,OnSeekBarChangeListener,OnSeekCompleteListener,OnPreparedListener {
	//����
	private View v;
	private Handler handler;
	private final int Update_SeekBar = 0;
	public static Boolean IsDestroy = false;
	private long LastSeek = 0;
	
	//��������϶�Seekbar���Ͳ�����Seekbar����Ƶ���ű仯.
//	private Boolean IsMove = false;
	//Seekbar�����̶�
	private int SeekBarMax = 1000;
	//��֪��Ļ���Ʊ仯
	private GestureDetector gestureDetector;

	//���沼�� top 
	private TextView flowTextView, nameTextView, batteryTextView, timeTextView;
	private ImageView batteryImageView;

	//���沼�� bottom
	private TextView currentTextView, totalTextView;
	private ImageView statusImageView;
	private SeekBar seekBar;

	//���캯��---����
	private VideoView videoView;
	private Activity activity;

	// videoview ���ڶ���Ƶ���п��Ƶĵȣ�activity�ô��ϴ�
	public MyMediaController(VideoView videoView, Activity activity) {
		super(activity);
		this.videoView = videoView;
		this.activity = activity;
		initView();
	}

	private void initView() {
		v = LayoutInflater.from(activity).inflate(R.layout.custom_mediacontroller, null);
		
		//��ȡ���ֽ���top
		flowTextView = (TextView) v.findViewById(R.id.id_textview_flow);
		nameTextView = (TextView) v.findViewById(R.id.id_textview_file_name);
		batteryTextView = (TextView) v.findViewById(R.id.id_textview_battery);
		batteryImageView = (ImageView) v.findViewById(R.id.id_imageview_battery);
		timeTextView = (TextView) v.findViewById(R.id.id_textview_time);
		
		//��ȡ���ֽ���bottom
		statusImageView = (ImageView) v.findViewById(R.id.mediacontroller_pause); 
		statusImageView.setOnClickListener(this);
		seekBar = (SeekBar) v.findViewById(R.id.mediacontroller_progress);
		seekBar.setOnSeekBarChangeListener(this);
		
//		��Ϊ��� TextView ��id��Vitamioָ����id���Ϳ����Զ���ȡʱ����ʾ
		currentTextView = (TextView) v.findViewById(R.id.mediacontroller_time_current);
//		��Ϊ��� TextView ��id��Vitamioָ����id���Ϳ����Զ���ȡʱ����ʾ
//		totalTextView = (TextView) v.findViewById(R.id.mediacontroller_time_total);
		
		//���Ʋ���
		gestureDetector = new GestureDetector(activity, new MyGestureListener());
		videoView.setOnTouchListener(this);
		videoView.setOnPreparedListener(this);
		videoView.setOnSeekCompleteListener(this);
		v.setOnTouchListener(this);
		
		handler = new Handler();
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case Update_SeekBar:
					long i = (long) msg.obj;
					seekBar.setProgress((int)i);
					break;
				}
			}
		};
	}
	
	//�Զ��� MediaController ���� View
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
	public void onPrepared(MediaPlayer mp) {
		Log.d("guohao", "���ģ�"+(int)videoView.getDuration());
		seekBar.setMax((int)videoView.getDuration());
	}
	//------------------------------------------------------------------------------
	//SeekBar �ı��¼�
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		Log.d("guohao", "��ǰ�ģ�"+progress);
		show();
		if (fromUser) {
			videoView.seekTo(progress);
			LastSeek = progress;
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
	//------------------------------------------------------------------------------
	@Override
	public void onSeekComplete(MediaPlayer mp) {
		videoView.seekTo(LastSeek);
	}
	//------------------------------------------------------------------------------
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
				statusImageView.setImageResource(R.drawable.mediacontroller_play);
			}else {
				videoView.start();
				statusImageView.setImageResource(R.drawable.mediacontroller_pause);
			}
		}
	}
	
	//------------------------------------------------------------------------------
	
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
	
	//�����Ƿ���
	public void setScreenOn(Boolean keepScreenOn) {
		videoView.setKeepScreenOn(keepScreenOn);
	}

	

}

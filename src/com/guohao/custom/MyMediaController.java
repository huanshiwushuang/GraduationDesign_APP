package com.guohao.custom;

import com.guohao.graduationdesign_app.R;
import com.guohao.util.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
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
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

@SuppressLint("ClickableViewAccessibility")
public class MyMediaController extends MediaController implements DialogInterface.OnKeyListener,OnBufferingUpdateListener,OnClickListener,OnTouchListener,OnSeekBarChangeListener,OnSeekCompleteListener,OnPreparedListener {
	//����
	private View v;
	private long LastSeek = 0;
	private ProgressDialog dialog;
	private Boolean IsSeekComplete = true;
	
	//SeekBar �Ƿ������ƶ�
	private Boolean IsMove = false;
	//��Activity����ʱ��ֹͣ���߳�
	public static Boolean IsDestroy = false;
	//��֪��Ļ���Ʊ仯
	private GestureDetector gestureDetector;

	//���沼�� top 
	private TextView flowTextView, nameTextView, batteryTextView, timeTextView;
	private ImageView batteryImageView;
	//���沼�� bottom
//	private TextView currentTextView, totalTextView;
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
//		currentTextView = (TextView) v.findViewById(R.id.mediacontroller_time_current);
//		��Ϊ��� TextView ��id��Vitamioָ����id���Ϳ����Զ���ȡʱ����ʾ
//		totalTextView = (TextView) v.findViewById(R.id.mediacontroller_time_total);
		
		//���Ʋ���
		gestureDetector = new GestureDetector(activity, new MyGestureListener());
		videoView.setOnTouchListener(this);
		videoView.setOnPreparedListener(this);
		videoView.setOnSeekCompleteListener(this);
		videoView.setOnBufferingUpdateListener(this);
		v.setOnTouchListener(this);
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
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		Log.d("guohao", "���ؽ��ȣ�"+percent);
		//���SeekBarû���ƶ���
		if (!IsMove) {
			//�����Զ�����ʱ����ʾ
			dialog = Util.getProgressDialog(activity);
			dialog.setOnKeyListener(this);
			if (!Util.isShowingProgressDialog()) {
				dialog.show();
			}
			dialog.setMessage("���ڼ��� "+percent+"%");
			if (percent >= 99) {
				Util.dismissProgressDialog();
				Log.d("guohao", "���٣�"+percent+"---"+Util.isShowingProgressDialog());
			}
		}
	}
	@Override
	public void onPrepared(MediaPlayer mp) {
		Log.d("guohao", "���ģ�"+(int)videoView.getDuration());
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
					if (videoView.isPlaying() && IsSeekComplete) {
						int i = (int) videoView.getCurrentPosition();
						Log.d("guohao", "���棺"+i);
						seekBar.setProgress(i);
					}
				}
			}
		}).start();
	}
	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Util.dismissProgressDialog();
            activity.finish();
        }
		return false;
	}
	//------------------------------------------------------------------------------
	//SeekBar �ı��¼�
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		//��������жϣ���Ϊ�����߳����� SeekBar ����VideoView ���Ž����Զ�ǰ����ʱ���Ǵ�����ƣ�������
		if (fromUser) {
			show();
			LastSeek = progress;
			videoView.seekTo(progress);
			Log.d("guohao", "��ǰ���ȣ�"+progress);
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		IsMove = true;
		IsSeekComplete = false;
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		IsMove = false;
		//�����϶�����ʱ����ʾ
		if (!Util.isShowingProgressDialog()) {
			dialog = Util.getProgressDialog(activity);
			dialog.setOnKeyListener(this);
		}
	}
	//------------------------------------------------------------------------------
	@Override
	public void onSeekComplete(MediaPlayer mp) {
		videoView.seekTo(LastSeek);
		IsSeekComplete = true;
		Log.d("guohao", "�����ȣ�"+LastSeek);
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

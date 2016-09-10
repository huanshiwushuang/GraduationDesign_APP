package com.guohao.graduationdesign_app;

import com.guohao.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Vitamio.isInitialized(this);
		setContentView(R.layout.activity_movie_play);
		initData();
		initView();
		startPlay();
		
		Util.showToast(MoviePlayActivity.this, playUrl);
	}

	private void startPlay() {
		videoView.setVideoPath(playUrl);
		videoView.setMediaController(new MediaController(this));
	}

	private void initView() {
		videoView = (VideoView) findViewById(R.id.id_videoview);
		videoView.setOnBufferingUpdateListener(this);
		videoView.setOnPreparedListener(this);
		videoView.setOnCompletionListener(this);
		videoView.setOnErrorListener(this);
		videoView.setOnSeekCompleteListener(this);
		videoView.setKeepScreenOn(true);
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

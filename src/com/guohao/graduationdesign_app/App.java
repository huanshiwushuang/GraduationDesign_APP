package com.guohao.graduationdesign_app;

import cn.ciaapp.sdk.CIAService;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.graphics.Bitmap;

public class App extends Application {
	private String appId = "534a0e09a0c1435686830ff40efc02d8";
	private String authKey = "8310e66c33fb48adb7b3af21ae84eff1";
	
	@Override
	public void onCreate() {
		super.onCreate();
		CIAService.init(this, appId, authKey);
		initImageLoader(getApplicationContext());
		Log.d("guohao", "启动了");
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		//缓存图片目录
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, "MoYing/Image/cache");
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.loading_url_error)
				.showImageOnFail(R.drawable.loading_fail)
				.showImageOnLoading(R.drawable.loading)
				.resetViewBeforeLoading(false)
				.imageScaleType(ImageScaleType.NONE)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.considerExifParams(false)
				.build();
		
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		
		config.memoryCacheExtraOptions(480, 800);
		config.memoryCache(new UsingFreqLimitedMemoryCache(2*1024*1024));	
		config.memoryCacheSize(10*1024*1024);
		config.threadPoolSize(3);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50*1024*1024);
		config.diskCache(new UnlimitedDiskCache(cacheDir));
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
//		config.writeDebugLogs(); // 如果 APP 上市，就移除它，这是打印日志的
		config.defaultDisplayImageOptions(options);
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}
}

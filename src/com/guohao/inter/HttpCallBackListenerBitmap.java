package com.guohao.inter;

import android.graphics.Bitmap;

public interface HttpCallBackListenerBitmap {
	void onFinish(Bitmap bitmap);
	void onError(String e);
}

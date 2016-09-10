package com.guohao.util;

import java.util.List;

import com.guohao.graduationdesign_app.LoginActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

public class Util {
	private static ProgressDialog progressDialog;
	
	public static boolean isEmpty(String string) {
		if (string == null || string.equals("")) {
			return true;
		}
		return false;
	}
	public static void showProgressDialog(Context context,String title) {
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(title);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	public static void showProgressDialog(Context context) {
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("���ڼ���...");
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	public static void dismissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	public static Boolean isShowingProgressDialog() {
		return progressDialog.isShowing();
	}
	
	//��ȡ����ͼƬ����Ϊ������ȡͼƬʱ����ȡ����СͼƬ
	public static String getBigImageAddress(String address) {
		//http://tu80s2.b0.upaiyun.com/upload/img/201506/3168.jpg
		int i = address.lastIndexOf(".");
		if (!address.substring(i-2, i).contains("_b")) {
			address = address.substring(0, i)+"_b"+address.substring(i, address.length());
		}
		return address;
	}
	
	
	//���������ڷ��� �������ݡ����磺��ʾ��Ϣ��
	public static String responseJsonObjectOK(String string) {
		String jsonOK = "{\""+Data.KEY_CODE+"\":\""+Data.VALUE_OK+"\",\""+Data.KEY_DATA+"\":\""+Data.VALUE_CONTENT+"\"}";
		return jsonOK.replace(Data.VALUE_CONTENT, string);
	}
	public static String responseJsonObjectError(String string) {
		String jsonError = "{\""+Data.KEY_CODE+"\":\""+Data.VALUE_ERROR+"\",\""+Data.KEY_DATA+"\":\""+Data.VALUE_CONTENT+"\"}";
		return jsonError.replace(Data.VALUE_CONTENT, string);
	}
	
	//��ȡ ��׿��Ļ metric
	public static DisplayMetrics getScreentMetric(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric;
	}
	
	//show Toast
	public static void showToast(Context c, String msg) {
		Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
	}
	
	//��ȡ Preferences
	public static SharedPreferences getPreferences(Context c) {
		return PreferenceManager.getDefaultSharedPreferences(c);
	}
	
	//��ȡ������Ϣ info
	public static NetworkInfo getNetworkInfo(Context c) {
		ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		return info;
	}
	
	//����ͼƬ ���
	public static void setImageWH(Activity a, ImageView image, int LINE_COUNT) {
		DisplayMetrics metric = getScreentMetric(a);
        int screenWidth = metric.widthPixels;
        LayoutParams params = image.getLayoutParams();
		params.width = screenWidth/LINE_COUNT;
		params.height = (int) (params.width/3.0*4.0);
		
		Editor editor = getPreferences(a).edit();
		editor.putInt(Data.K_IMAGE_WIDTH, params.width);
		editor.putInt(Data.K_IMAGE_HEIGHT, params.height);
		editor.commit();
	}
	
}

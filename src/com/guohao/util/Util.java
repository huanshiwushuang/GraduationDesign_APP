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
import android.widget.ProgressBar;
import android.widget.Toast;

public class Util {
	private static ProgressDialog progressDialog;
	private static ProgressBar progressBar;
	
	public static boolean isEmpty(String string) {
		if (string == null || string.equals("")) {
			return true;
		}
		return false;
	}
	public static void showProgressDialog(Context context,String msg) {
		if (progressBar == null) {
			progressDialog = new ProgressDialog(context);
		}
		progressDialog.setMessage(msg);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	public static void showProgressDialog(Context context) {
		if (progressBar == null) {
			progressDialog = new ProgressDialog(context);
		}
		progressDialog.setMessage("���ڼ���...");
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	public static void dismissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	public static ProgressDialog getProgressDialog(Context context) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(context);
		}
		progressDialog.setCancelable(false);
		progressDialog.setMessage("���ڼ���...");
		return progressDialog;
	}
	public static ProgressDialog getProgressDialog(Context context,String msg) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(context);
		}
		progressDialog.setCancelable(false);
		progressDialog.setMessage(msg);
		return progressDialog;
	}
	public static Boolean isShowingProgressDialog() {
		return progressDialog.isShowing();
	}
	public static void setMessage(String msg) {
		if (progressDialog != null) {
			progressDialog.setMessage(msg);
		}
	}
	
	//��ȡ����ͼƬ����Ϊ������ȡͼƬʱ����ȡ����СͼƬ
	public static String getBigImageAddress(String address) {
		//http://tu80s2.b0.upaiyun.com/upload/img/201506/3168.jpg
		if (address.endsWith("!list")) {
			address = address.substring(0, address.length()-5);
		}else {
			int i = address.lastIndexOf(".");
			if (i != -1 && !address.substring(i-2, i).contains("_b")) {
				address = address.substring(0, i)+"_b"+address.substring(i, address.length());
			}
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
		showToastOnce(c, msg);
//		Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
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
	//----------------------------------------------------
	/** ֮ǰ��ʾ������ */  
    private static String oldMsg ;  
    /** Toast���� */  
    private static Toast toast = null ;  
    /** ��һ��ʱ�� */  
    private static long oneTime = 0 ;  
    /** �ڶ���ʱ�� */  
    private static long twoTime = 0 ;  
      
    /** 
     * ��ʾToast 
     * @param context 
     * @param message 
     */  
    public static void showToastOnce(Context context,String message){  
        if(toast == null){  
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);  
            toast.show() ;  
            oneTime = System.currentTimeMillis() ;  
        }else{  
            twoTime = System.currentTimeMillis() ;  
            if(message.equals(oldMsg)){  
                if(twoTime - oneTime > Toast.LENGTH_SHORT){  
                    toast.show() ;  
                }  
            }else{  
                oldMsg = message ;  
                toast.setText(message) ;  
                toast.show() ;  
            }  
        }  
        oneTime = twoTime ;  
    }  
	
	
}

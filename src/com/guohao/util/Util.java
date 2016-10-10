package com.guohao.util;



import com.guohao.custom.MyAlertDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

public class Util {
	public static boolean isEmpty(String string) {
		if (string == null || string.equals("")) {
			return true;
		}
		return false;
	}
	public static void showProgressDialog(Context context,String msg) {
		showAlertDialog02(context, msg);
	}
	public static void showProgressDialog(Context context) {
		showAlertDialog02(context, "正在加载数据......");
	}
	public static void dismissProgressDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}
	public static Boolean isShowingProgressDialog() {
		if (dialog != null) {
			return dialog.isShowing();
		}
		return false;
	}
	public static void setMessage(String msg) {
		if (dialog != null) {
			dialog.setPrompt(msg);
		}
	}
	
	//获取到大图片，因为当初获取图片时，获取的是小图片
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
	
	
	//这两个用于返回 单条数据。例如：提示信息。
	public static String responseJsonObjectOK(String string) {
		String jsonOK = "{\""+Data.KEY_CODE+"\":\""+Data.VALUE_OK+"\",\""+Data.KEY_DATA+"\":\""+Data.VALUE_CONTENT+"\"}";
		return jsonOK.replace(Data.VALUE_CONTENT, string);
	}
	public static String responseJsonObjectError(String string) {
		String jsonError = "{\""+Data.KEY_CODE+"\":\""+Data.VALUE_ERROR+"\",\""+Data.KEY_DATA+"\":\""+Data.VALUE_CONTENT+"\"}";
		return jsonError.replace(Data.VALUE_CONTENT, string);
	}
	
	//获取 安卓屏幕 metric
	public static DisplayMetrics getScreentMetric(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric;
	}
	//show Toast
	public static void showToast(Context c, String msg) {
		showToastOnce(c, msg);
	}
	//获取 Preferences
	public static SharedPreferences getPreferences(Context c) {
		return PreferenceManager.getDefaultSharedPreferences(c);
	}
	
	//获取网络信息 info
	public static NetworkInfo getNetworkInfo(Context c) {
		ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		return info;
	}
	
	//设置图片 宽高
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
	/** 之前显示的内容 */  
    private static String oldMsg ;  
    /** Toast对象 */  
    private static Toast toast = null ;  
    /** 第一次时间 */  
    private static long oneTime = 0 ;  
    /** 第二次时间 */  
    private static long twoTime = 0 ;  
      
    /** 
     * 显示Toast 
     * @param context 
     * @param message 
     */  
    public static void showToastOnce(Context context,String message){  
    	if (context == null) {
			return;
		}
        if(toast == null){  
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);  
            toast.show() ;  
            oneTime = System.currentTimeMillis();
        }else{  
            twoTime = System.currentTimeMillis();  
            if(message.equals(oldMsg)){  
            	toast.cancel();
            	toast = Toast.makeText(context, message, Toast.LENGTH_SHORT); 
            	toast.show(); 
            }else{  
                oldMsg = message ;  
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT); 
                toast.show();
            }  
        }  
        oneTime = twoTime;  
    }  
    
    public static DisplayMetrics getDisplayMetrics(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}
	//------------------------------------------------------------
    private static MyAlertDialog dialog;
    public static MyAlertDialog showAlertDialog01(Context context) {
		dialog = new MyAlertDialog(context, MyAlertDialog.Layout01);
		dialog.show();
		return dialog;
	}
    public static MyAlertDialog showAlertDialog02(Context context, String prompt) {
		dialog = new MyAlertDialog(context, MyAlertDialog.Layout02);
		int screenHeight = dialog.getScreenHeight();
		dialog.setPrompt(prompt);
		dialog.setheight((int)(screenHeight/5.6));
		dialog.show();
		return dialog;
	}
    public static MyAlertDialog showAlertDialog03(Context context, String prompt, String hint, int updateFlag) {
		dialog = new MyAlertDialog(context, MyAlertDialog.Layout03, updateFlag);
		dialog.setPrompt(prompt);
		dialog.setHint(hint);
		if (updateFlag == HttpUtil.VISIT_USER_TABLE_UPDATE_PWD) {
			dialog.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
		dialog.show();
		return dialog;
	}
    public static MyAlertDialog showAlertDialog04(Context context, String title, String content) {
		dialog = new MyAlertDialog(context, MyAlertDialog.Layout04);
		dialog.setTitle(title);
		dialog.setContent(content);
		dialog.show();
		return dialog;
	}
}

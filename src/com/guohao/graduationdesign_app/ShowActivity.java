package com.guohao.graduationdesign_app;


import org.json.JSONException;
import org.json.JSONObject;

import com.guohao.inter.HttpCallBackListenerString;
import com.guohao.model.UserTable;
import com.guohao.util.Data;
import com.guohao.util.HttpUtil;
import com.guohao.util.Util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ShowActivity extends Activity {
	private Handler handler,handler2;
	private Runnable r;
	private SharedPreferences pre;
	private Boolean isLoginSuccess = false;
	private Boolean isPreJump = false;
	private final int LOGIN_SUCCESS = 0;
	private final int LOGIN_FAIL = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);
		initView();
		initNetwork();
	}

	private void initNetwork() {
		final String userLastLoginAccount = pre.getString(Data.K_User_Last_Login_Account, "");
		if (Util.isEmpty(userLastLoginAccount)) {
			return;
		}
		final String userPwd = pre.getString(Data.K_User_Pwd, "");
		UserTable user = new UserTable();
		user.setUserId(userLastLoginAccount);
		user.setUserPwd(userPwd);
		
		HttpUtil.visitUserTable(HttpUtil.VISIT_USER_TABLE_LOGIN, Data.URL_USER_TABLE, user, new HttpCallBackListenerString() {
			public void onFinish(String response) {
				JSONObject object;
				String code = null;
				try {
					object = new JSONObject(response);
					code = object.getString(Data.KEY_CODE);
					if (code.equals(Data.VALUE_OK)) {
						JSONObject obj = object.getJSONArray(Data.KEY_DATA).getJSONObject(0);
						
						//登录成功，先记住数据（用于在登录后直接跳转到主页面），跳转主页面
						Editor editor = Util.getPreferences(ShowActivity.this).edit();
						editor.putString(Data.K_User_Last_Login_Account, userLastLoginAccount);
						editor.putString(Data.K_User_Id, obj.getString("userId"));
						editor.putString(Data.K_User_Name, obj.getString("username"));
						String temp = obj.getString("userRegisteTime");
						editor.putString(Data.K_User_Registe_Time, temp == null?"":temp.substring(0, temp.length()-4));
						temp = obj.getString("userLastOnlineTime");
						editor.putString(Data.K_User_Last_Login_Time, temp == null?"":temp.substring(0, temp.length()-4));
						editor.putString(Data.K_User_Pwd, userPwd);
						editor.commit();
						isLoginSuccess = true;
						Message msg = handler2.obtainMessage();
						msg.what = LOGIN_SUCCESS;
						handler2.sendMessage(msg);
					}else {
						String data = object.getString(Data.KEY_DATA);
						Message msg = handler2.obtainMessage();
						msg.what = LOGIN_FAIL;
						msg.obj = data;
						handler2.sendMessage(msg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			
			public void onError(String e) {
				Message msg = handler2.obtainMessage();
				msg.what = LOGIN_FAIL;
				msg.obj = e;
				handler2.sendMessage(msg);
			}
		});
	}

	private void initView() {
		handler = new Handler();
		pre = Util.getPreferences(ShowActivity.this);
		handler2 = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case LOGIN_SUCCESS:
					if (isPreJump) {
						MainActivity.actionStart(ShowActivity.this);
						Util.showToast(ShowActivity.this, "登录成功");
						finish();
					}
					break;
				case LOGIN_FAIL:
					Util.showToast(ShowActivity.this, msg.obj.toString());
					LoginActivity.actionStart(ShowActivity.this);
					finish();
					break;
				}
			}
		};
		r = new Runnable() {
			public void run() {
				String lastIntoTime = pre.getString(Data.K_Time_Last_Into, "");
				if (Util.isEmpty(lastIntoTime)) {
					LoginActivity.actionStart(ShowActivity.this);
					finish();
				}else if ((System.currentTimeMillis()-Long.valueOf(lastIntoTime))/1000.0/60.0/60.0 > 3*24) {
					//如果此时登录时间距离上次大于了3天，请重新登录。
					LoginActivity.actionStart(ShowActivity.this);
					Util.showToast(ShowActivity.this, "登录状态失效，请重新登录");
					finish();
				}else {
					//倒计时完毕，准备跳转
					isPreJump = true;
					if (isLoginSuccess) {
						MainActivity.actionStart(ShowActivity.this);
						Util.showToast(ShowActivity.this, "登录成功");
						finish();
					}
					
				}
			}
		};
		handler.postDelayed(r, 3*1000);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		handler.removeCallbacks(r);
	}
}

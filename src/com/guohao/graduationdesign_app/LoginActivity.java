package com.guohao.graduationdesign_app;


import org.json.JSONException;
import org.json.JSONObject;

import com.guohao.inter.HttpCallBackListenerString;
import com.guohao.model.UserTable;
import com.guohao.util.Data;
import com.guohao.util.HttpUtil;
import com.guohao.util.StringUtil;
import com.guohao.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	private TextView registe,forgetPWD;
	private EditText account,pwd;
	private Button login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		initView();
		setAccountText();
	}
	
	private void setAccountText() {
		String s = Util.getPreferences(LoginActivity.this).getString(Data.K_User_Last_Login_Account, "");
		account.setText(s);
		if (!Util.isEmpty(s)) {
			pwd.requestFocus();
		}
	}

	private void initView() {
		account = (EditText) findViewById(R.id.id_edittext_account);
		pwd = (EditText) findViewById(R.id.id_edittext_pwd);
		
		registe = (TextView) findViewById(R.id.id_textview_registe);
		registe.setOnClickListener(this);
		
		login = (Button) findViewById(R.id.id_button_login);
		login.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_textview_registe:
			RegisteActivity.actionStart(LoginActivity.this);
			break;
		case R.id.id_button_login:
			if (!checkInput()) {
				return;
			}
			login();
			break;
		default:
			break;
		}
	}

	private Boolean checkInput() {
		String pwdString = pwd.getText().toString();
		if (!StringUtil.checkAccount(account.getText().toString())) {
			Util.showToast(LoginActivity.this, "非法账户");
			return false;
		}
		if (pwdString.length() < 6) {
			Util.showToast(LoginActivity.this, "密码不能少于 6 位");
			return false;
		}
		return true;
	}

	private void login() {
		NetworkInfo info = Util.getNetworkInfo(LoginActivity.this);
		if (info == null || !info.isAvailable()) {
			Util.showToast(LoginActivity.this, "无网络");
			return;
		}
		final String accountString = account.getText().toString();
		String pwdString = pwd.getText().toString();
		UserTable user = new UserTable();
		user.setUserId(accountString);
		user.setUserPwd(pwdString);
		Util.showProgressDialog(LoginActivity.this, "登录中...");
		HttpUtil.visitUserTable(HttpUtil.VISIT_USER_TABLE_LOGIN, Data.URL_USER_TABLE, user, new HttpCallBackListenerString() {
			
			public void onFinish(String response) {
				Util.dismissProgressDialog();
				JSONObject object;
				String code = null;
				try {
					object = new JSONObject(response);
					code = object.getString(Data.KEY_CODE);
					
					if (code.equals(Data.VALUE_OK)) {
						JSONObject obj = object.getJSONArray(Data.KEY_DATA).getJSONObject(0);
						
						//登录成功，先记住数据（用于在登录后直接跳转到主页面），跳转主页面
						Editor editor = Util.getPreferences(LoginActivity.this).edit();
						editor.putString(Data.K_User_Last_Login_Account, accountString);
						editor.putString(Data.K_User_Id, obj.getString("userId"));
						editor.putString(Data.K_User_Name, obj.getString("username"));
						editor.commit();
						
						MainActivity.actionStart(LoginActivity.this);
						finish();
					}else {
						String data = object.getString(Data.KEY_DATA);
						Looper.prepare();
						Util.showToast(LoginActivity.this, data);
						Looper.loop();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			
			public void onError(String e) {
				Util.dismissProgressDialog();
				Looper.prepare();
				Util.showToast(LoginActivity.this, e);
				Looper.loop();
			}
		});
	}

	
	
	public static void actionStart(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}
}

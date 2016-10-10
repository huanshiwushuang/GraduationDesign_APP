package com.guohao.graduationdesign_app;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;


import cn.ciaapp.sdk.CIAService;
import cn.ciaapp.sdk.VerificationListener;

import com.guohao.inter.HttpCallBackListenerString;
import com.guohao.model.UserTable;
import com.guohao.util.Data;
import com.guohao.util.HttpUtil;
import com.guohao.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RegisteActivity extends Activity implements OnClickListener {
	private Button registeButton;
	private EditText account,pwd,pwd2;
	private Handler handler;
//	private Boolean isRegiste = false;
	private final int Registe = 0;
	private final int Find_Account = 1;
	
	private String accountString;
	private String pwdString;
	private String pwd2String;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registe);
		
		initView();
	}
	
	private void initView() {
		account = (EditText) findViewById(R.id.id_edittext_account);
		pwd = (EditText) findViewById(R.id.id_edittext_pwd);
		pwd2 = (EditText) findViewById(R.id.id_edittext_pwd2);
		registeButton = (Button) findViewById(R.id.id_button_registe);
		registeButton.setOnClickListener(this);
		
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				JSONObject object = null;
				switch (msg.what) {
				case Find_Account:
					try {
						object = new JSONObject(String.valueOf(msg.obj));
						String code = object.getString(Data.KEY_CODE);
						
						if (code.equals(Data.VALUE_OK)) {
							Util.showToast(RegisteActivity.this, "该账号已注册");
							Util.dismissProgressDialog();
							finish();
						}else {
							Util.setMessage("正在注册...");
							sendVerificationRequest(accountString,pwdString);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						Util.dismissProgressDialog();
						Util.showToast(RegisteActivity.this, e.toString());
					}
					break;
				case Registe:
					try {
						object = new JSONObject(String.valueOf(msg.obj));
						String code = object.getString(Data.KEY_CODE);
						
						if (code.equals(Data.VALUE_OK)) {
							Util.showToast(RegisteActivity.this, object.getString(Data.KEY_DATA)+"，请登录");
						}else {
							Util.showToast(RegisteActivity.this, object.getString(Data.KEY_DATA));
						}
						Util.dismissProgressDialog();
						finish();
					} catch (JSONException e) {
						e.printStackTrace();
						Util.dismissProgressDialog();
						Util.showToast(RegisteActivity.this, e.toString());
					}
					break;
				}
			}
		};
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_button_registe:
			accountString = account.getText().toString();
			pwdString = pwd.getText().toString();
			pwd2String = pwd2.getText().toString();
			registe();
			break;
		}
	}

	
	private void registe() {
		if (!haveNetWork()) {
			Util.showToast(RegisteActivity.this, "无网络");
			return;
		}
		
		if (Util.isEmpty(accountString) || Util.isEmpty(pwdString) || Util.isEmpty(pwd2String)) {
			Util.showToast(RegisteActivity.this, "不能为空");
			return;
		}
		Matcher matcher = Pattern.compile("\\d{11}").matcher(accountString);
		if (!matcher.find()) {
			Util.showToast(RegisteActivity.this, "手机号错误");
			return;
		}
		if (pwdString.length() > 20 || pwdString.length() < 6) {
			Util.showToast(RegisteActivity.this, "密码长度错误");
			return;
		}
		if (!pwdString.equals(pwd2String)) {
			Util.showToast(RegisteActivity.this, "两次密码不相同");
			return;
		}
		//欠缺注册的验证，之前是否注册过了
		Util.showProgressDialog(this, "正在检查账户...");
		selectAccount(accountString,pwdString);
	}

	private void selectAccount(final String account, final String pwd) {
		UserTable user = new UserTable();
		user.setUserId(account);
		HttpUtil.visitUserTable(HttpUtil.VISIT_USER_TABLE_FIND_ACCOUNT, Data.URL_USER_TABLE, user, new HttpCallBackListenerString() {
			@Override
			public void onFinish(String response) {
				Message msg = handler.obtainMessage();
				msg.obj = response;
				msg.what = Find_Account;
				handler.sendMessage(msg);
			}
			@Override
			public void onError(String e) {
				Util.dismissProgressDialog();
				Looper.prepare();
				finish();
				Util.showToast(RegisteActivity.this, e);
				Looper.loop();
			}
		});
	}

	private void sendVerificationRequest(final String phoneNumber, final String pwd) {
		
		CIAService.startVerification(phoneNumber, new VerificationListener() {
            public void onStateChange(int status, String msg, String transId) {
            	
                switch (status) {
                    case CIAService.VERIFICATION_SUCCESS: // 验证成功
                        // TODO 进入下一步业务逻辑
                    	
                        Util.showToast(RegisteActivity.this, "验证成功");
                        //插入数据库。
                        startRegiste(phoneNumber,pwd);
                        break;
                    case CIAService.SECURITY_CODE_MODE: // 验证码模式
                        // 进入输入验证码的页面，并提示用户输入验证码
                    	Util.dismissProgressDialog();
                    	VerificationActivity.actionStart(RegisteActivity.this, phoneNumber, pwd, VerificationActivity.REGISTE_ACCOUNT);
                    	Util.showToast(RegisteActivity.this, "已拨打验证电话至："+phoneNumber);
                    	finish();
                        break;
                    case CIAService.VERIFICATION_FAIL:
                    	Util.dismissProgressDialog();
                        Util.showToast(RegisteActivity.this, "验证失败：" + msg);
                        break;
                    case CIAService.REQUEST_EXCEPTION:
                    	Util.dismissProgressDialog();
                        Util.showToast(RegisteActivity.this, "请求异常：" + msg);
                        break;
                    default:
                        // 服务器返回的信息
                        Util.showToast(RegisteActivity.this, msg);
                        break;
                }
            }
        });
	}

	protected void startRegiste(String account, String pwd) {
		UserTable user = new UserTable();
		user.setUserId(account);
		user.setUsername(System.nanoTime()+"");
		user.setUserPwd(pwd);
		HttpUtil.visitUserTable(HttpUtil.VISIT_USER_TABLE_REGISTE, Data.URL_USER_TABLE, user, new HttpCallBackListenerString() {
			
			public void onFinish(String response) {
				Util.dismissProgressDialog();
				Message msg = handler.obtainMessage();
				msg.obj = response;
				msg.what = Registe;
				handler.sendMessage(msg);
			}
			
			public void onError(String e) {
				Util.dismissProgressDialog();
				Looper.prepare();
				finish();
				Util.showToast(RegisteActivity.this, e);
				Looper.loop();
			}
		});
	}

	public static void actionStart(Context context) {
		Intent intent = new Intent(context, RegisteActivity.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		CIAService.cancelVerification();
	}
	private Boolean haveNetWork() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}
}

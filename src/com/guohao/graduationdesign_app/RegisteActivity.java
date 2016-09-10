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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RegisteActivity extends Activity implements OnClickListener {
	private Button registeButton;
	private EditText account,pwd,pwd2;
	private Handler handler;
	
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
				JSONObject object;
				try {
					object = new JSONObject(String.valueOf(msg.obj));
					String code = object.getString(Data.KEY_CODE);
					
					finish();
					if (code.equals(Data.VALUE_OK)) {
						showToast(object.getString(Data.KEY_DATA)+"，请登录");
					}else {
						showToast(object.getString(Data.KEY_DATA));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_button_registe:
			registe();
			break;
		default:
			break;
		}
	}

	
	private void registe() {
		if (!haveNetWork()) {
			showToast("无网络");
			return;
		}
		
		String accountString = account.getText().toString();
		String pwdString = pwd.getText().toString();
		String pwd2String = pwd2.getText().toString();
		if (Util.isEmpty(accountString) || Util.isEmpty(pwdString) || Util.isEmpty(pwd2String)) {
			showToast("不能为空");
			return;
		}
		Matcher matcher = Pattern.compile("\\d{11}").matcher(accountString);
		if (!matcher.find()) {
			showToast("手机号错误");
			return;
		}
		if (pwdString.length() > 20 || pwdString.length() < 6) {
			showToast("密码长度错误");
			return;
		}
		if (!pwdString.equals(pwd2String)) {
			showToast("两次密码不相同");
			return;
		}
		
		Util.showProgressDialog(this, "正在验证手机号...");
		sendVerificationRequest(accountString,pwdString);
	}

	private void sendVerificationRequest(final String phoneNumber, final String pwd) {
		
		CIAService.startVerification(phoneNumber, new VerificationListener() {
            public void onStateChange(int status, String msg, String transId) {
            	
                switch (status) {
                    case CIAService.VERIFICATION_SUCCESS: // 验证成功
                        // TODO 进入下一步业务逻辑
                    	Util.dismissProgressDialog();
                        showToast("验证成功");
                        //插入数据库。
                        startRegiste(phoneNumber,pwd);
                        break;
                    case CIAService.SECURITY_CODE_MODE: // 验证码模式
                        // 进入输入验证码的页面，并提示用户输入验证码
                    	Util.dismissProgressDialog();
                    	VerificationActivity.actionStart(RegisteActivity.this, phoneNumber, pwd, VerificationActivity.REGISTE_ACCOUNT);
                    	showToast("已拨打验证电话至："+phoneNumber);
                        break;
                    case CIAService.VERIFICATION_FAIL:
                    	Util.dismissProgressDialog();
                        showToast("验证失败：" + msg);
                        break;
                    case CIAService.REQUEST_EXCEPTION:
                    	Util.dismissProgressDialog();
                        showToast("请求异常：" + msg);
                        break;
                    default:
                        // 服务器返回的错误
                        showToast(msg);
                }
            }
        });
	}

	protected void startRegiste(String account, String pwd) {
		Util.showProgressDialog(RegisteActivity.this, "正在注册...");
		UserTable user = new UserTable();
		user.setUserId(account);
		user.setUsername(System.nanoTime()+"");
		user.setUserPwd(pwd);
		HttpUtil.visitUserTable(HttpUtil.VISIT_USER_TABLE_REGISTE, Data.URL_USER_TABLE, user, new HttpCallBackListenerString() {
			
			public void onFinish(String response) {
				Util.dismissProgressDialog();
				Message msg = handler.obtainMessage();
				msg.obj = response;
				handler.sendMessage(msg);
			}
			
			public void onError(String e) {
				Util.dismissProgressDialog();
				Looper.prepare();
				finish();
				showToast(e);
				Looper.loop();
			}
		});
	}

	public static void actionStart(Context context) {
		Intent intent = new Intent(context, RegisteActivity.class);
		context.startActivity(intent);
	}
	
	private void showToast(String msg) {
		Toast.makeText(RegisteActivity.this, msg, Toast.LENGTH_SHORT).show();
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

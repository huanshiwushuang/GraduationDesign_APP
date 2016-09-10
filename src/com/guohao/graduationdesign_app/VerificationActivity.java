package com.guohao.graduationdesign_app;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.guohao.inter.HttpCallBackListenerString;
import com.guohao.model.UserTable;
import com.guohao.util.Data;
import com.guohao.util.HttpUtil;
import com.guohao.util.Util;

import cn.ciaapp.sdk.CIAService;
import cn.ciaapp.sdk.VerificationListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class VerificationActivity extends Activity implements OnClickListener {
	public static final int REGISTE_ACCOUNT = 0;
	public static final int UPDATE_PWD = 1;
	private TextView showMsg;
	private Button time,verificationButton;
	private EditText verificationCode;
	private String account,pwd;
	private int flag;
	private static int t;
	private String timeString;
	private Handler handler;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verification);
		
		initView();
		getData();
		setData();
		reVerification();
	}
	
	//初始化
	private void initView() {
		showMsg = (TextView) findViewById(R.id.id_textview_send_msg);
		verificationCode = (EditText) findViewById(R.id.id_edittext_account);
		time = (Button) findViewById(R.id.id_button_time);
		time.setOnClickListener(this);
		timeString = time.getText().toString();
		verificationButton = (Button) findViewById(R.id.id_button_verification);
		verificationButton.setOnClickListener(this);
		
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				JSONObject object;
				try {
					object = new JSONObject(String.valueOf(msg.obj));
					String code = object.getString(Data.KEY_CODE);
					
					LoginActivity.actionStart(VerificationActivity.this);
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
	
	//获取数据
	private void getData() {
		Intent intent = getIntent();
		account = intent.getStringExtra("account");
		pwd = intent.getStringExtra("pwd");
		flag = intent.getIntExtra("flag", -1);
	}
	
	//设置数据
	private void setData() {
		showMsg.setText(showMsg.getText().toString()+account+"\n验证码是来电号码后4位");
	}
	
	//点击按钮，重新发送验证码
	private void reVerification() {
		time.setClickable(false);
		time.setBackgroundColor(Color.parseColor("#cccccc"));
		//倒计时
		setTime();
	}
	
	//发送验证码后，重置时间
	private void setTime() {
		//初始化一次初始值
		time.setText(timeString);
		String timeString = time.getText().toString();
		final Matcher matcher = Pattern.compile("\\d+").matcher(timeString);
		if (matcher.find()) {
			t = Integer.valueOf(matcher.group(0));
			
			final Handler handler = new Handler();
			Runnable runnable = new Runnable() {
				
				public void run() {
					if (t > 1) {
						handler.postDelayed(this, 1*1000);
						time.setText(--t+"s");
					}else {
						time.setText("重新验证");
						time.setClickable(true);
						time.setBackgroundColor(Color.parseColor("#eacb20"));
					}
				}
			};
			handler.postDelayed(runnable, 1*1000);
		}else {
			Log.d("guohao", "没找到时间");
		}
	}
	
	//点击按钮，验证code、重新发送验证码
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_button_verification:
			verifySecurityCode();
			break;
		case R.id.id_button_time:
			showToast("等待验证电话送达");
			callPhone(account);
			reVerification();
			break;
		default:
			break;
		}
	}
	
	//验证code
	private void verifySecurityCode() {
		String code = verificationCode.getText().toString();
		Matcher matcher = Pattern.compile("\\d{4}").matcher(code);
		if (!matcher.find()) {
			showToast("验证码有误");
			return;
		}
		
		Util.showProgressDialog(VerificationActivity.this, "正在验证...");
		CIAService.verifySecurityCode(code, new VerificationListener() {
			
			public void onStateChange(int status, String msg, String transId) {
				switch (status) {
				case CIAService.VERIFICATION_SUCCESS:
					Util.dismissProgressDialog();
					showToast("验证成功");
					
					//根据flag，判断是注册 还是 修改密码
					switch (flag) {
					case VerificationActivity.REGISTE_ACCOUNT:
						startRegiste();
						break;
					case VerificationActivity.UPDATE_PWD:
						startUpdatePWD();
						break;
					default:
						break;
					}
					break;
				case CIAService.VERIFICATION_FAIL:
					Util.dismissProgressDialog();
					showToast("验证失败："+msg);
				default:
					Util.dismissProgressDialog();
					showToast("Error："+msg);
					break;
				}
			}
		});
	}
	
	//开始注册
	protected void startRegiste() {
		Util.showProgressDialog(VerificationActivity.this, "正在注册...");
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
				LoginActivity.actionStart(VerificationActivity.this);
				showToast(e);
				Looper.loop();
			}
		});
	}
	
	//开始修改密码
	protected void startUpdatePWD() {
		
	}
	
	//拨打验证电话
	private void callPhone(final String phoneNumber) {
		CIAService.startVerification(phoneNumber, new VerificationListener() {
            public void onStateChange(int status, String msg, String transId) {
            	
                switch (status) {
                    case CIAService.SECURITY_CODE_MODE: // 验证码模式
                        // 进入输入验证码的页面，并提示用户输入验证码
                    	showToast("已拨打验证电话至："+phoneNumber);
                        break;
                    case CIAService.VERIFICATION_FAIL:
                        showToast("验证失败：" + msg);
                        break;
                    case CIAService.REQUEST_EXCEPTION:
                        showToast("请求异常：" + msg);
                        break;
                    default:
                        // 服务器返回的错误
                        showToast(msg);
                }
            }
        });
	}
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		CIAService.cancelVerification();
	}

	private void showToast(String msg) {
		Toast.makeText(VerificationActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * @param flag
	 * 		表示验证的是注册，或者是找回密码。（此类有静态属性可传入）
	 * 
	 * 
	 * */
	public static void actionStart(Context context,String account, String pwd,int flag) {
		Intent intent = new Intent(context, VerificationActivity.class);
		intent.putExtra("account", account);
		intent.putExtra("pwd", pwd);
		intent.putExtra("flag", flag);
		context.startActivity(intent);
	}

	
}

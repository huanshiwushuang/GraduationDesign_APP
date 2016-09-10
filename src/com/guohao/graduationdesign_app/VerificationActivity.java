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
	
	//��ʼ��
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
						showToast(object.getString(Data.KEY_DATA)+"�����¼");
					}else {
						showToast(object.getString(Data.KEY_DATA));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	//��ȡ����
	private void getData() {
		Intent intent = getIntent();
		account = intent.getStringExtra("account");
		pwd = intent.getStringExtra("pwd");
		flag = intent.getIntExtra("flag", -1);
	}
	
	//��������
	private void setData() {
		showMsg.setText(showMsg.getText().toString()+account+"\n��֤������������4λ");
	}
	
	//�����ť�����·�����֤��
	private void reVerification() {
		time.setClickable(false);
		time.setBackgroundColor(Color.parseColor("#cccccc"));
		//����ʱ
		setTime();
	}
	
	//������֤�������ʱ��
	private void setTime() {
		//��ʼ��һ�γ�ʼֵ
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
						time.setText("������֤");
						time.setClickable(true);
						time.setBackgroundColor(Color.parseColor("#eacb20"));
					}
				}
			};
			handler.postDelayed(runnable, 1*1000);
		}else {
			Log.d("guohao", "û�ҵ�ʱ��");
		}
	}
	
	//�����ť����֤code�����·�����֤��
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_button_verification:
			verifySecurityCode();
			break;
		case R.id.id_button_time:
			showToast("�ȴ���֤�绰�ʹ�");
			callPhone(account);
			reVerification();
			break;
		default:
			break;
		}
	}
	
	//��֤code
	private void verifySecurityCode() {
		String code = verificationCode.getText().toString();
		Matcher matcher = Pattern.compile("\\d{4}").matcher(code);
		if (!matcher.find()) {
			showToast("��֤������");
			return;
		}
		
		Util.showProgressDialog(VerificationActivity.this, "������֤...");
		CIAService.verifySecurityCode(code, new VerificationListener() {
			
			public void onStateChange(int status, String msg, String transId) {
				switch (status) {
				case CIAService.VERIFICATION_SUCCESS:
					Util.dismissProgressDialog();
					showToast("��֤�ɹ�");
					
					//����flag���ж���ע�� ���� �޸�����
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
					showToast("��֤ʧ�ܣ�"+msg);
				default:
					Util.dismissProgressDialog();
					showToast("Error��"+msg);
					break;
				}
			}
		});
	}
	
	//��ʼע��
	protected void startRegiste() {
		Util.showProgressDialog(VerificationActivity.this, "����ע��...");
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
	
	//��ʼ�޸�����
	protected void startUpdatePWD() {
		
	}
	
	//������֤�绰
	private void callPhone(final String phoneNumber) {
		CIAService.startVerification(phoneNumber, new VerificationListener() {
            public void onStateChange(int status, String msg, String transId) {
            	
                switch (status) {
                    case CIAService.SECURITY_CODE_MODE: // ��֤��ģʽ
                        // ����������֤���ҳ�棬����ʾ�û�������֤��
                    	showToast("�Ѳ�����֤�绰����"+phoneNumber);
                        break;
                    case CIAService.VERIFICATION_FAIL:
                        showToast("��֤ʧ�ܣ�" + msg);
                        break;
                    case CIAService.REQUEST_EXCEPTION:
                        showToast("�����쳣��" + msg);
                        break;
                    default:
                        // ���������صĴ���
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
	 * 		��ʾ��֤����ע�ᣬ�������һ����롣�������о�̬���Կɴ��룩
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

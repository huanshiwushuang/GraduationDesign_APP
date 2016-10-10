package com.guohao.graduationdesign_app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import cn.ciaapp.sdk.CIAService;
import cn.ciaapp.sdk.VerificationListener;

public class ForgetPwd extends Activity implements OnClickListener {
	private EditText phoneNum,pwd01,pwd02;
	private Button updateBtn;
	private Activity mActivity;
	
	private String accountString,pwdString01,pwdString02;
	private Handler handler;
	private final int Find_Account = 1;
	private final int UPDATE_SUCCESS = 2;
	private final int UPDATE_FAIL = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_pwd);
		
		initView();
		
	}
	
	
	private void initView() {
		mActivity = ForgetPwd.this;
		
		phoneNum = (EditText) findViewById(R.id.id_edittext_account);
		pwd01 = (EditText) findViewById(R.id.id_edittext_pwd);
		pwd02 = (EditText) findViewById(R.id.id_edittext_pwd2);
		updateBtn = (Button) findViewById(R.id.id_button_update_pwd);
		updateBtn.setOnClickListener(this);
		
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				JSONObject object = null;
				switch (msg.what) {
				case Find_Account:
					try {
						Log.d("guohao", "����ֵ2��"+msg.obj.toString());
						object = new JSONObject(String.valueOf(msg.obj));
						String code = object.getString(Data.KEY_CODE);
						if (code.equals(Data.VALUE_OK)) {
							//��֤�ֻ���
							Util.setMessage("������֤......");
							sendVerificationRequest(accountString,pwdString01);
						}else {
							Util.dismissProgressDialog();
							Util.showToast(ForgetPwd.this, "���˺���δע��");
						}
					} catch (JSONException e) {
						e.printStackTrace();
						Util.dismissProgressDialog();
						Util.showToast(mActivity, e.toString());
					}
					break;
				case UPDATE_SUCCESS:
					Util.dismissProgressDialog();
					Util.showToast(mActivity, "�޸ĳɹ�");
					finish();
					break;
				case UPDATE_FAIL:
					Util.dismissProgressDialog();
					Util.showToast(mActivity, "�޸�ʧ�ܣ�"+msg.obj.toString());
					finish();
					break;
				}
			}
		};
	}
private void sendVerificationRequest(final String phoneNumber, final String pwd) {
		
		CIAService.startVerification(phoneNumber, new VerificationListener() {
            public void onStateChange(int status, String msg, String transId) {
            	
                switch (status) {
                    case CIAService.VERIFICATION_SUCCESS: // ��֤�ɹ�
                        // TODO ������һ��ҵ���߼�
                    	
                        Util.showToast(mActivity, "��֤�ɹ�");
                        //�������ݿ�---�޸�����
                        updatePwd();
                        break;
                    case CIAService.SECURITY_CODE_MODE: // ��֤��ģʽ
                        // ����������֤���ҳ�棬����ʾ�û�������֤��
                    	Util.dismissProgressDialog();
                    	VerificationActivity.actionStart(mActivity, phoneNumber, pwd, VerificationActivity.UPDATE_PWD);
                    	Util.showToast(mActivity, "�Ѳ�����֤�绰����"+phoneNumber);
                    	finish();
                        break;
                    case CIAService.VERIFICATION_FAIL:
                    	Util.dismissProgressDialog();
                        Util.showToast(mActivity, "��֤ʧ�ܣ�" + msg);
                        break;
                    case CIAService.REQUEST_EXCEPTION:
                    	Util.dismissProgressDialog();
                        Util.showToast(mActivity, "�����쳣��" + msg);
                        break;
                    default:
                        // ���������ص���Ϣ
                        Util.showToast(mActivity, msg);
                        break;
                }
            }
        });
	}
	
	protected void updatePwd() {
		UserTable user = new UserTable();
		user.setUserId(accountString);
		user.setUserPwd(pwdString01);
		HttpUtil.visitUserTable(HttpUtil.VISIT_USER_TABLE_UPDATE_PWD, Data.URL_USER_TABLE, user, new HttpCallBackListenerString() {
			@Override
			public void onFinish(String response) {
				Message msg = handler.obtainMessage();
				String code = null;
				String data = null;
				try {
					JSONObject object = new JSONObject(response);
					code = object.getString(Data.KEY_CODE);
					data = object.getString(Data.KEY_DATA);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (code.equals(Data.VALUE_OK)) {
					JSONArray array = null;
					try {
						array = new JSONArray(data);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					msg.what = UPDATE_SUCCESS;
					handler.sendMessage(msg);
				}else {
					msg.obj = data.toString();
					msg.what = UPDATE_FAIL;
					handler.sendMessage(msg);
				}
			}
			@Override
			public void onError(String e) {
				Message msg = handler.obtainMessage();
				msg.what = UPDATE_FAIL;
				msg.obj = e;
				handler.sendMessage(msg);
			}
		});
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_button_update_pwd:
			accountString = phoneNum.getText().toString();
			pwdString01 = pwd01.getText().toString();
			pwdString02 = pwd02.getText().toString();
			registe();
			break;
		}
	}
	private void registe() {
		if (!haveNetWork()) {
			Util.showToast(mActivity, "������");
			return;
		}
		
		if (Util.isEmpty(accountString) || Util.isEmpty(pwdString01) || Util.isEmpty(pwdString02)) {
			Util.showToast(mActivity, "����Ϊ��");
			return;
		}
		Matcher matcher = Pattern.compile("\\d{11}").matcher(accountString);
		if (!matcher.find()) {
			Util.showToast(mActivity, "�ֻ��Ŵ���");
			return;
		}
		if (pwdString01.length() > 20 || pwdString01.length() < 6) {
			Util.showToast(mActivity, "���볤�ȴ���");
			return;
		}
		if (!pwdString01.equals(pwdString02)) {
			Util.showToast(mActivity, "�������벻��ͬ");
			return;
		}
		//Ƿȱע�����֤��֮ǰ�Ƿ�ע�����
		Util.showProgressDialog(this, "���ڼ���˻�...");
		selectAccount(accountString,pwdString01);
	}
	
	private void selectAccount(final String account, final String pwd) {
		UserTable user = new UserTable();
		user.setUserId(account);
//		user.setUsername(System.nanoTime()+"");
		user.setUserPwd(pwd);
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
				Util.showToast(mActivity, e);
				finish();
				Looper.loop();
			}
		});
	}

	private Boolean haveNetWork() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}

	public static void actionStart(Context context) {
		Intent intent = new Intent(context, ForgetPwd.class);
		context.startActivity(intent);
	}
}

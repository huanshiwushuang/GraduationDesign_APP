package com.guohao.custom;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guohao.graduationdesign_app.LoginActivity;
import com.guohao.graduationdesign_app.MoreMovieActivity;
import com.guohao.graduationdesign_app.R;
import com.guohao.inter.HttpCallBackListenerString;
import com.guohao.model.UserTable;
import com.guohao.util.Data;
import com.guohao.util.HttpUtil;
import com.guohao.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class MyAlertDialog implements OnClickListener,OnItemClickListener,OnItemSelectedListener {
	private Context mContext;
	private View v;
	private AlertDialog dialog;
	private AlertDialog.Builder builder;
	private int width,height;
	public int screenWidth,screenHeight;
	private LayoutInflater inflater;
	
	private TextView mYes,mNo;
	//---布局01---搜索
	public final static int Layout01 = 2;
	private EditText inputEdit;
	private Button searchBtn;
	private Spinner spinner;
	private List<String> list;
	private ArrayAdapter<String> adapter;
	private String[] strings;
	private int currentPosition = 0;
	//---布局02---正在加载
	public final static int Layout02 = 3;
	private ImageView rotateImg;
	private TextView title;
	//---布局03---更新用户信息
	public final static int Layout03 = 4;
	private Handler handler;
	private static final int UPDATE_SUCCESS = 5;
	private static final int UPDATE_FAIL = 6;
	private int flag; 
	private MyAlertDialog myAlertDialog;
	private String updateString;
	//---布局04---注销
	public final static int Layout04 = 7;
	private TextView content;
	
	//当前布局
	private static int currentLayout;
	
	
	//---构造函数---布局01
	public MyAlertDialog(Context context, int layout) {
		this(context, layout, -999);
	}
	//---构造函数---布局03
	public MyAlertDialog(Context context, int layout, int updateFlag) {
		mContext = context;
		
		currentLayout = layout;
		initView();
		initData();
		initListener();
		
		flag = updateFlag;
	}
	
	private void initListener() {
		switch (currentLayout) {
		case Layout01:
			mYes.setOnClickListener(this);
			mNo.setOnClickListener(this);
			searchBtn.setOnClickListener(this);
			adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_single_choice, list);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(this);
			break;
		case Layout03:
			mYes.setOnClickListener(this);
			mNo.setOnClickListener(this);
			break;
		case Layout04:
			mYes.setOnClickListener(this);
			mNo.setOnClickListener(this);
			break;
		}
	}
	private void initData() {
		DisplayMetrics metrics = Util.getDisplayMetrics((Activity)mContext);
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		width = (int) (screenWidth/(1.13));
		height = (int) (screenHeight/3.8);
		
		switch (currentLayout) {
		case Layout01:
			list = new ArrayList<String>();
			list.add("电影名");
			list.add("电影别名");
			list.add("演员");
			list.add("导演");
			strings = new String[]{"请输入电影名","请输入电影别名","请输入演员","请输入导演"};
			break;
		case Layout02:
			Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.custom_rotate_image);
			rotateImg.startAnimation(animation);
			break;
		}
		
	}
	private void initView() {
		builder = new AlertDialog.Builder(mContext);
		builder.setCancelable(false);
		inflater = LayoutInflater.from(mContext);
		
		switch (currentLayout) {
		case Layout01:
			v = inflater.inflate(R.layout.custom_search,new FrameLayout(mContext));
			inputEdit = (EditText) v.findViewById(R.id.id_edittext_search);
			searchBtn = (Button) v.findViewById(R.id.id_button_search);
			mYes = (TextView) v.findViewById(R.id.id_textview_yes);
			mNo = (TextView) v.findViewById(R.id.id_textview_no);
			spinner = (Spinner) v.findViewById(R.id.id_spinner);
			break;
		case Layout02:
			v = inflater.inflate(R.layout.custom_alertdialog_loading, new FrameLayout(mContext));
			rotateImg = (ImageView) v.findViewById(R.id.id_imageview_rotate);
			title = (TextView) v.findViewById(R.id.id_textview_prompt);
			break;
		case Layout03:
			v = inflater.inflate(R.layout.custom_alertdialog_update_info, new FrameLayout(mContext));
			title = (TextView) v.findViewById(R.id.id_textview_title);
			inputEdit = (EditText) v.findViewById(R.id.id_edittext_update);
			mYes = (TextView) v.findViewById(R.id.id_textview_yes);
			mNo = (TextView) v.findViewById(R.id.id_textview_no);
			handler = new Handler() {
				public void handleMessage(android.os.Message msg) {
					switch (msg.what) {
					case UPDATE_SUCCESS:
						Util.showToast(mContext, "修改成功");
						MyBg myBg = (MyBg) ((Activity)mContext).findViewById(R.id.id_mybg_username);
						myBg.setText("用户昵称："+updateString);
						break;
					case UPDATE_FAIL:
						Util.showToast(mContext, "修改失败："+msg.obj.toString());
						break;
					}
					
					myAlertDialog.dismiss();
				}
			};
			break;
		case Layout04:
			v = inflater.inflate(R.layout.custom_alertdialog_exit, new FrameLayout(mContext));
			title = (TextView) v.findViewById(R.id.id_textview_title);
			content = (TextView) v.findViewById(R.id.id_textview_message);
			mYes = (TextView) v.findViewById(R.id.id_textview_yes);
			mNo = (TextView) v.findViewById(R.id.id_textview_no);
			break;
		}
	}

	
	//---布局01-------------------------------------------------------------------------
	public void setYesText(String text) {
		if (mYes != null) {
			mYes.setText(text);
		}
	}
	public void setNoText(String text) {
		if (mNo != null) {
			mNo.setText(text);
		}
	}
	//---布局02----
	public void setPrompt(String prompt) {
		if (title != null) {
			title.setText(prompt);
		}
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public void setheight(int height) {
		this.height = height; 
	}
	//---布局03---
	public void setTitle(String text) {
		if (title != null) {
			title.setText(text);
		}
	}
	public void setHint(String hint) {
		if (inputEdit != null) {
			inputEdit.setHint(hint);
		}
	}
	public void setInputType(int type) {
		if (inputEdit != null) {
			inputEdit.setInputType(type);
		}
	}
	//---布局04---
	public void setContent(String text) {
		if (content != null) {
			content.setText(text);
		}
	}
	//---getter---
	public int getScreenWidth() {
		return screenWidth;
	}
	public int getScreenHeight() {
		return screenHeight;
	}
	public void show() {
		dialog = builder.show();
		dialog.setContentView(v);
		dialog.getWindow().setLayout(width, height);
		
		dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM); 
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}
	public void dismiss() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}
	public Boolean isShowing() {
		if (dialog != null) {
			return dialog.isShowing();
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		if (currentLayout == Layout01) {
			switch (v.getId()) {
			case R.id.id_button_search:
				spinner.performClick();
				break;
			case R.id.id_textview_yes:
				if (inputEdit.getText().toString().equals("")) {
					Util.showToast(mContext, "不能为空");
				}else {
					MoreMovieActivity.actionStartSearch(mContext, Data.Search, currentPosition+"", inputEdit.getText().toString());
					dismiss();
				}
				break;
			case R.id.id_textview_no:
				dismiss();
				break;
			}
		}else if (currentLayout == Layout03) {
			
			switch (v.getId()) {
			case R.id.id_textview_yes:
				dismiss();
				myAlertDialog = Util.showAlertDialog02(mContext, "正在更新......");
				updateString = inputEdit.getText().toString().trim();
				if (updateString.equals("")) {
					Util.showToast(mContext, "不能为空");
				}else {
					SharedPreferences p = Util.getPreferences(mContext);
					UserTable user = new UserTable();
					user.setUserId(p.getString(Data.K_User_Id, ""));
					switch (flag) {
					case HttpUtil.VISIT_USER_TABLE_UPDATE_USERNAME:
						user.setUsername(updateString);
						break;
					case HttpUtil.VISIT_USER_TABLE_UPDATE_PWD:
						user.setUserPwd(updateString);
						break;
					}
					
					HttpUtil.visitUserTable(flag, Data.URL_USER_TABLE, user, new HttpCallBackListenerString() {
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
				break;
			case R.id.id_textview_no:
				dismiss();
				break;
			}
		}else if (currentLayout == Layout04) {
			switch (v.getId()) {
			case R.id.id_textview_yes:
				dismiss();
				//清除数据，重新登录
				Editor editor = Util.getPreferences(mContext).edit();
				editor.clear();
				editor.commit();
				
				LoginActivity.actionStart(mContext);
				((Activity)mContext).finish();
				break;
			case R.id.id_textview_no:
				dismiss();
				break;
			}
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		searchBtn.setText(list.get(position));
		inputEdit.setHint(strings[position]);
		currentPosition = position;
	}
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
}

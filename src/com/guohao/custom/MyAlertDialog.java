package com.guohao.custom;

import java.util.ArrayList;
import java.util.List;

import com.guohao.graduationdesign_app.MoreMovieActivity;
import com.guohao.graduationdesign_app.R;
import com.guohao.util.Data;
import com.guohao.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
	private TextView showText;
	
	//当前布局
	private static int currentLayout = Layout01;
	
	
	//---构造函数---布局01
	public MyAlertDialog(Context context, int layout) {
		mContext = context;
		
		currentLayout = layout;
		initView();
		initData();
		initListener();
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
			showText = (TextView) v.findViewById(R.id.id_textview_prompt);
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
		if (showText != null) {
			showText.setText(prompt);
		}
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public void setheight(int height) {
		this.height = height; 
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

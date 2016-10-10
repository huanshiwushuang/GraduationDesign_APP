package com.guohao.custom;

import java.util.ArrayList;
import java.util.List;

import com.guohao.graduationdesign_app.R;
import com.guohao.util.Data;
import com.guohao.util.HttpUtil;
import com.guohao.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class MyBg extends FrameLayout implements OnClickListener,OnItemSelectedListener {
	private View view;
	private ImageView image;
	private TextView textView01,textView02;
	public static int TAG = 0;
	private final int Select_Pic_Quality = 1;
	private final int Select_Video_Quality = 2;
	private Spinner spinner;
	private List<String> list;
	private Boolean isFirst = false;
	

	public MyBg(Context context, AttributeSet attrs) {
		super(context, attrs);
		spinner = (Spinner) LayoutInflater.from(context).inflate(R.layout.custom_alertdialog_select_item, this).findViewById(R.id.id_spinner);
		spinner.setOnItemSelectedListener(this);
		view = LayoutInflater.from(context).inflate(R.layout.custom_my_bg, this);
		
		image = (ImageView) view.findViewById(R.id.id_imageview_pic);
		textView01 = (TextView) view.findViewById(R.id.id_textview_text01);
		textView02 = (TextView) view.findViewById(R.id.id_textview_text02);
		
		
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyBg);
		int imgId = array.getResourceId(R.styleable.MyBg_image, R.drawable.user_id);
		String text01 = array.getString(R.styleable.MyBg_text01);
		String text02 = array.getString(R.styleable.MyBg_text02);
		
		image.setImageResource(imgId);
		textView01.setText(text01);
		if (textView02 != null && !text02.equals("")) {
			textView02.setText(Html.fromHtml("<u>"+text02+"</u>"));
			textView02.setTag(TAG++);
			textView02.setOnClickListener(this);
		}
		array.recycle();
	}

	public void setText(String text) {
		if (textView01 != null) {
			textView01.setText(text);
		}
	}
	@Override
	public void onClick(View v) {
		int TAG = (int) v.getTag();
		ArrayAdapter<String> adapter;
		
		switch (TAG) {
		case 0:
			Util.showAlertDialog03(getContext(), "更新昵称", "请输入新昵称(1-20)", HttpUtil.VISIT_USER_TABLE_UPDATE_USERNAME);
			break;
		case 1:
			Util.showAlertDialog03(getContext(), "更新密码", "请输入新密码(6-20)", HttpUtil.VISIT_USER_TABLE_UPDATE_PWD);
			break;
		case 2:
			spinner.setTag(Select_Pic_Quality);
			list = new ArrayList<String>();
			list.add("请选择");
			list.add("高");
			list.add("低");
			adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_single_choice, list);
			spinner.setAdapter(adapter);
			spinner.performClick();
			isFirst = true;
			break;
		case 3:
			spinner.setTag(Select_Video_Quality);
			list = new ArrayList<String>();
			list.add("请选择");
			list.add("高");
			list.add("中");
			list.add("低");
			adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_single_choice, list);
			spinner.setAdapter(adapter);
			spinner.performClick();
			isFirst = true;
			break;
		case 4:
			Util.showAlertDialog04(getContext(), "注销", "将清除账户信息,是否确定注销？");
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (isFirst) {
			isFirst = false;
			return;
		}
		int tag = (int) parent.getTag();
		int select = -999;
		SharedPreferences p = Util.getPreferences(getContext());
		Editor editor = p.edit();
		MyBg myBg;
		switch (tag) {
		case Select_Pic_Quality:
			myBg = (MyBg) ((Activity)getContext()).findViewById(R.id.id_mybg_load_pic_size);
			switch (position) {
			case 1:
				select = Data.V_Pic_Loading_Big;
				myBg.setText("图片质量：高");
				break;
			case 2:
				select = Data.V_Pic_Loading_Small;
				myBg.setText("图片质量：低");
				break;
			}
			if (select != -999) {
				editor.putInt(Data.K_Pic_Loading, select);
			}
			break;
		case Select_Video_Quality:
			myBg = (MyBg) ((Activity)getContext()).findViewById(R.id.id_mybg_play_quality);
			switch (position) {
			case 1:
				select = Data.V_Video_Quality_High;
				myBg.setText("视频质量：高");
				break;
			case 2:
				select = Data.V_Video_Quality_Medium;
				myBg.setText("视频质量：中");
				break;
			case 3:
				select = Data.V_Video_Quality_Low;
				myBg.setText("视频质量：低");
				break;
			}
			if (select != -999) {
				editor.putInt(Data.K_Video_Quality, select);
			}
			break;
		}
		editor.commit();
	}
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Util.showToast(getContext(), "TAG:没选择");
	}
}

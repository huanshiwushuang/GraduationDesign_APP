package com.guohao.custom;


import java.util.ArrayList;
import java.util.List;

import com.guohao.graduationdesign_app.MoreMovieActivity;
import com.guohao.graduationdesign_app.R;
import com.guohao.util.Data;
import com.guohao.util.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;

public class Title extends LinearLayout implements OnTouchListener,OnClickListener,OnItemSelectedListener {
	private ImageView imageView; 
	private Context mContext;
	private EditText editText;
	private int currentPosition = 0;

	public Title(Context context, AttributeSet attrs) {
		super(context, attrs);
		View view = LayoutInflater.from(context).inflate(R.layout.custom_title, this);
		imageView = (ImageView) view.findViewById(R.id.id_imageview_search);
		imageView.setOnTouchListener(this);
		imageView.setOnClickListener(this);
		mContext = context;
	}

	public void onClick(View v) {
		List<String> list = new ArrayList<String>();
		list.add("电影名");
		list.add("电影别名");
		list.add("演员");
		list.add("导演");
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.custom_search, null);
		editText = (EditText) view.findViewById(R.id.id_edittext_search_input);
		Spinner spinner = (Spinner) view.findViewById(R.id.id_spinner_search);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, list);
		//为适配器设置下拉列表下拉时的菜单样式。    
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		
		spinner.setOnItemSelectedListener(this);
		spinner.setAdapter(adapter);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		final AlertDialog dialog;
		builder.setCancelable(false);
		builder.setTitle("搜索");
		builder.setView(view);
		builder.setPositiveButton("确定", null);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog = builder.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String searchValue = editText.getText().toString();
				if (Util.isEmpty(searchValue)) {
					Util.showToast(mContext, "不能为空");
					return;
				}
				MoreMovieActivity.actionStartSearch(mContext, Data.Search, currentPosition+"", searchValue);
				dialog.dismiss();
			}
		});
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			imageView.setImageResource(R.drawable.search_blue);
			break;
		case MotionEvent.ACTION_UP:
			//可能是防止 单击事件 被屏蔽
			v.performClick();
			imageView.setImageResource(R.drawable.search_white);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		currentPosition = position;
		switch (position) {
		case 0:
			editText.setHint("输入电影名");
			break;
		case 1:
			editText.setHint("输入电影别名");
			break;
		case 2:
			editText.setHint("输入演员名字");
			break;
		case 3:
			editText.setHint("输入导演名字");
			break;
		default:
			break;
		}
	}
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}

}

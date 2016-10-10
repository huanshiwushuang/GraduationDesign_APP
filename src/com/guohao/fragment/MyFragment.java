package com.guohao.fragment;

import com.guohao.custom.MyBg;
import com.guohao.graduationdesign_app.R;
import com.guohao.util.Data;
import com.guohao.util.Util;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MyFragment extends Fragment {
	private View view;
	private MyBg userId,username,registeTime,lastLoginTime,picQuality,videoQuality;
	private SharedPreferences p;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//特别注意，不能指定第二个参数。  因为这个 Fragment 会被添加到 ViewPager 里面。
		view = inflater.inflate(R.layout.fragment_my, null);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initView();
		
	}
	
	public void setUsername(String text) {
		username.setText(text);
	}
	
	private void initView() {
		p = Util.getPreferences(getActivity());
		
		userId = (MyBg) view.findViewById(R.id.id_mybg_user_id);
		username = (MyBg) view.findViewById(R.id.id_mybg_username);
		registeTime = (MyBg) view.findViewById(R.id.id_mybg_user_registe_time);
		lastLoginTime = (MyBg) view.findViewById(R.id.id_mybg_user_last_login_time);
		picQuality = (MyBg) view.findViewById(R.id.id_mybg_load_pic_size);
		videoQuality = (MyBg) view.findViewById(R.id.id_mybg_play_quality);
		
		userId.setText("用户ID："+p.getString(Data.K_User_Id, ""));
		username.setText("用户昵称："+p.getString(Data.K_User_Name, ""));
		registeTime.setText("注册时间："+p.getString(Data.K_User_Registe_Time, ""));
		lastLoginTime.setText("上次登录："+p.getString(Data.K_User_Last_Login_Time, ""));
		
		int picSize = p.getInt(Data.K_Pic_Loading, Data.V_Pic_Loading_Small);
		if (picSize == Data.V_Pic_Loading_Big) {
			picQuality.setText("图片质量：高");
		}else {
			picQuality.setText("图片质量：低");
		}
		switch (p.getInt(Data.K_Video_Quality, Data.V_Video_Quality_High)) {
		case Data.V_Video_Quality_High:
			videoQuality.setText("视频质量：高");
			break;
		case Data.V_Video_Quality_Medium:
			videoQuality.setText("视频质量：中");
			break;
		case Data.V_Video_Quality_Low:
			videoQuality.setText("视频质量：低");
			break;
		}
	}
}

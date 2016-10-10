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
		//�ر�ע�⣬����ָ���ڶ���������  ��Ϊ��� Fragment �ᱻ��ӵ� ViewPager ���档
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
		
		userId.setText("�û�ID��"+p.getString(Data.K_User_Id, ""));
		username.setText("�û��ǳƣ�"+p.getString(Data.K_User_Name, ""));
		registeTime.setText("ע��ʱ�䣺"+p.getString(Data.K_User_Registe_Time, ""));
		lastLoginTime.setText("�ϴε�¼��"+p.getString(Data.K_User_Last_Login_Time, ""));
		
		int picSize = p.getInt(Data.K_Pic_Loading, Data.V_Pic_Loading_Small);
		if (picSize == Data.V_Pic_Loading_Big) {
			picQuality.setText("ͼƬ��������");
		}else {
			picQuality.setText("ͼƬ��������");
		}
		switch (p.getInt(Data.K_Video_Quality, Data.V_Video_Quality_High)) {
		case Data.V_Video_Quality_High:
			videoQuality.setText("��Ƶ��������");
			break;
		case Data.V_Video_Quality_Medium:
			videoQuality.setText("��Ƶ��������");
			break;
		case Data.V_Video_Quality_Low:
			videoQuality.setText("��Ƶ��������");
			break;
		}
	}
}

package com.guohao.fragment;

import com.guohao.graduationdesign_app.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MyFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//�ر�ע�⣬����ָ���ڶ���������  ��Ϊ��� Fragment �ᱻ��ӵ� ViewPager ���档
		View view = inflater.inflate(R.layout.fragment_my, null);
		return view;
	}
}

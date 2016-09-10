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
		//特别注意，不能指定第二个参数。  因为这个 Fragment 会被添加到 ViewPager 里面。
		View view = inflater.inflate(R.layout.fragment_my, null);
		return view;
	}
}

package com.guohao.fragment;

import com.guohao.custom.ExpandView;
import com.guohao.graduationdesign_app.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class AllFragment extends Fragment implements OnClickListener {
	private ExpandView expandView;
	private View view;
	private String[] language,type,date,region,other;
	private TextView showTextView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//�ر�ע�⣬����ָ���ڶ���������  ��Ϊ��� Fragment �ᱻ��ӵ� ViewPager ���档
		view = inflater.inflate(R.layout.fragment_all, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initView();
	}

	private void initView() {
		language = new String[]{"ȫ��","����","����","Ӣ��","����","����","����","����"};
		type = new String[]{"ȫ��","����","ϲ��","����","�ƻ�","����","�ֲ�","����","���","ս��","����","���","����",
							"����","����","ð��","��ʷ","��ͥ","����","����","����","����","�˶�","��װ","��ɫ",
							"ͬ��","����","��Ƭ","��ɫ��Ӱ","��ͯ","��̨����","��¼Ƭ","���"};
		date = new String[]{"ȫ��","2016","2015","2014","2013","2012","2011","2010",
							"2009","2008","2007","2006","2005","2004����ǰ"};
		region = new String[]{"ȫ��","��½","���","̨��","����","����","Ӣ��","�ձ�",
							"����","�¹�","̩��","ӡ��","�����","������","���ô�","����"};
		other = new String[]{"����","�ر�","����"};
		//------------------------------------------------
		expandView = (ExpandView) view.findViewById(R.id.id_expandview);
		showTextView = (TextView) view.findViewById(R.id.id_textview_show);
		showTextView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (expandView.isExpand()) {
			showTextView.setText("��ʾ");
			expandView.collapse();
		}else {
			showTextView.setText("����");
			expandView.expand();
		}
	}
}

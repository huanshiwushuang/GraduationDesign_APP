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
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


public class AllFragment extends Fragment implements OnClickListener {
	private ExpandView expandView;
	private View view;
	private String[] language,type,date,region,other;
	private String[] languageSql,typeSql,dateSql,regionSql,otherSql;
	private TextView showTextView;
	private LinearLayout languageLayout,typeLayout,dateLayout,regionLayout,otherLayout;
	
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
		initLocalData(language,languageLayout);
		initLocalData(type,typeLayout);
		initLocalData(date,dateLayout);
		initLocalData(region,regionLayout);
		initLocalData(other,otherLayout);
	}

	private void initLocalData(String[] array,LinearLayout layout) {
		for (int i = 0; i < array.length; i++) {
			TextView t = new TextView(getActivity());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(5, 0, 5, 0);
			t.setLayoutParams(params);
			t.setText(array[i]);
			layout.addView(t);
		}
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
		languageSql = new String[]{};
		typeSql = new String[]{};
		dateSql = new String[]{};
		regionSql = new String[]{};
		otherSql = new String[]{};
		//------------------------------------------------
		languageLayout = (LinearLayout) view.findViewById(R.id.id_linearlayout_language);
		typeLayout = (LinearLayout) view.findViewById(R.id.id_linearlayout_type);
		dateLayout = (LinearLayout) view.findViewById(R.id.id_linearlayout_date);
		regionLayout = (LinearLayout) view.findViewById(R.id.id_linearlayout_region);
		otherLayout = (LinearLayout) view.findViewById(R.id.id_linearlayout_other);
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

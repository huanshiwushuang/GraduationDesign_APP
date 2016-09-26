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
		//特别注意，不能指定第二个参数。  因为这个 Fragment 会被添加到 ViewPager 里面。
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
		language = new String[]{"全部","国语","粤语","英语","韩语","日语","法语","其他"};
		type = new String[]{"全部","动作","喜剧","爱情","科幻","灾难","恐怖","悬疑","奇幻","战争","犯罪","惊悚","动画",
							"伦理","剧情","冒险","历史","家庭","歌舞","传记","音乐","西部","运动","古装","情色",
							"同性","武侠","短片","黑色电影","儿童","舞台艺术","纪录片","鬼怪"};
		date = new String[]{"全部","2016","2015","2014","2013","2012","2011","2010",
							"2009","2008","2007","2006","2005","2004及以前"};
		region = new String[]{"全部","大陆","香港","台湾","美国","法国","英国","日本",
							"韩国","德国","泰国","印度","意大利","西班牙","加拿大","其他"};
		other = new String[]{"最新","特别","好评"};
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
			showTextView.setText("显示");
			expandView.collapse();
		}else {
			showTextView.setText("隐藏");
			expandView.expand();
		}
	}
}

package com.guohao.fragment;

import com.guohao.custom.ExpandView;
import com.guohao.graduationdesign_app.R;
import com.guohao.util.Util;

import android.graphics.Color;
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
	
	private final String languageString = "language",
					typeString = "type",
					dateString = "date",
					regionString = "region",
					otherString = "other";
	private TextView[] languageTextView,typeTextView,dateTextView,regionTextView,otherTextView;
	
	//分类---当前选中状态
	private int languagePosition = 0,typePosition = 0,
				datePosition = 0,regionPosition = 0,otherPosition = 0;
	
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
		initLocalData(languageString,language,languageLayout,languageTextView);
		initLocalData(typeString,type,typeLayout,typeTextView);
		initLocalData(dateString,date,dateLayout,dateTextView);
		initLocalData(regionString,region,regionLayout,regionTextView);
		initLocalData(otherString,other,otherLayout,otherTextView);
	}

	private void initLocalData(String flag, String[] array,LinearLayout layout,TextView[] textViews) {
		for (int i = 0; i < array.length; i++) {
			TextView t = new TextView(getActivity());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(5, 0, 5, 0);
			t.setLayoutParams(params);
			t.setText(array[i]);
			if (i == 0) {
				t.setTextColor(Color.parseColor("#7DCBF5"));
			}
			t.setOnClickListener(this);
			//用这个标识---区分是哪一排的，方便点击事件的判断
			t.setTag(new Object[]{flag,i});
			textViews[i] = t;
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
		other = new String[]{"最新","热门","好评","可播放"};
		//--
		languageTextView = new TextView[language.length]; 
		typeTextView = new TextView[type.length];
		dateTextView = new TextView[date.length];
		regionTextView = new TextView[region.length];
		otherTextView = new TextView[other.length];
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
		switch (v.getId()) {
		case R.id.id_textview_show:
			if (expandView.isExpand()) {
				showTextView.setText("显示");
				expandView.collapse();
			}else {
				showTextView.setText("隐藏");
				expandView.expand();
			}
			break;
		default :
			Object obj = v.getTag();
			if (obj != null) {
				Object[] objects = (Object[]) obj;
				String flag = (String) objects[0];
				int index = (int) objects[1];
				
				switch (flag) {
				case languageString:
					languageTextView[languagePosition].setTextColor(Color.parseColor("#000000"));
					languageTextView[index].setTextColor(Color.parseColor("#7DCBF5"));
					//选中的第几个
					languagePosition = index;
					break;
				case typeString:
					typeTextView[typePosition].setTextColor(Color.parseColor("#000000"));
					typeTextView[index].setTextColor(Color.parseColor("#7DCBF5"));
					//选中的第几个
					typePosition = index;
					break;
				case dateString:
					dateTextView[datePosition].setTextColor(Color.parseColor("#000000"));
					dateTextView[index].setTextColor(Color.parseColor("#7DCBF5"));
					//选中的第几个
					datePosition = index;
					break;
				case regionString:
					regionTextView[regionPosition].setTextColor(Color.parseColor("#000000"));
					regionTextView[index].setTextColor(Color.parseColor("#7DCBF5"));
					//选中的第几个
					regionPosition = index;
					break;
				case otherString:
					otherTextView[otherPosition].setTextColor(Color.parseColor("#000000"));
					otherTextView[index].setTextColor(Color.parseColor("#7DCBF5"));
					//选中的第几个
					otherPosition = index;
					break;
				}
			}
			break;
		}
		
		
	}
}

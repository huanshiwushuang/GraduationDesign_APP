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
		//特别注意，不能指定第二个参数。  因为这个 Fragment 会被添加到 ViewPager 里面。
		view = inflater.inflate(R.layout.fragment_all, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initView();
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

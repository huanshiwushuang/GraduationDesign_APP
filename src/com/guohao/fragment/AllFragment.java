package com.guohao.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guohao.custom.ExpandView;
import com.guohao.custom.LoadMore;
import com.guohao.custom.LoadMore.OnLoadListener;
import com.guohao.graduationdesign_app.MovieDetailActivity;
import com.guohao.graduationdesign_app.R;
import com.guohao.inter.HttpCallBackListenerString;
import com.guohao.util.Data;
import com.guohao.util.HttpUtil;
import com.guohao.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class AllFragment extends Fragment implements OnClickListener,OnLoadListener,OnRefreshListener {
	private final int Loading_Data_Success = 1;
	private final int Loading_Data_Fail = 0;
	private final String NoMore = "没有更多了";
	private final String languageString = "language", typeString = "type", dateString = "date", regionString = "region",
			otherString = "other";
	private final int LINE_COUNT = 3;
	private int startLine = 1;
	// 每次加载多少部
	private final int loadCountOnce = 15;
	
	private ExpandView expandView;
	private View view;
	private TextView showTextView;
	private Handler handler;
	private LoadMore loadMore;
	private ConditionAdapter adapter;
	private SwipeRefreshLayout refreshLayout;

	private LinearLayout languageLayout, typeLayout, dateLayout, regionLayout, otherLayout;
	private String[] language, type, date, region, other;
	
	private TextView[] languageTextView, typeTextView, dateTextView, regionTextView, otherTextView;
	
	private List<String> list;
	private ListView listView;
	private View mListViewFooter;
	private TextView refreshPrompt;
	//表明这次刷新请求，必须要清空所有数据
	private Boolean thisIsRefresh = false;
	// 分类---当前选中状态
	private int languagePosition = 0, typePosition = 0, datePosition = 0, regionPosition = 0, otherPosition = 0;
	//碎片01 和 碎片02 同时完成才能够dismiss
	public static Boolean isComplete = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// 特别注意，不能指定第二个参数。 因为这个 Fragment 会被添加到 ViewPager 里面。
		view = inflater.inflate(R.layout.fragment_all, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initView();
		initLocalData(languageString, language, languageLayout, languageTextView);
		initLocalData(typeString, type, typeLayout, typeTextView);
		initLocalData(dateString, date, dateLayout, dateTextView);
		initLocalData(regionString, region, regionLayout, regionTextView);
		initLocalData(otherString, other, otherLayout, otherTextView);
		getNetworkData();
		setAdapter();
	}
	
	private void setAdapter() {
        listView.addFooterView(mListViewFooter, null, false);
		listView.setAdapter(adapter);
	}

	private void getNetworkData() {
		NetworkInfo info = Util.getNetworkInfo(getActivity());
		if (info == null || !info.isAvailable() ) {
			refreshLayout.setRefreshing(false);
			Util.showToast(getActivity(), "无网络");
			refreshPrompt.setVisibility(View.VISIBLE);
			return;
		}
		HttpUtil.visitMovieInfoTableCondition(startLine, startLine+loadCountOnce-1, languagePosition, typePosition, datePosition,
				regionPosition, otherPosition, new HttpCallBackListenerString() {
					public void onFinish(String response) {
						Message msg = handler.obtainMessage();
						String code = null;
						String data = null;
						try {
							JSONObject object = new JSONObject(response);
							code = object.getString(Data.KEY_CODE);
							data = object.getString(Data.KEY_DATA);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (code.equals(Data.VALUE_OK)) {
							JSONArray array = null;
							try {
								array = new JSONArray(data);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							if (array == null || array.length() <= 0) {
								msg.what = Loading_Data_Fail;
								msg.obj = NoMore;
							}else {
								msg.what = Loading_Data_Success;
								msg.obj = array;
							}
							handler.sendMessage(msg);
						} else {
							msg.obj = data;
							msg.what = Loading_Data_Fail;
							handler.sendMessage(msg);
						}
					}
					public void onError(String e) {
						Message msg = handler.obtainMessage();
						msg.obj = e;
						msg.what = Loading_Data_Fail;
						handler.sendMessage(msg);
					}
				});
	}

	private void initLocalData(String flag, String[] array, LinearLayout layout, TextView[] textViews) {
		for (int i = 0; i < array.length; i++) {
			TextView t = new TextView(getActivity());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.setMargins(5, 0, 5, 0);
			t.setLayoutParams(params);
			t.setText(array[i]);
			if (i == 0) {
				t.setTextColor(Color.parseColor("#7DCBF5"));
			}
			t.setOnClickListener(this);
			// 用这个标识---区分是哪一排的，方便点击事件的判断
			t.setTag(new Object[] { flag, i });
			textViews[i] = t;
			layout.addView(t);
		}
	}

	private void initView() {
		language = new String[] { "全部", "国语", "粤语", "英语", "韩语", "日语", "法语", "其他" };
		type = new String[] { "全部", "动作", "喜剧", "爱情", "科幻", "灾难", "恐怖", "悬疑", "奇幻", "战争", "犯罪", "惊悚", "动画", "伦理", "剧情",
				"冒险", "历史", "家庭", "歌舞", "传记", "音乐", "西部", "运动", "古装", "情色", "同性", "武侠", "短片", "黑色电影", "儿童", "舞台艺术",
				"纪录片", "鬼怪" };
		date = new String[] { "全部", "2016", "2015", "2014", "2013", "2012", "2011", "2010", "2009", "2008", "2007",
				"2006", "2005", "2004及以前" };
		region = new String[] { "全部", "大陆", "香港", "台湾", "美国", "法国", "英国", "日本", "韩国", "德国", "泰国", "印度", "意大利", "西班牙",
				"加拿大", "其他" };
		other = new String[] { "最新", "热门", "好评", "可播放" };
		// --
		languageTextView = new TextView[language.length];
		typeTextView = new TextView[type.length];
		dateTextView = new TextView[date.length];
		regionTextView = new TextView[region.length];
		otherTextView = new TextView[other.length];
		// ------------------------------------------------
		languageLayout = (LinearLayout) view.findViewById(R.id.id_linearlayout_language);
		typeLayout = (LinearLayout) view.findViewById(R.id.id_linearlayout_type);
		dateLayout = (LinearLayout) view.findViewById(R.id.id_linearlayout_date);
		regionLayout = (LinearLayout) view.findViewById(R.id.id_linearlayout_region);
		otherLayout = (LinearLayout) view.findViewById(R.id.id_linearlayout_other);
		// ------------------------------------------------
		adapter = new ConditionAdapter();
		mListViewFooter = LayoutInflater.from(getActivity()).inflate(R.layout.listview_footer, null);
        mListViewFooter.setVisibility(View.GONE);
		// ------------------------------------------------
		expandView = (ExpandView) view.findViewById(R.id.id_expandview);
		showTextView = (TextView) view.findViewById(R.id.id_textview_show);
		list = new ArrayList<String>();
		loadMore = (LoadMore) view.findViewById(R.id.id_loadmore_condition);
		loadMore.setOnLoadListener(this,mListViewFooter);
		listView = (ListView) view.findViewById(R.id.id_listview_condition);
		showTextView.setOnClickListener(this);
		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_swiperefreshlayout_refresh_condition);
		refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
		refreshLayout.setOnRefreshListener(this);
		refreshPrompt = (TextView) view.findViewById(R.id.id_textview_refresh_prompt_condition);
		// ------------------------------------------------
		
		handler = new Handler() {
			int i = 0;
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Loading_Data_Success:
					JSONArray array = (JSONArray) msg.obj;
					//如果这次是刷新请求
					if (thisIsRefresh) {
						thisIsRefresh = false;
						list.clear();
					}
					addDataToList(array);
					loadMoreDataEnd();
					break;
				case Loading_Data_Fail:
					String error = (String) msg.obj;
					Util.showToast(getActivity(), error);
					if (error.equals(NoMore) && thisIsRefresh) {
						thisIsRefresh = false;
						list.clear();
						adapter.notifyDataSetChanged();
					}
					if (startLine > loadCountOnce) {
						startLine -= loadCountOnce;
					}
					loadMoreDataEnd();
					break;
				default:
					break;
				}
				
				if (list.size() <= 0) {
					refreshPrompt.setVisibility(View.VISIBLE);
				}else {
					refreshPrompt.setVisibility(View.GONE);
				}
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						refreshLayout.setRefreshing(false);
					}
				}, 700);
			}
		};

	}
	protected void loadMoreDataEnd() {
        loadMore.setLoading(false);
        AllFragment.isComplete = true;
        if (RecommendFragment.isComplete) {
        	Util.dismissProgressDialog();
		}
	}
	private void addDataToList(JSONArray array) {
		StringBuilder builder = new StringBuilder();
		int j = array.length() / LINE_COUNT;
		int k = array.length() % LINE_COUNT;
		// 每类-有这么多行（比如热门电影）
		int m = (k == 0 ? j : j + 1);
		// 每类-最后一行有多少个
		int p = (k == 0 ? LINE_COUNT : k);
		// 每排的个数
		int o = LINE_COUNT;
		for (int i = 0; i < m; i++) {
			j = i * LINE_COUNT;
			o = (i == m - 1 ? p : LINE_COUNT);
			builder.append("[");
			for (int n = j; n < j + o; n++) {
				try {
					builder.append(array.get(n).toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (n != j + o - 1) {
					builder.append(",");
				}
			}
			builder.append("]");
			list.add(builder.toString());
			builder.delete(0, builder.length());
		}
		adapter.notifyDataSetChanged();
	}

	class ConditionAdapter extends BaseAdapter implements OnClickListener {
		Context mContext;
		LayoutInflater inflater;

		public ConditionAdapter() {
			mContext = getActivity();
			inflater = LayoutInflater.from(mContext);
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			String data = list.get(position);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.fragment_recommend_item_content, null);
				holder = new ViewHolder();
				holder.img01 = (ImageView) convertView.findViewById(R.id.id_imageview_img01);
				holder.text01 = (TextView) convertView.findViewById(R.id.id_textview_text01);

				holder.img02 = (ImageView) convertView.findViewById(R.id.id_imageview_img02);
				holder.text02 = (TextView) convertView.findViewById(R.id.id_textview_text02);

				holder.img03 = (ImageView) convertView.findViewById(R.id.id_imageview_img03);
				holder.text03 = (TextView) convertView.findViewById(R.id.id_textview_text03);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				JSONArray array = new JSONArray(data);
				// 获取图片行的--根节点
				LinearLayout layout = (LinearLayout) holder.img01.getParent().getParent();
				for (int j = 0; j < LINE_COUNT; j++) {
					LinearLayout l = (LinearLayout) layout.getChildAt(j);
					l.setVisibility(View.INVISIBLE);
				}
				for (int j = 0; j < array.length(); j++) {
					JSONObject object = array.getJSONObject(j);
					LinearLayout l = (LinearLayout) layout.getChildAt(j);

					l.setVisibility(View.VISIBLE);
					//标识---第几排，第几个
					l.setTag(new int[]{position,j});
					l.setOnClickListener(this);

					ImageView image = (ImageView) l.getChildAt(0);
					TextView text = (TextView) l.getChildAt(1);

					setImage(image, object.getString("moviePicLink"));
					text.setText(object.getString("movieName"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return convertView;
		}

		@Override
		public void onClick(View v) {
			LinearLayout layout = (LinearLayout) v;
			int[] i = (int[]) layout.getTag();
			int row = i[0];
			int col = i[1];
			try {
				JSONArray array = new JSONArray(list.get(row));
				JSONObject object = array.getJSONObject(col);
				MovieDetailActivity.actionStart(getActivity(), object.getString("movieInfoTableId"), object.getString("movieName"), object.getString("moviePicLink"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		public void setImage(final ImageView image, String picLink) {
			NetworkInfo info = Util.getNetworkInfo(getActivity());
			if (info == null || !info.isAvailable()) {
				return;
			}
			int i = Util.getPreferences(getActivity()).getInt(Data.K_Pic_Loading, Data.V_Pic_Loading_Small);
			if (i == Data.V_Pic_Loading_Big) {
				picLink = Util.getBigImageAddress(picLink);
			}
			Util.setImageWH(getActivity(), image, LINE_COUNT);
			ImageLoader loader = ImageLoader.getInstance();
			loader.displayImage(picLink, image);
		}
	}

	class ViewHolder {
		ImageView img01;
		TextView text01;

		ImageView img02;
		TextView text02;

		ImageView img03;
		TextView text03;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_textview_show:
			if (expandView.isExpand()) {
				showTextView.setText("显示");
				expandView.collapse();
			} else {
				showTextView.setText("隐藏");
				expandView.expand();
			}
			break;
		default:
			Object obj = v.getTag();
			if (obj != null) {
				Object[] objects = (Object[]) obj;
				String flag = (String) objects[0];
				int index = (int) objects[1];

				switch (flag) {
				case languageString:
					languageTextView[languagePosition].setTextColor(Color.parseColor("#000000"));
					languageTextView[index].setTextColor(Color.parseColor("#7DCBF5"));
					// 选中的第几个
					languagePosition = index;
					break;
				case typeString:
					typeTextView[typePosition].setTextColor(Color.parseColor("#000000"));
					typeTextView[index].setTextColor(Color.parseColor("#7DCBF5"));
					// 选中的第几个
					typePosition = index;
					break;
				case dateString:
					dateTextView[datePosition].setTextColor(Color.parseColor("#000000"));
					dateTextView[index].setTextColor(Color.parseColor("#7DCBF5"));
					// 选中的第几个
					datePosition = index;
					break;
				case regionString:
					regionTextView[regionPosition].setTextColor(Color.parseColor("#000000"));
					regionTextView[index].setTextColor(Color.parseColor("#7DCBF5"));
					// 选中的第几个
					regionPosition = index;
					break;
				case otherString:
					otherTextView[otherPosition].setTextColor(Color.parseColor("#000000"));
					otherTextView[index].setTextColor(Color.parseColor("#7DCBF5"));
					// 选中的第几个
					otherPosition = index;
					break;
				}
				Util.showProgressDialog(getActivity());
				thisIsRefresh = true;
				startLine = 1;
				getNetworkData();
			}
			break;
		}
	}

	@Override
	public void onLoad() {
		if (list.size() <= 0) {
			Util.showToast(getActivity(), "请下拉刷新");
			loadMoreDataEnd();
			return;
		}
		NetworkInfo info = Util.getNetworkInfo(getActivity());
		if (info == null || !info.isAvailable() ) {
			loadMoreDataEnd();
			Util.showToast(getActivity(), "无网络");
			return;
		}
		loadMore.postDelayed(new Runnable() {
            @Override
            public void run() {
            	startLine += loadCountOnce;
        		getNetworkData();
            }
        }, 1500);
	}
	@Override
	public void onRefresh() {
		NetworkInfo info = Util.getNetworkInfo(getActivity());
		if (info == null || !info.isAvailable() ) {
			refreshLayout.setRefreshing(false);
			Util.showToast(getActivity(), "无网络");
			return;
		}
		startLine = 1;
		thisIsRefresh = true;
		getNetworkData(); 
	}
}

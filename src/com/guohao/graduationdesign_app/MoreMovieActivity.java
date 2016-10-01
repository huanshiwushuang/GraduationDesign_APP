package com.guohao.graduationdesign_app;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guohao.custom.LoadMore;
import com.guohao.custom.LoadMore.OnLoadListener;
import com.guohao.fragment.RecommendFragment;
import com.guohao.inter.HttpCallBackListenerString;
import com.guohao.util.Data;
import com.guohao.util.HttpUtil;
import com.guohao.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MoreMovieActivity extends Activity implements OnLoadListener {
	private String want;
	private ListView listView;
	private View listviewFooter;
	private LoadMore loadMore;
	//根据 RecommendFragment 中的加载的电影数量，决定这里加载从 第几部开始
	private int startLine;
	private int nextLine;
	//每次加载的数量，根据这个 和 startLine ，决定加载第几条到第几条的数据。
	private final int loadCountOnce = 15;
	//每行的数量
	private final int LINE_COUNT = 3;
	
	private final int Load_Data_Fail = 0;
	private final int Load_Data_Success = 1;
	
	private Handler handler;
	private List<String> list;
	private MoreAdapter adapter;
	private String titleStr;
	private TextView titleTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_movie);
		Util.showProgressDialog(MoreMovieActivity.this);
		initData();
		initView();
		setBaseData();
		getData();
	}
	
	private void setBaseData() {
		titleTextView.setText(titleStr);
	}

	private void getData() {
		HttpUtil.visitMovieInfoTable(want, nextLine+"", nextLine+loadCountOnce-1+"", new HttpCallBackListenerString() {
			
			@Override
			public void onFinish(String response) {
				Message msg = handler.obtainMessage();
				try {
					JSONObject object = new JSONObject(response);
					String code = object.getString(Data.KEY_CODE);
					if (code.equals(Data.VALUE_OK)) {
						JSONArray array = object.getJSONArray(Data.KEY_DATA);
						int i = array.length();
						if (i <= 0) {
							msg.what = Load_Data_Fail;
							msg.obj = "没有更多了";
						}else {
							addDataToList(array);
							msg.what = Load_Data_Success;
						}
						Log.d("guohao", "这里的："+want+"---"+nextLine+"---"+array.toString());
					}else {
						msg.what = Load_Data_Fail;
						msg.obj = object.getString(Data.KEY_DATA);
					}
					handler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onError(String e) {
				Message msg = handler.obtainMessage();
				msg.what = Load_Data_Fail;
				msg.obj = e;
				handler.sendMessage(msg);
			}
			
			private void addDataToList(JSONArray array) {
				StringBuilder builder = new StringBuilder();
				int j = array.length()/LINE_COUNT;
				int k = array.length()%LINE_COUNT;
				//每类-有这么多行（比如热门电影）
				int m = (k == 0 ? j : j+1);
				//每类-最后一行有多少个
				int p = (k == 0 ? LINE_COUNT : k);
				//每排的个数
				int o = LINE_COUNT;
				for (int i = 0; i < m; i++) {
					j = i*LINE_COUNT;
					o = (i == m-1 ? p : LINE_COUNT);
					builder.append("[");
					for (int n = j; n < j+o; n++) {
						try {
							builder.append(array.get(n).toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (n != j+o-1) {
							builder.append(",");
						}
					}
					builder.append("]");
					list.add(builder.toString());
					builder.delete(0, builder.length());
				}
			}
		});
	}

	private void setData() {
		adapter.notifyDataSetChanged();
	}
	
	class MoreAdapter extends BaseAdapter implements OnClickListener {
		Activity mActivity = null;
		
		public MoreAdapter() {
			mActivity = MoreMovieActivity.this;
		}
		@Override
		public int getCount() {
			return list.size();
		}
		@Override
		public Object getItem(int position) {
			return list.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolderContent holderContent = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_recommend_item_content, null);
				holderContent = new ViewHolderContent();
				holderContent.img01 = (ImageView) convertView.findViewById(R.id.id_imageview_img01);
				holderContent.text01 = (TextView) convertView.findViewById(R.id.id_textview_text01);
				
				holderContent.img02 = (ImageView) convertView.findViewById(R.id.id_imageview_img02);
				holderContent.text02 = (TextView) convertView.findViewById(R.id.id_textview_text02);
				
				holderContent.img03 = (ImageView) convertView.findViewById(R.id.id_imageview_img03);
				holderContent.text03 = (TextView) convertView.findViewById(R.id.id_textview_text03);
				convertView.setTag(holderContent);
			}else {
				holderContent = (ViewHolderContent) convertView.getTag();
			}
			try {
				JSONArray array = new JSONArray(list.get(position));
				//获取图片行的--根节点
				LinearLayout layout = (LinearLayout) holderContent.img01.getParent().getParent();
				for (int j = 0; j < LINE_COUNT; j++) {
					LinearLayout l = (LinearLayout) layout.getChildAt(j);
					l.setVisibility(View.INVISIBLE);
				}
				for (int j = 0; j < array.length(); j++) {
					JSONObject object = array.getJSONObject(j);
					LinearLayout l = (LinearLayout) layout.getChildAt(j);
					
					l.setTag(R.id.Click_Position, position);
					l.setVisibility(View.VISIBLE);
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
		
		public void setImage(final ImageView image, String picLink) {
			NetworkInfo info = Util.getNetworkInfo(mActivity);
			if (info == null || !info.isAvailable()) {
				return;
			}
			int i = Util.getPreferences(mActivity).getInt(Data.K_Pic_Loading, Data.V_Pic_Loading_Small);
			if (i == Data.V_Pic_Loading_Big) {
				picLink = Util.getBigImageAddress(picLink);
			}
			Util.setImageWH(mActivity, image, LINE_COUNT);
			ImageLoader loader = ImageLoader.getInstance();
			loader.displayImage(picLink, image);
		}
		@Override
		public void onClick(View v) {
			int position = (int) v.getTag(R.id.Click_Position);
			try {
				JSONArray array = new JSONArray(list.get(position));
				JSONObject object = null;
				switch (v.getId()) {
				case R.id.id_linearlayout_movie00:
					object = (JSONObject) array.get(0);
					break;
				case R.id.id_linearlayout_movie01:
					object = (JSONObject) array.get(1);
					break;
				case R.id.id_linearlayout_movie02:
					object = (JSONObject) array.get(2);
					break;
				default:
					return;
				}
				MovieDetailActivity.actionStart(mActivity, object.getString("movieInfoTableId"), object.getString("movieName"), object.getString("moviePicLink"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	class ViewHolderContent {
		ImageView img01;
		TextView text01;
		
		ImageView img02;
		TextView text02;
		
		ImageView img03;
		TextView text03;
	}
	

	private void initView() {
		listView = (ListView) findViewById(R.id.id_listview_more_movie);
		list = new ArrayList<String>();
		loadMore = (LoadMore) findViewById(R.id.id_loadmore);
		
		listviewFooter = LayoutInflater.from(MoreMovieActivity.this).inflate(R.layout.listview_footer, null);
		listviewFooter.setVisibility(View.GONE);
		adapter = new MoreAdapter();
		
		listView.addFooterView(listviewFooter, null, false);
		listView.setAdapter(adapter);
		loadMore.setOnLoadListener(this,listviewFooter);
		titleTextView = (TextView) findViewById(R.id.id_textview_more_title);
		
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case Load_Data_Success:
					setData();
					loadMoreDataEnd();
					break;
				case Load_Data_Fail:
					String errorMsg = (String) msg.obj;
					Util.showToast(MoreMovieActivity.this, errorMsg);
					loadMoreDataEnd();
					break;
				}
			}
		};
	}


	protected void loadMoreDataEnd() {
        loadMore.setLoading(false);
        Util.dismissProgressDialog();
	}

	@Override
	public void onLoad() {
		NetworkInfo info = Util.getNetworkInfo(MoreMovieActivity.this);
		if (info == null || !info.isAvailable() ) {
			loadMoreDataEnd();
			Util.showToast(MoreMovieActivity.this, "无网络");
			return;
		}
        loadMore.postDelayed(new Runnable() {
            @Override
            public void run() {
            	nextLine += loadCountOnce;
            	//开始请求数据
            	getData();
            }
        }, 1500);

	}
	
	private void initData() {
		Intent intent = getIntent();
		want = intent.getStringExtra("want");
		switch (want) {
		case Data.Recommend:
			titleStr = "推荐电影";
			break;
		case Data.Hot:
			titleStr = "热门电影";
			break;
		case Data.Newest:
			titleStr = "最新电影";
			break;
		case Data.HighMark:
			titleStr = "高分电影";
			break;
		default:
			titleStr = "电影";
			break;
		}
		
		startLine = RecommendFragment.CLASS_COUNT+1;
		nextLine = startLine;
	}

	public static void actionStart(Context c, String want) {
		Intent intent = new Intent(c, MoreMovieActivity.class);
		intent.putExtra("want", want);
		c.startActivity(intent);
	}

}

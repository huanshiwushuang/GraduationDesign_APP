package com.guohao.fragment;



import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guohao.graduationdesign_app.MoreMovieActivity;
import com.guohao.graduationdesign_app.MovieDetailActivity;
import com.guohao.graduationdesign_app.R;
import com.guohao.inter.HttpCallBackListenerString;
import com.guohao.util.Data;
import com.guohao.util.HttpUtil;
import com.guohao.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RecommendFragment extends Fragment {
	private ListView listView;
	private View view;
	private TextView refreshPrompt;
	
	private final int Loading_Data_Success = 1;
	private final int Loading_Data_Fail = 0;
	
	private final String Recommend_M = "�Ƽ���Ӱ";
	private final String Hot_M = "���ŵ�Ӱ";
	private final String Newest_M = "���µ�Ӱ";
	private final String HighMark_M = "�߷ֵ�Ӱ";
	//ÿ�ж��ٲ� ��Ӱ
	private final int LINE_COUNT = 3;
	public static final int CLASS_COUNT = 15;
	//�洢����-ÿ������
	private List<String> list;
	private RecommendAdapter adapter;
	
	//��ʶ��---�������������Ƿ���ɣ����ظ����ˢ��ʱ����Ҫ��֤�� �������� ���ص� List ����Ժ󣬲Ž����´�ˢ�£�
	private SwipeRefreshLayout refreshLayout;
	private NetworkInfo info;
	
	private Handler handler = new Handler() {
		private String want,error;
		private JSONArray array;
		private String startLine,endLine;
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Loading_Data_Success:
				Object[] objects = (Object[]) msg.obj;
				want = (String) objects[0];
				array = (JSONArray) objects[1];
				startLine = (String) objects[2];
				endLine = (String) objects[3];
				
				next();
				setDataInList();
				break;
			case Loading_Data_Fail:
				Object[] objs = (Object[]) msg.obj;
				want = (String) objs[0];
				error = (String) objs[1];
				startLine = (String) objs[2];
				endLine = (String) objs[3];
				
				Util.showToast(getActivity(), error);
				next();
				break;
			default:
				break;
			}
			
		}
		private void setDataInList() {
			switch (want) {
			case Data.Recommend:
				//ռλ�ӵģ����� hot highMark �ȵı���λ�á�
				list.clear();
				list.add(Recommend_M);
				addDataToList(array);
				break;
			case Data.Hot:
				//ռλ�ӵģ����� hot highMark �ȵı���λ�á�
				list.add(Hot_M);
				addDataToList(array);
				break;
			case Data.Newest:
				//ռλ�ӵģ����� hot highMark �ȵı���λ�á�
				list.add(Newest_M);
				addDataToList(array);
				break;
			case Data.HighMark:
				//ռλ�ӵģ����� hot highMark �ȵı���λ�á�
				list.add(HighMark_M);
				addDataToList(array);
				break;
			default:
				break;
			}
			adapter.notifyDataSetChanged();
		}
		private void addDataToList(JSONArray array) {
			StringBuilder builder = new StringBuilder();
			int j = array.length()/LINE_COUNT;
			int k = array.length()%LINE_COUNT;
			//ÿ��-����ô���У��������ŵ�Ӱ��
			int m = (k == 0 ? j : j+1);
			//ÿ��-���һ���ж��ٸ�
			int p = (k == 0 ? LINE_COUNT : k);
			//ÿ�ŵĸ���
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
		private void next() {
			switch (want) {
			case Data.Recommend:
				getData(Data.Hot, startLine, endLine);
				break;
			case Data.Hot:
				getData(Data.Newest, startLine, endLine);
				break;
			case Data.Newest:
				getData(Data.HighMark, startLine, endLine);
				break;
			case Data.HighMark:
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						refreshLayout.setRefreshing(false);
						if (list.size() <= 0) {
							refreshPrompt.setVisibility(View.VISIBLE);
						}
					}
				}, 700);
				Util.dismissProgressDialog();
				break;
			}
		}
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//�ر�ע�⣬����ָ���ڶ���������  ��Ϊ��� Fragment �ᱻ��ӵ� ViewPager ���档
		view = inflater.inflate(R.layout.fragment_recommend, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		Util.showProgressDialog(getActivity(), "���ڼ�������...");
		info = Util.getNetworkInfo(getActivity());
		if (info != null && info.isAvailable()) {
			getData(Data.Recommend,"1",CLASS_COUNT+"");
		}else {
			Util.showToast(getActivity(), "������");
			refreshPrompt.setVisibility(View.VISIBLE);
			Util.dismissProgressDialog();
		}
	}
	
	private void initView() {
		list = new ArrayList<String>();
		listView = (ListView) view.findViewById(R.id.id_listview_recommend);
		refreshPrompt = (TextView) view.findViewById(R.id.id_textview_refresh_prompt);
		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_swiperefreshlayout_refresh);
		refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
		adapter = new RecommendAdapter();
		listView.setAdapter(adapter);
		
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				refreshPrompt.setVisibility(View.GONE);
				info = Util.getNetworkInfo(getActivity());
				if (info != null && info.isAvailable()) {
					getData(Data.Recommend,"1",CLASS_COUNT+"");
				}else {
					if (list.size() == 0) {
						refreshPrompt.setVisibility(View.VISIBLE);
					}
					Util.showToast(getActivity(), "������");
					refreshLayout.setRefreshing(false);
				}
			}
		});
	}
	private void getData(final String want, final String startLine, final String endLine) {
		if (info == null || !info.isAvailable() ) {
			refreshLayout.setRefreshing(false);
			Util.showToast(getActivity(), "������");
			return;
		}
		HttpUtil.visitMovieInfoTable(want, startLine, endLine, "", "", new HttpCallBackListenerString() {
			
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
					msg.what = Loading_Data_Success;
					msg.obj = new Object[]{want,array,startLine,endLine};
					handler.sendMessage(msg);
				}else {
					msg.obj = new Object[]{want,data,startLine,endLine};
					msg.what = Loading_Data_Fail;
					handler.sendMessage(msg);
				}
			}
			
			public void onError(String e) {
				Message msg = handler.obtainMessage();
				msg.obj = new Object[]{want,e,startLine,endLine};
				msg.what = Loading_Data_Fail;
				handler.sendMessage(msg);
			}
		});
	}
	
	class RecommendAdapter extends BaseAdapter implements OnClickListener {
		Context mContext;
		LayoutInflater inflater;
		final int TYPE_1 = 0;
		final int TYPE_2 = 1;
		
		public RecommendAdapter() {
			mContext = getActivity();
			inflater = LayoutInflater.from(mContext);
		}

		public int getCount() {
			return list.size();
		}
		
		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			int i = position%(int)(Math.ceil((float)CLASS_COUNT/(float)LINE_COUNT)+1);
			if (i == TYPE_1) {
				return TYPE_1;
			}else {
				return TYPE_2;
			}
		}
		
		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolderTitle holderTitle = null;
			ViewHolderContent holderContent = null;
			int i = getItemViewType(position);
			String data = list.get(position);
			
			if (convertView == null) {
				switch (i) {
				case TYPE_1:
					convertView = inflater.inflate(R.layout.fragment_recommend_item_title, null);
					holderTitle = new ViewHolderTitle();
					holderTitle.name = (TextView) convertView.findViewById(R.id.id_textview_name);
					holderTitle.more = (TextView) convertView.findViewById(R.id.id_textview_more);
					convertView.setTag(holderTitle);
					break;
				case TYPE_2:
					convertView = inflater.inflate(R.layout.fragment_recommend_item_content, null);
					holderContent = new ViewHolderContent();
					holderContent.img01 = (ImageView) convertView.findViewById(R.id.id_imageview_img01);
					holderContent.text01 = (TextView) convertView.findViewById(R.id.id_textview_text01);
					
					holderContent.img02 = (ImageView) convertView.findViewById(R.id.id_imageview_img02);
					holderContent.text02 = (TextView) convertView.findViewById(R.id.id_textview_text02);
					
					holderContent.img03 = (ImageView) convertView.findViewById(R.id.id_imageview_img03);
					holderContent.text03 = (TextView) convertView.findViewById(R.id.id_textview_text03);
					convertView.setTag(holderContent);
					break;
				default:
					break;
				}
			}else {
				switch (i) {
				case TYPE_1:
					holderTitle = (ViewHolderTitle) convertView.getTag();
					break;
				case TYPE_2:
					holderContent = (ViewHolderContent) convertView.getTag();
					break;
				default:
					break;
				}
			}
			switch (i) {
			case TYPE_1:
				holderTitle.name.setText(data);
				holderTitle.more.setText("����>>");
				
				holderTitle.more.setTag(R.id.Click_Position, position);
				holderTitle.more.setTag(R.id.Click_Type, i);
				holderTitle.more.setOnClickListener(this);
				break;
			case TYPE_2:
				try {
					JSONArray array = new JSONArray(data);
					//��ȡͼƬ�е�--���ڵ�
					LinearLayout layout = (LinearLayout) holderContent.img01.getParent().getParent();
					for (int j = 0; j < LINE_COUNT; j++) {
						LinearLayout l = (LinearLayout) layout.getChildAt(j);
						l.setVisibility(View.INVISIBLE);
					}
					for (int j = 0; j < array.length(); j++) {
						JSONObject object = array.getJSONObject(j);
						LinearLayout l = (LinearLayout) layout.getChildAt(j);
						
						l.setTag(R.id.Click_Position, position);
						l.setTag(R.id.Click_Type, i);
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
				break;
			default:
				break;
			}
			return convertView;
		}

		@Override
		public void onClick(View v) {
			int position = (int) v.getTag(R.id.Click_Position);
			int type = (int) v.getTag(R.id.Click_Type);
			
			if (type == TYPE_1) {
				switch (list.get(position)) {
				case Recommend_M:
					MoreMovieActivity.actionStart(getActivity(), Data.Recommend);
					break;
				case Hot_M:
					MoreMovieActivity.actionStart(getActivity(), Data.Hot);
					break;
				case Newest_M:
					MoreMovieActivity.actionStart(getActivity(), Data.Newest);
					break;
				case HighMark_M:
					MoreMovieActivity.actionStart(getActivity(), Data.HighMark);
					break;
				default:
					break;
				}
			}else if (type == TYPE_2) {
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
					MovieDetailActivity.actionStart(getActivity(), object.getString("movieInfoTableId"), object.getString("movieName"), object.getString("moviePicLink"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		public void setImage(final ImageView image, String picLink) {
			info = Util.getNetworkInfo(getActivity());
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
	class ViewHolderTitle {
		TextView name;
		TextView more;
	}
	class ViewHolderContent {
		ImageView img01;
		TextView text01;
		
		ImageView img02;
		TextView text02;
		
		ImageView img03;
		TextView text03;
	}
}

package com.guohao.graduationdesign_app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.guohao.custom.PlayButton;
import com.guohao.util.Data;
import com.guohao.util.HttpUtil;
import com.guohao.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MovieDetailActivity extends Activity {
	private String id;
	private String name;
	private String picLink;
	private ImageLoader loader;
	private Handler handler;
	private int picWidth;

	private ImageView moviePic;
	private TextView movieName, movieType, movieRegion, movieLanguage, movieReleaseUpdate, movieTime, movieDBGrade,
			movieOtherName, movieDirector, movieActor, movieIntroduce;

	private LinearLayout introduce;
	private LinearLayout layoutTV, layout1024P, layout640P, layout480P;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movie_detail_activity);
		initData();
		initView();
		showBaseData();
		getNetworkData();
	}

	private void showBaseData() {
		movieName.setText(name);
		LayoutParams params = moviePic.getLayoutParams();
		params.width = picWidth;
		params.height = (int) (picWidth / 3.0 * 4.0);

		int i = Util.getPreferences(MovieDetailActivity.this).getInt(Data.K_Pic_Loading, Data.V_Pic_Loading_Big);
		if (i == Data.V_Pic_Loading_Big) {
			picLink = Util.getBigImageAddress(picLink);
		}
		loader.displayImage(picLink, moviePic);

		LayoutParams params2 = introduce.getLayoutParams();
		params2.height = params.height;
		Log.d("guohao", "¹þ¹þ¹þ£º" + params2 + "---" + params2.height + "---" + params.height + "---"
				+ introduce.getHeight() + "---" + moviePic.getHeight());
	}

	private void getNetworkData() {
		NetworkInfo info = Util.getNetworkInfo(MovieDetailActivity.this);
		if (info == null || !info.isAvailable() ) {
			Util.showToast(MovieDetailActivity.this, "ÎÞÍøÂç");
			return;
		}
		Util.showProgressDialog(MovieDetailActivity.this);
		HttpUtil.getMovieDetail(handler, id);
	}

	private void initView() {
		loader = ImageLoader.getInstance();
		moviePic = (ImageView) findViewById(R.id.id_imageview_movie_pic);

		layoutTV = (LinearLayout) findViewById(R.id.id_linearlayout_tv);
		layout1024P = (LinearLayout) findViewById(R.id.id_linearlayout_1024p);
		layout640P = (LinearLayout) findViewById(R.id.id_linearlayout_640p);
		layout480P = (LinearLayout) findViewById(R.id.id_linearlayout_480p);

		introduce = (LinearLayout) findViewById(R.id.id_linearlayout_introduce);

		movieName = (TextView) findViewById(R.id.id_textview_movie_name);
		movieType = (TextView) findViewById(R.id.id_textview_movie_type);
		movieRegion = (TextView) findViewById(R.id.id_textview_movie_region);
		movieLanguage = (TextView) findViewById(R.id.id_textview_movie_language);
		movieReleaseUpdate = (TextView) findViewById(R.id.id_textview_movie_release_date);
		movieTime = (TextView) findViewById(R.id.id_textview_movie_time);
		movieDBGrade = (TextView) findViewById(R.id.id_textview_movie_db_grade);
		movieOtherName = (TextView) findViewById(R.id.id_textview_movie_other_name);
		movieDirector = (TextView) findViewById(R.id.id_textview_movie_director);
		movieActor = (TextView) findViewById(R.id.id_textview_movie_actor);
		movieIntroduce = (TextView) findViewById(R.id.id_textview_movie_introduce);

		handler = new Handler() {
			public void handleMessage(final android.os.Message msg) {
				switch (msg.what) {
				case HttpUtil.VISIT_MOVIE_INFO_DETAIL_OK:
					setOtherData((String) msg.obj);
					break;
				case HttpUtil.VISIT_MOVIE_INFO_DETAIL_ERROR:
					Util.showToast(MovieDetailActivity.this, (String) msg.obj);
					break;
				}
				Util.dismissProgressDialog();
			}
		};
	}

	protected void setOtherData(String jsonObject) {
		JSONObject object = null;
		String code = null;
		String data = null;
		try {
			object = new JSONObject(jsonObject);
			code = object.getString(Data.KEY_CODE);
			data = object.getString(Data.KEY_DATA);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (code.equals(Data.VALUE_OK)) {
			try {
				JSONObject obj = new JSONArray(data).getJSONObject(0);
				movieType.setText(obj.getString("movieType"));
				movieRegion.setText(obj.getString("movieRegion"));
				movieLanguage.setText(obj.getString("movieLanguage"));
				movieReleaseUpdate.setText(obj.getString("movieReleaseDate"));
				movieTime.setText(obj.getString("movieTime"));
				movieDBGrade.setText(obj.getString("movieDBGrade"));
				movieOtherName.setText(obj.getString("movieOtherName"));
				movieDirector.setText(obj.getString("movieDirector"));
				movieActor.setText(obj.getString("movieActor"));
				movieIntroduce.setText(obj.getString("movieIntroduce"));

				// ÏÔÊ¾²¥·ÅµØÖ·
				displayPlayTextView(Data.MOVIE_TV,obj);
				displayPlayTextView(Data.MOVIE_1024P,obj);
				displayPlayTextView(Data.MOVIE_640P,obj);
				displayPlayTextView(Data.MOVIE_480P,obj);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Util.showToast(MovieDetailActivity.this, data);
		}
	}

	private void displayPlayTextView(String flag, JSONObject obj) {
		JSONArray array = null;
		try {
			array = obj.getJSONArray(flag);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (array.length() > 0) {
			for (int i = 0; i < array.length(); i++) {
				PlayButton t = new PlayButton(MovieDetailActivity.this);
				t.setTextColor(Color.parseColor("#999999"));
				t.setPadding(0, 25, 0, 25);
				t.setGravity(Gravity.CENTER);
				t.setBackgroundResource(R.drawable.play_btn_bg);
				
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 10, 0, 10);
				t.setLayoutParams(params);
				
				String movieId = null;
				String thisMovieName = null;
				try {
					JSONObject object = array.getJSONObject(i);
					thisMovieName = object.getString("movieName");
					t.setText(thisMovieName);
					movieId = object.getString("movieId");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String url = Data.URL_MOVIE_PLAY_URL;
				url += "?tableName="+flag+"&movieId="+movieId;
				t.setClickRequest(thisMovieName,url);
				switch (flag) {
				case Data.MOVIE_TV:
					layoutTV.setVisibility(View.VISIBLE);
					((LinearLayout)layoutTV.getChildAt(1)).addView(t);
					break;
				case Data.MOVIE_1024P:
					layout1024P.setVisibility(View.VISIBLE);
					((LinearLayout)layout1024P.getChildAt(1)).addView(t);		
					break;
				case Data.MOVIE_640P:
					layout640P.setVisibility(View.VISIBLE);
					((LinearLayout)layout640P.getChildAt(1)).addView(t);
					break;
				case Data.MOVIE_480P:
					layout480P.setVisibility(View.VISIBLE);
					((LinearLayout)layout480P.getChildAt(1)).addView(t);
					break;
				default:
					break;
				}
			}
		}
	}

	private void initData() {
		Intent intent = getIntent();
		id = intent.getStringExtra("movieId");
		name = intent.getStringExtra("movieName");
		picLink = intent.getStringExtra("moviePicLink");
		picWidth = Util.getPreferences(MovieDetailActivity.this).getInt(Data.K_IMAGE_WIDTH, 200);
	}

	public static void actionStart(Context c, String movieId, String movieName, String moviePicLink) {
		Intent intent = new Intent(c, MovieDetailActivity.class);
		intent.putExtra("movieId", movieId);
		intent.putExtra("movieName", movieName);
		intent.putExtra("moviePicLink", moviePicLink);
		c.startActivity(intent);
	}
}

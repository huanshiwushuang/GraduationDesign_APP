package com.guohao.graduationdesign_app;


import java.util.ArrayList;
import java.util.List;

import com.guohao.fragment.AllFragment;
import com.guohao.fragment.MyFragment;
import com.guohao.fragment.RecommendFragment;
import com.guohao.util.Data;
import com.guohao.util.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ciaapp.b.b;

public class MainActivity extends FragmentActivity implements OnClickListener {
	private ViewPager viewPager;
	//选项卡 个数
	private int TAB_COUNT = 3;
	//3 个 Fragment 的对象
	private Fragment recommendFragment,allFragment,myFragment;
	//3 个 Tab 栏的布局对象。
	private LinearLayout recommendLayout,allLayout,myLayout;
	
	//存储 ImageView 的 list
	private List<ImageView> imageViews;
	//存储 TextView 的 list
	private List<TextView> textViews;
	
	//存储图片 id 的 数组。
	private int[] beforeId;
	private int[] afterId;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		initConfig();
		initView();
		setViewPager();
	}

	private void initConfig() {
		SharedPreferences p = Util.getPreferences(MainActivity.this);
		Editor editor = p.edit();
		//加载图片的大小
		editor.putInt(Data.K_Pic_Loading, p.getInt(Data.K_Pic_Loading, Data.V_Pic_Loading_Big));
		
		//更新进入时间
		editor.putString(Data.K_Time_Last_Into, System.currentTimeMillis()+"");
		
		editor.commit();
	}

	private void initView() {
		//ViewPager 
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setOffscreenPageLimit(TAB_COUNT);
		//图片的 id
		beforeId = new int[]{R.drawable.pic_tab_recommend,R.drawable.pic_tab_all,R.drawable.pic_tab_my};
		afterId = new int[]{R.drawable.pic_tab_recommend_2,R.drawable.pic_tab_all_2,R.drawable.pic_tab_my_2};
		
		//Tab 栏的布局。
		recommendLayout = (LinearLayout) findViewById(R.id.id_linearlayout_recommend);
		allLayout = (LinearLayout) findViewById(R.id.id_linearlayout_all);
		myLayout = (LinearLayout) findViewById(R.id.id_linearlayout_my);
		recommendLayout.setOnClickListener(this);
		allLayout.setOnClickListener(this);
		myLayout.setOnClickListener(this);
		
		//存储  Tab 栏的图片和文字
		imageViews = new ArrayList<ImageView>();
		textViews = new ArrayList<TextView>();
		
		//将 imageview 对象添加到 list 里面
		imageViews.add((ImageView) recommendLayout.getChildAt(0));
		imageViews.add((ImageView) allLayout.getChildAt(0));
		imageViews.add((ImageView) myLayout.getChildAt(0));
		//将 textview 对象添加到 list 里面
		textViews.add((TextView) recommendLayout.getChildAt(1));
		textViews.add((TextView) allLayout.getChildAt(1));
		textViews.add((TextView) myLayout.getChildAt(1));
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_linearlayout_recommend:
			viewPager.setCurrentItem(0, false);
			break;
		case R.id.id_linearlayout_all:
			viewPager.setCurrentItem(1, false);
			break;
		case R.id.id_linearlayout_my:
			viewPager.setCurrentItem(2, false);
			break;
		}
	}
	
	
	
	//设置 ViewPager 适配器，监听页面改变事件
	private void setViewPager() {
		viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
			
			@Override
			public int getCount() {
				return TAB_COUNT;
			}
			
			@Override
			public Fragment getItem(int index) {
				switch (index) {
				case 0:
					if (recommendFragment == null) {
						recommendFragment = new RecommendFragment();
					}
					return recommendFragment;
				case 1:
					if (allFragment == null) {
						allFragment = new AllFragment();
					}
					return allFragment;
				case 2:
					if (myFragment == null) {
						myFragment = new MyFragment();
					}
					return myFragment;
				}
				return null;
			}
		});
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			public void onPageSelected(int index) {
				//循环遍历，将 Tab 所有的图片回复到 灰色 状态
				for (int i = 0; i < imageViews.size(); i++) {
					imageViews.get(i).setImageResource(beforeId[i]);
				}
				//循环遍历，将 Tab 所有的文字回复到 灰色 状态
				for (int i = 0; i < textViews.size(); i++) {
					textViews.get(i).setTextColor(Color.parseColor("#c9bda6"));
				}
				//设置对应的 Tab 栏为选中状态
				imageViews.get(index).setImageResource(afterId[index]);
				textViews.get(index).setTextColor(Color.parseColor("#087f78"));
			}
			
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}
	
	public static void actionStart(Context c) {
		Intent intent = new Intent(c, MainActivity.class);
		c.startActivity(intent);
	}
	
	private static Toast t = null;
	private static long lastTime = 0;
	private static long thisTime = 0;
	@Override
	public void onBackPressed() {
		thisTime = System.currentTimeMillis();
		if (t == null) {
			t = Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT);
			t.show();
		}else {
			thisTime = System.currentTimeMillis();
			if (lastTime != 0 && (thisTime - lastTime) > 2*1000) {
				t = Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT);
				t.show();
			}else {
				finish();
			}
		}
		lastTime = thisTime;
	}
}

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
	//ѡ� ����
	private int TAB_COUNT = 3;
	//3 �� Fragment �Ķ���
	private Fragment recommendFragment,allFragment,myFragment;
	//3 �� Tab ���Ĳ��ֶ���
	private LinearLayout recommendLayout,allLayout,myLayout;
	
	//�洢 ImageView �� list
	private List<ImageView> imageViews;
	//�洢 TextView �� list
	private List<TextView> textViews;
	
	//�洢ͼƬ id �� ���顣
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
		//����ͼƬ�Ĵ�С
		editor.putInt(Data.K_Pic_Loading, p.getInt(Data.K_Pic_Loading, Data.V_Pic_Loading_Big));
		
		//���½���ʱ��
		editor.putString(Data.K_Time_Last_Into, System.currentTimeMillis()+"");
		
		editor.commit();
	}

	private void initView() {
		//ViewPager 
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setOffscreenPageLimit(TAB_COUNT);
		//ͼƬ�� id
		beforeId = new int[]{R.drawable.pic_tab_recommend,R.drawable.pic_tab_all,R.drawable.pic_tab_my};
		afterId = new int[]{R.drawable.pic_tab_recommend_2,R.drawable.pic_tab_all_2,R.drawable.pic_tab_my_2};
		
		//Tab ���Ĳ��֡�
		recommendLayout = (LinearLayout) findViewById(R.id.id_linearlayout_recommend);
		allLayout = (LinearLayout) findViewById(R.id.id_linearlayout_all);
		myLayout = (LinearLayout) findViewById(R.id.id_linearlayout_my);
		recommendLayout.setOnClickListener(this);
		allLayout.setOnClickListener(this);
		myLayout.setOnClickListener(this);
		
		//�洢  Tab ����ͼƬ������
		imageViews = new ArrayList<ImageView>();
		textViews = new ArrayList<TextView>();
		
		//�� imageview ������ӵ� list ����
		imageViews.add((ImageView) recommendLayout.getChildAt(0));
		imageViews.add((ImageView) allLayout.getChildAt(0));
		imageViews.add((ImageView) myLayout.getChildAt(0));
		//�� textview ������ӵ� list ����
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
	
	
	
	//���� ViewPager ������������ҳ��ı��¼�
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
				//ѭ���������� Tab ���е�ͼƬ�ظ��� ��ɫ ״̬
				for (int i = 0; i < imageViews.size(); i++) {
					imageViews.get(i).setImageResource(beforeId[i]);
				}
				//ѭ���������� Tab ���е����ֻظ��� ��ɫ ״̬
				for (int i = 0; i < textViews.size(); i++) {
					textViews.get(i).setTextColor(Color.parseColor("#c9bda6"));
				}
				//���ö�Ӧ�� Tab ��Ϊѡ��״̬
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
			t = Toast.makeText(MainActivity.this, "�ٰ�һ���˳�", Toast.LENGTH_SHORT);
			t.show();
		}else {
			thisTime = System.currentTimeMillis();
			if (lastTime != 0 && (thisTime - lastTime) > 2*1000) {
				t = Toast.makeText(MainActivity.this, "�ٰ�һ���˳�", Toast.LENGTH_SHORT);
				t.show();
			}else {
				finish();
			}
		}
		lastTime = thisTime;
	}
}

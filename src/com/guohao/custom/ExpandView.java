package com.guohao.custom;

import com.guohao.graduationdesign_app.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;

public class ExpandView extends FrameLayout {
	private Context mContext;
	private View view;
	private Boolean isExpand = true;
	private Animation expandAnim,collapseAnim;

	public ExpandView(Context context) {
		this(context,null);
	}
	public ExpandView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		initExpandView();
	}
	private void initExpandView() {
		int childCount = getChildCount();
		if (childCount > 0) {
			view = getChildAt(0);
			expandAnim = AnimationUtils.loadAnimation(mContext, R.anim.expand);
			expandAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
					
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					view.setVisibility(View.VISIBLE);
				}
			});
			collapseAnim = AnimationUtils.loadAnimation(mContext, R.anim.collapse);
			collapseAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
					
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					view.setVisibility(View.GONE);
				}
			});
			
		}
	}
	public void collapse() {
        if (isExpand) {
        	isExpand = false;
            view.clearAnimation();
            view.startAnimation(collapseAnim);
        }
    }
    public void expand() {
        if (!isExpand) {
        	isExpand = true;
        	view.clearAnimation();
            view.startAnimation(expandAnim);
        }
    }
    public boolean isExpand() {
        return isExpand;
    }
}

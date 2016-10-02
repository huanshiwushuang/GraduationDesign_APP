package com.guohao.custom;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * �̳���SwipeRefreshLayout,�Ӷ�ʵ�ֻ������ײ�ʱ�������ظ���Ĺ���.
 * 
 * @author mrsimple
 */
public class LoadMore extends LinearLayout implements OnScrollListener {
    private ListView mListView;
    private OnLoadListener mOnLoadListener;
    private int mTouchSlop;
    
    private int mDownY;
    private int mMoveY;
    private boolean isLoading = false;
    private View listviewFooter;

    public LoadMore(Context context) {
        this(context, null);
    }
    public LoadMore(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // ��ʼ��ListView����
        if (mListView == null) {
            getListView();
        }
    }
    private void getListView() {
    	ViewGroup group = this; 
    	while (true) {
        	int childs = group.getChildCount();
        	if (childs > 0) {
        		View childView = group.getChildAt(0);
				if (childView instanceof ListView) {
                    mListView = (ListView) childView;
                    // ���ù�����������ListView, ʹ�ù����������Ҳ�����Զ�����
                    mListView.setOnScrollListener(this);
                    break;
                }
				group = (ViewGroup) childView;
			}else {
				break;
			}
		}
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // ����
                mDownY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // �ƶ�
                mMoveY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                // ̧��
                if (canLoad()) {
                    loadData();
                }
                break;
        }

        return super.dispatchTouchEvent(event);
    }
    private boolean canLoad() {
        return isBottom() && !isLoading && isPullUp();
    }
    private boolean isBottom() {
        if (mListView != null && mListView.getAdapter() != null) {
            return mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
        }
        return false;
    }
    private boolean isPullUp() {
        return (mDownY - mMoveY) >= mTouchSlop;
    }
    private void loadData() {
        if (mOnLoadListener != null) {
            // ����״̬
            setLoading(true);
            mOnLoadListener.onLoad();
        }
    }
    public void setLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
        	listviewFooter.setVisibility(View.VISIBLE);
        }else {
        	listviewFooter.setVisibility(View.GONE);
        	mDownY = 0;
            mMoveY = 0;
		}
    }
    public void setOnLoadListener(OnLoadListener loadListener, View mListViewFooter) {
        mOnLoadListener = loadListener;
        listviewFooter = mListViewFooter;
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        // ����ʱ������ײ�Ҳ���Լ��ظ���
        if (canLoad()) {
            loadData();
        }
    }
    public static interface OnLoadListener {
        public void onLoad();
    }
}
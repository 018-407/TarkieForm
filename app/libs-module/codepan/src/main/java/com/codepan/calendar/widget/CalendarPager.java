package com.codepan.calendar.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.codepan.R;
import com.codepan.calendar.adapter.CalendarAdapter;

public class CalendarPager extends ViewPager {

	private final int numCol = 7;
	private final int numRow = 6;
	private boolean isSet;

	public CalendarPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(!isSet) {
			int spacing = getResources().getDimensionPixelOffset(R.dimen.one);
			View child = getChildAt(0);
			if(child instanceof GridView) {
				GridView gvCalendarGrid = (GridView) child;
				CalendarAdapter adapter = (CalendarAdapter) gvCalendarGrid.getAdapter();
				int width = (adapter.getParent().getMeasuredWidth() + spacing) * numCol;
				int height = (adapter.getParent().getMeasuredHeight() + spacing) * numRow;
				getLayoutParams().width = width - spacing;
				getLayoutParams().height = height - spacing;
			}
			isSet = true;
		}
	}
}

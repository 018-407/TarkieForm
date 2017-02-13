package com.codepan.calendar.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.codepan.R;
import com.codepan.calendar.adapter.CalendarAdapter;
import com.codepan.calendar.callback.Interface.OnPickDateCallback;
import com.codepan.calendar.callback.Interface.OnSelectDateCallback;
import com.codepan.calendar.object.DayObj;

import java.util.ArrayList;

public class CalendarItem extends Fragment {

	private OnSelectDateCallback selectDateCallback;
	private OnPickDateCallback pickDateCallback;
	private ArrayList<DayObj> dayList;
	private GridView gvCalendarGrid;
	private CalendarAdapter adapter;
	private int lastPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.calendar_item, container, false);
		gvCalendarGrid = (GridView) view.findViewById(R.id.gvCalendarGrid);
		gvCalendarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				int index = getLastSelected();
				dayList.get(index).isSelect = false;
				dayList.get(position).isSelect = true;
				adapter.notifyDataSetChanged();
				gvCalendarGrid.invalidate();
				lastPosition = position;
				if(pickDateCallback != null) {
					pickDateCallback.onPickDate(dayList.get(position).date);
				}
				if(selectDateCallback != null) {
					selectDateCallback.onSelectDate(dayList.get(position).date);
				}
			}
		});
		adapter = new CalendarAdapter(getActivity(), dayList);
		gvCalendarGrid.setAdapter(adapter);
		return view;
	}

	public void init(ArrayList<DayObj> dayList, OnPickDateCallback pickDateCallback,
					 OnSelectDateCallback selectDateCallback) {
		this.dayList = dayList;
		this.pickDateCallback = pickDateCallback;
		this.selectDateCallback = selectDateCallback;
	}

	public int getLastSelected() {
		for(DayObj obj: dayList) {
			if(obj.isSelect) {
				return dayList.indexOf(obj);
			}
		}
		return 0;
	}

	public void setSelected(String date) {
		for(DayObj obj: dayList) {
			obj.isSelect = obj.date.equals(date);
		}
		adapter.notifyDataSetChanged();
		gvCalendarGrid.invalidate();
	}
}

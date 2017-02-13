package com.codepan.calendar.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.codepan.R;
import com.codepan.calendar.adapter.ViewPagerAdapter;
import com.codepan.calendar.callback.Interface.OnSelectDateCallback;
import com.codepan.calendar.object.DayObj;
import com.codepan.calendar.object.MonthObj;
import com.codepan.calendar.widget.CalendarPager;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.codepan.calendar.callback.Interface.OnPickDateCallback;

public class CalendarView extends Fragment implements OnPickDateCallback, OnSelectDateCallback {

	private final int PREVIOUS = 0;
	private final int CURRENT = 1;
	private final int NEXT = 2;
	private int lastPosition = CURRENT;

	private CodePanLabel tvMonthYearCalendar, tvYearCalendar, tvDateCalendar;
	private CodePanButton btnCancelCalendar, btnSaveCalendar;
	private Button btnPreviousCalendar, btnNextCalendar;
	private OnPickDateCallback pickDateCallback;
	private OnFragmentCallback fragmentCallback;
	private ArrayList<Fragment> fragmentList;
	private String date, selectedDate;
	private ViewPagerAdapter adapter;
	private CalendarPager vpCalendar;
	private CalendarItem previous;
	private CalendarItem current;
	private CalendarItem next;
	private Calendar cal;

	@Override
	public void onStart() {
		super.onStart();
		setOnBackStack(true);
	}

	@Override
	public void onStop() {
		super.onStop();
		setOnBackStack(false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cal = Calendar.getInstance();
		if(date != null) {
			cal = CodePanUtils.getCalendar(date);
			selectedDate = date;
		}
		else {
			selectedDate = CodePanUtils.getDate();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.calendar_view_layout, container, false);
		tvMonthYearCalendar = (CodePanLabel) view.findViewById(R.id.tvMonthYearCalendar);
		tvYearCalendar = (CodePanLabel) view.findViewById(R.id.tvYearCalendar);
		tvDateCalendar = (CodePanLabel) view.findViewById(R.id.tvDateCalendar);
		btnCancelCalendar = (CodePanButton) view.findViewById(R.id.btnCancelCalendar);
		btnSaveCalendar = (CodePanButton) view.findViewById(R.id.btnSaveCalendar);
		btnPreviousCalendar = (Button) view.findViewById(R.id.btnPreviousCalendar);
		btnNextCalendar = (Button) view.findViewById(R.id.btnNextCalendar);
		vpCalendar = (CalendarPager) view.findViewById(R.id.vpCalendar);
		btnPreviousCalendar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vpCalendar.setCurrentItem(PREVIOUS, true);
			}
		});
		btnNextCalendar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vpCalendar.setCurrentItem(NEXT, true);
			}
		});
		btnSaveCalendar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().getSupportFragmentManager().popBackStack();
				if(pickDateCallback != null) {
					pickDateCallback.onPickDate(selectedDate);
				}
			}
		});
		btnCancelCalendar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});
		fragmentList = getFragmentList(lastPosition);
		adapter = new ViewPagerAdapter(getChildFragmentManager(), fragmentList);
		vpCalendar.setAdapter(adapter);
		vpCalendar.setCurrentItem(CURRENT, false);
		tvMonthYearCalendar.setText(getMonthYear());
		vpCalendar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				lastPosition = position;
				switch(position) {
					case PREVIOUS:
						cal.set(getYear(), getMonth() - 1, getDay());
						tvMonthYearCalendar.setText(getMonthYear());
						break;
					case NEXT:
						cal.set(getYear(), getMonth() + 1, getDay());
						tvMonthYearCalendar.setText(getMonthYear());
						break;
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if(state == ViewPager.SCROLL_STATE_IDLE) {
					switchItem(lastPosition);
				}
			}
		});
		displayDate(selectedDate);
		return view;
	}

	public void switchItem(final int position) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				fragmentList = getFragmentList(position);
				handler.sendMessage(handler.obtainMessage());
			}
		});
		bg.start();
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			adapter = new ViewPagerAdapter(getChildFragmentManager(), fragmentList);
			vpCalendar.setAdapter(adapter);
			vpCalendar.setCurrentItem(CURRENT, false);
			return true;
		}
	});

	public ArrayList<Fragment> getFragmentList(final int position) {
		ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
		switch(position) {
			case PREVIOUS:
				next = current;
				current = previous;
				previous = getPrevious();
				break;
			case CURRENT:
				previous = getPrevious();
				current = getCurrent();
				next = getNext();
				break;
			case NEXT:
				previous = current;
				current = next;
				next = getNext();
				break;
		}
		fragmentList.add(previous);
		fragmentList.add(current);
		fragmentList.add(next);
		return fragmentList;
	}

	public CalendarItem getPrevious() {
		Calendar pCal = Calendar.getInstance();
		pCal.set(getYear(), getMonth() - 1, getDay());
		ArrayList<DayObj> pDayList = plotCalendar(pCal);
		CalendarItem previous = new CalendarItem();
		previous.init(pDayList, this, this);
		return previous;
	}

	public CalendarItem getCurrent() {
		Calendar cCal = Calendar.getInstance();
		cCal.set(getYear(), getMonth(), getDay());
		ArrayList<DayObj> cDayList = plotCalendar(cCal);
		CalendarItem current = new CalendarItem();
		current.init(cDayList, this, this);
		return current;
	}

	public CalendarItem getNext() {
		Calendar nCal = Calendar.getInstance();
		nCal.set(getYear(), getMonth() + 1, getDay());
		ArrayList<DayObj> nDayList = plotCalendar(nCal);
		CalendarItem next = new CalendarItem();
		next.init(nDayList, this, this);
		return next;
	}


	public ArrayList<DayObj> plotCalendar(Calendar cal) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		MonthObj previous = getMonthDetails(year, month - 1, dayOfMonth);
		MonthObj current = getMonthDetails(year, month, dayOfMonth);
		int prevExcess = current.firstDayOfMonth - 1;
		prevExcess = prevExcess == 0 ? 7 : prevExcess;
		int firstElement = previous.noOfDays - prevExcess;
		int limit = previous.noOfDays;
		int day = 0;
		int reset = 0;
		ArrayList<DayObj> dayList = new ArrayList<DayObj>();
		for(int x = 1; x <= 42; x++) {
			DayObj obj = new DayObj();
			if(x == 1) {
				day = firstElement;
			}
			else {
				if(day == limit) {
					day = 0;
					limit = current.noOfDays;
					reset++;
				}
			}
			day++;
			obj.day = day;
			int m = 0;
			switch(reset) {
				case 0:
					if(month == 0) {
						month = 12;
						year -= 1;
					}
					m = month;
					obj.isActive = false;
					break;
				case 1:
					if(month == 12) {
						month = 0;
						year += 1;
					}
					m = month + 1;
					obj.isActive = true;
					break;
				case 2:
					if(month == 11) {
						month = -1;
						year += 1;
					}
					m = month + 2;
					obj.isActive = false;
					break;
			}
			obj.date = year + "-" + String.format(Locale.ENGLISH, "%02d", m) + "-" +
					String.format(Locale.ENGLISH, "%02d", day);
			if(obj.date.equals(selectedDate)) {
				obj.isSelect = true;
			}
			dayList.add(obj);
		}
		return dayList;
	}

	public String getMonthYear() {
		String strMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
		return strMonth + " " + cal.get(Calendar.YEAR);
	}

	public int getMonth() {
		return cal.get(Calendar.MONTH);
	}

	public int getYear() {
		return cal.get(Calendar.YEAR);
	}

	public int getDay() {
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public MonthObj getMonthDetails(int year, int month, int day) {
		MonthObj monthObj = new MonthObj();
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		cal.set(year, month, 1);
		monthObj.firstDayOfMonth = cal.get(Calendar.DAY_OF_WEEK);
		monthObj.noOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		return monthObj;
	}

	public void setOnPickDateCallback(OnPickDateCallback pickDateCallback) {
		this.pickDateCallback = pickDateCallback;
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	/**
	 * @param date (yyyy-mm-dd)
	 */
	public void setCurrentDate(String date) {
		this.date = date;
	}

	public String getDate() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return year + "-" + String.format(Locale.ENGLISH, "%02d", (month + 1)) + "-" +
				String.format(Locale.ENGLISH, "%02d", day);
	}


	@Override
	public void onPickDate(String date) {
		this.date = date;
	}

	public void setOnBackStack(boolean isOnBackStack) {
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}

	@Override
	public void onSelectDate(String date) {
		((CalendarItem) fragmentList.get(PREVIOUS)).setSelected(date);
		((CalendarItem) fragmentList.get(NEXT)).setSelected(date);
		this.selectedDate = date;
		displayDate(date);
	}

	public void displayDate(String date) {
		String year = CodePanUtils.getDisplayYear(date);
		String today = CodePanUtils.getDisplayDate(date);
		tvYearCalendar.setText(year);
		tvDateCalendar.setText(today);
	}
}

package com.mobileoptima.tarkieform;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.codepan.calendar.callback.Interface.OnPickDateCallback;
import com.codepan.calendar.view.CalendarView;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.constant.FieldType;
import com.mobileoptima.core.Data;
import com.mobileoptima.object.FieldObj;

import java.util.ArrayList;

public class PageFragment extends Fragment {

	private FragmentTransaction transaction;
	private ArrayList<FieldObj> fieldList;
	private ViewGroup container;
	private LinearLayout llPage;
	private SQLiteAdapter db;
	private int page = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = ((MainActivity) getActivity()).getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page_layout, container, false);
		llPage = (LinearLayout) view.findViewById(R.id.llPage);
		this.container = container;
		loadFields(db);
		return view;
	}

	public void loadFields(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					fieldList = Data.loadFields(db, page);
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View view = null;
			for(FieldObj obj : fieldList) {
				switch(obj.type) {
					case FieldType.SEC:
						view = inflater.inflate(R.layout.field_section_layout, container, false);
						CodePanLabel tvTitleSection = (CodePanLabel) view.findViewById(R.id.tvTitleSection);
						CodePanLabel tvDescSection = (CodePanLabel) view.findViewById(R.id.tvDescSection);
						tvTitleSection.setText(obj.name);
						if(obj.dDesc != null) {
							tvDescSection.setText(obj.dDesc);
							tvDescSection.setVisibility(View.VISIBLE);
						}
						else {
							tvDescSection.setVisibility(View.GONE);
						}
						break;
					case FieldType.TEXT:
						view = inflater.inflate(R.layout.field_text_layout, container, false);
						CodePanLabel tvQuestionText = (CodePanLabel) view.findViewById(R.id.tvQuestionText);
						CodePanTextField etAnswerText = (CodePanTextField) view.findViewById(R.id.etAnswerText);
						if(obj.isRequired) {
							requiredField(tvQuestionText, obj.name);
						}
						else {
							tvQuestionText.setText(obj.name);
						}
						break;
					case FieldType.NUM:
						view = inflater.inflate(R.layout.field_numeric_layout, container, false);
						CodePanLabel tvQuestionNumeric = (CodePanLabel) view.findViewById(R.id.tvQuestionNumeric);
						CodePanTextField etAnswerNumeric = (CodePanTextField) view.findViewById(R.id.etAnswerNumeric);
						if(obj.isRequired) {
							requiredField(tvQuestionNumeric, obj.name);
						}
						else {
							tvQuestionNumeric.setText(obj.name);
						}
						break;
					case FieldType.LTEXT:
						view = inflater.inflate(R.layout.field_long_text_layout, container, false);
						CodePanLabel tvQuestionLText = (CodePanLabel) view.findViewById(R.id.tvQuestionLText);
						CodePanTextField etAnswerLText = (CodePanTextField) view.findViewById(R.id.etAnswerLText);
						if(obj.isRequired) {
							requiredField(tvQuestionLText, obj.name);
						}
						else {
							tvQuestionLText.setText(obj.name);
						}
						break;
					case FieldType.DATE:
						view = inflater.inflate(R.layout.field_date_layout, container, false);
						CodePanLabel tvQuestionDate = (CodePanLabel) view.findViewById(R.id.tvQuestionDate);
						final CodePanButton btnCalendarDate = (CodePanButton) view.findViewById(R.id.btnCalendarDate);
						if(obj.isRequired) {
							requiredField(tvQuestionDate, obj.name);
						}
						else {
							tvQuestionDate.setText(obj.name);
						}
						btnCalendarDate.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								CalendarView calendar = new CalendarView();
								calendar.setOnPickDateCallback(new OnPickDateCallback() {
									@Override
									public void onPickDate(String date) {
										String selected = CodePanUtils.getCalendarDate(date, false, true);
										btnCalendarDate.setText(selected);
									}
								});
								transaction = getActivity().getSupportFragmentManager().beginTransaction();
								transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
										R.anim.fade_in, R.anim.fade_out);
								transaction.add(R.id.rlMain, calendar);
								transaction.addToBackStack(null);
								transaction.commit();
							}
						});
						break;
				}
				llPage.addView(view);
			}
			return true;
		}
	});

	public void setPage(int page) {
		this.page = page;
	}

	public void requiredField(CodePanLabel label, String text) {
		if(text != null) {
			int length = text.length();
			String name = text + "*";
			ArrayList<SpannableMap> list = new ArrayList<>();
			list.add(new SpannableMap(length, length + 1, Color.RED));
			SpannableStringBuilder ssb = CodePanUtils.customizeText(list, name);
			label.setText(ssb);
		}
	}
}

package com.mobileoptima.tarkieform;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.codepan.calendar.callback.Interface.OnPickDateCallback;
import com.codepan.calendar.view.CalendarView;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface.OnGpsFixedCallback;
import com.mobileoptima.callback.Interface.OnOptionSelectedCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnSignCallback;
import com.mobileoptima.constant.FieldType;
import com.mobileoptima.core.Data;
import com.mobileoptima.object.ChoiceObj;
import com.mobileoptima.object.FieldObj;

import java.util.ArrayList;

public class PageFragment extends Fragment implements OnFragmentCallback {

	private OnOverrideCallback overrideCallback;
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
			for(final FieldObj obj : fieldList) {
				switch(obj.type) {
					case FieldType.SEC:
						view = inflater.inflate(R.layout.field_section_layout, container, false);
						CodePanLabel tvTitleSec = (CodePanLabel) view.findViewById(R.id.tvTitleSec);
						CodePanLabel tvDescSec = (CodePanLabel) view.findViewById(R.id.tvDescSec);
						tvTitleSec.setText(obj.field);
						if(obj.description != null) {
							tvDescSec.setText(obj.description);
							tvDescSec.setVisibility(View.VISIBLE);
						}
						else {
							tvDescSec.setVisibility(View.GONE);
						}
						break;
					case FieldType.TEXT:
						view = inflater.inflate(R.layout.field_text_layout, container, false);
						CodePanLabel tvQuestionText = (CodePanLabel) view.findViewById(R.id.tvQuestionText);
						CodePanTextField etAnswerText = (CodePanTextField) view.findViewById(R.id.etAnswerText);
						if(obj.isRequired) {
							requiredField(tvQuestionText, obj.field);
						}
						else {
							tvQuestionText.setText(obj.field);
						}
						break;
					case FieldType.NUM:
						view = inflater.inflate(R.layout.field_numeric_layout, container, false);
						CodePanLabel tvQuestionNum = (CodePanLabel) view.findViewById(R.id.tvQuestionNum);
						CodePanTextField etAnswerNum = (CodePanTextField) view.findViewById(R.id.etAnswerNum);
						if(obj.isRequired) {
							requiredField(tvQuestionNum, obj.field);
						}
						else {
							tvQuestionNum.setText(obj.field);
						}
						break;
					case FieldType.LTEXT:
						view = inflater.inflate(R.layout.field_long_text_layout, container, false);
						CodePanLabel tvQuestionLText = (CodePanLabel) view.findViewById(R.id.tvQuestionLText);
						CodePanTextField etAnswerLText = (CodePanTextField) view.findViewById(R.id.etAnswerLText);
						if(obj.isRequired) {
							requiredField(tvQuestionLText, obj.field);
						}
						else {
							tvQuestionLText.setText(obj.field);
						}
						break;
					case FieldType.DATE:
						view = inflater.inflate(R.layout.field_date_layout, container, false);
						CodePanLabel tvQuestionDate = (CodePanLabel) view.findViewById(R.id.tvQuestionDate);
						final CodePanButton btnCalendarDate = (CodePanButton) view.findViewById(R.id.btnCalendarDate);
						if(obj.isRequired) {
							requiredField(tvQuestionDate, obj.field);
						}
						else {
							tvQuestionDate.setText(obj.field);
						}
						btnCalendarDate.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								CalendarView calendar = new CalendarView();
								calendar.setOnFragmentCallback(PageFragment.this);
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
					case FieldType.DD:
						view = inflater.inflate(R.layout.field_dropdown_layout, container, false);
						CodePanLabel tvQuestionDd = (CodePanLabel) view.findViewById(R.id.tvQuestionDd);
						final CodePanButton btnOptionsDd = (CodePanButton) view.findViewById(R.id.btnOptionsDd);
						if(obj.isRequired) {
							requiredField(tvQuestionDd, obj.field);
						}
						else {
							tvQuestionDd.setText(obj.field);
						}
						btnOptionsDd.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								ArrayList<ChoiceObj> items = Data.loadChoices(db);
								OptionsFragment options = new OptionsFragment();
								options.setItems(items, obj.field);
								options.setOnFragmentCallback(PageFragment.this);
								options.setOnOptionSelectedCallback(new OnOptionSelectedCallback() {
									@Override
									public void onOptionSelected(ChoiceObj obj) {
										btnOptionsDd.setText(obj.dDesc);
									}
								});
								transaction = getActivity().getSupportFragmentManager().beginTransaction();
								transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
										R.anim.fade_in, R.anim.fade_out);
								transaction.add(R.id.rlMain, options);
								transaction.addToBackStack(null);
								transaction.commit();
							}
						});
						break;
					case FieldType.YON:
						view = inflater.inflate(R.layout.field_yes_no_layout, container, false);
						CodePanLabel tvQuestionYon = (CodePanLabel) view.findViewById(R.id.tvQuestionYon);
						final CodePanButton btnNoYon = (CodePanButton) view.findViewById(R.id.btnNoYon);
						final CodePanButton btnYesYon = (CodePanButton) view.findViewById(R.id.btnYesYon);
						if(obj.isRequired) {
							requiredField(tvQuestionYon, obj.field);
						}
						else {
							tvQuestionYon.setText(obj.field);
						}
						btnNoYon.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								btnNoYon.setEnabled(false);
								btnYesYon.setEnabled(true);
							}
						});
						btnYesYon.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								btnNoYon.setEnabled(true);
								btnYesYon.setEnabled(false);
							}
						});
						break;
					case FieldType.MS:
						view = inflater.inflate(R.layout.field_multiple_selection_layout, container, false);
						CodePanLabel tvQuestionMs = (CodePanLabel) view.findViewById(R.id.tvQuestionMs);
						LinearLayout llChoicesMs = (LinearLayout) view.findViewById(R.id.llChoicesMs);
						if(obj.isRequired) {
							requiredField(tvQuestionMs, obj.field);
						}
						else {
							tvQuestionMs.setText(obj.field);
						}
						ArrayList<ChoiceObj> choices = Data.loadChoices(db);
						for(ChoiceObj choice : choices) {
							View v = inflater.inflate(R.layout.multiple_selection_item, container, false);
							LinearLayout llChoiceMs = (LinearLayout) v.findViewById(R.id.llChoiceMs);
							CodePanLabel tvChoiceMs = (CodePanLabel) v.findViewById(R.id.tvChoiceMs);
							final CheckBox cbChoiceMs = (CheckBox) v.findViewById(R.id.cbChoiceMs);
							tvChoiceMs.setText(choice.dDesc);
							llChoiceMs.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View view) {
									if(!cbChoiceMs.isChecked()) {
										cbChoiceMs.setChecked(true);
									}
									else {
										cbChoiceMs.setChecked(false);
									}
								}
							});
							llChoicesMs.addView(v);
						}
						break;
					case FieldType.LAB:
						view = inflater.inflate(R.layout.field_label_layout, container, false);
						CodePanLabel tvDescLabel = (CodePanLabel) view.findViewById(R.id.tvDescLabel);
						tvDescLabel.setText(obj.field);
						break;
					case FieldType.LINK:
						view = inflater.inflate(R.layout.field_link_layout, container, false);
						CodePanLabel tvQuestionLink = (CodePanLabel) view.findViewById(R.id.tvQuestionLink);
						CodePanButton btnUrlLink = (CodePanButton) view.findViewById(R.id.btnUrlLink);
						btnUrlLink.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri.parse(obj.url));
								startActivity(intent);
							}
						});
						tvQuestionLink.setText(obj.field);
						break;
					case FieldType.GPS:
						view = inflater.inflate(R.layout.field_gps_layout, container, false);
						CodePanLabel tvQuestionGps = (CodePanLabel) view.findViewById(R.id.tvQuestionGps);
						CodePanButton btnGetGps = (CodePanButton) view.findViewById(R.id.btnGetGps);
						if(obj.isRequired) {
							requiredField(tvQuestionGps, obj.field);
						}
						else {
							tvQuestionGps.setText(obj.field);
						}
						btnGetGps.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								if(!CodePanUtils.isGpsEnabled(getActivity())) {
									final AlertDialogFragment alert = new AlertDialogFragment();
									alert.setDialogTitle("Location Services");
									alert.setDialogMessage("Please enable GPS satellites and Google's location service to continue.");
									alert.setOnFragmentCallback(PageFragment.this);
									alert.setPositiveButton("Ok", new OnClickListener() {
										@Override
										public void onClick(View view) {
											alert.getDialogActivity().getSupportFragmentManager().popBackStack();
											Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
											startActivity(intent);
										}
									});
									alert.setNegativeButton("Cancel", new OnClickListener() {

										@Override
										public void onClick(View v) {
											alert.getDialogActivity().getSupportFragmentManager().popBackStack();
										}
									});
									transaction = getActivity().getSupportFragmentManager().beginTransaction();
									transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
											R.anim.fade_in, R.anim.fade_out);
									transaction.add(R.id.rlMain, alert);
									transaction.addToBackStack(null);
									transaction.commit();
								}
								else {
									SearchGpsFragment search = new SearchGpsFragment();
									search.setOnGpsFixedCallback(new OnGpsFixedCallback() {
										@Override
										public void onGpsFixed(double latitude, double longitude) {
										}
									});
									transaction = getActivity().getSupportFragmentManager().beginTransaction();
									transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
											R.anim.fade_in, R.anim.fade_out);
									transaction.add(R.id.rlMain, search);
									transaction.addToBackStack(null);
									transaction.commit();
								}
							}
						});
						break;
					case FieldType.SIG:
						view = inflater.inflate(R.layout.field_signature_layout, container, false);
						CodePanLabel tvQuestionSignature = (CodePanLabel) view.findViewById(R.id.tvQuestionSignature);
						CodePanButton btnAddSignature = (CodePanButton) view.findViewById(R.id.btnAddSignature);
						if(obj.isRequired) {
							requiredField(tvQuestionSignature, obj.field);
						}
						else {
							tvQuestionSignature.setText(obj.field);
						}
						btnAddSignature.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								SignatureFragment signature = new SignatureFragment();
								signature.setTitle(obj.field);
								signature.setOnFragmentCallback(PageFragment.this);
								signature.setOnSignCallback(new OnSignCallback() {
									@Override
									public void onSign(String fileName) {
									}
								});
								transaction = getActivity().getSupportFragmentManager().beginTransaction();
								transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
										R.anim.fade_in, R.anim.fade_out);
								transaction.add(R.id.rlMain, signature);
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


	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	@Override
	public void onFragment(boolean status) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(!status);
		}
	}
}

package com.mobileoptima.tarkieform;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
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
import com.mobileoptima.callback.Interface.OnCameraDoneCallback;
import com.mobileoptima.callback.Interface.OnDeletePhotoCallback;
import com.mobileoptima.callback.Interface.OnGpsFixedCallback;
import com.mobileoptima.callback.Interface.OnOptionSelectedCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.callback.Interface.OnSignCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.FieldType;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.core.Data;
import com.mobileoptima.object.ChoiceObj;
import com.mobileoptima.object.FieldObj;
import com.mobileoptima.object.FormObj;
import com.mobileoptima.object.GpsObj;
import com.mobileoptima.object.ImageObj;
import com.mobileoptima.object.PageObj;

import java.util.ArrayList;

public class PageFragment extends Fragment implements OnFragmentCallback {

	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private ArrayList<FieldObj> fieldList;
	private FragmentManager manager;
	private ViewGroup container;
	private LinearLayout llPage;
	private SQLiteAdapter db;
	private FormObj form;
	private PageObj page;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		manager = getActivity().getSupportFragmentManager();
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
					fieldList = Data.loadFields(db, form.ID, page);
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	public void setForm(FormObj form) {
		this.form = form;
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
						tvTitleSec.setText(obj.name);
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
							requiredField(tvQuestionText, obj.name);
						}
						else {
							tvQuestionText.setText(obj.name);
						}
						break;
					case FieldType.NUM:
						view = inflater.inflate(R.layout.field_numeric_layout, container, false);
						CodePanLabel tvQuestionNum = (CodePanLabel) view.findViewById(R.id.tvQuestionNum);
						CodePanTextField etAnswerNum = (CodePanTextField) view.findViewById(R.id.etAnswerNum);
						if(obj.isRequired) {
							requiredField(tvQuestionNum, obj.name);
						}
						else {
							tvQuestionNum.setText(obj.name);
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
								transaction = manager.beginTransaction();
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
							requiredField(tvQuestionDd, obj.name);
						}
						else {
							tvQuestionDd.setText(obj.name);
						}
						btnOptionsDd.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								ArrayList<ChoiceObj> items = Data.loadChoices(db, obj.ID);
								OptionsFragment options = new OptionsFragment();
								options.setItems(items, obj.name);
								options.setOnFragmentCallback(PageFragment.this);
								options.setOnOptionSelectedCallback(new OnOptionSelectedCallback() {
									@Override
									public void onOptionSelected(ChoiceObj obj) {
										btnOptionsDd.setText(obj.name);
									}
								});
								transaction = manager.beginTransaction();
								transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
										R.anim.fade_in, R.anim.fade_out);
								transaction.add(R.id.rlMain, options);
								transaction.addToBackStack(null);
								transaction.commit();
							}
						});
						break;
					case FieldType.YON:
					case FieldType.CB:
						view = inflater.inflate(R.layout.field_yes_no_layout, container, false);
						CodePanLabel tvQuestionYon = (CodePanLabel) view.findViewById(R.id.tvQuestionYon);
						final CodePanButton btnNoYon = (CodePanButton) view.findViewById(R.id.btnNoYon);
						final CodePanButton btnYesYon = (CodePanButton) view.findViewById(R.id.btnYesYon);
						if(obj.isRequired) {
							requiredField(tvQuestionYon, obj.name);
						}
						else {
							tvQuestionYon.setText(obj.name);
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
							requiredField(tvQuestionMs, obj.name);
						}
						else {
							tvQuestionMs.setText(obj.name);
						}
						ArrayList<ChoiceObj> choices = Data.loadChoices(db, obj.ID);
						for(ChoiceObj choice : choices) {
							View v = inflater.inflate(R.layout.multiple_selection_item, container, false);
							LinearLayout llChoiceMs = (LinearLayout) v.findViewById(R.id.llChoiceMs);
							CodePanLabel tvChoiceMs = (CodePanLabel) v.findViewById(R.id.tvChoiceMs);
							final CheckBox cbChoiceMs = (CheckBox) v.findViewById(R.id.cbChoiceMs);
							tvChoiceMs.setText(choice.name);
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
						tvDescLabel.setText(obj.name);
						break;
					case FieldType.LINK:
						view = inflater.inflate(R.layout.field_link_layout, container, false);
						CodePanLabel tvQuestionLink = (CodePanLabel) view.findViewById(R.id.tvQuestionLink);
						CodePanButton btnUrlLink = (CodePanButton) view.findViewById(R.id.btnUrlLink);
						btnUrlLink.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri.parse(obj.description));
								startActivity(intent);
							}
						});
						tvQuestionLink.setText(obj.name);
						break;
					case FieldType.GPS:
						view = inflater.inflate(R.layout.field_gps_layout, container, false);
						final CodePanLabel tvStatusGps = (CodePanLabel) view.findViewById(R.id.tvStatusGps);
						final CodePanLabel tvLatitudeGps = (CodePanLabel) view.findViewById(R.id.tvLatitudeGps);
						final CodePanLabel tvLongitudeGps = (CodePanLabel) view.findViewById(R.id.tvLongitudeGps);
						CodePanLabel tvQuestionGps = (CodePanLabel) view.findViewById(R.id.tvQuestionGps);
						CodePanButton btnGetGps = (CodePanButton) view.findViewById(R.id.btnGetGps);
						if(obj.isRequired) {
							requiredField(tvQuestionGps, obj.name);
						}
						else {
							tvQuestionGps.setText(obj.name);
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
									transaction = manager.beginTransaction();
									transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
											R.anim.fade_in, R.anim.fade_out);
									transaction.add(R.id.rlMain, alert);
									transaction.addToBackStack(null);
									transaction.commit();
								}
								else {
									if(CodePanUtils.withGooglePlayServices(getActivity())) {
										SearchGpsFragment search = new SearchGpsFragment();
										search.setOnFragmentCallback(PageFragment.this);
										search.setOnGpsFixedCallback(new OnGpsFixedCallback() {
											@Override
											public void onGpsFixed(GpsObj gps) {
												String status = "Location Acquired";
												String latitude = "Latitude: " + gps.latitude;
												String longitude = "Longitude: " + gps.longitude;
												tvStatusGps.setText(status);
												tvLatitudeGps.setText(latitude);
												tvLongitudeGps.setText(longitude);
												tvLatitudeGps.setVisibility(View.VISIBLE);
												tvLongitudeGps.setVisibility(View.VISIBLE);
											}
										});
										transaction = manager.beginTransaction();
										transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
												R.anim.fade_in, R.anim.fade_out);
										transaction.add(R.id.rlMain, search);
										transaction.addToBackStack(null);
										transaction.commit();
									}
								}
							}
						});
						break;
					case FieldType.SIG:
						view = inflater.inflate(R.layout.field_signature_layout, container, false);
						CodePanLabel tvQuestionSignature = (CodePanLabel) view.findViewById(R.id.tvQuestionSignature);
						CodePanButton btnAddSignature = (CodePanButton) view.findViewById(R.id.btnAddSignature);
						if(obj.isRequired) {
							requiredField(tvQuestionSignature, obj.name);
						}
						else {
							tvQuestionSignature.setText(obj.name);
						}
						btnAddSignature.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								SignatureFragment signature = new SignatureFragment();
								signature.setTitle(obj.name);
								signature.setOnFragmentCallback(PageFragment.this);
								signature.setOnSignCallback(new OnSignCallback() {
									@Override
									public void onSign(String fileName) {
									}
								});
								transaction = manager.beginTransaction();
								transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
										R.anim.fade_in, R.anim.fade_out);
								transaction.add(R.id.rlMain, signature);
								transaction.addToBackStack(null);
								transaction.commit();
							}
						});
						break;
					case FieldType.TIME:
						view = inflater.inflate(R.layout.field_time_layout, container, false);
						CodePanLabel tvQuestionTime = (CodePanLabel) view.findViewById(R.id.tvQuestionTime);
						CodePanButton btnGetTime = (CodePanButton) view.findViewById(R.id.btnGetTime);
						final CodePanLabel tvResultTime = (CodePanLabel) view.findViewById(R.id.tvResultTime);
						if(obj.isRequired) {
							requiredField(tvQuestionTime, obj.name);
						}
						else {
							tvQuestionTime.setText(obj.name);
						}
						btnGetTime.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								String time = CodePanUtils.getTime();
								String date = CodePanUtils.getDate();
								String result = "Time: " + date + " " + time;
								tvResultTime.setText(result);
								tvResultTime.setVisibility(View.VISIBLE);
							}
						});
						break;
					case FieldType.PHOTO:
						view = inflater.inflate(R.layout.field_photo_layout, container, false);
						CodePanLabel tvQuestionPhoto = (CodePanLabel) view.findViewById(R.id.tvQuestionPhoto);
						CodePanButton btnAddPhoto = (CodePanButton) view.findViewById(R.id.btnAddPhoto);
						final LinearLayout llGridPhoto = (LinearLayout) view.findViewById(R.id.llGridPhoto);
						if(obj.isRequired) {
							requiredField(tvQuestionPhoto, obj.name);
						}
						else {
							tvQuestionPhoto.setText(obj.name);
						}
						btnAddPhoto.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								FormFragment form = (FormFragment) manager.findFragmentByTag(Tag.FORM);
								CameraFragment camera = new CameraFragment();
								camera.setOnBackPressedCallback(form.getOnBackPressedCallback());
								camera.setOnOverrideCallback(overrideCallback);
								camera.setOnFragmentCallback(PageFragment.this);
								camera.setOnCameraDoneCallback(new OnCameraDoneCallback() {
									@Override
									public void onCameraDone(ArrayList<ImageObj> imageList) {
										updatePhotoGrid(llGridPhoto, imageList);
									}
								});
								transaction = manager.beginTransaction();
								transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
										R.anim.slide_in_ltr, R.anim.slide_out_ltr);
								transaction.add(R.id.rlMain, camera);
								transaction.hide(form);
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

	public void setPage(PageObj page) {
		this.page = page;
	}

	public void updatePhotoGrid(final LinearLayout llGridPhoto, final ArrayList<ImageObj> imageList) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		llGridPhoto.removeAllViews();
		for(ImageObj obj : imageList) {
			View view = inflater.inflate(R.layout.photo_item, container, false);
			CodePanButton btnPhoto = (CodePanButton) view.findViewById(R.id.btnPhoto);
			ImageView ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
			int size = CodePanUtils.getWidth(view);
			Bitmap bitmap = CodePanUtils.getBitmapThumbnails(getActivity(), App.FOLDER, obj.fileName, size);
			ivPhoto.setImageBitmap(bitmap);
			final int position = imageList.indexOf(obj);
			imageList.get(position).bitmap = bitmap;
			btnPhoto.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onPhotoGridItemClick(llGridPhoto, imageList, position);
				}
			});
			llGridPhoto.addView(view);
		}
		ViewParent parent = llGridPhoto.getParent();
		final HorizontalScrollView hsvPhoto = (HorizontalScrollView) parent.getParent();
		hsvPhoto.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View view, int l, int t, int r, int b, int ol,
									   int ot, int or, int ob) {
				hsvPhoto.removeOnLayoutChangeListener(this);
				hsvPhoto.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			}
		});
	}

	public void onPhotoGridItemClick(final LinearLayout llGridPhoto, final ArrayList<ImageObj> imageList, int position) {
		FormFragment form = (FormFragment) manager.findFragmentByTag(Tag.FORM);
		ImagePreviewFragment imagePreview = new ImagePreviewFragment();
		imagePreview.setImageList(imageList, position);
		imagePreview.setOnDeletePhotoCallback(new OnDeletePhotoCallback() {
			@Override
			public void onDeletePhoto(int position) {
				imageList.remove(position);
				llGridPhoto.removeViewAt(position);
				if(imageList.size() > 0) {
					invalidateViews(llGridPhoto, imageList);
				}
			}
		});
		imagePreview.setOnFragmentCallback(PageFragment.this);
		transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		transaction.add(R.id.rlMain, imagePreview);
		transaction.hide(form);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void invalidateViews(final LinearLayout llGridPhoto, final ArrayList<ImageObj> imageList) {
		int count = llGridPhoto.getChildCount();
		for(int i = 0; i < count; i++) {
			final int position = i;
			View view = llGridPhoto.getChildAt(i);
			CodePanButton btnPhoto = (CodePanButton) view.findViewById(R.id.btnPhoto);
			btnPhoto.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onPhotoGridItemClick(llGridPhoto, imageList, position);
				}
			});
		}
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

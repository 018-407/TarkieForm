package com.mobileoptima.tarkieform;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.Tab;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.object.EntryObj;
import com.mobileoptima.object.FieldObj;
import com.mobileoptima.object.FormObj;
import com.mobileoptima.object.PageObj;

import java.util.ArrayList;

public class FormFragment extends Fragment implements OnClickListener, OnBackPressedCallback,
		OnFragmentCallback {

	private CodePanButton btnNextForm, btnBackForm, btnSaveForm, btnCancelForm,
			btnDeleteForm, btnOptionsForm;
	private LinearLayout llPageForm, llDeleteForm;
	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private RelativeLayout rlOptionsForm;
	private ArrayList<PageObj> pageList;
	private FragmentManager manager;
	private CodePanLabel tvForm;
	private ViewGroup container;
	private SQLiteAdapter db;
	private EntryObj entry;
	private FormObj form;
	private PageObj page;
	private int index;

	@Override
	public void onStart() {
		super.onStart();
		if(overrideCallback != null) {
			overrideCallback.onOverride(true);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if(overrideCallback != null) {
			overrideCallback.onOverride(false);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((MainActivity) getActivity()).setOnBackPressedCallback(this);
		manager = getActivity().getSupportFragmentManager();
		db = ((MainActivity) getActivity()).getDatabase();
		db.openConnection();
		pageList = Data.loadPages(db, form.ID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.form_layout, container, false);
		rlOptionsForm = (RelativeLayout) view.findViewById(R.id.rlOptionsForm);
		btnCancelForm = (CodePanButton) view.findViewById(R.id.btnCancelForm);
		btnDeleteForm = (CodePanButton) view.findViewById(R.id.btnDeleteForm);
		btnOptionsForm = (CodePanButton) view.findViewById(R.id.btnOptionsForm);
		btnNextForm = (CodePanButton) view.findViewById(R.id.btnNextForm);
		btnBackForm = (CodePanButton) view.findViewById(R.id.btnBackForm);
		btnSaveForm = (CodePanButton) view.findViewById(R.id.btnSaveForm);
		llDeleteForm = (LinearLayout) view.findViewById(R.id.llDeleteForm);
		llPageForm = (LinearLayout) view.findViewById(R.id.llPageForm);
		tvForm = (CodePanLabel) view.findViewById(R.id.tvNameForm);
		btnNextForm.setOnClickListener(this);
		btnBackForm.setOnClickListener(this);
		btnSaveForm.setOnClickListener(this);
		btnCancelForm.setOnClickListener(this);
		btnDeleteForm.setOnClickListener(this);
		btnOptionsForm.setOnClickListener(this);
		rlOptionsForm.setOnClickListener(this);
		tvForm.setText(form.name);
		if(!pageList.isEmpty()) {
			this.page = pageList.get(index);
			PageFragment first = new PageFragment();
			first.setPage(page);
			first.setForm(form);
			first.setEntry(entry);
			first.setOnOverrideCallback(overrideCallback);
			transaction = manager.beginTransaction();
			transaction.setCustomAnimations(0, R.anim.slide_out_rtl,
					R.anim.slide_in_ltr, R.anim.slide_out_ltr);
			transaction.add(R.id.flForm, first, page.tag);
			transaction.commit();
			if(pageList.size() == 1) {
				btnNextForm.setText(R.string.finish);
			}
		}
		if(entry == null) {
			llDeleteForm.setVisibility(View.GONE);
		}
		this.container = container;
		setProgress();
		return view;
	}

	public void setForm(FormObj form) {
		this.form = form;
	}

	public void setEntry(EntryObj entry) {
		this.form = entry.form;
		this.entry = entry;
	}

	public void setProgress() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		for(PageObj obj : pageList) {
			View view = inflater.inflate(R.layout.progress_item, container, false);
			View vProgress = view.findViewById(R.id.vProgress);
			if(pageList.indexOf(obj) <= index) {
				vProgress.setEnabled(true);
			}
			else {
				vProgress.setEnabled(false);
			}
			llPageForm.addView(view);
		}
	}

	public void updateProgress() {
		int count = llPageForm.getChildCount();
		if(count > 0) {
			for(int i = 0; i < count; i++) {
				View view = llPageForm.getChildAt(i);
				if(view != null) {
					View vProgress = view.findViewById(R.id.vProgress);
					if(i <= index) {
						vProgress.setEnabled(true);
					}
					else {
						vProgress.setEnabled(false);
					}
				}
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackForm:
				onBackPressed();
				break;
			case R.id.btnNextForm:
				PageFragment current = (PageFragment) manager.findFragmentByTag(page.tag);
				FieldObj field = current.getUnfilledUpField();
				if(field != null) {
					final AlertDialogFragment alert = new AlertDialogFragment();
					alert.setDialogTitle("Required Field");
					if(field.name != null && !field.name.isEmpty()) {
						String message = "\"" + field.name + "\" is required.";
						alert.setDialogMessage(message);
						String font = getActivity().getResources().getString(R.string.proxima_nova_bold);
						ArrayList<SpannableMap> list = new ArrayList<>();
						int length = field.name.length() + 2;
						list.add(new SpannableMap(getActivity(), font, 0, length));
						alert.setSpannableList(list);
					}
					alert.setOnFragmentCallback(this);
					alert.setPositiveButton("Ok", new OnClickListener() {
						@Override
						public void onClick(View view) {
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
					if(incrementPage()) {
						PageFragment next = new PageFragment();
						next.setPage(page);
						next.setForm(form);
						next.setEntry(entry);
						next.setOnOverrideCallback(overrideCallback);
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
								R.anim.slide_in_ltr, R.anim.slide_out_ltr);
						transaction.add(R.id.flForm, next, page.tag);
						transaction.hide(current);
						transaction.addToBackStack(null);
						transaction.commit();
					}
					else {
						final AlertDialogFragment alert = new AlertDialogFragment();
						alert.setDialogTitle("Submit Entry");
						alert.setDialogMessage("Do you want to finalize and submit this entry?");
						alert.setOnFragmentCallback(this);
						alert.setPositiveButton("Submit", new OnClickListener() {
							@Override
							public void onClick(View view) {
								alert.getDialogActivity().getSupportFragmentManager().popBackStack();
								saveEntry(true);
							}
						});
						alert.setNegativeButton("Save", new OnClickListener() {
							@Override
							public void onClick(View view) {
								alert.getDialogActivity().getSupportFragmentManager().popBackStack();
								saveEntry(false);
							}
						});
						transaction = manager.beginTransaction();
						transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
								R.anim.fade_in, R.anim.fade_out);
						transaction.add(R.id.rlMain, alert);
						transaction.addToBackStack(null);
						transaction.commit();
					}
				}
				break;
			case R.id.btnOptionsForm:
				if(rlOptionsForm.getVisibility() == View.GONE) {
					CodePanUtils.fadeIn(rlOptionsForm);
				}
				else {
					CodePanUtils.fadeOut(rlOptionsForm);
				}
				break;
			case R.id.btnSaveForm:
				rlOptionsForm.performClick();
				saveEntry(false);
				break;
			case R.id.btnCancelForm:
				rlOptionsForm.performClick();
				cancelEntry();
				break;
			case R.id.btnDeleteForm:
				rlOptionsForm.performClick();
				deleteEntry();
				break;
			case R.id.rlOptionsForm:
				if(rlOptionsForm.getVisibility() == View.VISIBLE) {
					CodePanUtils.fadeOut(rlOptionsForm);
				}
				break;
		}
	}

	public void saveEntry(boolean isSubmit) {
		ArrayList<FieldObj> fieldList = new ArrayList<>();
		for(PageObj obj : pageList) {
			if(pageList.indexOf(obj) <= index) {
				Fragment fragment = manager.findFragmentByTag(obj.tag);
				PageFragment page = (PageFragment) fragment;
				if(page != null) {
					fieldList.addAll(page.getFieldList());
				}
			}
		}
		boolean result = false;
		if(entry != null) {
			result = TarkieLib.updateEntry(db, entry.ID, fieldList, isSubmit);
			CodePanUtils.alertToast(getActivity(), "Entry has been has successfully updated.");
		}
		else {
			result = TarkieLib.saveEntry(db, form.ID, fieldList, isSubmit);
			CodePanUtils.alertToast(getActivity(), "Entry has been has successfully saved.");
		}
		if(result) {
			((MainActivity) getActivity()).reloadEntries();
			((MainActivity) getActivity()).reloadPhotos();
			((MainActivity) getActivity()).updateSyncCount();
			((MainActivity) getActivity()).switchTab(Tab.ENTRIES);
			manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	}

	public void cancelEntry() {
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle("Discard this entry?");
		alert.setDialogMessage("You will lose your work and you will have to start over.");
		alert.setOnFragmentCallback(this);
		alert.setPositiveButton("Yes", new OnClickListener() {
			@Override
			public void onClick(View view) {
				manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
		});
		alert.setNegativeButton("No", new OnClickListener() {
			@Override
			public void onClick(View view) {
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

	public void deleteEntry() {
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle("Delete this Entry?");
		alert.setDialogMessage("Are you sure you want to delete this entry?");
		alert.setOnFragmentCallback(this);
		alert.setPositiveButton("Yes", new OnClickListener() {
			@Override
			public void onClick(View view) {
				TarkieLib.deleteEntry(db, entry.ID);
				((MainActivity) getActivity()).reloadEntries();
				((MainActivity) getActivity()).reloadPhotos();
				((MainActivity) getActivity()).switchTab(Tab.ENTRIES);
				manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
		});
		alert.setNegativeButton("No", new OnClickListener() {
			@Override
			public void onClick(View view) {
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

	public boolean incrementPage() {
		if(index < pageList.size() - 1) {
			this.index++;
			this.page = pageList.get(index);
			if(index == pageList.size() - 1) {
				btnNextForm.setText(R.string.finish);
			}
			updateProgress();
			return true;
		}
		return false;
	}

	public void decrementPage() {
		if(index != 0) {
			this.index--;
			this.page = pageList.get(index);
			btnNextForm.setText(R.string.next);
			updateProgress();
		}
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	@Override
	public void onBackPressed() {
		PageFragment current = (PageFragment) manager.findFragmentByTag(page.tag);
		if(current.withChanges()) {
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setDialogTitle("Discard Changes?");
			alert.setDialogMessage("You will lose your work on this page.");
			alert.setOnFragmentCallback(this);
			alert.setPositiveButton("Yes", new OnClickListener() {
				@Override
				public void onClick(View view) {
					alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					decrementPage();
				}
			});
			alert.setNegativeButton("No", new OnClickListener() {
				@Override
				public void onClick(View view) {
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
			manager.popBackStack();
			decrementPage();
		}
	}

	public OnBackPressedCallback getOnBackPressedCallback() {
		return this;
	}

	@Override
	public void onFragment(boolean status) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(!status);
		}
	}
}

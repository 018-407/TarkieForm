package com.mobileoptima.tarkieform;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.core.Data;
import com.mobileoptima.object.FormObj;
import com.mobileoptima.object.PageObj;

import java.util.ArrayList;

public class FormFragment extends Fragment implements OnClickListener, OnBackPressedCallback {

	private CodePanButton btnNextForm, btnBackForm;
	private OnOverrideCallback overrideCallback;
	private FragmentTransaction transaction;
	private ArrayList<PageObj> pageList;
	private FragmentManager manager;
	private CodePanLabel tvForm;
	private SQLiteAdapter db;
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
		tvForm = (CodePanLabel) view.findViewById(R.id.tvForm);
		btnNextForm = (CodePanButton) view.findViewById(R.id.btnNextForm);
		btnBackForm = (CodePanButton) view.findViewById(R.id.btnBackForm);
		btnNextForm.setOnClickListener(this);
		btnBackForm.setOnClickListener(this);
		tvForm.setText(form.name);
		if(!pageList.isEmpty()) {
			this.page = pageList.get(index);
			PageFragment first = new PageFragment();
			first.setPage(page);
			first.setForm(form);
			first.setOnOverrideCallback(overrideCallback);
			transaction = manager.beginTransaction();
			transaction.setCustomAnimations(0, R.anim.slide_out_rtl,
					R.anim.slide_in_ltr, R.anim.slide_out_ltr);
			transaction.add(R.id.flForm, first, page.tag);
			transaction.commit();
		}
		return view;
	}

	public void setForm(FormObj form) {
		this.form = form;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBackForm:
				onBackPressed();
				break;
			case R.id.btnNextForm:
				Fragment current = manager.findFragmentByTag(page.tag);
				if(incrementPage()) {
					PageFragment next = new PageFragment();
					next.setPage(page);
					next.setForm(form);
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
					//Save Entries
				}
				break;
		}
	}

	public boolean incrementPage() {
		if(index < pageList.size() - 1) {
			this.index++;
			this.page = pageList.get(index);
			if(index == pageList.size() - 1) {
				String finish = "Finish";
				btnNextForm.setText(finish);
			}
			return true;
		}
		return false;
	}

	public void decrementPage() {
		if(index != 0) {
			this.index--;
			this.page = pageList.get(index);
			String next = "Next";
			btnNextForm.setText(next);
		}
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	@Override
	public void onBackPressed() {
		getActivity().getSupportFragmentManager().popBackStack();
		decrementPage();
	}

	public OnBackPressedCallback getOnBackPressedCallback() {
		return this;
	}
}

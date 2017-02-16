package com.mobileoptima.tarkieform;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.database.SQLiteAdapter;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.object.FormObj;

public class FormFragment extends Fragment {

	private FragmentTransaction transaction;
	private CodePanLabel tvForm;
	private SQLiteAdapter db;
	private FormObj form;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = ((MainActivity) getActivity()).getDatabase();
		db.openConnection();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.form_layout, container, false);
		tvForm = (CodePanLabel) view.findViewById(R.id.tvForm);
		tvForm.setText(form.dDesc);
		PageFragment page = new PageFragment();
		transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(0, R.anim.slide_out_rtl,
				R.anim.slide_in_ltr, R.anim.slide_out_ltr);
		transaction.replace(R.id.flForm, page);
		transaction.addToBackStack(null);
		transaction.commit();
		return view;
	}

	public void setForm(FormObj form) {
		this.form = form;
	}
}

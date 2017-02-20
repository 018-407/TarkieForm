package com.mobileoptima.tarkieform;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CustomTypefaceSpan;

import java.util.ArrayList;

public class AlertDialogFragment extends Fragment {

	private String dialogTitle, dialogMessage, positiveButtonTitle, negativeButtonTitle;
	private OnClickListener positiveButtonOnClick, negativeButtonOnClick;
	public CodePanButton btnPositiveAlertDialog, btnNegativeAlertDialog;
	private CodePanLabel tvMessageAlertDialog, tvTitleAlertDialog;
	private int positiveButtonVisibility = View.GONE;
	private int negativeButtonVisibility = View.GONE;
	private int dialogMessageVisbility = View.VISIBLE;
	private OnFragmentCallback fragmentCallback;
	private SpannableStringBuilder ssb;
	private boolean isOnBackStack = false;
	private boolean hasFont = false;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.alert_dialog_layout, container, false);
		btnPositiveAlertDialog = (CodePanButton) view.findViewById(R.id.btnPositiveAlertDialog);
		btnNegativeAlertDialog = (CodePanButton) view.findViewById(R.id.btnNegativeAlertDialog);
		tvTitleAlertDialog = (CodePanLabel) view.findViewById(R.id.tvTitleAlertDialog);
		tvMessageAlertDialog = (CodePanLabel) view.findViewById(R.id.tvMessageAlertDialog);
		if(hasFont) {
			tvMessageAlertDialog.setText(ssb);
		}
		else {
			tvMessageAlertDialog.setText(dialogMessage);
		}
		tvTitleAlertDialog.setText(dialogTitle);
		btnPositiveAlertDialog.setText(positiveButtonTitle);
		btnNegativeAlertDialog.setText(negativeButtonTitle);
		btnPositiveAlertDialog.setOnClickListener(positiveButtonOnClick);
		btnNegativeAlertDialog.setOnClickListener(negativeButtonOnClick);
		btnPositiveAlertDialog.setVisibility(positiveButtonVisibility);
		btnNegativeAlertDialog.setVisibility(negativeButtonVisibility);
		tvMessageAlertDialog.setVisibility(dialogMessageVisbility);
		return view;
	}

	public void setDialogMessage(String dialogMessage) {
		this.dialogMessage = dialogMessage;
	}

	public void setDialogTitle(String dialogTitle) {
		this.dialogTitle = dialogTitle;
	}

	public void hideDialogMessage() {
		this.dialogMessageVisbility = View.GONE;
	}

	public void setFont(ArrayList<SpannableMap> list, Typeface typeface) {
		ssb = new SpannableStringBuilder(dialogMessage);
		for(SpannableMap obj : list) {
			ssb.setSpan(new CustomTypefaceSpan(typeface), obj.start, obj.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		}
		hasFont = true;
	}

	public void setPositiveButton(String positiveButtonTitle, OnClickListener onClick) {
		this.positiveButtonVisibility = View.VISIBLE;
		this.positiveButtonTitle = positiveButtonTitle;
		this.positiveButtonOnClick = onClick;
	}

	public void setNegativeButton(String negativeButtonTitle, OnClickListener onClick) {
		this.negativeButtonVisibility = View.VISIBLE;
		this.negativeButtonTitle = negativeButtonTitle;
		this.negativeButtonOnClick = onClick;
	}

	public FragmentActivity getDialogActivity() {
		return getActivity();
	}

	public boolean isOnBackStack() {
		return this.isOnBackStack;
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	public void setOnBackStack(boolean isOnBackStack) {
		this.isOnBackStack = isOnBackStack;
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}
}

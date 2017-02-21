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
import com.codepan.utils.CodePanUtils;
import com.codepan.utils.SpannableMap;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.CustomTypefaceSpan;

import java.util.ArrayList;

public class AlertDialogFragment extends Fragment {

	private String dialogMessage, positiveButtonTitle, negativeButtonTitle;
	private OnClickListener positiveButtonOnClick, negativeButtonOnClick;
	public CodePanButton btnPositiveAlertDialog, btnNegativeAlertDialog;
	private OnFragmentCallback fragmentCallback;
	private CodePanLabel tvMessageAlertDialog;
	private int positiveButtonVisibility = View.GONE;
	private int negativeButtonVisibility = View.GONE;
	private int dialogMessageVisbility = View.VISIBLE;
	private SpannableStringBuilder ssb;
	private boolean isOnBackStack = false;
	private boolean isBold = false;
	private int statusBarColorActive;
	private int statusBarColorInActive;

	@Override
	public void onStart(){
		super.onStart();
		setOnBackStack(true);
	}

	@Override
	public void onStop(){
		super.onStop();
		setOnBackStack(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		View view = inflater.inflate(R.layout.alert_dialog_layout, container, false);

		btnPositiveAlertDialog = (CodePanButton) view.findViewById(R.id.btnPositiveAlertDialog);
		btnNegativeAlertDialog = (CodePanButton) view.findViewById(R.id.btnNegativeAlertDialog);
		tvMessageAlertDialog = (CodePanLabel) view.findViewById(R.id.tvMessageAlertDialog);

		if(isBold){
			tvMessageAlertDialog.setText(ssb);
		}
		else{
			tvMessageAlertDialog.setText(dialogMessage);
		}

		btnPositiveAlertDialog.setText(positiveButtonTitle);
		btnNegativeAlertDialog.setText(negativeButtonTitle);

		btnPositiveAlertDialog.setOnClickListener(positiveButtonOnClick);
		btnNegativeAlertDialog.setOnClickListener(negativeButtonOnClick);

		btnPositiveAlertDialog.setVisibility(positiveButtonVisibility);
		btnNegativeAlertDialog.setVisibility(negativeButtonVisibility);

		if(negativeButtonVisibility == View.GONE){
//			btnPositiveAlertDialog.setBackgroundResource(R.drawable.state_rect_blue_pri_rad_ten_bot);
		}

		tvMessageAlertDialog.setVisibility(dialogMessageVisbility);

		return view;

	}

	public void setDialogMessage(String dialogMessage){
		this.dialogMessage = dialogMessage;
	}

	public void hideDialogMessage(){
		this.dialogMessageVisbility = View.GONE;
	}

	public void setBold(ArrayList<SpannableMap> list, Typeface vagBold){

		ssb = new SpannableStringBuilder(dialogMessage);

		for(SpannableMap obj : list){
			ssb.setSpan(new CustomTypefaceSpan(vagBold), obj.start, obj.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		}

		isBold = true;
	}

	public void setPositiveButton(String positiveButtonTitle, OnClickListener onClick){
		this.positiveButtonVisibility = View.VISIBLE;
		this.positiveButtonTitle = positiveButtonTitle;
		this.positiveButtonOnClick = onClick;
	}

	public void setNegativeButton(String negativeButtonTitle, OnClickListener onClick){
		this.negativeButtonVisibility = View.VISIBLE;
		this.negativeButtonTitle = negativeButtonTitle;
		this.negativeButtonOnClick = onClick;
	}

	public FragmentActivity getDialogActivity(){
		return getActivity();
	}

	public boolean isOnBackStack(){
		return this.isOnBackStack;
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback){
		this.fragmentCallback = fragmentCallback;
	}

	public void setStatusBarColorActive(int color){
		this.statusBarColorActive = color;
	}

	public void setStatusBarColorInActive(int color){
		this.statusBarColorInActive = color;
	}

	public void setOnBackStack(boolean isOnBackStack){
		this.isOnBackStack = isOnBackStack;
		if(fragmentCallback != null){
			fragmentCallback.onFragment(isOnBackStack);
		}
		if(isOnBackStack){
			CodePanUtils.setStatusBarColor(getActivity(), statusBarColorActive);
		}
		else{
			CodePanUtils.setStatusBarColor(getActivity(), statusBarColorInActive);
		}
	}

}

package com.mobileoptima.tarkieform;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.codepan.utils.CodePanUtils;

public class AuthorizationFragment extends Fragment implements OnClickListener {
	private EditText etCodeAuthorization;
	private TextView btnAuthorization;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.authorization_layout, container, false);
		view.findViewById(R.id.rlMain).setOnClickListener(this);
		etCodeAuthorization = (EditText) view.findViewById(R.id.etCodeAuthorization);
		btnAuthorization = (TextView) view.findViewById(R.id.btnAuthorization);
		btnAuthorization.setOnClickListener(this);
		etCodeAuthorization.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					btnAuthorization.performClick();
				}
				return false;
			}
		});
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		CodePanUtils.hideKeyboard(v, getActivity());
		switch(v.getId()) {
			case R.id.btnAuthorization:
				String code = etCodeAuthorization.getText().toString().trim();
				getActivity().getSupportFragmentManager().popBackStack();
				LoginFragment login = new LoginFragment();
				FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
				transaction.add(R.id.rlMain, login);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
			default:
				break;
		}
	}
}
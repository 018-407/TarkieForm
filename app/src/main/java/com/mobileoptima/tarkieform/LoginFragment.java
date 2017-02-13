package com.mobileoptima.tarkieform;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class LoginFragment extends Fragment implements OnClickListener {
	private EditText etUsernameLogin, etPasswordLogin;
	private TextView btnLogin;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.login_layout, container, false);
		etUsernameLogin = (EditText) view.findViewById(R.id.etUsernameLogin);
		etPasswordLogin = (EditText) view.findViewById(R.id.etPasswordLogin);
		btnLogin = (TextView) view.findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(this);
		etPasswordLogin.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					btnLogin.performClick();
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
		switch(v.getId()) {
			case R.id.btnLogin:
				CodePanUtils.hideKeyboard(v, getActivity());
				String username = etUsernameLogin.getText().toString().trim();
				String password = etPasswordLogin.getText().toString().trim();
				break;
			default:
				break;
		}
	}
}
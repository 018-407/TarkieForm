package com.mobileoptima.tarkieform;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanTextField;

public class AuthorizationFragment extends Fragment implements OnClickListener {

	private CodePanButton btnAuthorization;
	private CodePanTextField etCodeAuthorization;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.authorization_layout, container, false);
		etCodeAuthorization = (CodePanTextField) view.findViewById(R.id.etCodeAuthorization);
		btnAuthorization = (CodePanButton) view.findViewById(R.id.btnAuthorization);
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
		switch(v.getId()) {
			case R.id.btnAuthorization:
				String code = etCodeAuthorization.getText().toString().trim();
				break;
		}
	}
}
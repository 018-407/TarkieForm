package com.mobileoptima.tarkieform;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.codepan.utils.CodePanUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class LoginFragment extends Fragment implements OnClickListener {
	private EditText etUsernameLogin, etPasswordLogin;
	private ImageLoader imageLoader;
	private TextView btnLogin;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTheme(android.R.style.Theme);
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.login_layout, container, false);
		view.findViewById(R.id.rlMain).setOnClickListener(this);
		ImageView ivCompanyLogo = (ImageView) view.findViewById(R.id.ivLogoAuthorization);
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_logo)
				.showImageForEmptyUri(R.drawable.ic_logo)
				.cacheInMemory()
				.cacheOnDisc()
				.build();
		imageLoader.displayImage("https://hatscripts.com/r/03d6.png", ivCompanyLogo, options);
		etUsernameLogin = (EditText) view.findViewById(R.id.etUsernameLogin);
		etPasswordLogin = (EditText) view.findViewById(R.id.etPasswordLogin);
		etPasswordLogin.setTransformationMethod(new PasswordTransformationMethod());
		btnLogin = (TextView) view.findViewById(R.id.btnAuthorization);
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
		if(imageLoader != null) {
			imageLoader.destroy();
		}
	}

	@Override
	public void onClick(View v) {
		CodePanUtils.hideKeyboard(v, getActivity());
		switch(v.getId()) {
			case R.id.btnAuthorization:
				String username = etUsernameLogin.getText().toString().trim();
				String password = etPasswordLogin.getText().toString().trim();
				getActivity().getSupportFragmentManager().popBackStack();
				break;
			default:
				break;
		}
	}
}
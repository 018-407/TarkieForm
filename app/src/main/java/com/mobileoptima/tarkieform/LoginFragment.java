package com.mobileoptima.tarkieform;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.codepan.callback.Interface;
import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanTextField;
import com.mobileoptima.callback.Interface.OnLoginCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.Key;
import com.mobileoptima.constant.Module.Action;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class LoginFragment extends Fragment implements OnClickListener, OnRefreshCallback,
		OnBackPressedCallback, Interface.OnFragmentCallback {

	private CodePanTextField etUsernameLogin, etPasswordLogin;
	private OnOverrideCallback overrideCallback;
	private OnRefreshCallback refreshCallback;
	private OnLoginCallback loginCallback;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private boolean inOtherFragment;
	private CodePanButton btnLogin;
	private SQLiteAdapter db;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((MainActivity) getActivity()).setOnBackPressedCallback(this);
		db = ((MainActivity) getActivity()).getDatabase();
		db.openConnection();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_logo)
				.showImageForEmptyUri(R.drawable.ic_logo)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.login_layout, container, false);
		ImageView ivCompanyLogo = (ImageView) view.findViewById(R.id.ivLogoAuthorization);
		imageLoader.displayImage("https://hatscripts.com/r/03d6.png", ivCompanyLogo, options);
		etUsernameLogin = (CodePanTextField) view.findViewById(R.id.etUsernameLogin);
		etPasswordLogin = (CodePanTextField) view.findViewById(R.id.etPasswordLogin);
		etPasswordLogin.setTransformationMethod(new PasswordTransformationMethod());
		btnLogin = (CodePanButton) view.findViewById(R.id.btnAuthorization);
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
		switch(v.getId()) {
			case R.id.btnAuthorization:
				String username = etUsernameLogin.getText().toString().trim();
				String password = etPasswordLogin.getText().toString().trim();
				if(!username.isEmpty() && !password.isEmpty()) {
					if(CodePanUtils.isInternetConnected(getActivity())) {
						LoadingDialogFragment loading = new LoadingDialogFragment();
						Bundle bundle = new Bundle();
						bundle.putString(Key.USERNAME, username);
						bundle.putString(Key.PASSWORD, password);
						loading.setArguments(bundle);
						loading.setAction(Action.LOGIN);
						loading.setOnRefreshCallback(this);
						loading.setOnFragmentCallback(this);
						loading.setOnOverrideCallback(overrideCallback);
						FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
						transaction.add(R.id.rlMain, loading);
						transaction.addToBackStack(null);
						transaction.commit();
					}
					else {
						CodePanUtils.showAlertToast(getActivity(), "Internet connection required.");
					}
				}
				else {
					CodePanUtils.showAlertToast(getActivity(), "Please input username and password.");
				}
				break;
		}
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}

	public void setOnLoginCallback(OnLoginCallback loginCallback) {
		this.loginCallback = loginCallback;
	}

	@Override
	public void onRefresh() {
		getActivity().getSupportFragmentManager().popBackStack();
		if(refreshCallback != null) {
			refreshCallback.onRefresh();
		}
		if(loginCallback != null) {
			loginCallback.onLogin();
		}
	}

	public void setOnBackStack(boolean isOnBackStack) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(isOnBackStack);
		}
	}

	@Override
	public void onBackPressed() {
		if(!inOtherFragment) {
			getActivity().finish();
		}
		else {
			getActivity().getSupportFragmentManager().popBackStack();
		}
	}

	@Override
	public void onFragment(boolean status) {
		this.inOtherFragment = status;
		if(!status) {
			((MainActivity) getActivity()).setOnBackPressedCallback(this);
			if(overrideCallback != null) {
				overrideCallback.onOverride(true);
			}
		}
	}
}
package com.mobileoptima.tarkieform;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.callback.Interface.OnCreateDatabaseCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.callback.Interface.OnPermissionGrantedCallback;
import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.callback.Interface.OnUpgradeDatabaseCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.cache.SQLiteCache;
import com.mobileoptima.callback.Interface.OnInitializeCallback;
import com.mobileoptima.callback.Interface.OnLoginCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.RequestCode;
import com.mobileoptima.core.TarkieFormLib;

public class SplashFragment extends Fragment implements OnCreateDatabaseCallback,
		OnUpgradeDatabaseCallback, OnPermissionGrantedCallback, OnFragmentCallback {

	private final int DELAY = 2000;

	private boolean isPause, isPending, isRequired;
	private OnInitializeCallback initializeCallback;
	private OnOverrideCallback overrideCallback;
	private OnRefreshCallback refreshCallback;
	private FragmentTransaction transaction;
	private OnLoginCallback loginCallback;
	private SQLiteAdapter db;

	@Override
	public void onPause() {
		super.onPause();
		isPause = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(isPause) {
			checkPermission();
			if(isPending) {
				authenticate();
				isPending = false;
			}
		}
		isPause = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkPermission();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.splash_layout, container, false);
	}

	public void init() {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					db = SQLiteCache.getDatabase(getActivity(), App.DB);
					db.setOnCreateDatabaseCallback(SplashFragment.this);
					db.setOnUpgradeDatabaseCallback(SplashFragment.this);
					db.openConnection();
					TarkieFormLib.createTables(db);
					Thread.sleep(DELAY);
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if(initializeCallback != null) {
				initializeCallback.onInitialize(db);
			}
			if(!isPause) {
				authenticate();
			}
			else {
				isPending = true;
			}
			return true;
		}
	});

	public void setOnInitializeCallback(OnInitializeCallback initializeCallback) {
		this.initializeCallback = initializeCallback;
	}

	public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setOnLoginCallback(OnLoginCallback loginCallback) {
		this.loginCallback = loginCallback;
	}

	public void checkPermission() {
		if(CodePanUtils.isPermissionGranted(getActivity())) {
			if(isRequired) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
			init();
		}
		else {
			if(CodePanUtils.isPermissionHidden(getActivity())) {
				if(!isRequired) {
					//showPermissionNote();
				}
			}
			else {
				((MainActivity) getActivity()).setOnPermissionGrantedCallback(this);
				CodePanUtils.requestPermission(getActivity(), RequestCode.PERMISSION);
			}
		}
	}

	@Override
	public void onCreateDatabase(SQLiteAdapter db) {
		TarkieFormLib.createTables(db);
	}

	@Override
	public void onUpgradeDatabase(SQLiteAdapter db, int oldVersion, int newVersion) {
		TarkieFormLib.alterTables(db, oldVersion, newVersion);
	}

	@Override
	public void onFragment(boolean status) {
	}

	@Override
	public void onPermissionGranted(boolean isPermissionGranted) {
	}

	public void authenticate() {
		getActivity().getSupportFragmentManager().popBackStack();
		if(TarkieFormLib.getAPIKey(db).isEmpty()) {
			AuthorizationFragment authorization = new AuthorizationFragment();
			authorization.setOnOverrideCallback(overrideCallback);
			authorization.setOnRefreshCallback(refreshCallback);
			transaction = getActivity().getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.rlMain, authorization);
			transaction.addToBackStack(null);
			transaction.commit();
			return;
		}
		if(!TarkieFormLib.isLoggedIn(db)) {
			LoginFragment login = new LoginFragment();
			login.setOnOverrideCallback(overrideCallback);
			login.setOnRefreshCallback(refreshCallback);
			login.setOnLoginCallback(loginCallback);
			transaction = getActivity().getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.rlMain, login);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}
}
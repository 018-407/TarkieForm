package com.mobileoptima.tarkieform;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.callback.Interface.OnPermissionGrantedCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.cache.SQLiteCache;
import com.mobileoptima.callback.Interface.OnInitializeCallback;
import com.mobileoptima.constant.App;
import com.mobileoptima.constant.DialogTag;
import com.mobileoptima.constant.RequestCode;
import com.mobileoptima.core.TarkieLib;

public class SplashFragment extends Fragment implements OnPermissionGrantedCallback,
		OnFragmentCallback {

	private final int DELAY = 2000;
	private OnInitializeCallback initializeCallback;
	private FragmentTransaction transaction;
	private FragmentManager manager;
	private SQLiteAdapter db;
	private boolean isPause;

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
		}
		isPause = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		manager = getActivity().getSupportFragmentManager();
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
					db.openConnection();
					TarkieLib.createTables(db);
					TarkieLib.updateTables(db, 0, 0);
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
			manager.popBackStack();
			if(initializeCallback != null) {
				initializeCallback.onInitialize(db);
			}
			return true;
		}
	});

	public void setOnInitializeCallback(OnInitializeCallback initializeCallback) {
		this.initializeCallback = initializeCallback;
	}

	public void checkPermission() {
		if(CodePanUtils.isPermissionGranted(getActivity())) {
			if(CodePanUtils.isOnBackStack(getActivity(), DialogTag.PERMISSION)) {
				manager.popBackStack();
			}
			init();
		}
		else {
			if(CodePanUtils.isPermissionHidden(getActivity())) {
				if(!CodePanUtils.isOnBackStack(getActivity(), DialogTag.PERMISSION)) {
					showPermissionNote();
				}
			}
			else {
				((MainActivity) getActivity()).setOnPermissionGrantedCallback(this);
				CodePanUtils.requestPermission(getActivity(), RequestCode.PERMISSION);
			}
		}
	}

	@Override
	public void onFragment(boolean status) {
	}

	@Override
	public void onPermissionGranted(boolean isPermissionGranted) {
	}

	public void showPermissionNote() {
		String name = getString(R.string.app_name);
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setOnFragmentCallback(this);
		alert.setDialogTitle("Permissions Required");
		alert.setDialogMessage("Please enable the required permissions to continue using " + name + ".");
		alert.setPositiveButton("Settings", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				manager.popBackStack();
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
				getActivity().startActivity(intent);
			}
		});
		alert.setNegativeButton("Exit", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				manager.popBackStack();
				getActivity().finish();
			}
		});
		transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
				R.anim.fade_in, R.anim.fade_out);
		transaction.add(R.id.rlMain, alert, DialogTag.PERMISSION);
		transaction.addToBackStack(null);
		transaction.commit();
	}
}
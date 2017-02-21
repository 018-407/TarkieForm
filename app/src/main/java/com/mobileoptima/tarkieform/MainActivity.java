package com.mobileoptima.tarkieform;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnPermissionGrantedCallback;
import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.adapter.FormAdapter;
import com.mobileoptima.callback.Interface.OnInitializeCallback;
import com.mobileoptima.callback.Interface.OnLoginCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.RequestCode;
import com.mobileoptima.constant.Tag;
import com.mobileoptima.core.Data;
import com.mobileoptima.core.TarkieFormLib;
import com.mobileoptima.object.FormObj;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnInitializeCallback, OnLoginCallback,
		OnOverrideCallback, OnRefreshCallback {

	private OnPermissionGrantedCallback permissionGrantedCallback;
	private OnBackPressedCallback backPressedCallback;
	private boolean isInitialized, isOverridden;
	private FragmentTransaction transaction;
	private ArrayList<FormObj> formList;
	private RelativeLayout rlMain;
	private FormAdapter adapter;
	private int width, height;
	private SQLiteAdapter db;
	private ListView lvMain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		rlMain = (RelativeLayout) findViewById(R.id.rlMain);
		lvMain = (ListView) findViewById(R.id.lvMain);
		lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				FormObj obj = formList.get(i);
				FormFragment form = new FormFragment();
				form.setForm(obj);
				form.setOnOverrideCallback(MainActivity.this);
				transaction = getSupportFragmentManager().beginTransaction();
				transaction.setCustomAnimations(R.anim.slide_in_rtl, R.anim.slide_out_rtl,
						R.anim.slide_in_ltr, R.anim.slide_out_ltr);
				transaction.replace(R.id.rlMain, form, Tag.FORM);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
		init(savedInstanceState);
	}

	public void init(Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			if(isInitialized) {
				checkRevokedPermissions();
			}
			else {
				this.finish();
				overridePendingTransition(0, 0);
				Intent intent = new Intent(this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			}
		}
		else {
			SplashFragment splash = new SplashFragment();
			splash.setOnInitializeCallback(this);
			splash.setOnRefreshCallback(this);
			splash.setOnOverrideCallback(this);
			splash.setOnLoginCallback(this);
			transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.rlMain, splash);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	public void checkRevokedPermissions() {
		if(!CodePanUtils.isPermissionGranted(this)) {
			getSupportFragmentManager().popBackStack(null,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			Intent intent = new Intent(this, getClass());
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			this.finish();
		}
	}

	@Override
	public void onInitialize(SQLiteAdapter db) {
		this.isInitialized = true;
		this.db = db;
		loadForms(db);
	}

	@Override
	public void onRefresh() {
		authenticate();
	}

	@Override
	public void onLogin(String empID) {
	}

	@Override
	public void onOverride(boolean isOverridden) {
		this.isOverridden = isOverridden;
	}

	public void setOnPermissionGrantedCallback(OnPermissionGrantedCallback permissionGrantedCallback) {
		this.permissionGrantedCallback = permissionGrantedCallback;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		switch(requestCode) {
			case RequestCode.PERMISSION:
				if(grantResults.length > 0) {
					boolean isPermissionGranted = true;
					for(int result : grantResults) {
						if(result == PackageManager.PERMISSION_DENIED) {
							isPermissionGranted = false;
							break;
						}
					}
					if(permissionGrantedCallback != null) {
						permissionGrantedCallback.onPermissionGranted(isPermissionGranted);
					}
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
				break;
		}
	}

	public SQLiteAdapter getDatabase() {
		return this.db;
	}

	public void loadForms(final SQLiteAdapter db) {
		Thread bg = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					formList = Data.loadForms(db);
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
		public boolean handleMessage(Message message) {
			adapter = new FormAdapter(MainActivity.this, formList);
			lvMain.setAdapter(adapter);
			return true;
		}
	});

	public void setOnBackPressedCallback(OnBackPressedCallback backPressedCallback) {
		this.backPressedCallback = backPressedCallback;
	}

	@Override
	public void onBackPressed() {
		if(isInitialized) {
			if(isOverridden) {
				if(backPressedCallback != null) {
					backPressedCallback.onBackPressed();
				}
			}
			else {
				super.onBackPressed();
			}
		}
		else {
			this.finish();
		}
	}

	public void authenticate() {
		if(!TarkieFormLib.isAuthorized(db)) {
			AuthorizationFragment authorization = new AuthorizationFragment();
			authorization.setOnOverrideCallback(this);
			authorization.setOnRefreshCallback(this);
			transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.rlMain, authorization);
			transaction.addToBackStack(null);
			transaction.commit();
		}
		else {
			if(!TarkieFormLib.isLoggedIn(db)) {
				LoginFragment login = new LoginFragment();
				login.setOnOverrideCallback(this);
				login.setOnRefreshCallback(this);
//				login.setOnLoginCallback(this);
				transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.rlMain, login);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		this.width = rlMain.getWidth();
		this.height = rlMain.getHeight();
	}

	public int getHeight(){
		return this.height;
	}

	public int getWidth(){
		return this.width;
	}
}

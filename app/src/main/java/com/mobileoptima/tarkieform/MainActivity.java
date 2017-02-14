package com.mobileoptima.tarkieform;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.codepan.callback.Interface.OnPermissionGrantedCallback;
import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.callback.Interface.OnInitializeCallback;
import com.mobileoptima.callback.Interface.OnLoginCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.RequestCode;

public class MainActivity extends FragmentActivity implements OnInitializeCallback, OnLoginCallback,
		OnOverrideCallback, OnRefreshCallback {

	private OnPermissionGrantedCallback permissionGrantedCallback;
	private boolean isInitialized, isOverridden;
	private FragmentTransaction transaction;
	private SQLiteAdapter db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		AuthorizationFragment authorization = new AuthorizationFragment();
		transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.rlMain, authorization);
		transaction.addToBackStack(null);
		transaction.commit();
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
	}

	@Override
	public void onRefresh() {
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
}

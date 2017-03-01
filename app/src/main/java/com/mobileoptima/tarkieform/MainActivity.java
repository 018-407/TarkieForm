package com.mobileoptima.tarkieform;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.codepan.calendar.adapter.ViewPagerAdapter;
import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnPermissionGrantedCallback;
import com.codepan.callback.Interface.OnRefreshCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanButton;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.callback.Interface.OnInitializeCallback;
import com.mobileoptima.callback.Interface.OnLoginCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.Module.Action;
import com.mobileoptima.constant.RequestCode;
import com.mobileoptima.constant.Tab;
import com.mobileoptima.core.TarkieFormLib;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnClickListener, OnInitializeCallback,
		OnOverrideCallback, OnRefreshCallback, OnLoginCallback, OnPageChangeListener {

	private LinearLayout llSettingsMain, llSyncDataMain, llUpdateMasterMain,
			llAboutMain, llLogoutMain;
	private CodePanButton btnHomeMain, btnEntriesMain, btnPhotosMain, btnMenuMain;
	private CodePanLabel tvSyncCountMain, tvHomeMain, tvEntriesMain, tvPhotosMain, tvEmployeeMain;
	private OnPermissionGrantedCallback permissionGrantedCallback;
	private ImageView ivHomeMain, ivEntriesMain, ivPhotosMain;
	private OnBackPressedCallback backPressedCallback;
	private boolean isInitialized, isOverridden;
	private ArrayList<Fragment> fragmentList;
	private FragmentTransaction transaction;
	private CodePanButton btnSyncMain;
	private ViewPagerAdapter adapter;
	private ScrollView svMenuMain;
	private RelativeLayout rlMain;
	private DrawerLayout dlMain;
	private int width, height;
	private ViewPager vpMain;
	private SQLiteAdapter db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		llUpdateMasterMain = (LinearLayout) findViewById(R.id.llUpdateMasterMain);
		llSettingsMain = (LinearLayout) findViewById(R.id.llSettingsMain);
		llSyncDataMain = (LinearLayout) findViewById(R.id.llSyncDataMain);
		llAboutMain = (LinearLayout) findViewById(R.id.llAboutMain);
		llLogoutMain = (LinearLayout) findViewById(R.id.llLogoutMain);
		btnHomeMain = (CodePanButton) findViewById(R.id.btnHomeMain);
		btnEntriesMain = (CodePanButton) findViewById(R.id.btnEntriesMain);
		btnPhotosMain = (CodePanButton) findViewById(R.id.btnPhotosMain);
		btnSyncMain = (CodePanButton) findViewById(R.id.btnSyncMain);
		tvSyncCountMain = (CodePanLabel) findViewById(R.id.tvSyncCountMain);
		btnMenuMain = (CodePanButton) findViewById(R.id.btnMenuMain);
		ivHomeMain = (ImageView) findViewById(R.id.ivHomeMain);
		ivEntriesMain = (ImageView) findViewById(R.id.ivEntriesMain);
		ivPhotosMain = (ImageView) findViewById(R.id.ivPhotosMain);
		tvEmployeeMain = (CodePanLabel) findViewById(R.id.tvEmployeeMain);
		tvEntriesMain = (CodePanLabel) findViewById(R.id.tvEntriesMain);
		tvPhotosMain = (CodePanLabel) findViewById(R.id.tvPhotosMain);
		tvHomeMain = (CodePanLabel) findViewById(R.id.tvHomeMain);
		svMenuMain = (ScrollView) findViewById(R.id.svMenuMain);
		rlMain = (RelativeLayout) findViewById(R.id.rlMain);
		dlMain = (DrawerLayout) findViewById(R.id.dlMain);
		vpMain = (ViewPager) findViewById(R.id.vpMain);
		llUpdateMasterMain.setOnClickListener(this);
		llSettingsMain.setOnClickListener(this);
		llSyncDataMain.setOnClickListener(this);
		llAboutMain.setOnClickListener(this);
		llLogoutMain.setOnClickListener(this);
		btnSyncMain.setOnClickListener(this);
		btnHomeMain.setOnClickListener(this);
		btnEntriesMain.setOnClickListener(this);
		btnPhotosMain.setOnClickListener(this);
		btnMenuMain.setOnClickListener(this);
		vpMain.addOnPageChangeListener(this);
		int color = getResources().getColor(R.color.black_trans_twenty);
		dlMain.setScrimColor(color);
		init(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnHomeMain:
				vpMain.setCurrentItem(Tab.HOME);
				break;
			case R.id.btnEntriesMain:
				vpMain.setCurrentItem(Tab.ENTRIES);
				break;
			case R.id.btnPhotosMain:
				vpMain.setCurrentItem(Tab.PHOTOS);
				break;
			case R.id.btnMenuMain:
				if(dlMain.isDrawerOpen(svMenuMain)) {
					dlMain.closeDrawer(svMenuMain);
				}
				else {
					dlMain.openDrawer(svMenuMain);
				}
				break;
			case R.id.btnSyncMain:
				llSyncDataMain.performClick();
				break;
			case R.id.llSettingsMain:
				dlMain.closeDrawer(svMenuMain);
				break;
			case R.id.llSyncDataMain:
				dlMain.closeDrawer(svMenuMain);
				int count = TarkieFormLib.getCountSyncTotal(db);
				if(count > 0) {
					String transactions = count == 1 ? "transaction" : "transactions";
					String message = "You have " + count + " unsaved " + transactions + ". " +
							"Do you want to send it to the server now?";
					final AlertDialogFragment alert = new AlertDialogFragment();
					alert.setDialogTitle("Sync Data");
					alert.setDialogMessage(message);
					alert.setPositiveButton("Yes", new OnClickListener() {
						@Override
						public void onClick(View v) {
							alert.getDialogActivity().getSupportFragmentManager().popBackStack();
							LoadingDialogFragment loading = new LoadingDialogFragment();
							loading.setAction(Action.SYNC_DATA);
							loading.setOnRefreshCallback(MainActivity.this);
							loading.setOnOverrideCallback(MainActivity.this);
							transaction = getSupportFragmentManager().beginTransaction();
							transaction.add(R.id.rlMain, loading);
							transaction.addToBackStack(null);
							transaction.commit();
						}
					});
					alert.setNegativeButton("No", new OnClickListener() {
						@Override
						public void onClick(View v) {
							alert.getDialogActivity().getSupportFragmentManager().popBackStack();
						}
					});
					transaction = getSupportFragmentManager().beginTransaction();
					transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
							R.anim.fade_in, R.anim.fade_out);
					transaction.add(R.id.rlMain, alert);
					transaction.addToBackStack(null);
					transaction.commit();
				}
				else {
					CodePanUtils.showAlertToast(this, "No data to be synced.", Toast.LENGTH_SHORT);
				}
				break;
			case R.id.llUpdateMasterMain:
				dlMain.closeDrawer(svMenuMain);
				if(CodePanUtils.isInternetConnected(this)) {
					final AlertDialogFragment alert = new AlertDialogFragment();
					alert.setDialogTitle("Update Master list");
					alert.setDialogMessage("Do you want to download the latest master list?");
					alert.setPositiveButton("Yes", new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							alert.getDialogActivity().getSupportFragmentManager().popBackStack();
							LoadingDialogFragment loading = new LoadingDialogFragment();
							Bundle bundle = new Bundle();
							loading.setArguments(bundle);
							loading.setAction(Action.UPDATE_MASTERLIST);
							loading.setOnRefreshCallback(MainActivity.this);
							loading.setOnOverrideCallback(MainActivity.this);
							transaction = getSupportFragmentManager().beginTransaction();
							transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
									R.anim.fade_in, R.anim.fade_out);
							transaction.add(R.id.rlMain, loading);
							transaction.addToBackStack(null);
							transaction.commit();
						}
					});
					alert.setNegativeButton("Cancel", new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							alert.getDialogActivity().getSupportFragmentManager().popBackStack();
						}
					});
					transaction = getSupportFragmentManager().beginTransaction();
					transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
							R.anim.fade_in, R.anim.fade_out);
					transaction.add(R.id.rlMain, alert);
					transaction.addToBackStack(null);
					transaction.commit();
				}
				else {
					CodePanUtils.showAlertToast(this, "Internet connection required.");
				}
				break;
			case R.id.llAboutMain:
				dlMain.closeDrawer(svMenuMain);
				break;
			case R.id.llLogoutMain:
				dlMain.closeDrawer(svMenuMain);
				final AlertDialogFragment alert = new AlertDialogFragment();
				alert.setDialogTitle("Logout");
				alert.setDialogMessage("Are you sure you want to logout?");
				alert.setPositiveButton("Yes", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						alert.getDialogActivity().getSupportFragmentManager().popBackStack();
						boolean result = TarkieFormLib.logout(db);
						if(result) {
							alert.getDialogActivity().getSupportFragmentManager().popBackStack();
							onRefresh();
						}
					}
				});
				alert.setNegativeButton("Cancel", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					}
				});
				transaction = getSupportFragmentManager().beginTransaction();
				transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
						R.anim.fade_in, R.anim.fade_out);
				transaction.add(R.id.rlMain, alert);
				transaction.addToBackStack(null);
				transaction.commit();
				break;
		}
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
		updateUser();
		loadTabs();
	}

	@Override
	public void onRefresh() {
		authenticate();
		reloadForms();
	}

	public void loadTabs() {
		HomeFragment home = new HomeFragment();
		EntriesFragment entries = new EntriesFragment();
		PhotosFragment photos = new PhotosFragment();
		home.setOnOverrideCallback(this);
		entries.setOnOverrideCallback(this);
		fragmentList = new ArrayList<>();
		fragmentList.add(home);
		fragmentList.add(entries);
		fragmentList.add(photos);
		adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList);
		vpMain.setOffscreenPageLimit(2);
		vpMain.setAdapter(adapter);
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
				int count = getSupportFragmentManager().getBackStackEntryCount();
				if(count != 0) {
					super.onBackPressed();
				}
				else {
					if(vpMain.getCurrentItem() != Tab.HOME) {
						vpMain.setCurrentItem(Tab.HOME);
					}
					else {
						super.onBackPressed();
					}
				}
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
				login.setOnLoginCallback(this);
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

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public void reloadForms() {
		if(fragmentList != null) {
			Fragment fragment = fragmentList.get(Tab.HOME);
			HomeFragment home = (HomeFragment) fragment;
			home.loadForms(db);
		}
	}

	public void reloadEntries() {
		if(fragmentList != null) {
			Fragment fragment = fragmentList.get(Tab.ENTRIES);
			EntriesFragment entries = (EntriesFragment) fragment;
			entries.loadEntries(db);
		}
	}

	public void reloadPhotos() {
		if(fragmentList != null) {
			Fragment fragment = fragmentList.get(Tab.PHOTOS);
			PhotosFragment photos = (PhotosFragment) fragment;
			photos.loadPhotos(db);
		}
	}

	public void switchTab(int tab) {
		if(vpMain != null) {
			vpMain.setCurrentItem(tab);
		}
	}

	@Override
	public void onLogin() {
		reloadForms();
		reloadEntries();
		reloadPhotos();
		updateUser();
	}

	public void updateUser() {
		if(isInitialized) {
			String empID = TarkieFormLib.getEmployeeID(db);
			String name = TarkieFormLib.getEmployeeName(db, empID);
			tvEmployeeMain.setText(name);
			tvSyncCountMain.setText(String.valueOf(TarkieFormLib.getCountSyncTotal(db)));
		}
	}

	@Override
	public void onPageSelected(int position) {
		int green = getResources().getColor(R.color.green);
		int graySec = getResources().getColor(R.color.gray_sec);
		switch(position) {
			case Tab.HOME:
				ivHomeMain.setImageResource(R.drawable.ic_home_active);
				ivEntriesMain.setImageResource(R.drawable.ic_entries_inactive);
				ivPhotosMain.setImageResource(R.drawable.ic_photos_inactive);
				tvHomeMain.setTextColor(green);
				tvEntriesMain.setTextColor(graySec);
				tvPhotosMain.setTextColor(graySec);
				break;
			case Tab.ENTRIES:
				ivHomeMain.setImageResource(R.drawable.ic_home_inactive);
				ivEntriesMain.setImageResource(R.drawable.ic_entries_active);
				ivPhotosMain.setImageResource(R.drawable.ic_photos_inactive);
				tvHomeMain.setTextColor(graySec);
				tvEntriesMain.setTextColor(green);
				tvPhotosMain.setTextColor(graySec);
				break;
			case Tab.PHOTOS:
				ivHomeMain.setImageResource(R.drawable.ic_home_inactive);
				ivEntriesMain.setImageResource(R.drawable.ic_entries_inactive);
				ivPhotosMain.setImageResource(R.drawable.ic_photos_active);
				tvHomeMain.setTextColor(graySec);
				tvEntriesMain.setTextColor(graySec);
				tvPhotosMain.setTextColor(green);
				break;
		}
	}

	@Override
	public void onPageScrolled(int position, float offset, int px) {
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}
}

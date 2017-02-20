package com.mobileoptima.tarkieform;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.utils.CodePanUtils;
import com.mobileoptima.callback.Interface.OnGpsFixedCallback;

public class SearchGpsFragment extends Fragment implements LocationListener {

	private boolean runThread, isGpsFixed, isPause;
	private OnGpsFixedCallback gpsFixedCallback;
	private OnFragmentCallback fragmentCallback;
	private FragmentTransaction transaction;
	private LocationManager locationManager;
	private ImageView ivLoadingSearchGps;
	private double longitude, latitude;
	private long lastMillis;
	private Animation anim;
	private Thread bg;

	@Override
	public void onStart() {
		super.onStart();
		setOnBackStack(true);
		if(locationManager != null) {
			try {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
			}
			catch(SecurityException se) {
				se.printStackTrace();
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		setOnBackStack(false);
		if(locationManager != null) {
			try {
				locationManager.removeUpdates(this);
			}
			catch(SecurityException se) {
				se.printStackTrace();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(isPause && isGpsFixed) {
			showResult();
		}
		isPause = false;
	}

	@Override
	public void onPause() {
		super.onPause();
		isPause = true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_gps_layout, container, false);
		ivLoadingSearchGps = (ImageView) view.findViewById(R.id.ivLoadingSearchGps);
		anim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_clockwise);
		searchGps();
		return view;
	}

	public void searchGps() {
		ivLoadingSearchGps.startAnimation(anim);
		runThread = true;
		isGpsFixed = false;
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					while(runThread) {
						Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if(location != null) {
							if(location.getTime() != lastMillis) {
								longitude = location.getLongitude();
								latitude = location.getLatitude();
								isGpsFixed = true;
							}
							lastMillis = location.getTime();
						}
						Thread.sleep(1000);
						loaderHandler.sendMessage(loaderHandler.obtainMessage());
					}
				}
				catch(SecurityException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		bg.start();
	}

	Handler loaderHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if(runThread) {
				checkGPSStatus();
			}
			return true;
		}
	});

	public void checkGPSStatus() {
		if(CodePanUtils.isGpsEnabled(getActivity())) {
			if(isGpsFixed) {
				stopSearch();
				ivLoadingSearchGps.clearAnimation();
				if(!isPause) {
					showResult();
				}
			}
		}
		else {
			if(!isPause) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {
	}

	@Override
	public void onProviderEnabled(String s) {
	}

	@Override
	public void onProviderDisabled(String s) {
	}

	public void showResult() {
		getActivity().getSupportFragmentManager().popBackStack();
		String title = "GPS Acquired";
		String message = "GPS has been acquired, do you want to save this coordinates?";
		final AlertDialogFragment alert = new AlertDialogFragment();
		alert.setDialogTitle(title);
		alert.setDialogMessage(message);
		alert.setPositiveButton("Save", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.getDialogActivity().getSupportFragmentManager().popBackStack();
				if(gpsFixedCallback != null) {
					gpsFixedCallback.onGpsFixed(latitude, longitude);
				}
			}
		});
		alert.setNegativeButton("Cancel", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.getDialogActivity().getSupportFragmentManager().popBackStack();
			}
		});
		transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.rlMain, alert);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void setOnGpsFixedCallback(OnGpsFixedCallback gpsFixedCallback) {
		this.gpsFixedCallback = gpsFixedCallback;
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	public void stopSearch() {
		runThread = false;
		if(bg.isAlive()) {
			bg.interrupt();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSearch();
	}

	public void setOnBackStack(boolean isOnBackStack) {
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}
}

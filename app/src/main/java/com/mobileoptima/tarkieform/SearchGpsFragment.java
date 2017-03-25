package com.mobileoptima.tarkieform;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.utils.CodePanUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mobileoptima.callback.Interface.OnGpsFixedCallback;
import com.mobileoptima.core.TarkieLib;
import com.mobileoptima.object.GpsObj;

public class SearchGpsFragment extends Fragment implements LocationListener, ConnectionCallbacks,
		OnConnectionFailedListener {

	private final long FASTEST_UPDATE_INTERVAL = 1000;
	private final long UPDATE_INTERVAL = 5000;
	private final float ACCURACY = 100;

	private boolean runThread, isGpsFixed, isPause;
	private OnGpsFixedCallback gpsFixedCallback;
	private OnFragmentCallback fragmentCallback;
	private FragmentTransaction transaction;
	private GoogleApiClient googleApiClient;
	private LocationRequest locationRequest;
	private ImageView ivLoadingSearchGps;
	private long lastLocationUpdate;
	private Location location;
	private Animation anim;
	private GpsObj gps;
	private Thread bg;

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
		buildGoogleApiClient();
		googleApiClient.connect();
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
						GpsObj obj = TarkieLib.getGPS(getActivity(), location, lastLocationUpdate, UPDATE_INTERVAL, ACCURACY);
						if(obj.isValid) {
							isGpsFixed = true;
							gps = obj;
						}
						loaderHandler.sendMessage(loaderHandler.obtainMessage());
						Thread.sleep(1000);
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
					gpsFixedCallback.onGpsFixed(gps);
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
		if(googleApiClient.isConnected()) {
			stopLocationUpdates();
		}
	}

	public void setOnBackStack(boolean isOnBackStack) {
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}

	protected synchronized void buildGoogleApiClient() {
		googleApiClient = new GoogleApiClient.Builder(getActivity())
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		createLocationRequest();
	}

	protected void createLocationRequest() {
		locationRequest = new LocationRequest();
		locationRequest.setInterval(UPDATE_INTERVAL);
		locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	protected void startLocationUpdates() {
		try {
			LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
		}
		catch(SecurityException se) {
			se.printStackTrace();
		}
	}

	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		this.lastLocationUpdate = SystemClock.elapsedRealtime();
		this.location = location;
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.e("Google API Client", "connected");
		if(location == null) {
			try {
				location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
			}
			catch(SecurityException se) {
				se.printStackTrace();
			}
		}
		startLocationUpdates();
	}

	@Override
	public void onConnectionSuspended(int i) {
		googleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e("Google API Client", "connection failed");
	}
}

package com.mobileoptima.tarkieform;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.codepan.callback.Interface.OnBackPressedCallback;
import com.codepan.callback.Interface.OnFragmentCallback;
import com.codepan.database.SQLiteAdapter;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.codepan.widget.ProgressWheel;
import com.mobileoptima.callback.Interface.OnErrorCallback;
import com.mobileoptima.callback.Interface.OnOverrideCallback;
import com.mobileoptima.constant.Key;
import com.mobileoptima.constant.Module;
import com.mobileoptima.constant.Module.Action;
import com.mobileoptima.core.Rx;

import static com.codepan.callback.Interface.OnRefreshCallback;

public class LoadingDialogFragment extends Fragment implements OnClickListener, OnErrorCallback,
		OnBackPressedCallback, OnFragmentCallback {

	private Action action;
	private CodePanLabel tvTitleLoadingDialog, tvCountLoadingDialog;
	private String successMsg, failedMsg, error, message, title;
	private boolean result, isDone, isPause, inOtherFragment;
	private OnFragmentCallback fragmentCallback;
	private OnOverrideCallback overrideCallback;
	private ProgressWheel progressLoadingDialog;
	private OnRefreshCallback refreshCallback;
	private FragmentTransaction transaction;
	private int progress, max;
	private SQLiteAdapter db;
	private float percent;
	private Bundle bundle;
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
	public void onPause() {
		super.onPause();
		isPause = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(isPause && isDone) {
			showResult(message);
		}
		isPause = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((MainActivity) getActivity()).setOnBackPressedCallback(this);
		db = ((MainActivity) getActivity()).getDatabase();
		db.openConnection();
		bundle = this.getArguments();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.loading_dialog_layout, container, false);
		progressLoadingDialog = (ProgressWheel) view.findViewById(R.id.progressLoadingDialog);
		tvTitleLoadingDialog = (CodePanLabel) view.findViewById(R.id.tvTitleLoadingDialog);
		tvCountLoadingDialog = (CodePanLabel) view.findViewById(R.id.tvCountLoadingDialog);
		setMax(1);
		title = "Authorizing Device...";
		successMsg = "Authorization successful.";
		failedMsg = "Failed to authorize the device.";
		String authorizationCode = bundle.getString(Key.AUTH_CODE);
		authorizeDevice(db, authorizationCode);
		tvTitleLoadingDialog.setText(title);
		return view;
	}

	public void authorizeDevice(final SQLiteAdapter db, final String authorizationCode) {
		bg = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					result = Rx.authorizeDevice(db, authorizationCode, getErrorCallback());
					Thread.sleep(250);
					handler.sendMessage(handler.obtainMessage());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		bg.setName(Module.getTitle(Action.AUTHORIZE_DEVICE));
		bg.start();
	}

	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if(result) {
				updateProgress();
				if(progress >= max) {
					message = successMsg;
					isDone = true;
					if(!isPause) {
						showResult(message);
					}
				}
			}
			else {
				isDone = true;
				message = error != null ? error : failedMsg;
				if(!isPause) {
					showResult(message);
				}
			}
			return true;
		}
	});

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		}
	}

	public void updateProgress() {
		progress++;
		percent = ((float) progress / (float) max) * 100f;
		String percentage = (int) percent + "%";
		progressLoadingDialog.incrementProgress();
		progressLoadingDialog.setText(percentage);
		String count = progress + "/" + max;
		tvCountLoadingDialog.setText(count);
	}

	public void setMax(int max) {
		this.max = max;
		progress = 0;
		progressLoadingDialog.setmax(max);
		String count = "0/" + max;
		tvCountLoadingDialog.setText(count);
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public void setOnRefreshCallback(OnRefreshCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}

	public void setOnOverrideCallback(OnOverrideCallback overrideCallback) {
		this.overrideCallback = overrideCallback;
	}

	public void setOnFragmentCallback(OnFragmentCallback fragmentCallback) {
		this.fragmentCallback = fragmentCallback;
	}

	public void setOnBackStack(boolean isOnBackStack) {
		if(overrideCallback != null) {
			overrideCallback.onOverride(isOnBackStack);
		}
		if(fragmentCallback != null) {
			fragmentCallback.onFragment(isOnBackStack);
		}
	}

	public void showResult(String message) {
		if(!inOtherFragment) {
			getActivity().getSupportFragmentManager().popBackStack();
			final AlertDialogFragment alert = new AlertDialogFragment();
//			alert.setStatusBarColorInActive(R.color.sv_blue_sec);
			alert.setStatusBarColorActive(R.color.black);
			alert.setDialogMessage(message);
			alert.setPositiveButton("Okay", new OnClickListener() {
				@Override
				public void onClick(View v) {
					alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					if(refreshCallback != null) {
						refreshCallback.onRefresh();
					}
				}
			});
			transaction = getActivity().getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.rlMain, alert);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@Override
	public void onError(String error, String params, String response, boolean showError) {
		CodePanUtils.setErrorMsg(getActivity(), error, params, response);
		if(showError) {
			this.error = error;
		}
	}

	public OnErrorCallback getErrorCallback() {
		return this;
	}

	@Override
	public void onBackPressed() {
		if(!inOtherFragment) {
			String title = Module.getTitle(action);
			final AlertDialogFragment alert = new AlertDialogFragment();
			alert.setOnFragmentCallback(this);
			alert.setDialogMessage("Are you sure you want to cancel " + title + "?");
			alert.setPositiveButton("Yes", new OnClickListener() {
				@Override
				public void onClick(View v) {
					alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					alert.getDialogActivity().getSupportFragmentManager().popBackStack();
				}
			});
			alert.setNegativeButton("No", new OnClickListener() {
				@Override
				public void onClick(View v) {
					alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					if(isDone) {
						alert.getDialogActivity().getSupportFragmentManager().popBackStack();
					}
				}
			});
			transaction = getActivity().getSupportFragmentManager().beginTransaction();
//			if(Settings.ENABLE_TRANSITION) {
//				transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
//						R.anim.fade_in, R.anim.fade_out);
//			}
			transaction.add(R.id.rlMain, alert);
			transaction.addToBackStack(null);
			transaction.commit();
		}
		else {
			getActivity().getSupportFragmentManager().popBackStack();
		}
	}

	@Override
	public void onFragment(boolean status) {
		this.inOtherFragment = status;
	}

	public void stopProcess() {
		if(bg != null && bg.isAlive()) {
			bg.interrupt();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopProcess();
	}
}

